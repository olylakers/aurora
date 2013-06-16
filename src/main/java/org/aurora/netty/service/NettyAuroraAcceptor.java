package org.aurora.netty.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aurora.netty.channel.ChannelManager;
import org.aurora.netty.handler.ChannelStateCheckHandler;
import org.aurora.netty.handler.DispachPushEventHandler;
import org.aurora.netty.handler.DispachUpEventHandler;
import org.aurora.netty.handler.EventDispacher;
import org.aurora.netty.serializer.AuroraNettyEncoder;
import org.aurora.netty.serializer.AuroraNettyPushEventDecoder;
import org.aurora.netty.util.AuroraOptions;
import org.aurora.netty.util.ConfigUtil;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.taobao.top.xbox.util.NamedThreadFactory;
/**
 * @author hantong
 *
 * 2013-6-9 上午9:34:18 
 */
public class NettyAuroraAcceptor implements AuroraAcceptor {
	private static final Log LOG = LogFactory.getLog(NettyAuroraAcceptor.class);
	
	private HashedWheelTimer idleTimer = new HashedWheelTimer();
	private ServerBootstrap bootstrap;
	private ServerBootstrap receivePushbootstrap;
	private AtomicBoolean alive = new AtomicBoolean(false);
	
	private EventDispacher eventDispacher;
	private ChannelManager channelManager;
	private AuroraService auroraService;
	
	/**
	 * 存放Option列表
	 */
	private Map<String, Object> options = new HashMap<String, Object>();
	private LinkedHashMap<String, ChannelHandler> handlers = new LinkedHashMap<String, ChannelHandler>();
	
	
	public NettyAuroraAcceptor(Executor bossExecutor, Executor workerExecutor,
			int workerCount, EventDispacher eventDispacher,
			ChannelManager channelManager,
			AuroraService auroraService) { 
		this.channelManager = channelManager;
		this.auroraService = auroraService;
		this.eventDispacher = eventDispacher;
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				bossExecutor, workerExecutor, workerCount));
		ThreadFactory serverBossTF = new NamedThreadFactory("netty-receive-server-boss");
		ThreadFactory serverWorkerTF = new NamedThreadFactory("netty-receive-server-worker");
		receivePushbootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(serverBossTF),
                        Executors.newCachedThreadPool(serverWorkerTF)));
	}
	
	public void init(int outPort, int innerPort){
		bind(outPort);
		bindReceivePushPort(innerPort);
	}

	public Channel bind(int port) {
		SocketAddress socketAddress = new InetSocketAddress(port);
		return bind(socketAddress);
	}

	public Channel bind(SocketAddress socketAddress) {
		// 设置Option
		for (String key : options.keySet()) {
			bootstrap.setOption(key, options.get(key));
		}

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				// 注册各种自定义Handler
				for (String key : handlers.keySet()) {
					pipeline.addLast(key, handlers.get(key));
				}
				
				// 注册链路空闲检测Handler
				Integer writeIdleTime = ConfigUtil.parseInt(options.get(AuroraOptions.WRITE_IDLE_TIME));
				Integer readIdleTime = ConfigUtil.parseInt(options.get(AuroraOptions.READ_IDLE_TIME));
				if (writeIdleTime == null) {
					writeIdleTime = 10;
				}
				if (readIdleTime == null) {
					// 默认为写空闲的3倍
					readIdleTime = writeIdleTime * 3;
				}

				pipeline.addLast("timeout", new IdleStateHandler(idleTimer, readIdleTime, writeIdleTime, 0));
				pipeline.addLast("idleHandler", new ChannelStateCheckHandler(channelManager));

				// 注册事件分发Handler
				pipeline.addLast("dispatchHandler", new DispachUpEventHandler(eventDispacher,channelManager, auroraService));

				return pipeline;
			}
		});

		// 监听端口
		Channel channel  = 	bootstrap.bind(socketAddress);
		LOG.warn("netty server start");
		alive.set(true);

		return channel;
	}
	
	public Channel bindReceivePushPort(int innerPort) {
		SocketAddress socketAddress = new InetSocketAddress(innerPort);
		receivePushbootstrap.setOption("tcpNoDelay", true);
		receivePushbootstrap.setOption("reuseAddress", true);
		receivePushbootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				// 注册事件分发Handler
				pipeline.addLast("decoder", new AuroraNettyPushEventDecoder());
				pipeline.addLast("encoder", new AuroraNettyEncoder());
				DispachPushEventHandler dispachPushEventHandler = new DispachPushEventHandler(eventDispacher);
				pipeline.addLast("dispatchHandler", dispachPushEventHandler);

				return pipeline;
			}
		});

		// 监听端口
		Channel channel  = 	receivePushbootstrap.bind(socketAddress);
		LOG.warn("receive push event netty server start");
		return channel;
	}	
	
	public Map<String, Object> getOptions() {
		return options;
	}

	public Object getOption(String name) {
		return options.get(name);
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public void setOption(String name, Object value) {
		options.put(name, value);
	}

	public LinkedHashMap<String, ChannelHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(LinkedHashMap<String, ChannelHandler> handlers) {
		this.handlers = handlers;
	}

	public void shutDown() {
		idleTimer.stop();
		if(bootstrap != null){
			bootstrap.releaseExternalResources();
		}
	}
}

package org.aurora.netty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aurora.log.LogAsynWriter;
import org.aurora.netty.channel.ChannelManager;
import org.aurora.netty.handler.EventDispacher;
import org.aurora.netty.listener.ChannelEventListener;
import org.aurora.netty.listener.HandShakeMessageListener;
import org.aurora.netty.listener.MessageEventListener;
import org.aurora.netty.serializer.AuroraNettyDecoder;
import org.aurora.netty.serializer.AuroraNettyEncoder;
import org.aurora.netty.util.AuroraOptions;
import org.jboss.netty.channel.ChannelHandler;

import com.taobao.top.xbox.util.NamedThreadFactory;

/**
 * @author hantong
 *
 * 2013-6-9 上午9:39:43 
 */
public class AuroraServiceImpl implements AuroraService {
	private static final Log LOG = LogFactory.getLog(AuroraServiceImpl.class);
	
	private static final int outPort = 9000;   //暴露给外部的端口，用于长连接
	private static final int innerPort = 9001; //暴露给内部端口，用于接收其它系统传递过来的pushe event
	private LogAsynWriter logAsynWriter;

	private List<MessageEventListener> messageEventListeners = new ArrayList<MessageEventListener>();
	private List<ChannelEventListener> channelEventListeners = new ArrayList<ChannelEventListener>();
	
	/**
	 * 存放Option列表
	 */
	private Map<String, Object> options = new HashMap<String, Object>();
	private LinkedHashMap<String, ChannelHandler> handlers = new LinkedHashMap<String, ChannelHandler>();
	
	private AtomicBoolean isAlive = new AtomicBoolean(false);
	
	protected Executor bossExecutor;
	protected Executor workerExecutor;
	protected int workerCount;
	
	private ChannelManager channelManager = ChannelManager.getInstance();
	private EventDispacher eventDispacher;
	private AuroraAcceptor auroraAcceptor;
	
	private static ExecutorService getCachedExecutor(String name) {
		return Executors.newCachedThreadPool(new NamedThreadFactory(name));
	}
	
	public AuroraServiceImpl() {
		this(getCachedExecutor("AURORA-BOSS-PROCESSOR"), Runtime.getRuntime().availableProcessors() + 1);
	}

	public AuroraServiceImpl(Executor bossExecutor, int workerCount) {
		this( bossExecutor, getCachedExecutor("AURORA-WORKER-PROCESSOR"), workerCount);
	}

	public AuroraServiceImpl(Executor bossExecutor, Executor workerExecutor, int workerCount) {
		if (bossExecutor == null) {
			throw new IllegalArgumentException("bossExecutor can not be null.");
		} else if (workerExecutor == null) {
			throw new IllegalArgumentException("workerExecutor can not be null.");
		} else if (workerCount <= 0) {
			throw new IllegalArgumentException("workerCount required > 0.");
		} else if (workerExecutor instanceof ThreadPoolExecutor
				&& ((ThreadPoolExecutor) workerExecutor).getMaximumPoolSize() < workerCount) {
			throw new IllegalArgumentException("the maximum pool size of workerExecutor required >= workerCount.");
		}

		this.bossExecutor = bossExecutor;
		this.workerExecutor = workerExecutor;
		this.workerCount = workerCount;
	}
	
	public void start() {
		init();
		auroraAcceptor = new NettyAuroraAcceptor(bossExecutor, workerExecutor, workerCount, eventDispacher, channelManager, this);
		auroraAcceptor.setHandlers(handlers);
		auroraAcceptor.setOptions(options);
		auroraAcceptor.init(outPort, innerPort);
		isAlive.set(true);
	}

	public void shutdown() {
		isAlive.set(false);
		auroraAcceptor.shutDown();
	}

	/**
	 * @Title: init
	 * @Description: 初始化系统参数
	 * @author 简道
	 * @return void 返回类型
	 */
	protected void init() {
		handlers.put("encoder", new AuroraNettyEncoder());
		handlers.put("decoder", new AuroraNettyDecoder());
		
		options.put(AuroraOptions.TCP_NO_DELAY, true);
		options.put(AuroraOptions.KEEP_ALIVE, true);
		options.put(AuroraOptions.REUSE_ADDRESS, true);
		options.put(AuroraOptions.WRITE_IDLE_TIME, 60*30);
		options.put(AuroraOptions.READ_IDLE_TIME, 60*30);
		options.put(AuroraOptions.CONNECT_TIMEOUT, 30000);
		options.put(AuroraOptions.HANDSHAKE_TIMEOUT, 30000000);
		options.put(AuroraOptions.FLOW_LIMIT, 2000000);
		options.put(AuroraOptions.TIMEOUT_WHEN_FLOW_EXCEEDED, 0);
		options.put(AuroraOptions.MAX_THREAD_NUM_OF_DISPATCHER, 150);
		//
		initSystemListener();
		LOG.warn("start success");
	}

	/**
	 * @Title: initSystemListener
	 * @Description: 初始化系统监听器
	 * @author 简道
	 * @return void 返回类型
	 */
	protected void initSystemListener() {
		HandShakeMessageListener handShakeMessageListener = new HandShakeMessageListener();
		handShakeMessageListener.setLogAsynWriter(logAsynWriter);
		handShakeMessageListener.setEventDispacher(eventDispacher);
		messageEventListeners.add(handShakeMessageListener);
	}
	
	public boolean isAlive() {
		return false;
	}

	public List<ChannelEventListener> getChannelEventListeners() {
		return channelEventListeners;
	}

	public List<MessageEventListener> getMessageEventListeners() {
		return messageEventListeners;
	}
	
	public void addMessageEventListener(MessageEventListener messageEventListener){
		messageEventListeners.add(messageEventListener);
	}
	
	public void addChannelEventListener(ChannelEventListener channelEventListener){
		channelEventListeners.add(channelEventListener);
	}	

	public ChannelManager getChannelManager() {
		return channelManager;
	}
	
	public void setChannelManager(ChannelManager channelManager){
		this.channelManager = channelManager;
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

	public Executor getBossExecutor() {
		return bossExecutor;
	}

	public void setBossExecutor(Executor bossExecutor) {
		this.bossExecutor = bossExecutor;
	}

	public Executor getWorkerExecutor() {
		return workerExecutor;
	}

	public void setWorkerExecutor(Executor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	public int getWorkerCount() {
		return workerCount;
	}

	public void setWorkerCount(int workerCount) {
		this.workerCount = workerCount;
	}

	public EventDispacher getEventDispacher() {
		return eventDispacher;
	}

	public void setEventDispacher(EventDispacher eventDispacher) {
		this.eventDispacher = eventDispacher;
	}

	public AuroraAcceptor getAuroraAcceptor() {
		return auroraAcceptor;
	}

	public void setAuroraAcceptor(AuroraAcceptor auroraAcceptor) {
		this.auroraAcceptor = auroraAcceptor;
	}

	public void setMessageEventListeners(
			List<MessageEventListener> messageEventListeners) {
		this.messageEventListeners = messageEventListeners;
	}

	public void setChannelEventListeners(
			List<ChannelEventListener> channelEventListeners) {
		this.channelEventListeners = channelEventListeners;
	}

	public LogAsynWriter getLogAsynWriter() {
		return logAsynWriter;
	}

	public void setLogAsynWriter(LogAsynWriter logAsynWriter) {
		this.logAsynWriter = logAsynWriter;
	}
}

package org.aurora.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.taobao.top.xbox.util.NamedThreadFactory;

/**
 * @author hantong
 *
 * 2013-6-16 上午9:26:08 
 */
public class PushClientFactory {
	
	private int serverPort = 9001;
	private int connectTimeOut = 3000;
	private ClientBootstrap bootstrap;
	
	private List<AtomicInteger> sendSuccessNumList = new ArrayList<AtomicInteger>();
	private List<AtomicInteger> sendFailedNumList = new ArrayList<AtomicInteger>();
	
	/**
	 * 发送数据超时时间
	 */
	private int timeout = 200;
	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public List<AtomicInteger> getSendSuccessNumList() {
		return sendSuccessNumList;
	}

	public void setSendSuccessNumList(List<AtomicInteger> sendSuccessNumList) {
		this.sendSuccessNumList = sendSuccessNumList;
	}

	public List<AtomicInteger> getSendFailedNumList() {
		return sendFailedNumList;
	}

	public void setSendFailedNumList(List<AtomicInteger> sendFailedNumList) {
		this.sendFailedNumList = sendFailedNumList;
	}

	public void init() {
		ThreadFactory clientBossTF = new NamedThreadFactory("netty-client-boss");
		ThreadFactory clientWorkerTF = new NamedThreadFactory("netty-client-worker");
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(clientBossTF),
				Executors.newCachedThreadPool(clientWorkerTF)));
		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(System.getProperty("comet.netty.tcp.nodelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(System.getProperty("comet.netty.tcp.reuseaddress", "true")));
		
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){

			public ChannelPipeline getPipeline() throws Exception {
		        // Create a default pipeline implementation.
		        ChannelPipeline pipeline = Channels.pipeline();

		        pipeline.addLast("decoder", new PushClientDecoder());
		        pipeline.addLast("handler", new PushClientHandler());
		        return pipeline;
	        }
			
		});
	}



	public PushClient createClient(String ip){
		PushClient client = new PushClient(bootstrap, ip, serverPort,connectTimeOut,timeout);
		sendSuccessNumList.add(client.getSendSuccessNum());
		sendFailedNumList.add(client.getSendFailedNum());
		client.init();
		return client;
	}
}

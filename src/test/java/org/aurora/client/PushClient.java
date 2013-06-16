package org.aurora.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.aurora.netty.client.serializer.ClientJsonSerializer;
import org.aurora.netty.event.PushEvent;
import org.aurora.netty.serializer.Serializer;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * @author hantong
 *
 * 2013-6-16 上午9:25:13 
 */
public class PushClient {
	public static final Serializer SERIALIZER = new ClientJsonSerializer();

	String ip;
	int port;
	ClientBootstrap bootstrapl;
	int version = 1;
	Channel channel;
	
	int c_timeout = 5000;
	int timeout = 200;
	
	private AtomicInteger sendSuccessNum = new AtomicInteger();
	private AtomicInteger sendFailedNum = new AtomicInteger();
	
	public AtomicInteger getSendSuccessNum() {
		return sendSuccessNum;
	}

	public void setSendSuccessNum(AtomicInteger sendSuccessNum) {
		this.sendSuccessNum = sendSuccessNum;
	}

	public AtomicInteger getSendFailedNum() {
		return sendFailedNum;
	}

	public void setSendFailedNum(AtomicInteger sendFailedNum) {
		this.sendFailedNum = sendFailedNum;
	}

	private final ChannelFutureListener sendListener = new  ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) throws Exception {
			if(future.isSuccess()){
				sendSuccessNum.incrementAndGet();
			}else{
				sendFailedNum.incrementAndGet();
			}
		}
	};
	
	PushClient(ClientBootstrap bootstrap,String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
		this.bootstrapl = bootstrap;
	}
	
	public PushClient(ClientBootstrap bootstrap,String ip, int port,int timeou) {
		super();
		this.ip = ip;
		this.port = port;
		this.bootstrapl = bootstrap;
		if(timeout > 0){
			this.c_timeout = timeout;
		}
	}
	
	public PushClient(ClientBootstrap bootstrap,String ip, int port,int c_timeout,int timeout) {
		super();
		this.ip = ip;
		this.port = port;
		this.bootstrapl = bootstrap;
		if(c_timeout > 0){
			this.c_timeout = c_timeout;
		}
		if(timeout > 0){
			this.timeout = timeout;
		}
	}
	
	public void sendMsg(String userKey, String msg, Integer id) throws Exception{
		PushEvent pushEvent = new PushEvent();
		pushEvent.setUserKey(userKey+id);
		pushEvent.setMessageContent(msg);
		byte[] hrBytes = SERIALIZER.serialize(pushEvent);
		send(hrBytes);
	}
	
	public void send(byte[] msg) throws Exception {
		ChannelBuffer sendBuffer = ChannelBuffers.dynamicBuffer();
		sendBuffer.writeBytes(msg);
		//
		ChannelFuture channelFuture = channel.write(sendBuffer);
		channelFuture.addListener(sendListener);
	}
	
	public void init(){
		ChannelFuture future = bootstrapl.connect(new InetSocketAddress(ip,port));
		if(future.awaitUninterruptibly(c_timeout ,TimeUnit.MILLISECONDS)){
			channel = future.getChannel();
		}else{
			throw new RuntimeException("connect time out");
		}
	}

	public void destory() {
		if(channel != null){
			channel.close();
		}
	}

	public boolean isActive() {
		return channel.isConnected() && channel.isOpen();
	}
}

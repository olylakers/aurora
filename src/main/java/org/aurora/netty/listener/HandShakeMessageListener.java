package org.aurora.netty.listener;

import java.util.Date;

import org.aurora.channel.handshake.HandShakeAck;
import org.aurora.channel.handshake.HandShakeEnd;
import org.aurora.channel.handshake.HandShakeRequest;
import org.aurora.log.ConnectionLog;
import org.aurora.log.LogAsynWriter;
import org.aurora.netty.channel.AuroraChannel;
import org.aurora.netty.handler.EventDispacher;
import org.aurora.netty.util.ChannelUtil;
import org.aurora.netty.util.HandShakeUtil;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * 监听握手消息
 * @author hantong
 *
 * 2013-6-7 下午1:21:42 
 */
public class HandShakeMessageListener implements MessageEventListener {
	private EventDispacher eventDispacher;
	private LogAsynWriter logAsynWriter;
	
	public LogAsynWriter getLogAsynWriter() {
		return logAsynWriter;
	}

	public void setLogAsynWriter(LogAsynWriter logAsynWriter) {
		this.logAsynWriter = logAsynWriter;
	}

	public EventDispacher getEventDispacher() {
		return eventDispacher;
	}

	public void setEventDispacher(EventDispacher eventDispacher) {
		this.eventDispacher = eventDispacher;
	}

	public EventStatus messageReceived(ChannelHandlerContext context,
			AuroraChannel channel, Object messageEvent) {
		if(messageEvent instanceof HandShakeRequest){
			processHandShakeInit(context, channel, (HandShakeRequest)messageEvent);
		}else if(messageEvent instanceof HandShakeEnd){
			processHandShakeEnd(context, channel, (HandShakeEnd)messageEvent);
		}
		return EventStatus.Continue;
	}
	
	private void processHandShakeInit(ChannelHandlerContext context, AuroraChannel channel, HandShakeRequest handShakeRequest){
		HandShakeAck handShakeAck = new HandShakeAck(ChannelUtil.genEventKey(handShakeRequest.getAppkey(), handShakeRequest.getUser(), handShakeRequest.getId()));
		// start timer
		HandShakeUtil.resetHandshakeTimeout(channel);
		//
		channel.write(handShakeAck);		
	}
	
	private void processHandShakeEnd(ChannelHandlerContext context, AuroraChannel channel, HandShakeEnd handShakeEnd){
		HandShakeUtil.cancelHandshakeTimeout(channel);
		
		channel.getAuroraService().getChannelManager().addUser(handShakeEnd.getKey(), channel.getId());
		channel.setUserKey(handShakeEnd.getKey());
		logChannelConnect(channel);
	}
	
	private void logChannelConnect(AuroraChannel channel){
		ConnectionLog connectionLog = new ConnectionLog();
		connectionLog.setIp(channel.getRemoteAddress().toString());
		connectionLog.setUser(channel.getUserKey());
		connectionLog.setLogTime(new Date());
		logAsynWriter.debug(connectionLog);
	}

}

package org.aurora.netty.listener;

import java.util.Date;

import org.aurora.heartbeat.HeartBeat;
import org.aurora.log.HeartBeatLog;
import org.aurora.log.LogAsynWriter;
import org.aurora.netty.channel.AuroraChannel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * 监听heartbeat message
 * @author hantong
 *
 * 2013-6-13 下午7:04:52 
 */
public class HeartBeatMessageListener implements MessageEventListener {
   
	LogAsynWriter heartAsynWriter;
	
	public LogAsynWriter getHeartAsynWriter() {
		return heartAsynWriter;
	}

	public void setHeartAsynWriter(LogAsynWriter heartAsynWriter) {
		this.heartAsynWriter = heartAsynWriter;
	}

	public EventStatus messageReceived(ChannelHandlerContext context,
			AuroraChannel channel, Object messageEvent) {
		if(messageEvent instanceof HeartBeat){
			HeartBeatLog heartBeatLog = new HeartBeatLog();
			heartBeatLog.setUserKey(channel.getUserKey());
			heartBeatLog.setLogTime(new Date());
			heartAsynWriter.debug(heartBeatLog);
		}
		
		return EventStatus.Continue;
	}

}

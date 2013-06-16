package org.aurora.netty.listener;

import org.aurora.log.ExceptionLog;
import org.aurora.log.LogAsynWriter;
import org.aurora.netty.channel.AuroraChannel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;

/**
 * @author hantong
 *
 * 2013-6-9 下午3:19:25 
 */
public class LogExceptionEventListener implements ExceptionEventListener {

	private LogAsynWriter logAsynWriter;
	public EventStatus exceptionCaught(ChannelHandlerContext ctx,
			AuroraChannel channel, ExceptionEvent e) {
		ExceptionLog exceptionLog = new ExceptionLog();
		exceptionLog.setChannel(channel);
		exceptionLog.setExceptionMsg(e.getCause().toString());
		exceptionLog.setType(ExceptionLog.MESSAGE_EVENT_EXCEPTION);
		logAsynWriter.debug(exceptionLog);
		return EventStatus.Continue;
	}
	
	public LogAsynWriter getLogAsynWriter() {
		return logAsynWriter;
	}
	public void setLogAsynWriter(LogAsynWriter logAsynWriter) {
		this.logAsynWriter = logAsynWriter;
	}

}

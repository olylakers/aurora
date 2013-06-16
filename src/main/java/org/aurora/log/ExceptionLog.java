package org.aurora.log;

import org.aurora.netty.channel.AuroraChannel;

public class ExceptionLog extends EventLog {

	private static final long serialVersionUID = -2492773945010202098L;
	
	public static final String CHANNEL_EVENT_EXCEPTION = "#channel_event_exception#";
	public static final String MESSAGE_EVENT_EXCEPTION = "#message_event_exception#";
	
	private AuroraChannel channel;
	private String exceptionMsg;
	private String type;
	public AuroraChannel getChannel() {
		return channel;
	}
	public void setChannel(AuroraChannel channel) {
		this.channel = channel;
	}
	public String getExceptionMsg() {
		return exceptionMsg;
	}
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toString(){
		StringBuffer errLog = new StringBuffer();
		errLog
		.append(channel).append(",")
		.append(type).append(",")
		.append(exceptionMsg);
		return errLog.toString();
	}
}

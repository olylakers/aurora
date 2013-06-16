package org.aurora.netty.event;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

/**
 * @author hantong
 *
 * 2013-6-7 上午10:25:41 
 */
public abstract class AbstractAuroraEvent implements AuroraEvent {
	protected byte eventType;
	protected Object messageContent;
	
	/**
	 * 消息对应channel
	 */
	protected Channel channel;
	
	public byte getEventType() {
		return eventType;
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}

	public Object getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(Object messageContent) {
		this.messageContent = messageContent;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public abstract ChannelFuture onEvent();
}

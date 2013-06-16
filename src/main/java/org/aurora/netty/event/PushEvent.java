package org.aurora.netty.event;

import org.jboss.netty.channel.ChannelFuture;

/**
 * 向channel push message 的事件
 * @author hantong
 *
 * 2013-6-6 下午8:58:31 
 */
public class PushEvent extends AbstractAuroraEvent{
	
	private String userKey;

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	@Override
	public ChannelFuture onEvent() {
		ChannelFuture channelFuture = channel.write(messageContent);
		return channelFuture;
	}
}

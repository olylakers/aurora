package org.aurora.netty.event;

import org.jboss.netty.channel.ChannelFuture;

/**
 * 向push消息来源方反馈推送结果
 * @author hantong
 *
 * 2013-6-6 下午9:05:59 
 */
public class ResponseEvent extends AbstractAuroraEvent {

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

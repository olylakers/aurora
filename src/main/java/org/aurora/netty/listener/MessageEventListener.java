package org.aurora.netty.listener;

import java.util.EventListener;

import org.aurora.netty.channel.AuroraChannel;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * 监听netty 消息事件
 * @author hantong
 *
 * 2013-6-3 下午11:31:45 
 */
public interface MessageEventListener extends EventListener {
	
	/**
	 * 当接受到新消息时触发
	 * @param context
	 * @param channel
	 * @param messageEvent
	 * @return
	 */
	public EventStatus messageReceived(final ChannelHandlerContext context, final AuroraChannel channel, final Object messageEvent);
}

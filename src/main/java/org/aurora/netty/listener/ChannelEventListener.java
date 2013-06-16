package org.aurora.netty.listener;

import java.util.EventListener;

import org.aurora.netty.channel.AuroraChannel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

/**
 * 监听netty channel事件，如：
 * 1.channel connected
 * 2.channel closed
 * 。。。。
 * @author hantong
 *
 * 2013-5-25 下午4:04:24 
 */
public interface ChannelEventListener extends EventListener {
	
	/**
	 * 有新连接创建时触发
	 * @param context
	 * @param channel
	 * @param channelStateEvent
	 * @return
	 */
	public EventStatus channelConnected(final ChannelHandlerContext context, final AuroraChannel channel, final ChannelStateEvent channelStateEvent);
	
	/**
	 * 连接关闭时触发
	 * @param context
	 * @param channel
	 * @param channelStateEvent
	 * @return
	 */
	public EventStatus channelClosed(final ChannelHandlerContext context, final AuroraChannel channel, final ChannelStateEvent channelStateEvent);
}

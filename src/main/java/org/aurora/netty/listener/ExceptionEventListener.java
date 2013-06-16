package org.aurora.netty.listener;

import java.util.EventListener;

import org.aurora.netty.channel.AuroraChannel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;

/**
 * @author hantong
 *
 * 2013-6-9 下午3:17:35 
 */
public interface ExceptionEventListener extends EventListener {

	EventStatus exceptionCaught(ChannelHandlerContext ctx, AuroraChannel channel, ExceptionEvent e);

}

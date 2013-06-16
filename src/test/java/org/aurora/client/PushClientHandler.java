package org.aurora.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * @author hantong
 *
 * 2013-6-16 上午9:47:22 
 */
public class PushClientHandler  extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
	}
	  
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) 
	{
	    e.getChannel().close();
	}
}

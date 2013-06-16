package org.aurora.netty.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aurora.netty.channel.AuroraChannel;
import org.aurora.netty.channel.ChannelManager;
import org.aurora.netty.service.AuroraService;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 上行事件分发处理器，即分发接受的
 * @author hantong
 *
 * 2013-5-17 下午5:02:00 
 */
public class DispachUpEventHandler extends SimpleChannelUpstreamHandler {
	private Log log = LogFactory.getLog(DispachUpEventHandler.class);
	private static final Log EXCEPTION_LOG = LogFactory.getLog("org.aurora.exception.log");
	
	private EventDispacher eventDispacher;
	private ChannelManager channelManager;
	private AuroraService auroraService;
	
	public DispachUpEventHandler(EventDispacher eventDispacher, ChannelManager channelManager, AuroraService auroraService){
		this.eventDispacher = eventDispacher;
		this.auroraService = auroraService;
		this.channelManager = channelManager;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		AuroraChannel auroraChannel = channelManager.getChannel(e.getChannel().getId());
		eventDispacher.dispacheMessageEvent(ctx, auroraChannel, e.getMessage());
		super.messageReceived(ctx, e);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		AuroraChannel auroraChannel = new AuroraChannel(e.getChannel(), auroraService);
		auroraService.getChannelManager().addChannel(auroraChannel.getId(), auroraChannel);
		eventDispacher.dispacheChannelEvent(ctx, auroraChannel, e);
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		closeChannel(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		closeChannel(ctx, e);
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		EXCEPTION_LOG.error("channel has been close by the exception: "+e.getCause().toString());
		e.getChannel().close();
	}
	
	private void closeChannel(ChannelHandlerContext context, ChannelStateEvent e) throws Exception{
		AuroraChannel auroraChannel = auroraService.getChannelManager().getChannel(e.getChannel().getId());
		auroraService.getChannelManager().closeChannel(auroraChannel.getId(), auroraChannel.getUserKey());
		eventDispacher.dispacheChannelEvent(context, auroraChannel, e);
		EXCEPTION_LOG.warn("channel has been close: "+auroraChannel!=null?auroraChannel.getUserKey():e.getChannel().getId());
		super.channelDisconnected(context, e);
	}

	public ChannelManager getChannelManager() {
		return channelManager;
	}

	public void setChannelManager(ChannelManager channelManager) {
		this.channelManager = channelManager;
	}

	public EventDispacher getEventDispacher() {
		return eventDispacher;
	}

	public void setEventDispacher(EventDispacher eventDispacher) {
		this.eventDispacher = eventDispacher;
	}

	public AuroraService getAuroraService() {
		return auroraService;
	}

	public void setAuroraService(AuroraService auroraService) {
		this.auroraService = auroraService;
	}
}

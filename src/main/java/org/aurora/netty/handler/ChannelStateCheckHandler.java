package org.aurora.netty.handler;

import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.channel.AuroraChannel;
import org.aurora.netty.channel.ChannelManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

/**
 * channel 空闲检测handler
 * @author hantong
 *
 * 2013-6-8 下午10:38:35 
 */
public class ChannelStateCheckHandler extends IdleStateAwareChannelHandler {
	private static Log log = LogFactory.getLog(ChannelStateCheckHandler.class);
	
	ChannelManager channelManager;
	public ChannelStateCheckHandler(ChannelManager channelManager) {
		this.channelManager = channelManager;
	}

	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
		if (e.getState() == IdleState.WRITER_IDLE) {
			AuroraChannel auroraChannel = channelManager.getChannel(e.getChannel().getId());
			if (auroraChannel != null) {
				auroraChannel.write(HeartBeat.getSingleton());
			} else {
				log.warn("writer idle on channel, but hsfChannel is not managed.");
			}
		} else if (e.getState() == IdleState.READER_IDLE) {
			log.error("channel:{} is time out.");
			handleUpstream(ctx, new DefaultExceptionEvent(e.getChannel(), new SocketTimeoutException(
					"force to close channel(" + ctx.getChannel().getRemoteAddress() + "), reason: time out.")));

			e.getChannel().close();
			//
			AuroraChannel auroraChannel = channelManager.getChannel(e.getChannel().getId());
			if (auroraChannel != null) {
				channelManager.removeChannel(auroraChannel.getId());
			}
		}
		super.channelIdle(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (e.getMessage() == HeartBeat.getSingleton()) {
			AuroraChannel auroraChannel = channelManager.getChannel(e.getChannel().getId());
			if (auroraChannel != null) {
				long now = System.currentTimeMillis();
				auroraChannel.getHeartBeatsStatInfo().getReceivedNum().incrementAndGet();
				auroraChannel.getHeartBeatsStatInfo().setLastestReceived(now);
			}
		}
		super.messageReceived(ctx, e);
	}
}

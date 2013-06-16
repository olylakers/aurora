package org.aurora.netty.handler;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aurora.netty.channel.AuroraChannel;
import org.aurora.netty.channel.ChannelManager;
import org.aurora.netty.event.PushEvent;
import org.aurora.netty.event.ResponseEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 接受其他系统发送过来的push消息
 * @author hantong
 *
 * 2013-5-17 下午5:02:00 
 */
public class DispachPushEventHandler extends SimpleChannelUpstreamHandler {
	private Log log = LogFactory.getLog(DispachPushEventHandler.class);
	private static final Log messageLog = LogFactory.getLog("org.aurora.message.log");
	private static final Log EXCEPTION_LOG = LogFactory.getLog("org.aurora.exception.log");

	private EventDispacher eventDispacher;
	
	public DispachPushEventHandler(EventDispacher eventDispacher){
		this.eventDispacher = eventDispacher;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(e.getMessage() instanceof PushEvent){
			PushEvent event = (PushEvent)e.getMessage();
			AuroraChannel auroraChannel = ChannelManager.getInstance().getChannel(event.getUserKey());
			event.setChannel(auroraChannel);
			if(auroraChannel !=null){
				auroraChannel.getMessageStatInfo().getReceivedNum().incrementAndGet();
				eventDispacher.dispacheMessageEvent(ctx, auroraChannel, event);
			}else{
				ResponseEvent responseEvent = new ResponseEvent();
				responseEvent.setUserKey(event.getUserKey());
				responseEvent.setChannel(e.getChannel());
				responseEvent.setMessageContent("can't find channel");
				messageLog.error(event.getUserKey()+":can't find channel for this user");
				e.getChannel().write("can't find channel for user: "+event.getUserKey());
			}
		}
		super.messageReceived(ctx, e);
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		EXCEPTION_LOG.error("receive server push channel occured exception, e:"+e.getCause().toString());
		e.getChannel().close();
	}
}

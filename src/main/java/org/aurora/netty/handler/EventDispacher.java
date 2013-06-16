package org.aurora.netty.handler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aurora.log.ExceptionLog;
import org.aurora.log.LogAsynWriter;
import org.aurora.netty.channel.AuroraChannel;
import org.aurora.netty.event.PushEvent;
import org.aurora.netty.event.ResponseEvent;
import org.aurora.netty.listener.ChannelEventListener;
import org.aurora.netty.listener.EventStatus;
import org.aurora.netty.listener.MessageEventListener;
import org.aurora.netty.protocol.ProtocolUtils;
import org.aurora.netty.protocol.RequestWrapper;
import org.aurora.netty.util.AbortPolicyHandler;
import org.aurora.netty.util.AuroraThreadFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;


/**
 * 事件分发
 * @author hantong
 *
 * 2013-5-17 下午5:13:38 
 */
public class EventDispacher {
	private LogAsynWriter logAsynWriter;
	private LogAsynWriter channelLogWriter;
	private Executor messageExecutor;
	private Executor channelExecutor;
	private Executor exceptionExecutor;
	private int maximumPoolSize = 150;
	private int queueCapacity = 1000000;
	
	public EventDispacher(){		
		messageExecutor = getExecutor();
		exceptionExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1, new AuroraThreadFactory("exceptionExecutor thread", true));
		channelExecutor = Executors.newCachedThreadPool(new AuroraThreadFactory("channelExecutor thread", true));
	}
	
	private Executor getExecutor() {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maximumPoolSize, maximumPoolSize, 60L,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity), new AuroraThreadFactory(
						"EventDispatcherProcessor", true), new AbortPolicyHandler("EventDispatcherProcessor"));
		return threadPoolExecutor;
	}

	public Executor getChannelExecutor() {
		return channelExecutor;
	}

	public void setChannelExecutor(Executor channelExecutor) {
		this.channelExecutor = channelExecutor;
	}

	public Executor getExceptionExecutor() {
		return exceptionExecutor;
	}

	public void setExceptionExecutor(Executor exceptionExecutor) {
		this.exceptionExecutor = exceptionExecutor;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public void setMessageExecutor(Executor messageExecutor) {
		this.messageExecutor = messageExecutor;
	}

	public Executor getMessageExecutor() {
		return messageExecutor;
	}
	
	public LogAsynWriter getLogAsynWriter() {
		return logAsynWriter;
	}

	public void setLogAsynWriter(LogAsynWriter logAsynWriter) {
		this.logAsynWriter = logAsynWriter;
	}

	public LogAsynWriter getChannelLogWriter() {
		return channelLogWriter;
	}

	public void setChannelLogWriter(LogAsynWriter channelLogWriter) {
		this.channelLogWriter = channelLogWriter;
	}

	public void dispacheChannelEvent(final ChannelHandlerContext context, final AuroraChannel channel, final ChannelStateEvent channelStateEvent){
		//只处理通道连接事件
		if(channelStateEvent.getState().equals(ChannelState.CONNECTED)){
			final boolean isConnected = channelStateEvent.getValue() != null;
			
			channelExecutor.execute(new Runnable() {
				public void run() {
					try{
						for (ChannelEventListener listener : channel.getAuroraService().getChannelEventListeners()) {
							EventStatus eventStatus = EventStatus.Continue;
							if(isConnected){
								eventStatus = listener.channelConnected(context, channel, channelStateEvent);
							}else{
								eventStatus =listener.channelConnected(context, channel, channelStateEvent);
							}
							
							if(EventStatus.Break.equals(eventStatus)){
								break;
							}
						}	
					}catch(Exception e){
						ExceptionLog exceptionLog = new ExceptionLog();
						exceptionLog.setChannel(channel);
						exceptionLog.setType(ExceptionLog.CHANNEL_EVENT_EXCEPTION);
						exceptionLog.setExceptionMsg(e.getMessage());
						logAsynWriter.log(exceptionLog);//TODO:add 重写AuroraCHannel的toString方法
					}finally{
						
					}
				}
			});
		}
	}
	
	public void dispacheMessageEvent(final ChannelHandlerContext context, final AuroraChannel channel, final Object message){
		Executor threadPool = messageExecutor;
		try{
			threadPool.execute(new Runnable() {
				public void run() {
					try{
						if(message instanceof RequestWrapper){
							RequestWrapper requestWrapper = (RequestWrapper)message;
							final Object decodeObject = ProtocolUtils.DEFAULT_SERIALIZER.deserialize(requestWrapper.getRequestBody());
							
							//拦截push消息
							if(decodeObject instanceof PushEvent){
								dispachePushEvent(context, channel, (PushEvent)decodeObject);
							}else{
								for (MessageEventListener messageEventListener : channel.getAuroraService().getMessageEventListeners()) {
									if(messageEventListener.messageReceived(context, channel, decodeObject).equals(EventStatus.Break)){
										break;
									}
								}	
							}	
						}else if(message instanceof PushEvent){
							dispachePushEvent(context, channel, (PushEvent)message);
						}else{
							for (MessageEventListener messageEventListener : channel.getAuroraService().getMessageEventListeners()) {
								if(messageEventListener.messageReceived(context, channel, message).equals(EventStatus.Break)){
									break;
								}
							}
						}
					}catch(Exception e){
						ExceptionLog exceptionLog = new ExceptionLog();
						exceptionLog.setChannel(channel);
						exceptionLog.setType(ExceptionLog.MESSAGE_EVENT_EXCEPTION);
						exceptionLog.setExceptionMsg(e.getMessage());
						logAsynWriter.log(exceptionLog);//TODO:add 重写AuroraCHannel的toString方法
					}
				}
			});
		}catch(RejectedExecutionException e){
			ExceptionLog exceptionLog = new ExceptionLog();
			exceptionLog.setChannel(channel);
			exceptionLog.setType(ExceptionLog.MESSAGE_EVENT_EXCEPTION);
			exceptionLog.setExceptionMsg(e.getMessage());
			logAsynWriter.log(exceptionLog);//TODO:add 重写AuroraCHannel的toString方法	
		}
	}
	
	/**
	 * 处理push消息
	 * @param context
	 * @param channel
	 * @param pushEvent
	 */
	public void dispachePushEvent(final ChannelHandlerContext context, final AuroraChannel channel, final PushEvent pushEvent){
		pushEvent.onEvent();
	}
	
	/**
	 * 反馈push结果
	 * @param context
	 * @param channel
	 * @param responseEvent
	 */
	public void dispacheResponseEvent(final ChannelHandlerContext context, final AuroraChannel channel, final ResponseEvent responseEvent){
		
	}
}

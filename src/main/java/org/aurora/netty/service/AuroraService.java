package org.aurora.netty.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aurora.netty.channel.ChannelManager;
import org.aurora.netty.listener.ChannelEventListener;
import org.aurora.netty.listener.MessageEventListener;
import org.jboss.netty.channel.ChannelHandler;

/**
 * 提供长连接服务的service
 * @author hantong
 *
 * 2013-5-9 下午9:26:51 
 */
public interface AuroraService {
	
	/**
	 * 是否处于活动状态
	 * @return
	 */
	boolean isAlive();
	
	/**
	 * 停止服务
	 */
	void shutdown();
	
	/**
	 * 启动服务
	 */
	void start();
	
	/**
	 * 获取通道事件监听器
	 * @return
	 */
	List<ChannelEventListener> getChannelEventListeners();
	
	List<MessageEventListener> getMessageEventListeners();
	
	void addMessageEventListener(MessageEventListener messageEventListener);
	
	void addChannelEventListener(ChannelEventListener channelEventListener);
	
	ChannelManager getChannelManager();
	
	Map<String, Object> getOptions();

	Object getOption(String opName);
	void setOptions(Map<String, Object> options);

	void setOption(String opName, Object opValue);

	LinkedHashMap<String, ChannelHandler> getHandlers();
	void setHandlers(LinkedHashMap<String, ChannelHandler> handlers);
	
}

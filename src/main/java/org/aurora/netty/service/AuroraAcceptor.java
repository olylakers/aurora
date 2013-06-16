/**
 * 
 */
package org.aurora.netty.service;

import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;

/**
 * server端网络操作接口
 * @author hantong
 *
 * 2013-5-6 下午1:37:37 
 */
public interface AuroraAcceptor{
	
	/**
	 * 监听指定端口
	 * @param port
	 * @return
	 */
	public Channel bind(int port);
	
	/**
	 * 监听指定地址
	 * @param socketAddress
	 * @return
	 */
	public Channel bind(SocketAddress socketAddress);	
	
	void init(int outPort, int innerPort);
	
	Map<String, Object> getOptions();

	Object getOption(String opName);
	void setOptions(Map<String, Object> options);

	void setOption(String opName, Object opValue);

	LinkedHashMap<String, ChannelHandler> getHandlers();
	void setHandlers(LinkedHashMap<String, ChannelHandler> handlers);
	
	void shutDown();
}

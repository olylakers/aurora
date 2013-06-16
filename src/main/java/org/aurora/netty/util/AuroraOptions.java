package org.aurora.netty.util;

/**
 * aurora netty 相关的配置选项
 * @author hantong
 *
 * 2013-6-9 上午10:49:59 
 */
public class AuroraOptions {
	/**
	 * tcpNoDelay, default is true.
	 */
	public static final String TCP_NO_DELAY = "tcpNoDelay";
	/**
	 * KeepAlive, default is true.
	 */
	public static final String KEEP_ALIVE = "keepAlive";
	/**
	 * reuseAddress，default is true.
	 */
	public static final String REUSE_ADDRESS = "reuseAddress";
	/**
	 * 写空闲时间(秒), default is 10.
	 */
	public static final String WRITE_IDLE_TIME = "writeIdleTime";
	/**
	 * 读空闲时间(秒), default is 60.
	 */
	public static final String READ_IDLE_TIME = "readIdleTime";
	/**
	 * 握手超时时间(毫秒), default is 30000
	 */
	public static final String HANDSHAKE_TIMEOUT = "handshakeTimeout";
	/**
	 * 流量限额，default is 2000000
	 */
	public static final String FLOW_LIMIT = "flowLimit";
	/**
	 * 申请流量超时时间(毫秒), default is 0
	 */
	public static final String TIMEOUT_WHEN_FLOW_EXCEEDED = "timeoutWhenFlowExceeded";
	/**
	 * 分发器的最大线程数，default is 150
	 */
	public static final String MAX_THREAD_NUM_OF_DISPATCHER = "maxThreadNumOfDispatcher";
	/**
	 * 重连频率(毫秒), default is.10000
	 */
	public static final String RECONNECT_INTERVAL = "reconnectInterval";
	/**
	 * 建立连接超时时间(毫秒), default is.30000
	 */
	public static final String CONNECT_TIMEOUT = "connectTimeout";
}

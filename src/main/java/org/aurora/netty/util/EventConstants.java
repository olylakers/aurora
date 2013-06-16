package org.aurora.netty.util;

/**
 * @author hantong
 *
 * 2013-6-7 上午10:26:56 
 */
public class EventConstants {
	
	//Event type  常量
	public static final byte PUSH_EVENT = 1;
	public static final byte RESPONSE_EVENT = 2;
	
	//标识消息类型
	public static final byte HAND_SHAKE = 0;   //握手消息
	public static final byte NORMAL_BIZ= 1;    //普通业务消息
	public static final byte PUSH_BIZ= 2;    //Push业务消息
	public static final byte RESPONSE_BIZ= 3;    //Push业务消息
	
	
	//标示处于握手阶段
	public static final byte MARK_INIT = 0;   //发起握手消息
	public static final byte MARK_ACK = 1;   //握手相应消息
	public static final byte MARK_END = 2;   //握手完成消息

}

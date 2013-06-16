package org.aurora.channel.handshake;

import java.io.Serializable;

/**
 * 握手请求响应消息，用于server接受到client握手请求后，返回响应请求
 * @author hantong
 *
 * 2013-6-2 下午7:09:31 
 */
public class HandShakeAck implements Serializable {

	private static final long serialVersionUID = 637688924690717395L;
	private String key;
	
	public HandShakeAck(){
	}
	
	public HandShakeAck(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public  static HandShakeAck getHandShakeAck(byte[] body){
		return null;//TODO
	}
	
	public String toString(){
		return key;
	}
}

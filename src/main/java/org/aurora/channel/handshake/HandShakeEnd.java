package org.aurora.channel.handshake;

import java.io.Serializable;

/**
 * 握手请求完成消息。client和server3次交互来完成握手过程：
 * 1.client  发送HandShakeRequest到server
 * 2.server接受到HandShakeRequest后，回复HandShakeAck给client
 * 3.client接受到HandShakeAck后，向server发送HandShakeEnd
 * 4.Server接受到HandShakeEnd后，client和Server连接正式建立
 * @author hantong
 *
 * 2013-6-2 下午7:11:02 
 */
public class HandShakeEnd implements Serializable {
	private static final long serialVersionUID = 3196107604969327086L;
	private String key;
	
	public HandShakeEnd(){
	}
	
	public HandShakeEnd(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}

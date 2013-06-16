package org.aurora.heartbeat;

import java.io.Serializable;


/**
 * 心跳消息
 * @author hantong
 *
 * 2013-6-5 下午9:03:52
 */
public class HeartBeat implements Serializable {
	private static final long serialVersionUID = 4072041522709455392L;
	public static final byte[] BYTES = new byte[0];

	private static HeartBeat instance = new HeartBeat();

	public static HeartBeat getSingleton() {
		return instance;
	}

	private HeartBeat() {
	}
	
	public String toString(){
		return "heartbeat: "+System.currentTimeMillis();
	}
}

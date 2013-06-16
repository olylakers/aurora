package org.aurora.channel.handshake;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * 握手请求消息，phone  client通过握手请求消息传递用户认证信息
 * appkey，user，id等
 * @author hantong
 *
 * 2013-6-2 下午7:02:20 
 */
public class HandShakeRequest implements Serializable {
	
	private static final long serialVersionUID = -5392514150199327282L;

	/**
	 * 分配给每个用户appkey
	 */
	private String appkey;
	
	private String user;
	
	private String id;
	
	public HandShakeRequest() {
	}
	
	public HandShakeRequest(String appkey, String user, String id) {
		this.appkey = appkey;
		this.user = user;
		this.id = id;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static byte[] buildHandShakeRequestBody(String appkey, String user, String id){
		return null; //TODO:
	}
	
	public static HandShakeRequest getHandShakeRequest(byte[] content){
		return null;
	}
	
	public static void main(String[] args){
		HandShakeRequest handShakeRequest = new HandShakeRequest("ddadafasfafaf4141414$%", "31313131fsff", "3131313fffa");
		String handShakeRequestString = JSON.toJSONString(handShakeRequest);
		System.out.println(handShakeRequestString);
		HandShakeRequest handShakeRequest2 = (HandShakeRequest) JSON.parse(handShakeRequestString.getBytes());
		System.out.println(handShakeRequest2.getAppkey());
	}
}

package org.aurora.log;

/**
 * 客户端每次连接上来都纪录一下，方便排查问题
 * @author hantong
 *
 * 2013-6-5 上午10:07:26 
 */
public class ConnectionLog extends EventLog {

	private static final long serialVersionUID = -7134356061838169127L;
	private static final String SPLIT = "%!";
	
	
	public ConnectionLog() {
		this.setClassName("1");
	}
	/*
	 *连接指定的appkey 
	 */
	private String appkey;
	/*
	 * 连接时如果指定了user，则记录下来
	 */
	private String user;
	/*
	 * 记录连接发起的ip
	 */
	private String ip;
	/*
	 * 接收连接的主机名
	 */
	private String localhost;
	/*
	 * 记录连接处理的结果，成功还是失败，用于查找问题 
	 */
	private int code;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getLocalhost() {
		return localhost;
	}
	public void setLocalhost(String localhost) {
		this.localhost = localhost;
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
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClassName())
		.append(SPLIT)
		.append(user)
		.append(ip)
		.append(SPLIT)
		.append(SPLIT)
		.append(this.getLogTime());
		return sb.toString();
	}
}

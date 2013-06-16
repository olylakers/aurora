package org.aurora.log;

/**
 * @author hantong
 *
 * 2013-6-13 下午7:06:20 
 */
public class HeartBeatLog extends EventLog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5401130783376511784L;
	public String userKey;

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	
	public String toString(){
		return userKey+"  "+logTime;
	}
}

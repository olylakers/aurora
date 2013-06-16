package org.aurora.netty.util;

import org.apache.commons.lang.StringUtils;


/**
 * @author hantong
 *
 * 2013-5-29 下午9:46:49 
 */
public class ChannelUtil {
	
	/**
	 * 生成channel对应的key
	 * @param appkey
	 * @param user
	 * @param id
	 * @return
	 */
	public static String genEventKey(String appkey,String user,String id){
		StringBuilder key = new StringBuilder(appkey);
		if(StringUtils.isNotBlank(user)){
			key.append(":").append(user);
		}else{
			key.append(":").append(ChannelConstants.DEFAULT_USER);
		}
		if(StringUtils.isNotBlank(id)){
			key.append(":").append(id);
		}else{
			key.append(":").append(ChannelConstants.DEFAULT_ID);
		}
		return key.toString();
	}
}

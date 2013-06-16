package org.aurora.netty.listener;

/**
 * event 处理结果，是继续处理还是中止处理
 * @author hantong
 *
 * 2013-6-4 下午9:05:49 
 */
public enum EventStatus {
	/**
	 * 继续处理
	 */
	Continue,
	
	/**
	 * 中止处理
	 */
	Break;
}

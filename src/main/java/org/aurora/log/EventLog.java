package org.aurora.log;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author hantong
 *
 * 2013-6-4 下午10:26:28 
 */
public class EventLog implements Serializable {

	private static final long serialVersionUID = 3842763518982248754L;

	protected String className;
	protected Date logTime;
	protected Long beginTime;
	
	public Long getBeginTime() {
		return beginTime;
	}

	/**
	 * 整个请求事务占用的时间，包括平台消耗时间和服务消耗时间
	 */
	private long transactionConsumeTime;
	
	/**
	 * 时间打点队列
	 */
	protected BlockingQueue<Object[]> timeStampQueue = new LinkedBlockingQueue<Object[]>();

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}
	
	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public BlockingQueue<Object[]> getTimeStampQueue() {
		return timeStampQueue;
	}
	
	public long getTransactionConsumeTime() {
		return transactionConsumeTime;
	}


	public void setTransactionConsumeTime(long transactionConsumeTime) {
		this.transactionConsumeTime = transactionConsumeTime;
	}

	public void track(String pipeName,Long timestamp)
	{
		timeStampQueue.add(new Object[]{pipeName,timestamp});
	}
	
	public void track()
	{		
		this.beginTime = System.currentTimeMillis();
	}
}

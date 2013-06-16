package org.aurora.log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.LogFactory;

import com.taobao.top.xbox.asynlog.AsynWriter;

/**
 * 异步输出日志
 * @author hantong
 *
 * 2013-6-4 下午10:51:51 
 */
public class LogAsynWriter {
	private AsynWriter<EventLog> asynLogWriter;
	private String logName;
	private Lock lock = new ReentrantLock();
	public void setLogName(String logName) {
		this.logName = logName;
	}
	public void init()throws Exception{
		asynLogWriter = new AsynWriter<EventLog>(LogFactory.getLog(logName),null);
		asynLogWriter.init();
	}
	public void log(EventLog log){
		if(log == null){
			return ;
		}
		if(asynLogWriter == null){
			System.out.println("asynLogWriter is null");
			System.out.println(log);
		}else{
			asynLogWriter.write(log);
		}
	}
	/**
	 * debug输出，此方法用于临时打开（由于初始化一个AsynWriter要启动很多个线程，如果不用的话会占用资源，所以做成Lazy初始化的方式）
	 * @param log
	 */
	public void debug(EventLog log){
		if(asynLogWriter == null){
			if(lock.tryLock()){
				try{
					if(asynLogWriter == null){
						asynLogWriter = new AsynWriter<EventLog>(LogFactory.getLog(logName),null);
						asynLogWriter.init();
					}
				}finally{
					lock.unlock();
				}
			}
		}
		asynLogWriter.write(log);
	}

}

package org.aurora.netty.server;

import org.aurora.log.LogAsynWriter;
import org.aurora.netty.listener.HeartBeatMessageListener;
import org.aurora.netty.service.AuroraServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hantong
 *
 * 2013-6-13 下午4:49:12 
 */
public class Server {
	public static void main(String[] args){
		String[] paths = {"application-context.xml"};
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(paths);
		
		
		AuroraServiceImpl auroraServiceImpl = (AuroraServiceImpl)context.getBean("auroraService");
		LogAsynWriter logAsynWriter = (LogAsynWriter)context.getBean("logAsynWriter");
		LogAsynWriter heartbeatLog = (LogAsynWriter)context.getBean("heartbeatLog");
		HeartBeatMessageListener heartBeatMessageListener = new HeartBeatMessageListener();
		heartBeatMessageListener.setHeartAsynWriter(heartbeatLog);
		auroraServiceImpl.addMessageEventListener(heartBeatMessageListener);
		auroraServiceImpl.setLogAsynWriter(logAsynWriter);
		auroraServiceImpl.start();
		System.out.println("server is start");
	}
}

package org.aurora.server;

import org.aurora.log.LogAsynWriter;
import org.aurora.netty.service.AuroraServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hantong
 *
 * 2013-6-10 下午6:43:12 
 */
public class Server {
	
	public static void main(String[] args){
		String[] paths = {"application-context.xml"};
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(paths);
		
		
		AuroraServiceImpl auroraServiceImpl = (AuroraServiceImpl)context.getBean("auroraService");
		LogAsynWriter logAsynWriter = (LogAsynWriter)context.getBean("logAsynWriter");
		auroraServiceImpl.setLogAsynWriter(logAsynWriter);
		auroraServiceImpl.start();
	}
}

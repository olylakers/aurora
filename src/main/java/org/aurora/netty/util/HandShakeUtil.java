package org.aurora.netty.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aurora.channel.handshake.HandShakeAck;
import org.aurora.channel.handshake.HandShakeRequest;
import org.aurora.channel.handshake.HandShakeEnd;
import org.aurora.netty.channel.AuroraChannel;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

/**
 * 握手请求辅助类
 * @author hantong
 *
 * 2013-6-2 下午6:50:36 
 */
public class HandShakeUtil {
	private static final Log LOG = LogFactory.getLog(HandShakeUtil.class);
	
	private static HashedWheelTimer timer = new HashedWheelTimer();

	static{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				timer.stop();
			}
		}));
	}
	
	public static void resetHandshakeTimeout(final AuroraChannel channel) {
		Timeout timeout = channel.getHandShakeTimeout();
		if (timeout != null) {
			timeout.cancel();
		}

		//
		final Integer delay = ConfigUtil.parseInt(channel.getAuroraService().getOption(ChannelOptions.HANDSHAKE_TIMEOUT), null);
		if (delay == null) {
			return;
		}
		TimerTask task = new TimerTask() {

			public void run(Timeout timeout) throws Exception {
				LOG.error("channel "+channel+" handshake timeout "+delay+", close it");
				channel.close();
			}
		};
		timeout = timer.newTimeout(task, delay, TimeUnit.MILLISECONDS);
		channel.setHandShakeTimeout(timeout);
	}

	public static void cancelHandshakeTimeout(AuroraChannel channel) {
		// cancel timeout
		Timeout timeout = channel.getHandShakeTimeout();
		if (timeout != null) {
			timeout.cancel();
		}
	}	
	
	/**
	 * 判断请求消息是否是握手消息
	 * @param message
	 * @return
	 */
	public static boolean isHandShakeMsg(Object message){
		if(message != null){
			return (message instanceof HandShakeRequest
					|| message instanceof HandShakeAck
					|| message instanceof HandShakeEnd);
		}else{
			return false;
		}
	}
}

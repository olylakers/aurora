package org.aurora.netty.channel;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.aurora.channel.stat.ChannelStatInfo;
import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.service.AuroraService;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.Timeout;

/**
 * 对netty channel封装
 * @author hantong
 *
 * 2013-5-6 下午1:43:57 
 */
public class AuroraChannel implements Channel {
	
	/**
	 * 握手超时任务
	 */
	private Timeout handShakeTimeout;
	
	/**
	 * 消息统计信息
	 */
	private ChannelStatInfo messageStatInfo  = new ChannelStatInfo();
	
	/**
	 * 心跳统计信息
	 */
	private ChannelStatInfo heartBeatsStatInfo = new ChannelStatInfo();
	
	/**
	 * channel上请求的id序列号
	 */
	private AtomicLong seqId = new AtomicLong();
	
	private AuroraService auroraService;
	
	/**
	 * 被包装的netty原生channel
	 */
	private Channel wrappedChannel;
	private String userKey;
	
	private volatile boolean needLock = true;
	private ReentrantLock statisticLock = new ReentrantLock();
	private ChannelManager channelManager;

	private final ChannelFutureListener sendMsgSuccessListener = new ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) throws Exception {
			//
			if (future.isSuccess()) {
				boolean lock = needLock;
				try {
					if (lock) {
						statisticLock.lock();
					}
					//
					long now = System.currentTimeMillis();
					messageStatInfo.getSentNum().incrementAndGet();
					messageStatInfo.setLastestSent(now);

				} finally {
					if (lock) {
						statisticLock.unlock();
					}
				}
			}else{
				messageStatInfo.getFailSendNum().incrementAndGet();
			}
		}
	};
	private final ChannelFutureListener sendHeartBeatSuccessListener = new ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) throws Exception {
			//
			if (future.isSuccess()) {
				boolean lock = needLock;
				try {
					if (lock) {
						statisticLock.lock();
					}
					//
					long now = System.currentTimeMillis();
					heartBeatsStatInfo.getSentNum().incrementAndGet();
					heartBeatsStatInfo.setLastestSent(now);

				} finally {
					if (lock) {
						statisticLock.unlock();
					}
				}
			}else{
				
			}
		}
	};
	private final ChannelFutureListener closeListener = new ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) throws Exception {
			Timeout timeout = AuroraChannel.this.handShakeTimeout;
			if (timeout != null) {
				timeout.cancel();
			}
		}
	};
	
	public AuroraChannel(Channel wrappedChannel, AuroraService auroraService){
		this.wrappedChannel = wrappedChannel;
		this.auroraService = auroraService;
		this.wrappedChannel.getCloseFuture().addListener(closeListener);
	}

	public ChannelManager getChannelManager() {
		return channelManager;
	}

	public void setChannelManager(ChannelManager channelManager) {
		this.channelManager = channelManager;
	}

	public int compareTo(Channel arg0) {
		return wrappedChannel.compareTo(arg0);
	}

	public Integer getId() {
		return wrappedChannel.getId();
	}

	public ChannelFactory getFactory() {
		return wrappedChannel.getFactory();
	}

	public Channel getParent() {
		return wrappedChannel.getParent();
	}

	public ChannelConfig getConfig() {
		return wrappedChannel.getConfig();
	}

	public ChannelPipeline getPipeline() {
		return wrappedChannel.getPipeline();
	}

	public boolean isOpen() {
		return wrappedChannel.isOpen();
	}

	public boolean isBound() {
		return wrappedChannel.isBound();
	}

	public boolean isConnected() {
		return wrappedChannel.isConnected();
	}

	public SocketAddress getLocalAddress() {
		return wrappedChannel.getLocalAddress();
	}

	public SocketAddress getRemoteAddress() {
		return wrappedChannel.getRemoteAddress();
	}

	public ChannelFuture write(Object message) {
		ChannelFuture channelFuture  = wrappedChannel.write(message);
		if(message instanceof HeartBeat){
			channelFuture.addListener(sendHeartBeatSuccessListener);
		}else{
			channelFuture.addListener(sendMsgSuccessListener);
		}
		return null;
	}

	public ChannelFuture write(Object message, SocketAddress remoteAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture bind(SocketAddress localAddress) {
		return wrappedChannel.bind(localAddress);
	}

	public ChannelFuture connect(SocketAddress remoteAddress) {
		return wrappedChannel.connect(remoteAddress);
	}

	public ChannelFuture disconnect() {
		return wrappedChannel.disconnect();
	}

	public ChannelFuture unbind() {
		return wrappedChannel.unbind();
	}

	public ChannelFuture close() {
		return wrappedChannel.close();
	}

	public ChannelFuture getCloseFuture() {
		return wrappedChannel.getCloseFuture();
	}

	public int getInterestOps() {
		return wrappedChannel.getInterestOps();
	}

	public boolean isReadable() {
		return wrappedChannel.isReadable();
	}

	public boolean isWritable() {
		return wrappedChannel.isWritable();
	}

	public ChannelFuture setInterestOps(int interestOps) {
		return wrappedChannel.setInterestOps(interestOps);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.Channel#setReadable(boolean)
	 */
	public ChannelFuture setReadable(boolean readable) {
		return wrappedChannel.setReadable(readable);
	}

	public Timeout getHandShakeTimeout() {
		return handShakeTimeout;
	}

	public void setHandShakeTimeout(Timeout handShakeTimeout) {
		this.handShakeTimeout = handShakeTimeout;
	}

	public ChannelStatInfo getMessageStatInfo() {
		return messageStatInfo;
	}

	public ChannelStatInfo getHeartBeatsStatInfo() {
		return heartBeatsStatInfo;
	}

	public AtomicLong getSeqId() {
		return seqId;
	}

	public AuroraService getAuroraService() {
		return auroraService;
	}

	public void setAuroraService(AuroraService auroraService) {
		this.auroraService = auroraService;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
}

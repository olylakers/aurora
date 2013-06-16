package org.aurora.netty.channel;

import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 连接通道管理，维护用户和通道之间的对应关系
 * 
 * @author hantong
 * 
 *         2013-5-29 下午10:18:45
 */
public class ChannelManager {

	private static final Log log = LogFactory.getLog("org.aurora.message.log");
	private static final ChannelManager instance = new ChannelManager();
	/**
	 * 维护通道ID和通道之间对应关系
	 */
	private ConcurrentHashMap<Integer, AuroraChannel> channels = new ConcurrentHashMap<Integer, AuroraChannel>(
			1024 * 100);

	/**
	 * 维护用户和通道ID之间对应关系,当客户端第一次连接的时候，channelId，而没有用户的token，appkey等信息，
	 * 所以只能把channelId和AuroraChannel的对应关系 等握手成功后，才能建立用户和channel之间对应关系
	 */
	private ConcurrentHashMap<String, Integer> userChannels = new ConcurrentHashMap<String, Integer>(
			1024 * 100);

	private ChannelManager() {
		Timer timer = new Timer("write send msg num and heartbeat num");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				for (Entry<Integer, AuroraChannel> entry : channels.entrySet()) {
					AuroraChannel auroraChannel = entry.getValue();
					if (StringUtils.isNotBlank(auroraChannel.getUserKey())) {
						log.warn(auroraChannel.getUserKey()
								+ ": receive num:"
								+ auroraChannel.getMessageStatInfo()
										.getReceivedNum()
								+ ":send num:"
								+ auroraChannel.getMessageStatInfo()
										.getSentNum()
								+ ":failed num:"
								+ auroraChannel.getMessageStatInfo()
										.getFailSendNum());
					} else {
						log.warn(auroraChannel.getId()
								+ ": receive num:"
								+ auroraChannel.getMessageStatInfo()
										.getReceivedNum()
								+ ":send num:"
								+ auroraChannel.getMessageStatInfo()
										.getSentNum()
								+ ":failed num:"
								+ auroraChannel.getMessageStatInfo()
										.getFailSendNum());
					}
					
				}
			}
		}, 10000, 10000);
	}

	public static ChannelManager getInstance() {
		return instance;
	}

	/**
	 * 添加一个channel
	 * 
	 * @param channelId
	 * @param channel
	 */
	public void addChannel(Integer channelId, AuroraChannel channel) {
		channels.put(channelId, channel);
	}

	/**
	 * 移出channel，一般用于phone client和server断开连接时
	 * 
	 * @param channelId
	 * @return
	 */
	public void removeChannel(Integer channelId) {
		channels.remove(channelId);
	}

	/**
	 * 根据通道ID获取连接通道
	 * 
	 * @param channelId
	 * @return
	 */
	public AuroraChannel getChannel(Integer channelId) {
		return channels.get(channelId);
	}

	/**
	 * 握手成功后，维护用户和通道之间的对应关系
	 * 
	 * @param userKey
	 * @param channelId
	 */
	public void addUser(String userKey, Integer channelId) {
		userChannels.put(userKey, channelId);
	}

	/**
	 * 连接断开之后，解除用户通道关系
	 * 
	 * @param userKey
	 */
	public void removeUser(String userKey) {
		userChannels.remove(userKey);
	}

	/**
	 * 获取用户连接通道
	 * 
	 * @param userKey
	 * @return
	 */
	public AuroraChannel getChannel(String userKey) {
		Integer channelId = userChannels.get(userKey);
		if (channelId == null) {
			return null;
		} else {
			return channels.get(channelId);
		}
	}

	public void closeChannel(Integer channelId, String userKey) {
		channels.remove(channelId);
		userChannels.remove(userKey);
	}
}

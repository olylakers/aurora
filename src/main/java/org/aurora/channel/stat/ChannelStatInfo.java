package org.aurora.channel.stat;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * channel统计数据
 * @author hantong
 *
 * 2013-6-2 下午9:37:01 
 */
public class ChannelStatInfo implements Serializable {

	private static final long serialVersionUID = -3659217200958019050L;
	
	private long lastestReceived;
	private long lastestSent;
	private AtomicLong receivedNum = new AtomicLong();
	private AtomicLong sentNum = new AtomicLong();
	private AtomicLong failSendNum = new AtomicLong();

	public AtomicLong getReceivedNum() {
		return receivedNum;
	}

	public AtomicLong getSentNum() {
		return sentNum;
	}

	public AtomicLong getFailSendNum() {
		return failSendNum;
	}

	public long getLastestReceived() {
		return lastestReceived;
	}

	public void setLastestReceived(Long lastestReceived) {
		this.lastestReceived = lastestReceived;
	}

	public void setLastestReceivedIfLater(Long lastestReceived) {
		if (lastestReceived == null) {
			return;
		}
		if (this.lastestReceived < lastestReceived) {
			this.lastestReceived = lastestReceived;
		}
	}

	public Long getLastestSent() {
		return lastestSent;
	}

	public void setLastestSent(Long lastestSent) {
		this.lastestSent = lastestSent;
	}

	public void setLastestSentIfLater(Long lastestSent) {
		if (lastestSent == null) {
			return;
		}
		if (this.lastestSent < lastestSent) {
			this.lastestSent = lastestSent;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatisticInfo [lastestReceived=");
		builder.append(lastestReceived);
		builder.append(", lastestSent=");
		builder.append(lastestSent);
		builder.append(", receivedNum=");
		builder.append(receivedNum);
		builder.append(", sentNum=");
		builder.append(sentNum);
		builder.append("]");
		return builder.toString();
	}

}

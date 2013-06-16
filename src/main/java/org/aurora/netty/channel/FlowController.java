package org.aurora.netty.channel;

/**
 * 流量控制器,接口定义参照semaphore
 * @author hantong
 *
 * 2013-5-9 下午10:21:46 
 */
public interface FlowController {
	public abstract void acquire() throws InterruptedException;

	public abstract void acquire(int permits) throws InterruptedException;

	public abstract boolean acquire(int permits, int timeout);

	public abstract void release();

	public abstract void release(int permits);

	public abstract int getAvailable();

	public abstract void setThreshold(int newThreshold);

	public abstract int getThreshold();
}

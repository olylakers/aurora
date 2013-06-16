package org.aurora.netty.channel;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 基于{@link java.util.concurrent.Semaphore}的缺省流量控制器
 * @author hantong
 *
 * 2013-5-9 下午10:29:48 
 */
public class DefaultFlowController implements FlowController {
	private int threshold = 2000000;
	private Semaphore gate = new Semaphore(threshold, false);

	public void acquire() throws InterruptedException {
		gate.acquire();
	}

	public void acquire(int permits) throws InterruptedException {
		gate.acquire(permits);
	}

	public boolean acquire(int permits, int timeout) {
		try {
			return gate.tryAcquire(permits, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}	
	}

	public void release() {
		gate.release();
	}

	public void release(int permits) {
		gate.release(permits);
	}

	public int getAvailable() {
		return gate.availablePermits();
	}

	public void setThreshold(int threshold) {
		if (threshold <= 0) {
			throw new IllegalArgumentException("threshold must great than 0.");
		}
		
		this.threshold = threshold;
	}

	public int getThreshold() {
		return threshold;
	}

}

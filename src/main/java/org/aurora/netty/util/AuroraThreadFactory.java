package org.aurora.netty.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hantong
 *
 * 2013-6-3 下午11:22:00 
 */
public class AuroraThreadFactory implements ThreadFactory {

	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final String mPrefix;

	private final boolean mDaemo;

	private final ThreadGroup mGroup;

	public AuroraThreadFactory() {
		this("pool-" + POOL_SEQ.getAndIncrement(), false);
	}

	public AuroraThreadFactory(String prefix) {
		this(prefix, false);
	}

	public AuroraThreadFactory(String prefix, boolean daemo) {
		mPrefix = prefix + "-thread-";
		mDaemo = daemo;
		SecurityManager s = System.getSecurityManager();
		mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}

	public Thread newThread(Runnable runnable) {
		String name = mPrefix + mThreadNum.getAndIncrement();
		Thread ret = new Thread(mGroup, runnable, name, 0);
		ret.setDaemon(mDaemo);
		return ret;
	}

	public ThreadGroup getThreadGroup() {
		return mGroup;
	}

}

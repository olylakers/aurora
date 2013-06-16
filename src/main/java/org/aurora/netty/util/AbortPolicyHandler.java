package org.aurora.netty.util;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hantong
 *
 * 2013-6-3 下午11:25:02 
 */
public class AbortPolicyHandler extends AbortPolicy {
	private static final Log LOG = LogFactory.getLog(AbortPolicyHandler.class);
	private final String threadName;

	public AbortPolicyHandler(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		String msg = String.format("Thread pool is EXHAUSTED!"
						+ " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d), Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s) !",
						threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
						e.getLargestPoolSize(), e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(),
						e.isTerminated(), e.isTerminating());
		LOG.warn(msg);
		throw new RejectedExecutionException(msg);
	}
}

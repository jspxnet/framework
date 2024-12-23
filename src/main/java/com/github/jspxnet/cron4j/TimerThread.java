/*
 * cron4j - A pure Java cron-like scheduler
 * 
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.jspxnet.cron4j;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.SystemUtil;
import lombok.Getter;

/**
 * <p>
 * TimeThreads are used by {@link Scheduler} instances. A TimerThread spends
 * most of the time sleeping. It wakes up every minute and it requests to the
 * scheduler the spawning of a {@link LauncherThread}.
 * </p>
 * 
 * @author Carlo Pelliccia
 * @since 2.0
 */
class TimerThread extends Thread {

	/**
	 * A GUID for this object.
     * -- GETTER --
     */
	@Getter
    private final String guid;

	/**
	 * The owner scheduler.
	 */
	private final Scheduler scheduler;

	/**
	 * Builds the timer thread.
	 * 
	 * @param scheduler
	 *            The owner scheduler.
	 */
	public TimerThread(Scheduler scheduler) {
		this.scheduler = scheduler;
		if (this.scheduler == null || "NONE".equals(this.scheduler.getGuid())) {
			setName("cron4j::[NONE]");
			guid = SystemUtil.getPid();
			return;
		}
		guid = SystemUtil.getPid() + "_" + scheduler.getGuid();
		// Thread name.
		//final String name = "cron4j::[" + scheduler.getTaskConf().getName() + "]::timer[" + guid + "]";
		setName("cron4j::[" + this.scheduler.getTaskConf().getName() + "]::timer[" + guid + "]");
	}


    /**
	 * It has been reported that the {@link Thread#sleep(long)} method sometimes
	 * exits before the requested time has passed. This one offers an
	 * alternative that sometimes could sleep a few millis more than requested,
	 * but never less.
	 * 
	 * @param millis
	 *            The length of time to sleep in milliseconds.
	 * @throws InterruptedException
	 *             If another thread has interrupted the current thread. The
	 *             <i>interrupted status</i> of the current thread is cleared
	 *             when this exception is thrown.
	 * @see Thread#sleep(long)
	 */
	private void safeSleep(long millis) throws InterruptedException {
		long done = 0;
		do {
			long before = System.currentTimeMillis();
			sleep(millis - done);
			long after = System.currentTimeMillis();
			done += Math.abs(after - before);
		} while (done < millis);
	}

	/**
	 * Overrides {@link Thread#run()}.
	 */
	@Override
	public void run() {
		// What time is it?
		long millis = System.currentTimeMillis();
		// Calculating next minute.
		long nextSecond = ((millis / DateUtil.SECOND) + 1) * DateUtil.SECOND;
		// Work until the scheduler is started.
		for (;;) {
			// Coffee break 'till next minute comes!
			long sleepTime = (nextSecond - System.currentTimeMillis());
			if (sleepTime > 0) {
				try {
					safeSleep(sleepTime);
				} catch (InterruptedException e) {
					// Must exit!
					break;
				}
			}
			//当前时间
			millis = System.currentTimeMillis();
			// Launching the launching thread!
			scheduler.spawnLauncher(millis);
			// Calculating next minute.
			nextSecond = ((millis / DateUtil.SECOND) + 1) * DateUtil.SECOND;
		}
	}
}

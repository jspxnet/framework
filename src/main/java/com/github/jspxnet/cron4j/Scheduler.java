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

import com.github.jspxnet.sioc.scheduler.TaskProxy;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.BooleanUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import java.io.File;
import java.util.*;

/**
 * <p>
 * The cron4j scheduler.
 * </p>
 *
 * @author Carlo Pelliccia
 */
public class Scheduler {

    /**
     * A GUID for this scheduler.
     */
    private TaskProxy taskProxy;

    /**
     * The time zone applied by the scheduler.
     */
    private TimeZone timezone = null;

    /**
     * The daemon flag. If true the scheduler and its spawned threads acts like
     * daemons.
     */
    private boolean daemon = true;

    /**
     * The state flag. If true the scheduler is started and running, otherwise
     * it is paused and no task is launched.
     */
    private boolean started = false;

    /**
     * Registered {@link TaskCollector}s list.
     */
    private final List<TaskCollector> collectors = Collections.synchronizedList(new ArrayList<>());

    /**
     * The {@link MemoryTaskCollector} used for memory stored tasks. Represented
     * here for convenience, it is also the first element in the
     * {@link Scheduler#collectors} list.
     */
    private final MemoryTaskCollector memoryTaskCollector = new MemoryTaskCollector();

    /**
     * The {@link FileTaskCollector} used for reading tasks from files.
     * Represented here for convenience, it is also the second element in the
     * {@link Scheduler#collectors} list.
     */
    private final FileTaskCollector fileTaskCollector = new FileTaskCollector();

    /**
     * Registered {@link SchedulerListener}s list.
     */
    private final List<SchedulerListener> listeners = Collections.synchronizedList(new ArrayList<>());

    /**
     * The thread checking the clock and requesting the spawning of launcher
     * threads.
     */
    private TimerThread timer = null;

    /**
     * Currently running {@link LauncherThread} instances.
     */
    final private List<LauncherThread> launchers = Collections.synchronizedList(new ArrayList<>());

    /**
     * Currently running {@link TaskExecutor} instances.
     */
    final private List<TaskExecutor> executors = Collections.synchronizedList(new ArrayList<>());

    /**
     * Internal lock, used to synchronize status-aware operations.
     */
    private final Object lock = new Object();

    /**
     * It builds and prepares a brand new Scheduler instance.
     */
    public Scheduler() {
        collectors.add(memoryTaskCollector);
        collectors.add(fileTaskCollector);
    }


    public Scheduler(TaskProxy taskProxy) {
        collectors.add(memoryTaskCollector);
        collectors.add(fileTaskCollector);
        setDaemon(true);
        schedule(taskProxy);
    }


    /**
     * It returns the GUID for this scheduler.
     *
     * @return The GUID for this scheduler.
     */
    public String getGuid() {
        return taskProxy.getScheduledId();
    }

    /**
     * <p>
     * Sets the time zone applied by the scheduler.
     * </p>
     * <p>
     * Current system time is adapted to the supplied time zone before comparing
     * it with registered scheduling patterns. The result is that any supplied
     * scheduling pattern is treated according to the specified time zone. In
     * example, suppose:
     * </p>
     * <ul>
     * <li>System time: 10:00</li>
     * <li>System time zone: GMT+1</li>
     * <li>Scheduler time zone: GMT+3</li>
     * </ul>
     * <p>
     * The scheduler, before comparing system time with patterns, translates
     * 10:00 from GMT+1 to GMT+3. It means that 10:00 becomes 12:00. The
     * resulted time is then used by the scheduler to activate tasks. So, in the
     * given configuration at the given moment, any task scheduled as
     * <em>0 12 * * *</em> will be executed, while any <em>0 10 * * *</em> will
     * not.
     * </p>
     *
     * @param timezone The time zone applied by the scheduler.
     */
    public void setTimeZone(TimeZone timezone) {
        this.timezone = timezone;
    }

    /**
     * Returns the time zone applied by the scheduler.
     *
     * @return The time zone applied by the scheduler.
     */
    public TimeZone getTimeZone() {
        return timezone != null ? timezone : TimeZone.getDefault();
    }

    /**
     * Tests whether this scheduler is a daemon scheduler.
     *
     * @return true if this scheduler is a daemon scheduler; false otherwise.
     */
    public boolean isDaemon() {
        return daemon;
    }

    /**
     * Marks this scheduler daemon flag. When a scheduler is marked as a daemon
     * scheduler it spawns only daemon threads. The Java Virtual Machine exits
     * when the only threads running are all daemon threads.
     * <p>
     * This method must be called before the scheduler is started.
     *
     * @param on If true, the scheduler will spawn only daemon threads.
     * @throws IllegalStateException If the scheduler is started.
     */
    public void setDaemon(boolean on) throws IllegalStateException {
        synchronized (lock) {
            if (started) {
                throw new IllegalStateException("scheduler already started");
            }
            this.daemon = on;
        }
    }

    /**
     * Tests if this scheduler is started.
     *
     * @return true if the scheduler is started, false if it is stopped.
     */
    public boolean isStarted() {
        synchronized (lock) {
            return started;
        }
    }

    public SchedulerDto getTaskConf() {
        if (taskProxy == null) {
            return null;
        }
        SchedulerDto dto = BeanUtil.copy(taskProxy, SchedulerDto.class);
        dto.setGuid(taskProxy.getScheduledId());
        dto.setClassName(taskProxy.getBean().getClass().getName());
        dto.setStarted(BooleanUtil.toInt(started));
        return dto;
    }

    /**
     * 强制运行
     * @throws Exception 异常
     */
    public void forceRun() throws Exception {
        if (taskProxy == null) {
           throw  new Exception("forceRun 运行错误， taskProxy 不能为空");
        }
        taskProxy.forceRun();
    }


    /**
     * Adds a {@link File} instance to the scheduler. Every minute the file will
     * be parsed. The scheduler will execute any declared task whose scheduling
     * pattern matches the current system time.
     * <p>
     * See {@link CronParser} documentation for informations about the file
     * contents syntax.
     *
     * @param file The {@link File} instance.
     */
    public void scheduleFile(File file) {
        fileTaskCollector.addFile(file);
    }

    /**
     * Removes a {@link File} instance previously scheduled with the
     * {@link Scheduler#scheduleFile(File)} method.
     *
     * @param file The {@link File} instance.
     */
    public void descheduleFile(File file) {
        fileTaskCollector.removeFile(file);
    }

    /**
     * Returns an array containing any {@link File} previously scheduled with
     * the {@link Scheduler#scheduleFile(File)} method.
     *
     * @return An array containing any {@link File} previously scheduled with
     * the {@link Scheduler#scheduleFile(File)} method.
     */
    public File[] getScheduledFiles() {
        return fileTaskCollector.getFiles();
    }

    /**
     * Adds a custom {@link TaskCollector} instance to the scheduler. The
     * supplied object, once added to the scheduler, will be query every minute
     * for its task list. The scheduler will execute any of the returned tasks
     * whose scheduling pattern matches the current system time.
     *
     * @param collector The custom {@link TaskCollector} instance.
     */
    public void addTaskCollector(TaskCollector collector) {
        synchronized (collectors) {
            collectors.add(collector);
        }
    }

    /**
     * Removes a previously registered custom {@link TaskCollector} instance.
     *
     * @param collector The custom {@link TaskCollector} instance.
     */
    public void removeTaskCollector(TaskCollector collector) {
        synchronized (collectors) {
            collectors.remove(collector);
        }
    }

    /**
     * Returns an array containing any custom {@link TaskCollector} instance
     * previously registered in the scheduler with the
     * {@link Scheduler#addTaskCollector(TaskCollector)} method.
     *
     * @return An array containing any custom {@link TaskCollector} instance
     * previously registered in the scheduler with the
     * {@link Scheduler#addTaskCollector(TaskCollector)} method.
     */
    public TaskCollector[] getTaskCollectors() {
        synchronized (collectors) {
            // Discard the first 2 elements in the list.
            int size = collectors.size() - 2;
            TaskCollector[] ret = new TaskCollector[size];
            for (int i = 0; i < size; i++) {
                ret[i] = collectors.get(i + 2);
            }
            return ret;
        }
    }

    /**
     * Adds a {@link SchedulerListener} to the scheduler. A
     * {@link SchedulerListener} is notified every time a task is launching, has
     * succeeded or has failed.
     *
     * @param listener The listener.
     */
    public void addSchedulerListener(SchedulerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a {@link SchedulerListener} previously registered with the
     * {@link Scheduler#addSchedulerListener(SchedulerListener)} method.
     *
     * @param listener The listener.
     */
    public void removeSchedulerListener(SchedulerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Returns an array containing any {@link SchedulerListener} previously
     * registered with the
     * {@link Scheduler#addSchedulerListener(SchedulerListener)} method.
     *
     * @return An array containing any {@link SchedulerListener} previously
     * registered with the
     * {@link Scheduler#addSchedulerListener(SchedulerListener)} method.
     */
    public SchedulerListener[] getSchedulerListeners() {
        synchronized (listeners) {
            int size = listeners.size();
            SchedulerListener[] ret = new SchedulerListener[size];
            for (int i = 0; i < size; i++) {
                ret[i] = listeners.get(i);
            }
            return ret;
        }
    }

    /**
     * Returns an array containing any currently executing task, in the form of
     * {@link TaskExecutor} objects. Each running task is executed by a
     * different thread. A {@link TaskExecutor} object allows the control of the
     * running task. The inner {@link Task} representation could be retrieved,
     * the status of the task could be detected and the thread could be
     * interrupted using any standard {@link Thread} method (
     * {@link Thread#interrupt()}, {@link Thread#isAlive() etc}.
     *
     * @return An array containing any currently executing task, in the form of
     * {@link TaskExecutor} objects.
     */
    public TaskExecutor[] getExecutingTasks() {
        if (ObjectUtil.isEmpty(executors)) {
            return null;
        }
        synchronized (executors) {
            int size = executors.size();
            TaskExecutor[] ret = new TaskExecutor[size];
            for (int i = 0; i < size; i++) {
                ret[i] = executors.get(i);
            }
            return ret;
        }
    }

    /**
     * This method schedules a task execution.
     *
//    @param pattern The scheduling pattern for the task.
     * @param task              The task, as a plain Runnable object.
     * @return The task auto-generated ID assigned by the scheduler. This ID can
     * be used later to reschedule and deschedule the task, and also to
     * retrieve informations about it.
     * @throws InvalidPatternException If the supplied pattern is not valid.
     */
    public String schedule(TaskProxy task)
            throws InvalidPatternException {
        if (task == null) {
            throw new RuntimeException("不允许放入空任务");
        }
        this.taskProxy = task;
        return schedule(task.getPattern(), new RunnableTask(task,task.getScheduledId()));
    }

    /**
     * This method schedules a task execution.
     *
     * @param schedulingPattern The scheduling pattern for the task.
     * @param task              The task, as a plain Runnable object.
     * @return The task auto-generated ID assigned by the scheduler. This ID can
     * be used later to reschedule and deschedule the task, and also to
     * retrieve informations about it.
     * @throws InvalidPatternException If the supplied pattern is not valid.
     * @since 2.0
     */
    public String schedule(String schedulingPattern, Task task)
            throws InvalidPatternException {
        return schedule(new SchedulingPattern(schedulingPattern), task);
    }

    /**
     * This method schedules a task execution.
     *
     * @param schedulingPattern The scheduling pattern for the task.
     * @param task              The task, as a plain Runnable object.
     * @return The task auto-generated ID assigned by the scheduler. This ID can
     * be used later to reschedule and deschedule the task, and also to
     * retrieve informations about it.
     * @since 2.0
     */
    public String schedule(SchedulingPattern schedulingPattern, Task task) {
        return memoryTaskCollector.add(schedulingPattern, task);
    }


    /**
     * This method changes the scheduling pattern of a task.
     *
     * @param id                The ID assigned to the previously scheduled task.
     * @param schedulingPattern The new scheduling pattern for the task.
     * @throws InvalidPatternException If the supplied pattern is not valid.
     */
    public void reschedule(String id, String schedulingPattern)
            throws InvalidPatternException {
        reschedule(id, new SchedulingPattern(schedulingPattern));
    }

    /**
     * This method changes the scheduling pattern of a task.
     *
     * @param id                The ID assigned to the previously scheduled task.
     * @param schedulingPattern The new scheduling pattern for the task.
     * @since 2.0
     */
    public void reschedule(String id, SchedulingPattern schedulingPattern) {

        SchedulingPattern result = memoryTaskCollector.update(id, schedulingPattern);
        if (result!=null)
        {
            taskProxy.setPattern(result.toString());
        }
    }

    /**
     * This methods cancels the scheduling of a task.
     *
     * @param id The ID of the task.
     * @deprecated Use {@link Scheduler#deschedule(String)}.
     */
    public void deschedule(Object id) {
        deschedule((String) id);
    }

    /**
     * This methods cancels the scheduling of a task.
     *
     * @param id The ID of the task.
     */
    public void deschedule(String id) {
        memoryTaskCollector.remove(id);
    }

    /**
     * This method retrieves a previously scheduled task.
     *
     * @param id The task ID.
     * @return The requested task, or null if the task was not found.
     * @since 2.0
     */
    public Task getTask(String id) {
        return memoryTaskCollector.getTask(id);
    }

    /**
     * This method retrieves a previously scheduled task scheduling pattern.
     *
     * @param id The task ID.
     * @return The requested scheduling pattern, or null if the task was not
     * found.
     * @since 2.0
     */
    public SchedulingPattern getSchedulingPattern(String id) {
        return memoryTaskCollector.getSchedulingPattern(id);
    }

    /**
     * This method retrieves the Runnable object of a previously scheduled task.
     *
     * @param id The task ID.
     * @return The Runnable object of the task, or null if the task was not
     * found.
     * @deprecated Use {@link Scheduler#getTask(String)}.
     */
    public Runnable getTaskRunnable(Object id) {
        Task task = getTask((String) id);
        if (task instanceof RunnableTask) {
            RunnableTask rt = (RunnableTask) task;
            return rt.getRunnable();
        } else {
            return null;
        }
    }

    /**
     * This method retrieves the scheduling pattern of a previously scheduled
     * task.
     *
     * @param id The task ID.
     * @return The scheduling pattern of the task, or null if the task was not
     * found.
     * @deprecated Use {@link Scheduler#getSchedulingPattern(String)}.
     */
    public String getTaskSchedulingPattern(Object id) {
        return getSchedulingPattern((String) id).toString();
    }

    /**
     * Executes immediately a task, without scheduling it.
     *
     * @param task The task.
     * @return The {@link TaskExecutor} executing the given task.
     * @throws IllegalStateException If the scheduler is not started.
     */
    public TaskExecutor launch(Task task) {
        synchronized (lock) {
            if (!started) {
                throw new IllegalStateException("Scheduler not started");
            }
            return spawnExecutor(task);
        }
    }

    /**
     * This method starts the scheduler. When the scheduled is started the
     * supplied tasks are executed at the given moment.
     *
     * @throws IllegalStateException Thrown if this scheduler is already started.
     */
    public void start() throws IllegalStateException {
        synchronized (lock) {
            if (started) {
                throw new IllegalStateException("Scheduler already started");
            }
            // Starts the timer thread.
            timer = new TimerThread(this);
            timer.setDaemon(daemon);
            timer.start();
            // Change the state of the scheduler.
            started = true;
        }
    }

    /**
     * This method stops the scheduler execution. Before returning, it waits the
     * end of all the running tasks previously launched. Once the scheduler has
     * been stopped it can be started again with a start() call.
     *
     * @throws IllegalStateException Thrown if this scheduler is not started.
     */
    public void stop() throws IllegalStateException {
        if (timer == null) {
            return;
        }
        synchronized (lock) {
            if (!started) {
                throw new IllegalStateException("Scheduler not started");
            }
            // Interrupts the timer and waits for its death.
            timer.interrupt();
            tillThreadDies(timer);
            timer = null;
            // Interrupts any running launcher and waits for its death.
            for (LauncherThread launcher : launchers) {
                launcher.interrupt();
                tillThreadDies(launcher);
            }
            launchers.clear();
			/*for (;;) {
				LauncherThread launcher = null;
				synchronized (launchers) {
					if (launchers.size() == 0) {
						break;
					}
					launcher = launchers.remove(0);
				}
				launcher.interrupt();
				tillThreadDies(launcher);
			}
			launchers.clear();*/
            // Interrupts any running executor and waits for its death.
            // Before exiting wait for all the active tasks end.
            for (TaskExecutor executor : executors) {
                if (executor.canBeStopped()) {
                    executor.stop();
                }
                tillExecutorDies(executor);
            }
            executors.clear();
			/*
			for (;;) {
				TaskExecutor executor = null;
				synchronized (executors) {
					if (executors.size() == 0) {
						break;
					}
					executor = executors.remove(0);
				}
				if (executor.canBeStopped()) {
					executor.stop();
				}
				tillExecutorDies(executor);
			}
			executors.clear();*/
            // Change the state of the object.
            started = false;
        }
    }

    // -- PACKAGE RESERVED METHODS --------------------------------------------

    /**
     * Starts a launcher thread.
     *
     * @param referenceTimeInMillis Reference time in millis for the launcher.
     * @return The spawned launcher.
     */
    LauncherThread spawnLauncher(long referenceTimeInMillis) {
        TaskCollector[] nowCollectors;
        synchronized (collectors) {
            int size = collectors.size();
            nowCollectors = new TaskCollector[size];
            for (int i = 0; i < size; i++) {
                nowCollectors[i] = collectors.get(i);
            }
        }
        LauncherThread l = new LauncherThread(this, nowCollectors,
                referenceTimeInMillis);
        synchronized (launchers) {
            launchers.add(l);
        }
        l.setDaemon(daemon);
        l.start();
        return l;
    }

    /**
     * Starts the given task within a task executor.
     *
     * @param task The task.
     * @return The spawned task executor.
     */
    TaskExecutor spawnExecutor(Task task) {
        TaskExecutor e = new TaskExecutor(this, task);
        synchronized (executors) {
            executors.add(e);
        }
        e.start();
        return e;
    }

    /**
     * This method is called by a launcher thread to notify that the execution
     * is completed.
     *
     * @param launcher The launcher which has completed its task.
     */
    void notifyLauncherCompleted(LauncherThread launcher) {
        synchronized (launchers) {
            launchers.remove(launcher);
        }
    }

    /**
     * This method is called by a task executor to notify that the execution is
     * completed.
     *
     * @param executor The executor which has completed its task.
     */
    void notifyExecutorCompleted(TaskExecutor executor) {
        synchronized (executors) {
            executors.remove(executor);
        }
    }

    /**
     * Notifies every registered listener that a task is going to be launched.
     *
     * @param executor The task executor.
     */
    void notifyTaskLaunching(TaskExecutor executor) {
        synchronized (listeners) {
            for (SchedulerListener l : listeners) {
                l.taskLaunching(executor);
            }
        }
    }

    /**
     * Notifies every registered listener that a task execution has successfully
     * completed.
     *
     * @param executor The task executor.
     */
    void notifyTaskSucceeded(TaskExecutor executor) {
        synchronized (listeners) {
            for (SchedulerListener listener : listeners) {
                listener.taskSucceeded(executor);
            }
        }
    }

    /**
     * Notifies every registered listener that a task execution has failed due
     * to an uncaught exception.
     *
     * @param executor  The task executor.
     * @param exception The exception.
     */
    void notifyTaskFailed(TaskExecutor executor, Throwable exception) {
        synchronized (listeners) {
            if (!listeners.isEmpty()) {
                for (SchedulerListener l : listeners) {
                    l.taskFailed(executor, exception);
                }
            } else {
                // Logs on console if no one has been notified about it.
                exception.printStackTrace();
            }
        }
    }

    // -- PRIVATE METHODS -----------------------------------------------------

    /**
     * It waits until the given thread is dead. It is similar to
     * {@link Thread#join()}, but this one avoids {@link InterruptedException}
     * instances.
     *
     * @param thread The thread.
     */
    private void tillThreadDies(Thread thread) {
        boolean dead = false;
        do {
            try {
                thread.join(DateUtil.MINUTE);
                dead = true;
            } catch (InterruptedException e) {
                ;
            }
        } while (!dead);
    }

    /**
     * It waits until the given task executor is dead. It is similar to
     * {@link TaskExecutor#join()}, but this one avoids
     * {@link InterruptedException} instances.
     *
     * @param executor The task executor.
     */
    private void tillExecutorDies(TaskExecutor executor) {
        boolean dead = false;
        do {
            try {
                executor.join();
                dead = true;
            } catch (InterruptedException e) {
                ;
            }
        } while (!dead);
    }


}

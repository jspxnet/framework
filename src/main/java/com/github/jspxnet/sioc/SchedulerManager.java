package com.github.jspxnet.sioc;

import com.github.jspxnet.sioc.scheduler.TaskProxy;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.Set;

public interface SchedulerManager {
    int add(Object bean);

    boolean add(String id, String pattern, Runnable runnable);

    boolean add(TaskProxy taskProxy);

    int size();

    Set<String> keySet();

    Scheduler get(String id);

    Scheduler remove(String id);

    void shutdown();
}

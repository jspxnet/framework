package com.github.jspxnet.boot;

import lombok.extern.slf4j.Slf4j;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author ChenYuan
 * <p>
 * google-gson issue # 402: Memory Leak in web application; comment # 25
 * https://code.google.com/p/google-gson/issues/detail?id=402
 */
@Slf4j
class ThreadLocalImmolate {


    private Boolean debug = false;

    ThreadLocalImmolate(boolean debug) {
        this.debug = debug;
    }

    Integer immolate() {
        int count = 0;
        try {
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            for (final Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread == null || thread.getName().toLowerCase().contains("gc")) {
                    continue;
                }
                log.info("immolated  clear thread " + thread.getName());
                count += clear(threadLocalsField.get(thread));
                count += clear(inheritableThreadLocalsField.get(thread));
                if ("jspxDataBaseThread".equalsIgnoreCase(thread.getName())) {
                    thread.interrupt();
                }
                if (thread.getClass().getName().startsWith("java.util.Timer") || thread.getClass().getName().startsWith("jspx.")) {
                    clearReferencesStopTimerThread(thread);
                }
            }
            log.info("immolated " + count + " values in ThreadLocals " + inheritableThreadLocalsField.getName());
        } catch (Exception e) {
            throw new Error("ThreadLocalImmolate.immolate()", e);
        }
        return count;
    }

    private int clear(final Object threadLocalMap) throws Exception {
        if (threadLocalMap == null) {
            return 0;
        }
        int count = 0;
        Field tableField = threadLocalMap.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
        Object table = tableField.get(threadLocalMap);
        for (int i = 0, length = Array.getLength(table); i < length; ++i) {
            Object entry = Array.get(table, i);
            if (entry != null) {
                final Object threadLocal = ((WeakReference) entry).get();
                if (threadLocal != null) {
                    log(i, threadLocal);
                    Array.set(table, i, null);
                    ++count;
                }
            }
        }
        return count;
    }

    private void clearReferencesStopTimerThread(Thread thread) {
        try {
            try {
                Field newTasksMayBeScheduledField = thread.getClass().getDeclaredField("newTasksMayBeScheduled");
                newTasksMayBeScheduledField.setAccessible(true);
                Field queueField = thread.getClass().getDeclaredField("queue");
                queueField.setAccessible(true);
                Object queue = queueField.get(thread);
                Method clearMethod = queue.getClass().getDeclaredMethod("clear");
                clearMethod.setAccessible(true);

                newTasksMayBeScheduledField.setBoolean(thread, false);
                clearMethod.invoke(queue);
                queue.notify();

            } catch (NoSuchFieldException var11) {
                Method cancelMethod = thread.getClass().getDeclaredMethod("cancel");

                cancelMethod.setAccessible(true);
                cancelMethod.invoke(thread);
            }
        } catch (Exception e) {
            log.info(thread.getName() + " " + thread.getClass().getName() + " " + e.getLocalizedMessage());
        }

    }

    private void log(int i, Object threadLocal) {
        if (!debug) {
            return;
        }
        if (threadLocal.getClass() != null && threadLocal.getClass().getEnclosingClass() != null && threadLocal.getClass().getEnclosingClass().getName() != null) {
            log.info("ThreadLocalMap(" + i + "): " + threadLocal.getClass().getEnclosingClass().getName());
        } else if (threadLocal.getClass() != null &&
                threadLocal.getClass().getName() != null) {
            log.info("ThreadLocalMap(" + i + "): " + threadLocal.getClass().getName());
        } else {
            log.info("ThreadLocalMap(" + i + "): cannot identify Threadlocal class name");
        }
    }
}
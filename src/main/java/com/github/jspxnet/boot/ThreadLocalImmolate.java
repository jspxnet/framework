package com.github.jspxnet.boot;

import com.github.jspxnet.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;


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
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread == null || thread.getName().toLowerCase().contains("gc")) {
                    continue;
                }
                log.info("immolated  clear thread " + thread.getName());
                count += clear(threadLocalsField.get(thread));
                count += clear(inheritableThreadLocalsField.get(thread));
                thread.interrupt();
                thread.join(DateUtil.SECOND);
            }
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread == null || thread.getName().toLowerCase().contains("gc")) {
                    continue;
                }
               thread.stop();
            }
            System.exit(1);
        } catch (Exception e) {
            System.exit(0);
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

                    Array.set(table, i, null);
                    ++count;
                }
            }
        }
        return count;
    }


}
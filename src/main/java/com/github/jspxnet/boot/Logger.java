package com.github.jspxnet.boot;

/**
 * 没有实际用处，只是为了兼容老版本
 */
public interface Logger {
    void debug(String var1);

    void debug(String var1, Throwable var2);

    void info(String var1);

    void info(String var1, Throwable var2);

    void warn(String var1);

    void warn(String var1, Throwable var2);

    void error(String var1);

    void error(String var1, Throwable var2);

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    boolean isFatalEnabled();

    void error(Throwable var1);

    void warn(Throwable var1);
}

package com.github.jspxnet.txweb.context;

public class ThreadContextHolder {
    private static final ContextHolderStrategy STRATEGY = new DefultContextHolderStrategy();

    public ThreadContextHolder() {
    }

    public static void clearContext() {
        STRATEGY.clearContext();
    }

    public static ActionContext getContext() {
        return STRATEGY.getContext();
    }


}

package com.github.jspxnet.txweb.context;

public interface ContextHolderStrategy {
    void clearContext();

    ActionContext getContext();

    void setContext(ActionContext var1);

}

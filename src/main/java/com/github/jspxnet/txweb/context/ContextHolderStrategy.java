package com.github.jspxnet.txweb.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ContextHolderStrategy {
    void clearContext();


    ActionContext getContext();

    void setContext(ActionContext var1);

}

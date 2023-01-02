package com.github.jspxnet.txweb.context;

import com.github.jspxnet.txweb.AssertException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class DefultContextHolderStrategy implements ContextHolderStrategy{
    private static final ThreadLocal<ActionContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    public DefultContextHolderStrategy() {
    }

    @Override
    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }


    static public void createContext(HttpServletRequest request, HttpServletResponse response,Map<String, Object> params) {
        CONTEXT_HOLDER.set(createEmptyContext(request,response,params));
    }

    @Override
    public ActionContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    @Override
    public void setContext(ActionContext context) {
        AssertException.isNull(context, "Only non-null SecurityContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }


    private static ActionContext createEmptyContext(HttpServletRequest request, HttpServletResponse response,Map<String, Object> params) {
        ActionContext actionContext = new ActionContext();
        actionContext.setRequest(request);
        actionContext.setResponse(response);
        actionContext.setEnvironment(params);
        return actionContext;
    }


}

package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import javax.servlet.http.HttpServletResponse;

/**
 * 专门显示错误信息
 */
public class ErrorResult extends ResultSupport {
    final private static TemplateConfigurable CONFIGURABLE = new TemplateConfigurable();

    static {
        CONFIGURABLE.addAutoIncludes(ENV_TEMPLATE.getString(Environment.autoIncludes));
    }

    public ErrorResult() {

    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletResponse response = actionContext.getResponse();
        //浏览器缓存控制begin
        String browserCache = actionContext.getString(ActionEnv.BROWSER_CACHE);
        boolean noCache = !StringUtil.isNull(browserCache) && !StringUtil.toBoolean(browserCache);
        if (noCache) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache,must-revalidate");
            response.setDateHeader("Expires", 0);
        }
        //浏览器缓存控制end
        TXWebUtil.errorPrint("",actionContext.getFieldInfo(), response, HttpStatusType.HTTP_status_500);
    }
}
package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.result.MarkdownResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by chenYuan on 2017/6/17.
 * markdown格式解析
 */
public class MarkdownHandle extends ActionHandle {
    final public static String NAME = "md";
    final static private String MD_NAMESPACE = "md";


    @Override
    public void doing(HttpServletRequest request, HttpServletResponse response) throws Exception {

       //////////////////////////////////环境参数 begin
        Map<String, Object> envParams = createEnvironment(request, response);
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        ActionConfig actionConfig = webConfigManager.getActionConfig((String) envParams.get(ActionEnv.Key_ActionName), MD_NAMESPACE, true);
        ActionInvocation actionInvocation = null;
        try {
            actionInvocation = new DefaultActionInvocation(actionConfig, envParams, NAME, null, request, response,false);
            actionInvocation.initAction();
            actionInvocation.invoke();
        } finally {
            if (actionInvocation!=null)
            {
                actionInvocation.executeResult(new MarkdownResult());
            }
        }
        ////////////////////action end
    }
}

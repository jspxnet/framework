package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 * author ChenYuan
 * date 2017/6/17
 * @author Administrator
 */
@Slf4j
public abstract class WebHandle {
    final public static int REQUEST_MAX_LENGTH = 5242880; //最大json请求不超过5M
    abstract public void doing(final HttpServletRequest request, final HttpServletResponse response) throws Exception;

    static protected ActionConfig getActionConfig(Map<String, Object> valueMap) throws Exception {

        String namePart = (String)valueMap.get(ActionEnv.Key_ActionName);
        String namespace = (String)valueMap.get(ActionEnv.Key_Namespace);
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        return webConfigManager.getActionConfig(namePart, namespace, true);
    }

    static protected Map<String, Object> createEnvironment(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        String namePart = URLUtil.getFileNamePart(request.getServletPath());
        String namespace = TXWebUtil.getNamespace(request.getServletPath());
        if (!StringUtil.hasLength(namespace)) {
            namespace = TXWeb.global;
        }
        if (!StringUtil.hasLength(namePart) || "/".equals(namePart)) {
            namePart = "index";
        }
        ////////////////////action begin

        //////////////////////////////////环境参数 begin
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put(ActionEnv.Key_ActionName, namePart);
        envParams.put(ActionEnv.Key_Namespace, namespace);
        envParams.put(ActionEnv.Key_RealPath, Dispatcher.getRealPath());
        envParams.put(ActionEnv.Key_Request, request);
        envParams.put(ActionEnv.Key_Response, response);
        ///////////////////////////////////环境参数 end
        return envParams;
    }

}

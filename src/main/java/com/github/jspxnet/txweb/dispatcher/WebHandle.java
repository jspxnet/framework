package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.sioc.Sioc;
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
import java.io.Serializable;
import java.util.Map;

/**
 *
 * author ChenYuan
 * date 2017/6/17
 * @author Administrator
 */
@Slf4j
public abstract class WebHandle implements Serializable {
    final public static int REQUEST_MAX_LENGTH = 5242880*2; //最大json请求不超过10M
    abstract public void doing(HttpServletRequest request, HttpServletResponse response) throws Exception;

    static protected ActionConfig getActionConfig(HttpServletRequest request) throws Exception {
        String namePart = URLUtil.getFileNamePart(request.getRequestURI());
        String namespace = URLUtil.getNamespace(request.getRequestURI());
        if (!StringUtil.hasLength(namespace)) {
            namespace = TXWeb.global;
        }
        if (!StringUtil.hasLength(namePart) || StringUtil.BACKSLASH.equals(namePart)) {
            namePart = "index";
        }
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        return webConfigManager.getActionConfig(namePart, namespace, true);
    }

    static public Map<String, Object> createEnvironment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String namePart = URLUtil.getFileNamePart(request.getRequestURI());
        String namespace = URLUtil.getNamespace(request.getRequestURI());
        if (!StringUtil.hasLength(namespace)) {
            namespace = TXWeb.global;
        }

        if (!StringUtil.hasLength(namePart) || StringUtil.BACKSLASH.equals(namePart)) {
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

    static public Map<String, Object> createRocEnvironment(ActionConfig actionConfig, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> envParams = createEnvironment( request,  response);
        //命名空间初始化begin
        String namespace = actionConfig.getNamespace();
        if (!Sioc.global.equals(namespace) && !StringUtil.isEmpty(namespace))
        {
            envParams.put(ActionEnv.Key_Namespace, namespace);
        }
        String  namePart =  StringUtil.substringAfter(StringUtil.toLowerCase(request.getRequestURI()),namespace);

        if (namePart!=null&& namePart.contains(StringUtil.DOT))
        {
            namePart = namePart.substring(0, namePart.lastIndexOf(StringUtil.DOT));
        }
        if (namePart!=null&&namePart.startsWith(StringUtil.BACKSLASH))
        {
            namePart = namePart.substring(1);
        }
        if (StringUtil.isEmpty(namePart))
        {
            namePart = "index";
        }
        if (!StringUtil.isEmpty(namePart))
        {
            envParams.put(ActionEnv.Key_ActionName, namePart);
        }
        //命名空间初始化end
        return envParams;
    }

}

package com.github.jspxnet.txweb.dispatcher.service;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.IService;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.result.WebServiceResult;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 封装 ActionInvocation 提供外部调用
 */
public class RocService extends IService {
    final public static String NAME = "roc";


    @Override
    public String doing(HttpServletRequest request, HttpServletResponse response, String call) throws Exception {
        ///////////////////读取ajax请求 end
        if (StringUtil.isNull(call)) {
            JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid params.参数无效,无效的请求"));
            return errorResultJson.toString(4);
        }
        //////////////////初始begin
        if (call.length() > REQUEST_MAX_LENGTH) {
            JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid params.参数无效,长度超出范围"));
            return errorResultJson.toString(4);
        }
        return callAction(request, response, call, false);
    }

    String callAction(HttpServletRequest request, HttpServletResponse response, String call, boolean secret) throws Exception {
        //判断是XML还是JSON begin

        String rpc = StringUtil.trim(call);
        JSONObject jsonData = null;
        boolean jsonFormat = true;
        if (StringUtil.isXml(rpc)) {
            //XML格式 invalid_parameters
            try {
                jsonData = XML.toJSONObject(rpc);
            } catch (JSONException e) {
                JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid params.参数无效,无效的请求"));
                return "<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + XMLUtil.format(XML.toString(errorResultJson, Environment.rocResult));
            }
            jsonFormat = false;

        } else if (StringUtil.isJsonObject(rpc)) {
            //JSON格式
            try {
                jsonData = new JSONObject(rpc);
            } catch (JSONException e) {
                return new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid params.参数无效,无效的请求")).toString(4);
            }
        }

        if (jsonData == null) {
            return new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid params.参数无效,不能识别的格式")).toString(4);
        }
        //判断是XML还是JSON end


        JSONObject methodCall = jsonData.getJSONObject(Environment.rocMethodCall);
        //兼容格式调整
        if (methodCall != null && jsonData.containsKey(Environment.rocMethodCall)) {
            jsonData = methodCall;
        }

        //////////////////初始end

        String namespace;
        String namePart = jsonData.getString(Environment.rocId);
        if (StringUtil.isNull(namePart)) {
            namePart = URLUtil.getFileNamePart(request.getRequestURI());
        }
        if (namePart != null && namePart.contains(TXWebUtil.AT)) {
            namespace = StringUtil.substringAfter(namePart, TXWebUtil.AT);
            namePart = StringUtil.substringBefore(namePart, TXWebUtil.AT);
        } else {
            namespace = jsonData.getString(Environment.namespace);
        }
        if (namespace == null) {
            namespace = TXWebUtil.getNamespace(request.getServletPath());
        }
        ///////////////////////////////////环境参数 begin
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put(ActionEnv.Key_RealPath, Dispatcher.getRealPath());
        envParams.put(ActionEnv.Key_Request, request);
        envParams.put(ActionEnv.Key_Response, response);
        ///////////////////////////////////环境参数 end

        envParams.put(ActionEnv.Key_ActionName, namePart);
        envParams.put(ActionEnv.Key_Namespace, namespace);

        ActionConfig actionConfig = webConfigManager.getActionConfig(namePart, namespace, true);
        if (actionConfig == null) {
            envParams.clear();
            return new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "class not found.找不到执行对象")).toString(4);
        }

        if (secret != actionConfig.isSecret()) {
            return new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "forbidden not secret request roc.禁止非加密方式调用")).toString(4);
            //加密调用这里返回
        }
        jsonData.put(Environment.rocFormat, jsonFormat ? "json" : "xml");
        //在高并发下，ajax请求会出现异常，必须使用synchronized

        //执行action返回数据begin
        ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, envParams, NAME, jsonData, request, response);
        actionInvocation.initAction();
        actionInvocation.invoke();
        WebServiceResult serviceResult = new WebServiceResult();
        actionInvocation.executeResult(serviceResult);
        return serviceResult.getResult();
        //执行action返回数据end
    }
}

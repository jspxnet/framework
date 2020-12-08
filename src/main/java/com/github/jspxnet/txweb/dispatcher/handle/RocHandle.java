package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.result.RocResult;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ChenYuan on 2017/6/17.
 * RPC ,ROC 调用,进入这里表示就是一个ROC调用了
 */
@Slf4j
public class RocHandle extends ActionHandle {
    final public static String NAME = "roc";
    final public static String HTTP_HEAND_NAME = "application/json";
    final public static String DATA_FIELD = "dataField";

    final private static Lock LOCK = new ReentrantLock();
    static String getRequestReader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String callStr = RequestUtil.getReader(request);
        //////////////////初始begin
        if (callStr.length() > REQUEST_MAX_LENGTH) {
            TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "Invalid params.参数无效,长度超出范围")).toString(4), WebOutEnumType.JSON.getValue(), response);
            return null;
        }
        return callStr;
    }

    @Override
    public void doing(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String callStr = getRequestReader(request, response);
        callAction(request, response, callStr, false);
    }

    static void callAction(HttpServletRequest request, HttpServletResponse response, String call, boolean secret) throws Exception {

        //判断是XML还是JSON begin
        String rpc = StringUtil.trim(call);
        JSONObject jsonData = null;
        boolean jsonFormat = true;

        if (StringUtil.isXml(rpc)) {
            //XML格式
            try {
                jsonData = XML.toJSONObject(rpc);
            } catch (JSONException e) {
                log.error("xml的ROC请求数据错误", e);
                JSONObject errorResultJson = new JSONObject(RocResponse.error(-32600, "xml的ROC请求数据错误"));
                TXWebUtil.print("<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + XMLUtil.format(XML.toString(errorResultJson, Environment.rocResult)), WebOutEnumType.XML.getValue(), response);
            }
            jsonFormat = false;
        }
        if (StringUtil.isJsonObject(rpc)) {
            //JSON格式
            try {
                jsonData = new JSONObject(rpc);
            } catch (JSONException e) {
                log.error("json的ROC请求错误", e);
                e.printStackTrace();
                TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "json的ROC请求错误")).toString(4), WebOutEnumType.JSON.getValue(), response);
                return;
            }
        }

        //为了兼用 api restFull 方式，这里允许为空,默认构造配置
        if (jsonData == null) {
            jsonData = new JSONObject();
            JSONObject methodJson = new JSONObject();
            methodJson.put(Environment.rocName, URLUtil.getFileNamePart(request.getRequestURI()));
            jsonData.put(Environment.rocMethod, methodJson);
            jsonData.put(Environment.rocFormat, WebOutEnumType.JSON.getName());
        }

        JSONObject methodJson = jsonData.getJSONObject(Environment.rocMethod);
        if (methodJson == null) {
            methodJson = new JSONObject();
            methodJson.put(Environment.rocName, URLUtil.getFileNamePart(request.getRequestURI()));
            jsonData.put(Environment.rocMethod, methodJson);
        }
        //判断是XML还是JSON end

        JSONObject methodCall = jsonData.getJSONObject(Environment.rocMethodCall);
        //兼容格式调整
        if (methodCall != null) {
            jsonData = methodCall;
        }
        //////////////////初始end

        String namespace = TXWebUtil.getNamespace(request.getServletPath());
        String  namePart = jsonData.getString(Environment.rocId);
        if (StringUtil.isNull(namePart)) {
            namePart = URLUtil.getFileNamePart(request.getRequestURI());
        }
        if (namePart != null && namePart.contains(TXWebUtil.AT)) {
            namePart = StringUtil.substringBefore(namePart, TXWebUtil.AT);
            namespace = StringUtil.substringAfter(namePart, TXWebUtil.AT);
        }
        ///////////////////////////////////环境参数 begin


        WebConfigManager webConfigManager = TXWebConfigManager.getInstance();
        ActionConfig actionConfig = webConfigManager.getActionConfig(namePart, namespace, true);
        if (actionConfig == null) {
            TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "class not found.找不到执行对象")).toString(4), WebOutEnumType.JSON.getValue(), response);
            return;
        }

        if (actionConfig.isSecret() && !secret) {
            TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "forbidden not secret request roc.禁止非加密方式调用")).toString(4), WebOutEnumType.JSON.getValue(), response);
            return;
            //加密调用这里返回
        }

        if (!jsonData.containsKey(Environment.rocFormat)) {
            jsonData.put(Environment.rocFormat, jsonFormat ? "json" : "xml");
        }
        //在高并发下，ajax请求会出现异常，必须使用synchronized response 存在线程安全问题
        //执行action返回数据begin  多例模式下为必须

        executeActionInvocation( actionConfig,  jsonData, request, response);
    }


    static public void executeActionInvocation(ActionConfig actionConfig, JSONObject jsonData,HttpServletRequest request,HttpServletResponse response) throws Exception
    {

        JSONObject dataField = jsonData.getJSONObject(DATA_FIELD);
        Map<String, Object> envParams = createEnvironment(request, response);
        LOCK.lock();
        try {
            ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, envParams, NAME, jsonData, request, response);
            actionInvocation.initAction();
            if (ActionSupport.NONE.equalsIgnoreCase(actionInvocation.invoke()))
            {
                return;
            }
            /*if (response.isCommitted())
            {
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement stackTraceElement:Thread.currentThread().getStackTrace())
                {
                    sb.append(stackTraceElement.getLineNumber()).append(StringUtil.COLON).append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(StringUtil.CRLF);
                }
                log.error("response 已经提交并且关闭,调用方法:{}",sb.toString());
                return;
            }*/
            actionInvocation.executeResult(new RocResult(dataField));
        } finally {
            LOCK.unlock();
        }
        //执行action返回数据end
    }

    static public void execute(ActionProxy actionProxy) throws Exception
    {
        ActionSupport action = actionProxy.getAction();
        //ROC 普通调用
        Object result = TXWebUtil.invokeJson(actionProxy);
        if (action != null && action.getResult()==null && result != null) {
            action.setResult(result);
        }
    }
}

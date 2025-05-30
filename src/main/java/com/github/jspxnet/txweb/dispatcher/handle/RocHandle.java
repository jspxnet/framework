package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.WebHandle;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.result.RocResult;
import com.github.jspxnet.txweb.util.ParamUtil;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by ChenYuan on 2017/6/17.
 * RPC ,ROC 调用,进入这里表示就是一个ROC调用了
 */
@Slf4j
public class RocHandle extends WebHandle {
    final public static String NAME = "roc";
    final public static String HTTP_HEAND_NAME = "application/json";
    final public static String DATA_FIELD = "dataField";


    static String getRequestReader(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String callStr = RequestUtil.getReader(request);
        //////////////////初始begin
        if (callStr!=null&&callStr.length() > REQUEST_MAX_LENGTH) {
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
        if (StringUtil.isXml(rpc)) {
            //XML格式
            try {
                jsonData = XML.toJSONObject(rpc);
            } catch (JSONException e) {
                log.error("xml的ROC请求数据错误", e);
                JSONObject errorResultJson = new JSONObject(RocResponse.error(-32600, "xml的ROC请求数据错误"));
                TXWebUtil.print("<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + XMLUtil.format(XML.toString(errorResultJson, Environment.rocResult)), WebOutEnumType.XML.getValue(), response);
            }
        }
        if (!StringUtil.isNull(rpc)&&StringUtil.isJsonObject(rpc)) {
            //JSON格式
            try {
                jsonData = new JSONObject(rpc);
            } catch (JSONException e) {
                log.error("json的ROC请求错误", e);
                e.printStackTrace();
                TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "json的ROC请求错误")).toString(4), WebOutEnumType.JSON.getValue(), response);
                return;
            }
        } /*else if (!StringUtil.isNull(rpc)&&rpc.contains("="))
        {
            //把www_form 格式转换为json
            Map<String, String[]> queryParameters = HttpUtil.parseQueryString(rpc);

            jsonData = new JSONObject();
            for (String varName:queryParameters.keySet())
            {
                String[] values = queryParameters.get(varName);
                if (ObjectUtil.isEmpty(values))
                {
                    jsonData.put(varName,StringUtil.empty);
                } else if (values.length==1)
                {
                    jsonData.put(varName,values[0]);
                } else
                {
                    jsonData.put(varName,values);
                }
            }
        }*/

        //为了兼用 api restFull 方式，这里允许为空,默认构造配置
/*        if (jsonData == null) {
            jsonData = new JSONObject();
            JSONObject methodJson = new JSONObject();
            jsonData.put(Environment.rocMethod, methodJson);
            jsonData.put(Environment.rocFormat, WebOutEnumType.JSON.getName());
        }*/
        /*
        {
     "version": "3.0",  //版本,不是必须
     "protocol": "jspx.net-roc",  //协议说明
     "format": "json",  //返回的格式要求,可以是xml，默认位json
      "method": {
        "name": "调用的方法",    //调用的方法名称
        "params": {'参数名称1':'参数值1','参数名称2':'参数值2',...}       //方法参数
       }

    },
    "params": {                //类对象参数就是类的set方法,文档中叫全局参数
        "参数1": 1,
        "参数2": 2
    }
    }
         */

        ///////////////////////////////////环境参数 begin
        ActionConfig actionConfig = getActionConfig(request);
        if (actionConfig == null) {
            TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "class not found.找不到执行对象")).toString(4), WebOutEnumType.JSON.getValue(), response);
            return;
        }

        if (actionConfig.isSecret() && !secret) {
            TXWebUtil.print(new JSONObject(RocResponse.error(-32600, "forbidden not secret request roc.禁止非加密方式调用")).toString(4), WebOutEnumType.JSON.getValue(), response);
            return;
            //加密调用这里返回
        }

        //在高并发下，ajax请求会出现异常，必须使用synchronized response 存在线程安全问题
        //执行action返回数据begin  多例模式下为必须
        jsonData = ParamUtil.getRequestStdJson(jsonData);
        executeActionInvocation( actionConfig, jsonData, request, response);
    }


    static public void executeActionInvocation(ActionConfig actionConfig, JSONObject jsonData,HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        JSONObject dataField = jsonData.getJSONObject(DATA_FIELD);
        Map<String, Object> envParams = createRocEnvironment(actionConfig,request, response);
        ActionInvocation actionInvocation = null;
        try {
            actionInvocation = new DefaultActionInvocation(actionConfig, envParams, NAME, jsonData, request, response);
            actionInvocation.initAction();
            actionInvocation.invoke();
        } finally {
            if (actionInvocation!=null)
            {
                actionInvocation.executeResult(new RocResult(dataField));
            }
        }
    }

    static public void execute(Action action,ActionContext actionContext) throws Exception {
        actionContext.setResult(TXWebUtil.invokeJson(action,actionContext,actionContext.getMethod()));
    }
}

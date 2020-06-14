package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by chenyuan on 2015-7-10.
 * 这里只是一个适配器，真是执行是 getRocAutoResult  方法
 */

public class WebServiceResult extends ResultSupport {
    private JSONObject json = new JSONObject();
    private boolean outXml = false;

    public String getResult() {
        //如果头部设置为javascript mootools IE下会自动执行
        if (outXml) {
            try {
                return "<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + (debug ? XMLUtil.format(XML.toString(json, Environment.rocResult)) : XML.toString(json, Environment.rocResult));
            } catch (JSONException e) {
                return new JSONObject(RocResponse.error(-32700, " xml 格式解析异常")).toString(4);
            }
        } else {
            return debug ? json.toString(4) : json.toString();
        }
    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws JSONException {
        Object methodResult = getRocAutoResult(actionInvocation);
        json = getResultJson(methodResult);
        JSONObject callJson = actionInvocation.getActionProxy().getCallJson();
        if (callJson != null && "xml".equalsIgnoreCase(callJson.getString(Environment.rocFormat))) {
            outXml = true;
        }
    }
}
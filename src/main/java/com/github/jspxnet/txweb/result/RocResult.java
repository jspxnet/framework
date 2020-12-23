/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.json.XMLParserConfiguration;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-6-30
 * Time: 12:25:17
 * 判断是否使用 Key_resultMethods  配置的格式返回json,还是更具rpc调用方式
 * com.github.jspxnet.txweb.result.RocResult
 * <p>
 * 信息显示默认方法
 * if (msg.success==1)
 * {
 * alert("成功");
 * } else
 * {
 * alert(msg.error.message);
 * }
 */
@Slf4j
public class RocResult extends ResultSupport {
    /**
     * 输出要显示的字段
     */
    private JSONObject dataField = null;

    public RocResult(JSONObject dataField) {
        this.dataField = dataField;
    }

    public RocResult() {

    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();
        if (response.isCommitted()) {

            StringBuilder sb = new StringBuilder();
            for (StackTraceElement stackTraceElement:Thread.currentThread().getStackTrace())
            {
                sb.append(stackTraceElement.getLineNumber()).append(StringUtil.COLON).append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(StringUtil.CRLF);
            }
            log.error("response 已经提交并且关闭,调用方法:{}",sb.toString());
            return;
        }

        checkCache(action, response);
        Object methodResult = getRocAutoResult(actionInvocation);
        JSONObject json;
        if (methodResult == null) {
            json = new JSONObject();
        } else if (methodResult instanceof JSONObject) {
            json = (JSONObject) methodResult;
        } else if (methodResult instanceof RocResponse) {
            json = new JSONObject(methodResult,dataField);
        } else if (methodResult instanceof RocException) {
            json = new JSONObject(((RocException)methodResult).getResponse(),dataField);
        } else if (methodResult instanceof Exception) {
            json = new JSONObject(RocResponse.error(ErrorEnumType.WARN.getValue(),ThrowableUtil.getThrowableMessage((Exception)methodResult)));
        }
        else {
            json = new JSONObject(RocResponse.success(methodResult),dataField);
        }
        //如果头部设置为javascript mootools IE下会自动执行
        JSONObject callJson = actionInvocation.getActionProxy().getCallJson();
        if (callJson != null && KEY_RocFormatXML.equalsIgnoreCase(callJson.getString(Environment.rocFormat))) {
            String result = XML.toString(json, Environment.rocResult, new XMLParserConfiguration());
            if (debug) {
                result = XMLUtil.format(result);
            }
            TXWebUtil.print("<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + result,WebOutEnumType.XML.getValue(), response);
        } else {
            String result = debug ? json.toString(4) : json.toString();
            if (1!=json.getInt(ActionSupport.SUCCESS))
            {
                if (json.containsKey("code")&&ErrorEnumType.NEED_LOGIN.getValue()==json.getInt("code"))
                {
                    TXWebUtil.print(json, WebOutEnumType.JSON.getValue(), response, HttpStatusType.HTTP_status_401);
                } else
                if (json.containsKey("code")&&ErrorEnumType.POWER.getValue()==json.getInt("code"))
                {
                    TXWebUtil.print(json, WebOutEnumType.JSON.getValue(), response, HttpStatusType.HTTP_status_403);
                } else
                {
                    TXWebUtil.print(json, WebOutEnumType.JSON.getValue(), response, HttpStatusType.HTTP_status_OK);
                }
            } else
            {
                TXWebUtil.print(result, WebOutEnumType.JSON.getValue(), response);
            }

        }
        json.clear();
    }


}
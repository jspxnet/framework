package com.github.jspxnet.txweb.dispatcher.service;

import com.github.jspxnet.boot.environment.Environment;

import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.handle.RsaRocHandle;
import com.github.jspxnet.txweb.result.RocResponse;

import com.github.jspxnet.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主要负责rsa解密工作
 */
public class RsaRocService extends RocService {

    final public static String name = "rsaroc";


    @Override
    public String doing(HttpServletRequest request, HttpServletResponse response, String call) throws Exception {
        ///////////////////读取ajax请求 end
        if (StringUtil.isNull(call)||call.length() > REQUEST_MAX_LENGTH) {
            JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.FORMAT.getValue(), "Invalid params.参数无效,无效的请求"));
            return errorResultJson.toString(4);
        }


        String rpc = StringUtil.trim(call);
        JSONObject jsonData = null;
        if (StringUtil.isXml(rpc)) {
            //XML格式
            try {
                jsonData = XML.toJSONObject(rpc);
            } catch (JSONException e) {
                JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.FORMAT.getValue(), "Invalid Request.无效的请求"));
                return "<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + XMLUtil.format(XML.toString(errorResultJson, Environment.rocResult));
            }
        }
        if (StringUtil.isJsonObject(rpc)) {
            //JSON格式
            try {
                jsonData = new JSONObject(rpc);
            } catch (JSONException e) {
                JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.FORMAT.getValue(), "Invalid Request.无效的请求"));
                return errorResultJson.toString(4);
            }
        }

        if (jsonData == null) {
            JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.FORMAT.getValue(), "Invalid Request.无效的请求"));
            return errorResultJson.toString(4);
        }

        String secretData = RsaRocHandle.getSecretDecode(jsonData);
        return callAction(request, response, secretData, true);
    }
}

package com.github.jspxnet.txweb.ueditor.define;

import com.github.jspxnet.json.JSONObject;

/**
 * 处理状态接口
 *
 * @author hancong03@baidu.com
 */
public interface State {

    boolean isSuccess();

    void putInfo(String name, String val);

    void putInfo(String name, long val);

    JSONObject toJson();

    String toJsonString();

}

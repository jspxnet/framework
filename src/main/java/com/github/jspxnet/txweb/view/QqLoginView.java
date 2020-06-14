package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.qq.connect.QQConnectException;
import com.qq.connect.oauth.Oauth;

/**
 * 实用QQ登陆的时候就点击这个页面
 */
@HttpMethod(caption = "QQ登陆入口")
public class QqLoginView extends ActionSupport {

    @Override
    public String execute() throws Exception {
        if (!config.getBoolean(Environment.useQQLogin)) {
            printError("后台系统已经关闭QQ一键登录", 500);
            return NONE;
        }
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
        return NONE;
    }
}

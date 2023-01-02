/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 13-11-20
 * Time: 上午9:41
 */
@HttpMethod(caption = "页面安全跳转")
public class JumpFilterView extends ActionSupport {
    public JumpFilterView() {

    }

    //1:表示本页代理处理跳转
    private int promptly = 0;

    //安全判断表达式
    private String[] expressions = ArrayUtil.EMPTY_STRING_ARRAY;

    private String url = StringUtil.empty;

    public int getPromptly() {
        return promptly;
    }

    public void setPromptly(int promptly) {
        this.promptly = promptly;
    }


    public String[] getExpressions() {
        return expressions;
    }

    @Param(request = false)
    public void setExpressions(String[] expressions) {
        this.expressions = expressions;
    }

    public boolean isSafe() {
        if (StringUtil.isNull(url)) {
            return false;
        }
        for (String exp : expressions) {
            if (url.matches(exp)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String execute() throws Exception {
        if (promptly == 1 && isSafe()) {
            getResponse().sendRedirect(url);
        }
        return NONE;
    }
}
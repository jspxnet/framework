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

import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.support.ActionSupport;
import lombok.extern.slf4j.Slf4j;


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
public class RpcResult extends ResultSupport {
    private Object result;
    public RpcResult() {

    }

    public Object getResult()
    {
        return result;
    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (ActionSupport.NONE.equalsIgnoreCase(actionContext.getActionResult()))
        {
            return;
        }
        //返回类型必须一致
        result = getRocAutoResult(actionInvocation);
    }

}
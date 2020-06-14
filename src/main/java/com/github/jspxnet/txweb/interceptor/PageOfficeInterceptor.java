/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.support.ActionSupport;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 13-11-7
 * Time: 下午3:14
 * 拦截放入PageOffice 控件
 */
public class PageOfficeInterceptor extends InterceptorSupport {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    private String serverPage = "/poserver.do";

    public String getServerPage() {
        return serverPage;
    }

    public void setServerPage(String serverPage) {
        this.serverPage = serverPage;
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        ActionSupport action = actionInvocation.getActionProxy().getAction();

        /*
        PageOfficeCtrl pageOfficeCtrl =  new com.zhuozhengsoft.pageoffice.PageOfficeCtrl(action.getRequest());
        pageOfficeCtrl.setServerPage(serverPage);
        action.put("pageOfficeCtrl",pageOfficeCtrl);

        //放入开启模式begin
        for (OpenModeType openModeType:OpenModeType.values())
        {
            action.put(openModeType.toString(),openModeType);
        }
        //放入开启模式end

        //放入边样式begin
        for (BorderStyleType borderStyleType:BorderStyleType.values())
        {
            action.put(borderStyleType.toString(),borderStyleType);
        }
        //放入边样式end

        action.put("wordWriter",new com.zhuozhengsoft.pageoffice.wordwriter.WordDocument());
        action.put("numberColor",new Color(200,100,100));
        action.put("editColor",new Color(80, 80, 80));
        action.put("editBgColor",new Color(241,197,141));
        action.put("dateBgColor",new Color(255, 175, 175));
        action.put("selectBgColor",new Color(238, 215, 125));
*/
        return actionInvocation.invoke();
    }
}
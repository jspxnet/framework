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
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.utils.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-3-29
 * Time: 15:17:25
 * 返回 FusionCharts 报表图形XML数据,FusionCharts是一个收费项目，价格不菲，不过很好用
 */
public class FusionChartsXmlResult extends ResultSupport {
    private static final byte[] BOM_DATA = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public FusionChartsXmlResult() {

    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws IOException, ServletException {
        Action action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();
        Object o = action.getResult();
        if (o == null) {
            return;
        }
        String browserCache = action.getEnv(ActionEnv.BROWSER_CACHE);
        boolean noCache = (!StringUtil.isNull(browserCache) && ("false".equalsIgnoreCase(browserCache) || "0".equals(browserCache)));
        if (noCache) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            response.setDateHeader("Expires", 0);
        }
        response.setContentType("text/xml; charset=UTF-8");
        response.setCharacterEncoding(Environment.defaultEncode);
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(BOM_DATA);
        Writer out = new OutputStreamWriter(outputStream, Environment.defaultEncode);
        if (o instanceof String) {
            out.write((String) o);
        } else {
            out.write(o.toString());
        }
        out.flush();
        out.close();
        action.setResult(null);

    }

}
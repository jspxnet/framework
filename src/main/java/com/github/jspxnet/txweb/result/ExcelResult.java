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
import com.github.jspxnet.scriptmark.core.script.ScriptTypeConverter;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ChenYuan
 * date: 12-4-8
 * Time: 上午12:00
 * To change this template use File | Settings | File Templates.
 * Excel 格式返回
 */
public class ExcelResult extends ResultSupport {

    static private String getExcelCaption(Object o) {
        StringBuilder sb = new StringBuilder();
        List<SoberColumn> soberColumn = AnnotationUtil.getColumnList(o.getClass());
        for (SoberColumn column : soberColumn) {
            if (column.isHidden()) {
                continue;
            }
            sb.append(column.getCaption()).append(",");
        }
        if (sb.toString().endsWith(","))
        {
            sb.setLength(sb.length()-1);
        }
        sb.append(StringUtil.CRLF);
        return sb.toString();
    }

    static private String getExcelString(Object o) {
        StringBuilder sb = new StringBuilder();
        List<SoberColumn> soberColumn = AnnotationUtil.getColumnList(o.getClass());
        for (SoberColumn column : soberColumn) {
            if (column.isHidden()) {
                continue;
            }
            Object putValue = BeanUtil.getProperty(o, column.getName());
            String value = StringUtil.csvString(ScriptTypeConverter.toString(putValue));
            sb.append(value).append(",");
        }
        if (sb.toString().endsWith(","))
        {
            sb.setLength(sb.length()-1);
        }
        sb.append(StringUtil.CRLF);
        return sb.toString();
    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception
    {
        Action action = actionInvocation.getActionProxy().getAction();
        action.setActionResult(ActionSupport.NONE);
        HttpServletResponse response = action.getResponse();
        String browserCache = action.getEnv(ActionEnv.BROWSER_CACHE);
        if (!StringUtil.isNull(browserCache) && !StringUtil.toBoolean(browserCache)) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            response.setDateHeader("Expires", 0);
        }
        Object obj = action.getResult();
        if (obj == null) {
            TXWebUtil.errorPrint("无数据",null,response, HttpStatusType.HTTP_status_404);
            return;
        }

        String encode = Environment.defaultEncode;
        if (RequestUtil.isIeBrowser(action.getRequest()))
        {
            encode = "GBK";
        }


        String name = DateUtil.getDateST();
        //如果头部设置为javascript mootools IE下会自动执行
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(name, encode) + ".csv");// 设定输出文件头
        response.setContentType("application/ms-excel");// 定义输出类型
        response.setCharacterEncoding(encode);
        PrintWriter write = response.getWriter();
        if (obj instanceof Collection) {
            Collection<Object> cols = (Collection) obj;
            int i = 0;
            for (Object o : cols) {
                if (i == 0) {
                    write.write(getExcelCaption(o));
                }
                write.write(getExcelString(o));
                i++;
            }
        } else {
            write.write(getExcelCaption(obj));
            write.write(getExcelString(obj));
        }
        write.flush();
        write.close();
    }
}
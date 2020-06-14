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

import com.github.jspxnet.boot.sign.HttpStatusType;
import com.itextpdf.text.pdf.BaseFont;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.HtmlUtil;
import org.slf4j.Logger;
import com.github.jspxnet.boot.environment.Environment;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-13
 * Time: 上午10:18
 * 将网页转换为PDF输出
 * <p>
 * 中文问题，样式表必须有
 * <p>
 * body{
 * font-family: SimSun;
 * }
 * <p>
 * //并且加载字体
 * fontResolver.addFont("D:\\website\\webapps\\root\\WEB-INF\\fonts\\simsun.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
 */
public class HtmlPdfResult extends ResultSupport {
    private static final Logger log = LoggerFactory.getLogger(HtmlPdfResult.class);
    private final static TemplateConfigurable configurable = new TemplateConfigurable();
    private static final String defaultEncode = envTemplate.getString(Environment.encode, Environment.defaultEncode);
    private static String templatePath = envTemplate.getString(Environment.templatePath);
    private static String fontsPath = envTemplate.getString(Environment.fontsPath);

    static {
        configurable.addAutoIncludes(envTemplate.getString(Environment.autoIncludes));
    }


    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();

        //浏览器缓存控制begin
        checkCache(action, response);
        //浏览器缓存控制end

        File f = new File(action.getTemplatePath(), action.getTemplateFile());
        FileSource fileSource = new FileSource(f, action.getTemplateFile(), defaultEncode);
        //如果使用cache 就使用uri

        String cacheKey = EncryptUtil.getMd5(f.getAbsolutePath()); //为了防止特殊符号错误，转换为md5 格式
        configurable.setSearchPath(new String[]{action.getTemplatePath(), Dispatcher.getRealPath(), templatePath});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(cacheKey, fileSource, configurable);
        } catch (Exception e) {
            if (debug) {
                log.debug("file not found:" + f.getAbsolutePath(), e);
                TXWebUtil.errorPrint("file not found:" + f.getAbsolutePath() + "\r\n" + e.getMessage(), null,response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("file not found,不存在的文件", null,response, HttpStatusType.HTTP_status_404);
            }
            return;
        }
        scriptMark.setRootDirectory(Dispatcher.getRealPath());
        scriptMark.setCurrentPath(action.getTemplatePath());

        //输出模板数据
        Writer out = new StringWriter();
        Map<String, Object> valueMap = action.getEnv();
        initPageEnvironment(action, valueMap);
        scriptMark.process(out, valueMap);
        valueMap.clear();
        out.flush();
        out.close();

        //请求编码begin
        response.setCharacterEncoding(Dispatcher.getEncode());
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(cacheKey + ".pdf", "UTF-8"));// 设定输出文件头
        //请求编码end

        ServletOutputStream outputStream = response.getOutputStream();
        try {
            ITextRenderer renderer = new ITextRenderer();
            // 解决中文支持问题
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(fontsPath + "simsun.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            //解决图片的相对路径问题
            renderer.getSharedContext().setBaseURL(new URL(action.getTemplatePath()).toString());
            renderer.setDocumentFromString(HtmlUtil.getSafeFilter(out.toString()));
            renderer.layout();
            renderer.createPDF(response.getOutputStream());
        } catch (Exception e) {
            if (debug) {
                log.debug("pdf create out", e);
                TXWebUtil.errorPrint(StringUtil.toBrLine(e.getMessage()),null, response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("PDF输出失败",null, response, HttpStatusType.HTTP_status_404);
            }
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }

        }
    }
}
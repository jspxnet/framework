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
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.util.TXWebUtil;

import lombok.extern.slf4j.Slf4j;

import com.github.jspxnet.boot.environment.Environment;

import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.utils.StringUtil;
import org.w3c.dom.Document;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xml.sax.InputSource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-11-13
 * Time: 上午12:29
 * 将网页转换为图片输出
 */
@Slf4j
public class HtmlImgResult extends ResultSupport {
    private final static TemplateConfigurable CONFIGURABLE = new TemplateConfigurable();


    static {
        CONFIGURABLE.addAutoIncludes(ENV_TEMPLATE.getString(Environment.autoIncludes));
    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {

        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletResponse response = actionContext.getResponse();

        Action action = actionInvocation.getActionProxy().getAction();
        //HttpServletResponse response = action.getResponse();

        //浏览器缓存控制begin
        checkCache(actionContext);
        //浏览器缓存控制end

        File f = new File(action.getTemplatePath(), action.getTemplateFile());
        FileSource fileSource = new FileSource(f, action.getTemplateFile(), Dispatcher.getEncode());
        //如果使用cache 就使用uri

        String cacheKey = EncryptUtil.getMd5(f.getAbsolutePath()); //为了防止特殊符号错误，转换为md5 格式
        String templatePath = ENV_TEMPLATE.getString(Environment.templatePath);
        CONFIGURABLE.setSearchPath(new String[]{action.getTemplatePath(), Dispatcher.getRealPath(), templatePath});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(cacheKey, fileSource, CONFIGURABLE);
        } catch (Exception e) {
            if (DEBUG) {
                log.debug("file not found:" + f.getAbsolutePath(), e);
                TXWebUtil.errorPrint("file not found:" + f.getAbsolutePath() + "," + e.getMessage(),null, response, HttpStatusType.HTTP_status_404);
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

        String imgType = "png";
        //请求编码begin
        String contentType = action.getEnv(ActionEnv.CONTENT_TYPE);
        if (!StringUtil.isNull(contentType)) {
            response.setContentType(contentType);
            if (contentType.contains("jpg")) {
                imgType = "jpg";
            }
            if (contentType.contains("gif")) {
                imgType = "gif";
            }
        } else {
            response.setContentType("image/x-png");
            response.setCharacterEncoding(Dispatcher.getEncode());
        }
        //请求编码end


        int width = StringUtil.toInt(action.getEnv("width"));
        if (width <= 0) {
            width = action.getInt("width", 760);
        }
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            Graphics2DRenderer g2r = new Graphics2DRenderer();
            InputSource is = new InputSource(new BufferedReader(new StringReader(out.toString())));
            Document dom =  XMLResource.load(is).getDocument();
            g2r.setDocument(dom, action.getTemplatePath());
            Dimension dim = new Dimension(width, 1000);

            // do layout with temp buffer
            BufferedImage buff = new BufferedImage((int) dim.getWidth(), (int) dim.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) buff.getGraphics();
            g2r.layout(g, new Dimension(width, 1000));
            g.dispose();

            // get size
            Rectangle rect = g2r.getMinimumSize();
            // render into real buffer
            buff = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g = (Graphics2D) buff.getGraphics();
            g2r.render(g);
            g.dispose();

            ImageIO.write(buff, imgType, outputStream);
        } catch (Exception e) {
            if (DEBUG) {
                log.info("file not found:" + f.getAbsolutePath(), e);
                TXWebUtil.errorPrint("file not found:" + f.getAbsolutePath() + "," + StringUtil.toBrLine(e.getMessage()),null, response, HttpStatusType.HTTP_status_404);
            } else {
                TXWebUtil.errorPrint("file not found,不存在的文件", null,response, HttpStatusType.HTTP_status_404);
            }
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}
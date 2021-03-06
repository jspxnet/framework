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

import com.github.jspxnet.txweb.Action;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import com.github.jspxnet.boot.environment.Environment;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.support.ActionSupport;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;


/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-13
 * Time: 下午2:15
 * 将pdf 的一个页面转换为图片输出
 */
public class PdfPageImgResult extends ResultSupport {
    private static final Logger log = LoggerFactory.getLogger(PdfPageImgResult.class);
    private final static TemplateConfigurable configurable = new TemplateConfigurable();

    static {
        configurable.addAutoIncludes(ENV_TEMPLATE.getString(Environment.autoIncludes));
    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        Action action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();

        //浏览器缓存控制begin
        checkCache(action, response);
        //浏览器缓存控制end

        response.setContentType("image/x-png");
        response.setCharacterEncoding(Dispatcher.getEncode());

        File file = (File) action.getResult();
        if (file == null || !file.isFile()) {
            return;
        }
        int currentPage = action.getInt("currentPage", 0);
        if (currentPage < 0) {
            currentPage = 0;
        }
        ServletOutputStream outputStream = response.getOutputStream();
        InputStream is = new FileInputStream(file);
        try {
            PDDocument pdf = PDDocument.load(is);
            if (currentPage >= pdf.getNumberOfPages()) {
                currentPage = (pdf.getNumberOfPages() - 1);
            }
            PDFRenderer renderer = new PDFRenderer(pdf);
            BufferedImage rendererImg = renderer.renderImageWithDPI(currentPage, 120, ImageType.RGB);
            ImageIO.write(rendererImg, "jpg", outputStream);// 写图片
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(file.getPath(), e);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }

        }
    }
}
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
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-13
 * Time: 下午2:15
 * 将pdf 的一个页面转换为图片输出
 */
@Slf4j
public class PdfPageImgResult extends ResultSupport {


    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletResponse response = actionContext.getResponse();
        Action action = actionInvocation.getActionProxy().getAction();

        //浏览器缓存控制begin
        checkCache(actionContext);
        //浏览器缓存控制end

        response.setContentType("image/x-png");
        response.setCharacterEncoding(Dispatcher.getEncode());

        File file = (File) actionContext.getResult();
        if (file == null || !file.isFile()) {
            return;
        }
        int currentPage = action.getInt("currentPage", 0);
        if (currentPage < 0) {
            currentPage = 0;
        }
        ServletOutputStream outputStream = response.getOutputStream();

        try {
            PDDocument pdf = Loader.loadPDF(file);
            if (currentPage >= pdf.getNumberOfPages()) {
                currentPage = (pdf.getNumberOfPages() - 1);
            }
            PDFRenderer renderer = new PDFRenderer(pdf);
            BufferedImage rendererImg = renderer.renderImageWithDPI(currentPage, 120, ImageType.RGB);
            ImageIO.write(rendererImg, "jpg", outputStream);// 写图片

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
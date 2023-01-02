/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.PhotoString;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.StringUtil;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

/**
 * Created by yuan on 14-3-23.
 * 加密字符串转换到图片显示，防止拷贝
 * <p>
 * com.github.jspxnet.txweb.view.TransPhotoView
 */
@HttpMethod(caption = "加密转换到图片")
public class TransPhotoView extends ActionSupport {
    private String txt = "";
    private int width = 120;
    private int height = 20;
    private String bgColor = "#FFFFFF";
    private String fileType = "png";
    private String color = null;

    private boolean safe = true;

    public String getTxt() {
        return txt;
    }

    @Param(caption = "文本")
    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getWidth() {
        return width;
    }

    @Param(caption = "宽")
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    @Param(caption = "高")
    public void setHeight(int height) {
        this.height = height;
    }

    public String getBgColor() {
        return bgColor;
    }

    @Param(caption = "背景色")
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getColor() {
        return color;
    }

    @Param(caption = "颜色")
    public void setColor(String color) {
        this.color = color;
    }

    @Param(request = false)
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Param(request = false)
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String execute() throws Exception {
        if (StringUtil.isNull(txt)) {
            return NONE;
        }
        if (safe && RequestUtil.isPirated(getRequest())) {
            return NONE;
        }
        HttpServletResponse response = getResponse();
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        Color bgCol = new Color(Integer.parseInt(StringUtil.trim(StringUtil.replace(bgColor, "#", "")), 16));
        Color fontColor = StringUtil.isNull(color) ? null : new Color(Integer.parseInt(StringUtil.trim(StringUtil.replace(color, "#", "")), 16));
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        PhotoString validateCode = new PhotoString(width, height, bgCol, fontColor, encrypt.getDecode(txt));
        ServletOutputStream out = response.getOutputStream();
        try {
            ImageIO.write(validateCode.getBufferImage(), fileType, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return NONE;
    }

}
/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.cache.ValidateCodeCache;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.PhotoString;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

/**
 * Created by chenyuan on 14-5-7.
 * <p>
 * 表达式方式验证
 */
@HttpMethod(caption = "验证计算图片")
public class ValidateExpImgView extends ActionSupport {

    public ValidateExpImgView() {
        setActionResult(NONE);
    }

    @Ref
    private ValidateCodeCache validateCodeCache;

    // 图片的宽度。
    @Getter
    private int width = 70;
    // 图片的高度。
    @Getter
    private int height = 24;
    // 验证码干扰线数

    @Getter
    private String bgColor = "#FFFFFF";
    @Getter
    private String color = null;

    private String fileType = "png";
    private boolean safe = true;


    @Param(caption = "宽", min = 0)
    public void setWidth(int width) {
        this.width = width;
    }

    @Param(caption = "高", min = 0)
    public void setHeight(int height) {
        this.height = height;
    }

    @Param(caption = "背景色", min = 0)
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    @Param(caption = "颜色", min = 0)
    public void setColor(String color) {
        this.color = color;
    }

    @Param(caption = "安全验证")
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    @Param(caption = "图片类型")
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    @Override
    public String execute() throws Exception {
        if (safe && RequestUtil.isPirated(getRequest())) {
            return NONE;
        }
        IUserSession userSession = getUserSession();
        HttpServletResponse response = getResponse();
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/" + fileType);
        Integer A = RandomUtil.getRandomInt(1, 11);
        Integer B = RandomUtil.getRandomInt(1, 11);
        Integer C;
        String viewCode = "";
        if (A > B) {
            C = A - B;
            viewCode = NumberUtil.toString(A) + "-" + NumberUtil.toString(B) + StringUtil.EQUAL;
        } else if (A < 9 && B < 9 || A.equals(B)) {
            C = A * B;
            viewCode = NumberUtil.toString(A) + "X" + NumberUtil.toString(B) + StringUtil.EQUAL;
        } else {
            C = A + B;
            viewCode = NumberUtil.toString(A) + "+" + NumberUtil.toString(B) + StringUtil.EQUAL;
        }
        PhotoString validateCode = new PhotoString(width, height, new Color(Integer.parseInt(StringUtil.trim(StringUtil.replace(bgColor, "#", "")), 16)), StringUtil.isNull(color) ? null : new Color(Integer.parseInt(StringUtil.trim(StringUtil.replace(color, "#", "")), 16)), viewCode);
        ImageIO.write(validateCode.getBufferImage(), fileType, response.getOutputStream());

        if (userSession != null) {
            validateCodeCache.addImgCode(EncryptUtil.getMd5(userSession.getId()), NumberUtil.toString(C));
        }
        return super.execute();
    }
}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import com.github.jspxnet.cache.ValidateCodeCache;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.ValidateCode;
import lombok.Getter;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-31
 * Time: 22:56:19
 */
@HttpMethod(caption = "验证图片")
public class ValidateImgView extends ActionSupport {

    public ValidateImgView() {
        setActionResult(NONE);
    }

    @Ref
    private ValidateCodeCache validateCodeCache;

    // 图片的宽度。
    @Getter
    private int width = 76;
    // 图片的高度。
    @Getter
    private int height = 25;
    // 验证码干扰线数
    private int lineCount = 20;

    @Getter
    private int length = 5;

    private String fileType = "png";

    private boolean safe = true;


    @Param(caption = "宽")
    public void setWidth(int width) {
        this.width = width;
    }

    @Param(caption = "高")
    public void setHeight(int height) {
        this.height = height;
    }

    @Param(caption = "干扰线条数", request = false)
    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    @Param(request = false)
    public void setLength(int length) {
        this.length = length;
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
        if (safe && RequestUtil.isPirated(getRequest())) {
            return NONE;
        }
        IUserSession userSession = getUserSession();
        HttpServletResponse response = getResponse();
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/" + fileType);
        ValidateCode validateCode = new ValidateCode(width, height, length, lineCount);
        String code = validateCode.makeCode();
        ImageIO.write(validateCode.getBufferImage(), fileType, response.getOutputStream());

        if (userSession != null) {
            validateCodeCache.addImgCode(EncryptUtil.getMd5(userSession.getId()), code);
        }
        return super.execute();
    }
}
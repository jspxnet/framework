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

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成二维码，条码等,默认生成二维码
 * 必须使用多例模式
 *
 */
@HttpMethod(caption = "二维码条码")
public class QRCodeView extends ActionSupport {
    private String txt = "";
    private int width = 140;
    private int height = 140;
    private String fileType = "png";
    private static final int color = 0xFFFFFFFF;
    private static final int bgColor = 0xff000000;
    private boolean safe = true;

    private int format = 11;  //CODE_39,CODE_93,CODE_128,


    public String getTxt() {
        return txt;
    }

    @Param(caption = "数据", max = 250)
    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getWidth() {
        return width;
    }

    @Param(caption = "宽", min = 0)
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    @Param(caption = "高", min = 0)
    public void setHeight(int height) {
        this.height = height;
    }

    public static int getColor() {
        return color;
    }

    public static int getBgColor() {
        return bgColor;
    }

    @Param(request = false)
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public void setFormat(int format) {
        this.format = format;
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
        if (RequestUtil.isPirated(request)) {
            return NONE;
        }
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();

        String sTxt = encrypt.getDecode(txt);
        if (!StringUtil.hasLength(sTxt)) {
            sTxt = txt;
        }

        BarcodeFormat bFormat;
        if (StringUtil.isNull(txt)) {
            return NONE;
        }

        switch (format) {
            case 6:
                bFormat = BarcodeFormat.EAN_8;
                break;
            case 7:
                bFormat = BarcodeFormat.EAN_13;
                break;
            case 14:
                bFormat = BarcodeFormat.UPC_A;
                break;
            case 2:
                bFormat = BarcodeFormat.CODE_39;
                break;
            case 4:
                bFormat = BarcodeFormat.CODE_128;
                break;
            case 8:
                bFormat = BarcodeFormat.ITF;
                break;
            case 10:
                bFormat = BarcodeFormat.PDF_417;
                break;
            case 1:
                bFormat = BarcodeFormat.CODABAR;
                break;
            case 5:
                bFormat = BarcodeFormat.DATA_MATRIX;
                break;
            case 20:
                bFormat = BarcodeFormat.AZTEC;
                break;
            default: {
                bFormat = BarcodeFormat.QR_CODE;
            }
        }

        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/" + fileType);
        OutputStream out = response.getOutputStream();
        try {
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            // 指定纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            // 指定编码格式
            hints.put(EncodeHintType.CHARACTER_SET, Environment.defaultEncode);
            BitMatrix matrix = new MultiFormatWriter().encode(sTxt, bFormat, width, height, hints);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? bgColor : color);
                }
            }
            ImageIO.write(image, fileType, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return NONE;
    }

}
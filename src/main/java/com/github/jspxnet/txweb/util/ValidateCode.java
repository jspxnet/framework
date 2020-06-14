/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.util;

import com.github.jspxnet.utils.ColorUtil;
import com.github.jspxnet.utils.FontUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成器
 *
 * @author dsna
 */
public class ValidateCode {
    // 图片的宽度。
    private int width = 76;
    // 图片的高度。
    private int height = 26;
    // 验证码字符个数
    private int codeCount = 5;
    // 验证码干扰线数
    private int lineCount = 14;
    // 验证码
    private String code = null;

    private char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '9'};

    public ValidateCode() {

    }

    /**
     * @param width  图片宽
     * @param height 图片高
     */
    public ValidateCode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param lineCount 干扰线条数
     */
    public ValidateCode(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
    }

    public BufferedImage getBufferImage() {
        int x = 0, fontHeight = 0, codeY = 0;
        int red = 0, green = 0, blue = 0;

        x = width / (codeCount + 2);//每个字符的宽度
        fontHeight = height - 2;//字体的高度
        codeY = height - 4;

        // 图像buffer
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        // 生成随机数
        Random random = new Random();
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体

        Font font = FontUtil.getFont(fontHeight);
        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 8);
            int ye = ys + random.nextInt(height / 8);
            red = random.nextInt(200);
            green = random.nextInt(200);
            blue = random.nextInt(200);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }

        // randomCode记录随机产生的验证码
        Color[] colors = ColorUtil.getColorArray(codeCount + 1);

        // 随机产生codeCount个字符的验证码。
        FontMetrics fontMetrics = g.getFontMetrics();
        int w = 2;
        for (int i = 0; i < codeCount; i++) {
            char C = code.charAt(i);
            g.setColor(colors[i]);
            g.drawString(StringUtil.empty + C, w, codeY);
            // 将产生的四个随机数组合在一起。
            w = w + fontMetrics.charWidth(C);
        }
        // 将四位数字的验证码保存到Session中。

        return bufferedImage;
    }

    public String makeCode() {
        StringBuilder randomCode = new StringBuilder();
        for (int i = 0; i < this.codeCount; i++) {
            randomCode.append(codeSequence[RandomUtil.getRandomInt(0, codeSequence.length - 1)]);
        }
        code = randomCode.toString();
        return code;
    }
}
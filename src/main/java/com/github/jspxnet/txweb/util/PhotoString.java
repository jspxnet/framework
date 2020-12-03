/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.util;

import com.github.jspxnet.utils.ColorUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by yuan on 14-3-23.
 * 图片转换
 */
public class PhotoString {
    // 图片的宽度。
    private int width = 140;
    // 图片的高度。
    private int height = 40;
    // 验证码干扰线数
    private int lineCount = 12;

    private String code = StringUtil.empty;
    private Color bgColor = Color.WHITE;
    private Color fontColor = null;


    public PhotoString() {

    }


    /**
     * @param width     图片宽
     * @param height    图片高
     * @param bgColor   背景色
     * @param fontColor 字体色
     * @param code      内容
     */
    public PhotoString(int width, int height, Color bgColor, Color fontColor, String code) {
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.fontColor = fontColor;
        this.code = code;
    }

    /**
     * @param width     图片宽
     * @param height    图片高
     * @param bgColor   背景色
     * @param fontColor 字体色
     * @param lineCount 行数
     * @param code      内容
     */
    public PhotoString(int width, int height, Color bgColor, Color fontColor, int lineCount, String code) {
        this.width = width;
        this.height = height;
        this.lineCount = lineCount;
        this.bgColor = bgColor;
        this.fontColor = fontColor;
        this.code = code;
    }


    public BufferedImage getBufferImage() {

        int x = 0, fontHeight = 0, codeY = 0;
        int red = 0, green = 0, blue = 0;

        fontHeight = height - 4;//字体的高度
        codeY = height - 4;

        // 图像buffer
        // 验证码图片Buffer
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        // 生成随机数

        // 将图像填充为白色
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        // 创建字体
        g.setFont(g.getFont().deriveFont(Font.PLAIN, fontHeight));

        Random random = new Random();
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


        boolean randomFont = fontColor == null;

        FontMetrics fontMetrics = g.getFontMetrics();
        int w = 2;
        for (int i = 0; i < code.length(); i++) {

            char C = code.charAt(i);
            if (randomFont) {
                fontColor = ColorUtil.getRandomColor();
            }
            g.setColor(fontColor);
            g.setFont(fontMetrics.getFont().deriveFont(RandomUtil.getRandomInt(0, 2)));
            g.drawString(StringUtil.empty + C, w, codeY);
            w = w + fontMetrics.charWidth(C) + RandomUtil.getRandomInt(1, 3);
        }
        // 将四位数字的验证码保存到Session中。
        return bufferedImage;
    }

}
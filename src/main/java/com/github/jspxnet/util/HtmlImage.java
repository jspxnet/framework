/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.utils.ImageUtil;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * HTML2JPG,HTML页面转图片的实现方法。
 *
 * @author 老紫竹(Java世纪网, java2000.net)
 */
public class HtmlImage extends JFrame {
    public HtmlImage(String url, File file, int w, int h) throws Exception {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setPage(url);
        JScrollPane jsp = new JScrollPane(editorPane);
        getContentPane().add(jsp);

        this.setLocation(0, 0);
        this.setVisible(true); // 如果这里不设置可见，则里面的图片等无法截取

        // 如果不延时，则图片等可能没有时间下载显示
        // 具体的秒数需要根据网速等调整
        Thread.sleep(5 * 1000);
        setSize(10000, 10000);
        pack();
        // BufferedImage image = new BufferedImage(editorPane.getWidth(),
        // editorPane.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage image = new BufferedImage(editorPane.getWidth(), editorPane.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        editorPane.paint(graphics2D);

        if (w <= 0) {
            w = 800;
        }
        if (h <= 0) {
            h = 600;
        }
        BufferedImage image1 = ImageUtil.scale(image, w, h);
        ImageIO.write(image1, "jpg", file);
        dispose();
    }


    public static void main(String[] args) throws Exception {
        //new HtmlImage("http://www.google.cn", new File("d:/file.jpg"));
        //  converter(args[0],new File(args[1]), StringUtil.toInt(args[2]),StringUtil.toInt(args[3]));
        converter("http://www.google.cn", new File("d:/file.jpg"), 1024, 768);
    }

    public static void converter(String url, File jpgFile, int w, int h) throws Exception {
        new HtmlImage(url, jpgFile, w, h);
    }
}
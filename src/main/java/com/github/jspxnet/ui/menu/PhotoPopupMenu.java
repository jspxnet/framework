/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.menu;

import com.github.jspxnet.utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-6
 * Time: 上午12:35
 */
public class PhotoPopupMenu extends JPopupMenu {
    private BufferedImage backgroundImage;
    private Color bgColor = new Color(252, 254, 254);
    private Color topColor = new Color(233, 238, 240);
    private Color bottomColor = new Color(197, 206, 211); //Color.WHITE

    public PhotoPopupMenu() {
        setBackground(bgColor);
    }


    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (backgroundImage != null) {
            g2d.drawImage(ImageUtil.scale(backgroundImage, getWidth(), getHeight()), 0, 0, getWidth(), getHeight(), null);
        }

        g2d.setPaint(new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor));
        g2d.fillRect(2, 0, 20, getHeight());

        g2d.setColor(Color.RED);
    }

}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.label;

import com.github.jspxnet.utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-27
 * Time: 下午1:02
 */
public class SideLabel extends JLabel {
    private double side = 8;

    public double getSide() {
        return side;
    }

    public void setSide(double side) {
        this.side = side;
    }

    @Override
    public void setIcon(Icon icon) {
        if (icon == null) {
            super.setIcon(null);
            return;
        }
        BufferedImage bi = ImageUtil.toImage(icon);
        Graphics2D g2d = (Graphics2D) bi.getGraphics();
        g2d.setColor(new Color(167, 196, 214));
        Shape shape = new RoundRectangle2D.Double(0, 0, bi.getWidth() - 1, bi.getHeight() - 1, side, side);
        g2d.draw(shape);
        super.setIcon(new ImageIcon(bi));
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), side, side);
        g2d.setClip(rect);
        super.paintComponent(g);
    }


}
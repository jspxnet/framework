/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.button;

import com.github.jspxnet.utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-19
 * Time: 上午12:56
 * 图标成按钮方框,或者以缩放方式显示
 */
public class RectButton extends JLabel {
    private Color inBorderColor = new Color(103, 118, 127);
    private BufferedImage image = null;
    private boolean mouseIn = false;
    private float angle = 5f;

    public RectButton() {
        setFocusable(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseIn = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseIn = false;
                repaint();
            }
        });
    }

    public Color getInBorderColor() {
        return inBorderColor;
    }

    public void setInBorderColor(Color inBorderColor) {
        this.inBorderColor = inBorderColor;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void setIcon(Icon icon) {
        if (icon != null) {
            int h = icon.getIconHeight() + 8;
            int w = icon.getIconWidth() + 8;
            setPreferredSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
            image = ImageUtil.toImage(icon);
            super.setIcon(new ImageIcon(image));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (mouseIn) {

            Shape shape = new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, angle - 1, angle - 1);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f));
            g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(197, 206, 211)));
            g2d.fill(shape);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));

            g2d.setColor(inBorderColor);
            shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, angle, angle);
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            shape = new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, angle - 1, angle - 1);
            g2d.draw(shape);
        }
        if (image != null) {
            g2d.drawImage(image, 4, 4, image.getWidth(), image.getHeight(), null);
        }
    }

}
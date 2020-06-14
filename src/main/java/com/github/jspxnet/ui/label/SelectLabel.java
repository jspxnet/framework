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

import com.github.jspxnet.ui.icon.IconPath;
import com.github.jspxnet.utils.ImageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-2
 * Time: 上午11:25
 */
public class SelectLabel extends JLabel {
    private Color inBorderColor = new Color(158, 162, 167);
    private BufferedImage backgroundImage = null;
    private BufferedImage popImage = null;
    private BufferedImage image = null;
    private boolean mouseIn = false;
    private float side = 3f;

    public SelectLabel() {
        try {
            popImage = ImageIO.read(IconPath.class.getResource("selectpop.png"));
            backgroundImage = ImageIO.read(IconPath.class.getResource("selectLabel_bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public BufferedImage getPopImage() {
        return popImage;
    }

    public void setPopImage(BufferedImage popImage) {
        this.popImage = popImage;
    }

    public boolean isMouseIn() {
        return mouseIn;
    }

    public void setMouseIn(boolean mouseIn) {
        this.mouseIn = mouseIn;
    }

    public float getSide() {
        return side;
    }

    public void setSide(float side) {
        this.side = side;
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        if (icon != null) {
            image = ImageUtil.getIcon(this);
            int h = icon.getIconHeight() + 8;
            if (h < 24) {
                h = 24;
            }
            setPreferredSize(new Dimension(image.getWidth() + image.getWidth() + 10, h));
            super.setIcon(null);
        }

    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        //  super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (mouseIn) {
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth() - 2, getHeight() - 2, null);
            }

            g2d.setColor(inBorderColor);
            Shape shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, side, side);
            g2d.draw(shape);
            g2d.setColor(Color.WHITE);
            shape = new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, side - 1, side - 1);
            g2d.draw(shape);

        }
        if (popImage != null) {
            int w = getWidth() - popImage.getWidth();
            int h = getHeight() - popImage.getHeight();
            g2d.drawImage(popImage, w - 4, h / 2, popImage.getWidth(), popImage.getHeight(), null);
        }
        g2d.drawImage(image, 4, image.getHeight() / 2, image.getWidth(), image.getHeight(), null);
    }
}
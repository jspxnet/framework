/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.field;

import com.github.jspxnet.ui.icon.IconPath;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-27
 * Time: 上午10:32
 */
public class SideTextField extends JTextField {
    private Border inBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(51, 139, 192));
    private Border outBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(157, 188, 208));
    private BufferedImage backgroundIcon = null;
    private boolean overIcon = false;


    public SideTextField() {
        setBorder(outBorder);
        try {
            backgroundIcon = ImageIO.read(IconPath.class.getResource("textfield_find.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                int w = getWidth() - backgroundIcon.getWidth() - 5;
                int h = getHeight() - backgroundIcon.getHeight();
                if (w < e.getX() && e.getX() < getWidth() && h / 2 < e.getY() && e.getY() < getHeight()) {
                    SideTextField.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    overIcon = true;
                } else {
                    SideTextField.this.setCursor(Cursor.getDefaultCursor());
                    overIcon = false;
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(inBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(outBorder);
            }
        });
    }

    public boolean isOverIcon() {
        return overIcon;
    }

    public void setOverIcon(boolean overIcon) {
        this.overIcon = overIcon;
    }

    public Border getInBorder() {
        return inBorder;
    }

    public void setInBorder(Border inBorder) {
        this.inBorder = inBorder;
    }

    public Border getOutBorder() {
        return outBorder;
    }

    public void setOutBorder(Border outBorder) {
        this.outBorder = outBorder;
    }

    public BufferedImage getBackgroundIcon() {
        return backgroundIcon;
    }

    public void setBackgroundIcon(BufferedImage backgroundIcon) {
        this.backgroundIcon = backgroundIcon;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundIcon != null) {
            int w = getWidth() - backgroundIcon.getWidth() - 4;
            int h = getHeight() - backgroundIcon.getHeight();
            g.drawImage(backgroundIcon, w, h / 2 - 1, backgroundIcon.getWidth(), backgroundIcon.getHeight(), null);
        }


    }

}
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
import com.github.jspxnet.utils.StringUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-27
 * Time: 上午11:37
 */
public class FindTextField extends JTextField {
    private BufferedImage backgroundIcon = null;
    private boolean overIcon = false;
    private float angle = 8f;

    public FindTextField() {
        try {
            backgroundIcon = ImageIO.read(IconPath.class.getResource("textfield_find.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBorder(new EmptyBorder(0, 0, 0, 0));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                int w = getWidth() - backgroundIcon.getWidth() - 5;
                int h = getHeight() - backgroundIcon.getHeight();
                if (w < e.getX() && e.getX() < getWidth() && h / 2 < e.getY() && e.getY() < getHeight()) {
                    FindTextField.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    overIcon = true;
                } else {
                    FindTextField.this.setCursor(Cursor.getDefaultCursor());
                    overIcon = false;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                String text = getText();
                if (!StringUtil.hasLength(text) || " ".equals(text)) {
                    setText(getToolTipText());
                }
                setForeground(Color.GRAY);
            }
        });
        setForeground(Color.GRAY);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isOverIcon() {
        return overIcon;
    }

    public void setOverIcon(boolean overIcon) {
        this.overIcon = overIcon;
    }

    public BufferedImage getBackgroundIcon() {
        return backgroundIcon;
    }

    public void setBackgroundIcon(BufferedImage backgroundIcon) {
        this.backgroundIcon = backgroundIcon;
    }

    @Override
    public void paintBorder(Graphics g) {
        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, angle, angle);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(157, 188, 208));
        g2d.draw(rect);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setClip(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, angle, angle));
        super.paintComponent(g);
        if (backgroundIcon != null) {
            int w = getWidth() - backgroundIcon.getWidth() - 4;
            int h = getHeight() - backgroundIcon.getHeight();
            g.drawImage(backgroundIcon, w, h / 2 - 1, backgroundIcon.getWidth(), backgroundIcon.getHeight(), null);
        }

    }


}
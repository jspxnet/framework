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
 * date: 11-6-1
 * Time: 下午9:10
 */
public class SelectTextField extends JTextField {
    private Border inBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(51, 139, 192));
    private Border outBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(157, 188, 208));
    private BufferedImage popImage = null;
    private BufferedImage selectPopImage = null;
    private boolean overIcon = false;


    public SelectTextField() {
        setBorder(outBorder);
        try {
            popImage = ImageIO.read(IconPath.class.getResource("selectpop.png"));
            selectPopImage = ImageIO.read(IconPath.class.getResource("selectpop2.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (selectPopImage != null) {
                    int w = getWidth() - selectPopImage.getWidth() - 7;
                    int h = getHeight() - selectPopImage.getHeight();
                    if (w <= e.getX() && e.getX() < getWidth() && h / 2 < e.getY() && e.getY() < getHeight()) {
                        SelectTextField.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        overIcon = true;
                        repaint();
                    } else {
                        SelectTextField.this.setCursor(Cursor.getDefaultCursor());
                        overIcon = false;
                        repaint();
                    }
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
                overIcon = false;
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

    public BufferedImage getPopImage() {
        return popImage;
    }

    public void setPopImage(BufferedImage popImage) {
        this.popImage = popImage;
    }

    public BufferedImage getSelectPopImage() {
        return selectPopImage;
    }

    public void setSelectPopImage(BufferedImage selectPopImage) {
        this.selectPopImage = selectPopImage;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (overIcon && selectPopImage != null) {
            int w = getWidth() - selectPopImage.getWidth() - 2;
            int h = getHeight() - selectPopImage.getHeight();
            g.drawImage(selectPopImage, w, h / 2, selectPopImage.getWidth(), selectPopImage.getHeight(), null);
        } else {
            if (popImage != null) {
                int w = getWidth() - popImage.getWidth() - 7;
                int h = getHeight() - popImage.getHeight();
                g.drawImage(popImage, w, h / 2 + 2, popImage.getWidth(), popImage.getHeight(), null);
            }
        }
    }

}
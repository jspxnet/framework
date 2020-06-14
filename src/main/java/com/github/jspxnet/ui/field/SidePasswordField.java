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
import com.github.jspxnet.utils.ImageUtil;
import com.github.jspxnet.utils.StringUtil;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-1
 * Time: 下午1:53
 */
public class SidePasswordField extends JPasswordField {
    private Border inBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(51, 139, 192));
    private Border outBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(157, 188, 208));
    private boolean overIcon = false;
    private BufferedImage backgroundImage = null;
    private BufferedImage backgroundIcon = null;
    private int startTextWidth = 0;
    final private String zwf = "　　　";

    public SidePasswordField() {
        setText(zwf);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkText();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkText();
            }
        });

        try {
            backgroundImage = ImageIO.read(IconPath.class.getResource("keybord.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBorder(outBorder);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                int w = getWidth() - backgroundImage.getWidth() - 5;
                int h = getHeight() - backgroundImage.getHeight();
                if (w < e.getX() && e.getX() < getWidth() && h / 2 < e.getY() && e.getY() < getHeight()) {
                    SidePasswordField.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    overIcon = true;
                } else {
                    SidePasswordField.this.setCursor(Cursor.getDefaultCursor());
                    overIcon = false;
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(inBorder);
                checkText();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(outBorder);
            }
        });
        startTextWidth = StringUtil.getLength(zwf) * 14 + 4;

    }

    public void checkText() {

        String text = new String(super.getPassword());
        //String text = super.getText();
        if (StringUtil.isNull(text) || !text.startsWith(zwf)) {
            text = zwf;
            super.setText(text);
        }
        int caret = getCaretPosition();
        if (caret < zwf.length()) {
            super.setCaretPosition(zwf.length());
        }
    }


    @Override
    public void setText(String text) {
        if (StringUtil.isNull(text) || !text.startsWith(zwf)) {
            text = zwf;
        }
        super.setText(text);
    }

    @Override
    @Deprecated
    public String getText() {
        String text = super.getText();
        if (zwf.equals(text)) {
            return StringUtil.empty;
        }
        if (text.startsWith(zwf)) {
            text = StringUtil.substringAfter(text, zwf);
        }
        return text;
    }

    @Override
    public char[] getPassword() {
        String text = new String(super.getPassword());
        if (zwf.equals(text)) {
            return new char[0];
        }
        if (text.startsWith(zwf)) {
            text = StringUtil.substringAfter(text, zwf);
        }
        return text.toCharArray();
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
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


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundIcon != null) {
            int w = getWidth() - backgroundIcon.getWidth() - 4;
            int h = getHeight() - backgroundIcon.getHeight();
            g.drawImage(backgroundIcon, w, h / 2 - 1, backgroundIcon.getWidth(), backgroundIcon.getHeight(), null);
        }
        if (backgroundImage != null) {

            int w = backgroundImage.getWidth();
            if (w > startTextWidth - 6) {
                w = startTextWidth - 6;
            }
            int h = backgroundImage.getHeight();
            if (h > getHeight() - 6) {
                h = getHeight() - 6;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), Color.WHITE));
            g2d.fillRect(0, 0, w, h);

            g.drawImage(ImageUtil.scale(backgroundImage, w, h), 4, (getHeight() - h) / 2, null);
        }


    }

}
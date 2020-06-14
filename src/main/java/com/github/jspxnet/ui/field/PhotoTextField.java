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

import com.github.jspxnet.utils.ImageUtil;
import com.github.jspxnet.utils.StringUtil;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-1
 * Time: 下午10:25
 */
public class PhotoTextField extends SelectTextField {
    private BufferedImage image;
    private int startTextWidth = 0;
    final private String zwf = "　 　";

    public PhotoTextField() {
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
        checkText();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        startTextWidth = StringUtil.getLength(zwf) * 14 + 4;
    }

    public int getStartTextWidth() {
        return startTextWidth;
    }

    public void setStartTextWidth(int startTextWidth) {
        this.startTextWidth = startTextWidth;
    }

    public void checkText() {
        String text = super.getText();
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int w = image.getWidth();
            if (w > startTextWidth - 6) {
                w = startTextWidth - 6;
            }
            int h = image.getHeight();
            if (h > getHeight() - 6) {
                h = getHeight() - 6;
            }
            g.drawImage(ImageUtil.scale(image, w, h), 4, (getHeight() - h) / 2, null);
        }
    }

}
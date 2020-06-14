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

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-2
 * Time: 上午10:27
 * 一个按钮，两个动作事件
 */
public class TwoButton extends JButton {
    private Icon rolloverLeftIcon;
    private Icon rolloverRightIcon;
    private Icon pressedLeftIcon;
    private Icon pressedRightIcon;

    private boolean overRightIcon = true;

    public TwoButton() {
        setOpaque(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusable(false);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (rolloverLeftIcon.getIconWidth() * 2 / 3 + 4 < e.getX()) {
                    if (!overRightIcon) {
                        overRightIcon = true;
                        repaint();
                    }
                } else {
                    if (overRightIcon) {
                        overRightIcon = false;
                        repaint();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (rolloverLeftIcon.getIconWidth() * 2 / 3 + 4 < e.getX()) {
                    if (!overRightIcon) {
                        overRightIcon = true;
                        repaint();
                    }
                } else {
                    if (overRightIcon) {
                        overRightIcon = false;
                        repaint();
                    }
                }
            }
        });

    }

    @Override
    public Icon getRolloverIcon() {
        if (overRightIcon) {
            return rolloverRightIcon;
        } else {
            return rolloverLeftIcon;
        }
    }

    @Override
    public Icon getPressedIcon() {
        if (overRightIcon) {
            return pressedRightIcon;
        } else {
            return pressedLeftIcon;
        }
    }

    public Icon getRolloverLeftIcon() {
        return rolloverLeftIcon;
    }

    public void setRolloverLeftIcon(Icon rolloverLeftIcon) {
        this.rolloverLeftIcon = rolloverLeftIcon;
    }

    public Icon getRolloverRightIcon() {
        return rolloverRightIcon;
    }

    public void setRolloverRightIcon(Icon rolloverRightIcon) {
        this.rolloverRightIcon = rolloverRightIcon;
    }

    public Icon getPressedLeftIcon() {
        return pressedLeftIcon;
    }

    public void setPressedLeftIcon(Icon pressedLeftIcon) {
        this.pressedLeftIcon = pressedLeftIcon;
    }

    public Icon getPressedRightIcon() {
        return pressedRightIcon;
    }

    public void setPressedRightIcon(Icon pressedRightIcon) {
        this.pressedRightIcon = pressedRightIcon;
    }

    public boolean isOverRightIcon() {
        return overRightIcon;
    }

    public void setOverRightIcon(boolean overRightIcon) {
        this.overRightIcon = overRightIcon;
    }
}
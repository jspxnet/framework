/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.tree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-29
 * Time: 下午10:19
 */
public class UserListTree extends JTree {
    private Rectangle treeMouseRect;
    private Rectangle mouseSelectRect;
    private BufferedImage overBackground;
    private BufferedImage backgroundImage;
    private BufferedImage selectBackground;
    private Color overColor = new Color(197, 227, 248);
    private Color selectColor = new Color(252, 236, 173);

    public UserListTree() {

        putClientProperty("JTree.lineStyle", "None");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                treeMouseRect = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            //鼠标悬浮
            @Override
            public void mouseMoved(MouseEvent e) {
                TreePath treePath = getPathForLocation(e.getX(), e.getY());
                treeMouseRect = getPathBounds(treePath);
                repaint();
            }
        });

        class UserTreeSelectionListener implements TreeSelectionListener {
            @Override
            public void valueChanged(TreeSelectionEvent event) {

                //鼠标选择事件
                TreePath treePath = event.getPath();
                if (treePath != null) {
                    mouseSelectRect = getPathBounds(treePath);
                } else {
                    treePath = null;
                }
                repaint();
                //鼠标选择
            }
        }
        addTreeSelectionListener(new UserTreeSelectionListener());
        setOpaque(false);
        setRootVisible(false);
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (backgroundImage != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.1f));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
        }

        if (mouseSelectRect != null) {
            if (selectBackground != null) {
                g2d.drawImage(selectBackground, 0, mouseSelectRect.y, getWidth() - 2, mouseSelectRect.height, null);
            } else {
                g2d.setColor(selectColor);
                g2d.fillRect(0, mouseSelectRect.y, getWidth() - 2, mouseSelectRect.height);
            }
        }
        // 获取Graphics2D
        if (treeMouseRect != null) {
            if (overBackground != null) {
                g2d.drawImage(overBackground, 0, treeMouseRect.y, getWidth() - 2, treeMouseRect.height, null);
            } else {
                g2d.setColor(overColor);
                g2d.fillRect(0, treeMouseRect.y, getWidth() - 2, treeMouseRect.height);
            }
        }
        super.paint(g);
    }

    public BufferedImage getOverBackground() {
        return overBackground;
    }

    public void setOverBackground(BufferedImage overBackground) {
        this.overBackground = overBackground;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public BufferedImage getSelectBackground() {
        return selectBackground;
    }

    public void setSelectBackground(BufferedImage selectBackground) {
        this.selectBackground = selectBackground;
    }

    public Color getOverColor() {
        return overColor;
    }

    public void setOverColor(Color overColor) {
        this.overColor = overColor;
    }

    public Color getSelectColor() {
        return selectColor;
    }

    public void setSelectColor(Color selectColor) {
        this.selectColor = selectColor;
    }
}
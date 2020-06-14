/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.frame;

import com.github.jspxnet.ui.icon.IconPath;
import com.github.jspxnet.utils.SwingUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-3
 * Time: 下午2:38
 * 提示信息窗口
 */
public class TipsFrame extends PhotoFrame {
    static final public int SHOW_STYLE_DEFAULT = 0; //默认
    static final public int SHOW_STYLE_BUBBLE = 1; //气泡方式
    private int showStyle = 1;


    public TipsFrame() {
        try {
            setBackgroundImage(ImageIO.read(IconPath.class.getResource("tips_bg.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getMaxButton().setVisible(false);
        getMinButton().setVisible(false);
        getHelpButton().setVisible(false);
        setBounds(0, 0, 220, 200);
        SwingUtil.setOpaque(getContentPane(), false);
    }

    public int getShowStyle() {
        return showStyle;
    }

    public void setShowStyle(int showStyle) {
        this.showStyle = showStyle;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        final JFrame frame = this;
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        switch (showStyle) {
            case SHOW_STYLE_DEFAULT: {
                return;
            }
            case SHOW_STYLE_BUBBLE: {
                //气泡  动画移动
                (new Thread() {
                    @Override
                    synchronized public void run() {
                        int toX = dimension.width - getWidth() - 10;
                        int toY = dimension.height - getHeight() - 35;
                        frame.setLocation(toX, dimension.height);
                        for (int y = dimension.height; y > toY; y--) {
                            frame.setLocation(toX, y);
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!isInterrupted()) {
                            interrupt();
                        }
                    }
                }).start();
                break;
            }
            default: {

            }

        }

    }


}
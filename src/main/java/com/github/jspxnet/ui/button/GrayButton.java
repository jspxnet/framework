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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-19
 * Time: 上午12:16
 * 灰色按钮,当图片在上边的时候，当前图片会自动变成灰色 gray 参数控制，先灰，还是先亮
 */
public class GrayButton extends JLabel {
    //点击后是否保持
    private boolean autoEnabled = true;

    public GrayButton() {
        setFocusable(false);
        setEnabled(false);
    }

    public boolean isAutoEnabled() {
        return autoEnabled;
    }

    public void setAutoEnabled(boolean autoEnabled) {
        this.autoEnabled = autoEnabled;
    }

    @Override
    public void setIcon(final Icon icon) {
        super.setIcon(icon);
        if (icon == null) {
            return;
        }
        BufferedImage bi = ImageUtil.toImage(icon);
        bi = ImageUtil.gray(bi);
        setDisabledIcon(new ImageIcon(bi));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (autoEnabled) {
                    setEnabled(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (autoEnabled) {
                    setEnabled(false);
                }
            }
        });

    }

}
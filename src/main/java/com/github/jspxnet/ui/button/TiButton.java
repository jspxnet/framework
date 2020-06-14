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
import java.awt.image.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-23
 * Time: 下午12:04
 */
public class TiButton extends JLabel {
    private float alpha = 0.7f;

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setBackgroundIcon(final Icon bgIcon) {
        final Icon showIcon = getIcon();
        final BufferedImage bufImg = ImageUtil.toImage(showIcon);
        final TiButton btn = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                BufferedImage bgPanel = ImageUtil.createImage(btn.getRootPane());
                BufferedImage background = ImageUtil.subImage(bgPanel, btn.getBounds());
                if (background == null) {
                    return;
                }
                BufferedImage out = ImageUtil.alpha(background, bufImg, alpha);
                setIcon(new ImageIcon(out));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(bgIcon);
            }

        });
        setIcon(bgIcon);
    }


}
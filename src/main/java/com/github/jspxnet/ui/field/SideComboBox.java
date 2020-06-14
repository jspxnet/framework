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

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-1
 * Time: 下午8:17
 */
public class SideComboBox extends JComboBox {
    private Border inBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(51, 139, 192));
    private Border outBorder = BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(157, 188, 208));

    public SideComboBox() {
        getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
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
}
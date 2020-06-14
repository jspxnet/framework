/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.checkbox;

import com.github.jspxnet.ui.icon.IconPath;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-1
 * Time: 下午5:43
 */
public class SideCheckBox extends JCheckBox {

    private ImageIcon check01Icon = new ImageIcon(IconPath.class.getResource("Check_01.png"));
    private ImageIcon check02Icon = new ImageIcon(IconPath.class.getResource("Check_02.png"));
    private ImageIcon check01borderIcon = new ImageIcon(IconPath.class.getResource("Check_01_border.png"));
    private ImageIcon check02borderIcon = new ImageIcon(IconPath.class.getResource("Check_02_border.png"));

    private boolean mouseIn = false;

    public SideCheckBox() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseIn = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseIn = false;
            }
        });
    }

    @Override
    public Icon getIcon() {
        if (mouseIn) {
            return check01borderIcon;
        }
        return check01Icon;
    }

    @Override
    public Icon getSelectedIcon() {
        if (mouseIn) {
            return check02borderIcon;
        }
        return check02Icon;
    }


}
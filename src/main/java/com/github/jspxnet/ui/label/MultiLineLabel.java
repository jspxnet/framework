/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.label;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-8-31
 * Time: 下午5:00
 */
public class MultiLineLabel extends JTextArea {
    public MultiLineLabel(String s) {
        super(s);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        // 设置为自动换行
        setLineWrap(true);
        setWrapStyleWord(true);
        setHighlighter(null);
        setEditable(false);

        // 设置为label的边框，颜色和字体
        LookAndFeel.installBorder(this, "Label.border");
        LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground", "Label.font");

    }
}
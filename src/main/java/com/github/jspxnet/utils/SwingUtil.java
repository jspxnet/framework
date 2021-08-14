/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-4
 * Time: 0:08:20
 */
public final class SwingUtil {

    private SwingUtil() {

    }


    //得到JDK支持的基本字体,并且扩充常用字体
    static public String[] getSystemFontList() {
        //微软雅黑
        String[] fontArray = Toolkit.getDefaultToolkit().getFontList();
        if (hasFont("SimSun")) {
            fontArray = (String[]) ArrayUtil.add(fontArray, 0, "SimSun");
        }
        if (hasFont("Tahoma")) {
            fontArray = (String[]) ArrayUtil.add(fontArray, 0, "Tahoma");
        }
        if (hasFont("微软雅黑")) {
            fontArray = (String[]) ArrayUtil.add(fontArray, 0, "微软雅黑");
        }
        if (hasFont("宋体")) {
            fontArray = (String[]) ArrayUtil.add(fontArray, 0, "宋体");
        }
        return fontArray;
    }

    /**
     * @param name 字体名称
     * @return 得到字体的相关信息，主要是别名
     */
    static public FontMetrics getFontMetrics(String name) {
        Font f = new Font(name, Font.PLAIN, 12);
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }

    /**
     * @param name 字体名称
     * @return 可以判断当前系统是否存在这个字体
     */
    static public boolean hasFont(String name) {
        Font f = new Font(name, Font.PLAIN, 12);
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
        return fm != null && name.equalsIgnoreCase(fm.getFont().getFamily());
    }

    static public String[] getSystemLookAndFeelList() {
        UIManager.LookAndFeelInfo[] lookAndFeelInfoArray = UIManager.getInstalledLookAndFeels();
        String[] result = null;
        for (UIManager.LookAndFeelInfo lf : lookAndFeelInfoArray) {
            result = ArrayUtil.add(result, lf.getClassName());
        }
        return result;
    }


    /**
     * 设置界面桌面居中显示
     *
     * @param frame frame
     */
    static public void center(Component frame) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getWidth()) / 2, (d.height - frame.getHeight()) / 2);
    }

    /**
     * 设置界面桌面居右显示
     *
     * @param frame 窗体
     * @param size  偏移量
     */
    static public void right(Window frame, int size) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width - frame.getWidth() - size - 2, (d.height - frame.getHeight()) / 2);
    }


    /**
     * 隐藏圆角，设定可见区域
     *
     * @param frame  窗体
     * @param width  宽
     * @param height 高
     * @param arc    幅度
     */
    static public void setVisibleRegion(Window frame, int width, int height, double arc) {
        Shape shape = new RoundRectangle2D.Double(0, 0, width, height, arc, arc);
        AWTUtilities.setWindowShape(frame, shape);
    }

    static public void setLabelText(JLabel label, String longString) {
        StringBuilder builder = new StringBuilder("<html>");
        char[] chars = longString.toCharArray();
        FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        for (int beginIndex = 0, limit = 1; ; limit++) {
            if (fontMetrics.charsWidth(chars, beginIndex, limit) < label.getWidth()) {
                if (beginIndex + limit < chars.length) {
                    continue;
                }
                builder.append(chars, beginIndex, limit);
                break;
            }
            builder.append(chars, beginIndex, limit - 1).append("<br/>");
            beginIndex += limit - 1;
            limit = 1;
        }
        builder.append("</html>");
        label.setText(builder.toString());
    }


    static public void setOpaque(Container container, boolean isOpaque) {
        if (container instanceof JPanel) {
            if (container instanceof JPanel) {
                ((JPanel) container).setOpaque(isOpaque);
            }
        }
        for (Component comm : container.getComponents()) {
            if (comm instanceof JPanel) {
                setOpaque((Container) comm, isOpaque);
            }
            if (comm instanceof JLabel) {
                ((JLabel) comm).setOpaque(isOpaque);
            }
            if (comm instanceof JCheckBox) {
                ((JCheckBox) comm).setOpaque(isOpaque);
            }
            if (comm instanceof JButton) {
                ((JButton) comm).setOpaque(isOpaque);
            }
            if (comm instanceof JEditorPane) {
                ((JEditorPane) comm).setOpaque(isOpaque);
            }
            if (comm instanceof JEditorPane) {
                ((JEditorPane) comm).setOpaque(isOpaque);
            }
            if (comm instanceof JSplitPane) {
                ((JSplitPane) comm).setOpaque(isOpaque);
            }
            if (comm instanceof JScrollPane) {
                ((JScrollPane) comm).setOpaque(isOpaque);
                ((JScrollPane) comm).getViewport().setOpaque(isOpaque);
            }
        }
    }

    static public void updateUIFont(Font f) {
        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.black);
        UIManager.put("Button.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("DishMenuItem.font", f);
        UIManager.put("List.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("TextPane.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("Awt.font", f);
        UIManager.put("Swing.font", f);
        UIManager.put("Dialog.font", f);


        UIManager.put("Button.font ", f);
        UIManager.put("ToggleButton.font ", f);
        UIManager.put("RadioButton.font ", f);
        UIManager.put("ColorChooser.font ", f);
        UIManager.put("ToggleButton.font ", f);
        UIManager.put("ComboBoxItem.font ", f);
        UIManager.put("InternalFrame.titleFont ", f);
        UIManager.put("MenuBar.font ", f);
        UIManager.put("CheckBoxMenuItem.font ", f);
        UIManager.put("PopupMenu.font ", f);
        UIManager.put("OptionPane.font ", f);
        UIManager.put("Panel.font ", f);
    }
}
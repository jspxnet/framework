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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-19
 * Time: 上午11:41
 */
public class StyleLabel extends JLabel {
    /**
     * 每个字之间的距离
     */
    private int tracking;

    /**
     * 返回一个简单轮廓样式的JLabel
     *
     * @param text     文字
     * @param fontSize 字体大小
     * @return 返回一个简单轮廓样式的JLabel
     */
    public static StyleLabel getOutlineLabel(String text, float fontSize) {
        return getOutlineLabel(text, 0, Color.WHITE, Color.BLACK, fontSize);
    }

    /**
     * 返回一个简单轮廓样式的JLabel
     *
     * @param text      文字
     * @param tracking  文字间距
     * @param fontColor 字的颜色
     * @param lineColor 边框样色
     * @param fontSize  字体大小
     * @return 返回一个简单轮廓样式
     */
    public static StyleLabel getOutlineLabel(String text, int tracking, Color fontColor, Color lineColor, float fontSize) {
        StyleLabel label = new StyleLabel(text, tracking, fontSize);
        label.setLeftShadow(0, 1, Color.BLACK);
        label.setRightShadow(1, 1, Color.BLACK);
        label.setForeground(Color.yellow);

        return label;
    }

    /**
     * 返回一个阴影遮蔽样式的JLabel
     *
     * @param text     文字
     * @param fontSize 字体大小
     * @return 返回一个阴影遮蔽样式的JLabel
     */
    public static StyleLabel getShadowLabel(String text, float fontSize) {
        return getShadowLabel(text, 0, Color.WHITE, Color.GRAY, Color.BLACK, fontSize);
    }

    /**
     * 返回一个阴影遮蔽样式的JLabel
     *
     * @param text       文字
     * @param tracking   字间距
     * @param fontColor  字体颜色
     * @param leftColor  阴影颜色
     * @param rightColor 字体侧边颜色
     * @param fontSize   字体大小
     * @return 返回一个阴影遮蔽样式的JLabel
     */
    public static StyleLabel getShadowLabel(String text, int tracking, Color fontColor, Color leftColor, Color rightColor, float fontSize) {
        StyleLabel label = new StyleLabel(text, tracking, fontSize);

        label.setLeftShadow(2, 2, leftColor);
        label.setRightShadow(2, 3, rightColor);
        label.setForeground(fontColor);

        return label;
    }

    /**
     * 返回一个3D样式的JLabel
     *
     * @param text     文字
     * @param fontSize 字体大小
     * @return 返回一个3D样式的JLabel
     */
    public static StyleLabel get3DLabel(String text, float fontSize) {
        return get3DLabel(text, 0, Color.WHITE, Color.GRAY, fontSize);
    }

    /**
     * 返回一个阴影遮蔽样式的JLabel
     *
     * @param text      文字
     * @param tracking  字间距
     * @param fontColor 字体颜色
     * @param sideColor 字体侧边颜色
     * @param fontSize  字体大小
     * @return 返回一个阴影遮蔽样式的JLabel
     */
    public static StyleLabel get3DLabel(String text, int tracking, Color fontColor, Color sideColor, float fontSize) {
        StyleLabel label = new StyleLabel(text, tracking, fontSize);
        label.setLeftShadow(2, 2, sideColor);
        label.setRightShadow(-1, -2, sideColor);
        label.setForeground(fontColor);
        return label;
    }

    /**
     * 构造方法
     *
     * @param text     文字
     * @param tracking 每个字之间的距离
     * @param fontSize 字体大小
     */
    private StyleLabel(String text, int tracking, float fontSize) {
        super(text);
        this.tracking = tracking;
        setFont(getFont().deriveFont(fontSize));
    }

    private int left_x, left_y, right_x, right_y;

    private Color left_color, right_color;

    public void setLeftShadow(int x, int y, Color color) {
        left_x = x;
        left_y = y;
        left_color = color;
    }

    public void setRightShadow(int x, int y, Color color) {
        right_x = x;
        right_y = y;
        right_color = color;
    }

    @Override
    public Dimension getPreferredSize() {
        String text = getText();
        FontMetrics fm = this.getFontMetrics(getFont());

        int w = fm.stringWidth(text);
        w += (text.length() - 1) * tracking;
        w += left_x + right_x;

        int h = fm.getHeight();
        h += left_y + right_y;

        return new Dimension(w, h);
    }

    @Override
    public void paintComponent(Graphics g) {
        // 打开文字抗锯齿
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        char[] chars = getText().toCharArray();
        FontMetrics fm = getFontMetrics(getFont());
        int h = fm.getAscent();
        g.setFont(getFont());
        int x = 0;
        for (char ch : chars) {
            int w = fm.charWidth(ch) + tracking;
            g.setColor(left_color);
            g.drawString("" + ch, x - left_x, h - left_y);
            g.setColor(right_color);
            g.drawString("" + ch, x + right_x, h + right_y);
            g.setColor(getForeground());
            g.drawString("" + ch, x, h);
            x += w;
        }
    }

    public static void main(String[] args) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        panel.add(StyleLabel.getOutlineLabel("显示为简单轮廓样式", 50));
        panel.add(StyleLabel.get3DLabel("显示为3D样式", 50));
        panel.add(StyleLabel.getShadowLabel("显示为阴影遮蔽样式", 50));
        panel.add(StyleLabel.getOutlineLabel("显示为简单轮廓样式", 12));

        JDialog frame = new JDialog();
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);

        frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getSize().height) / 2);

        frame.setVisible(true);
    }
}
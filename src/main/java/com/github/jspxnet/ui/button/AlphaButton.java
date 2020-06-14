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
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-3
 * Time: 下午3:48
 */
public class AlphaButton extends JButton {
    private float alpha = 0.8f;

    public AlphaButton() {
        setFocusable(false);//设置没有焦点
        setBorder(null);//设置不画按钮边框
        setContentAreaFilled(false);//设置不画按钮背景
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void setIcon(Icon icon) {
        if (icon == null) {
            super.setIcon(null);
            return;
        }
        try {

            BufferedImage img = ImageUtil.toImage(icon);
            MediaTracker mt = new MediaTracker(this);//为此按钮添加媒体跟踪器
            mt.addImage(img, 0);//在跟踪器添加图片，下标为0
            mt.waitForAll();   //等待加载
            int w = img.getWidth();//读取图片长度
            int h = img.getHeight();//读取图片宽度

            GraphicsConfiguration gc = new JFrame().getGraphicsConfiguration(); // 本地图形设备
            Image image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);//建立透明画布
            Graphics2D g = (Graphics2D) image.getGraphics(); //在画布上创建画笔
            Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .9f); //指定透明度为半透明90%
            g.setComposite(alpha);
            g.drawImage(img, 0, 0, this); //注意是,将image画到g画笔所在的画布上
            g.setColor(Color.black);//设置颜色为黑色
            g.drawString(getText(), 25, 20);//写字
            g.dispose(); //释放内存
            super.setIcon(new ImageIcon(image)); //把刚才生成的半透明image变成ImageIcon,贴到按钮上去
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRolloverIcon(Icon icon) {
        try {

            BufferedImage img = ImageUtil.toImage(icon);
            MediaTracker mt = new MediaTracker(this);//为此按钮添加媒体跟踪器
            mt.addImage(img, 0);//在跟踪器添加图片，下标为0
            mt.waitForAll();   //等待加载
            int w = img.getWidth();//读取图片长度
            int h = img.getHeight();//读取图片宽度
            GraphicsConfiguration gc = new JFrame().getGraphicsConfiguration(); // 本地图形设备
            Image image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);//建立透明画布
            Graphics2D g = (Graphics2D) image.getGraphics(); //在画布上创建画笔
            //指定透明度为半透明
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(img, 0, 0, this); //注意是,将image画到g画笔所在的画布上
            g.setColor(Color.black);//设置颜色为黑色
            g.drawString(getText(), 25, 20);//写字
            g.dispose(); //释放内存
            super.setRolloverIcon(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
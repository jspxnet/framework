/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.graphics.screen;

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * ****************************************************************
 * 该JavaBean可以直接在其他Java应用程序中调用，实现屏幕的"拍照"
 * This JavaBean is used transfer snapshot the GUI in a
 * Java application! You can embeded
 * it in transfer your java application source code, and us
 * it transfer snapshot the right GUI of the application
 *
 * @author liluqun ([mail]liluqun@263.net[/mail])
 * @version 1.0
 * <p>
 * ***************************************************
 */

public class GuiCamera {
    private String fileName; //文件的前缀
    static int serialNum = 0;
    private String imageFormat; //图像文件的格式
    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * *************************************************************
     * 默认的文件前缀为GuiCamera，文件格式为PNG格式
     * The default construct will use the default
     * Image file surname "GuiCamera",
     * and default image format "png"
     * **************************************************************
     */
    public GuiCamera() {
        fileName = "screen";
        imageFormat = "png";
    }

    /**
     * *************************************************************
     *
     * @param s      the surname of the snapshot file
     * @param format the format of the image file,
     *               it can be "jpg" or "png"
     *               本构造支持JPG和PNG文件的存储
     *               **************************************************************
     */
    public GuiCamera(String s, String format) {

        fileName = s;
        imageFormat = format;
    }

    /**
     * snapShot the Gui once
     * 对屏幕进行拍照
     *
     * @return 图片地址
     */
    public String snapShot() {
        try {
            //拷贝屏幕到一个BufferedImage对象screenshot
            BufferedImage screenShot = (new Robot()).createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));

            //根据文件前缀变量和文件格式变量，自动生成文件名

            File f = new File(fileName, serialNum + StringUtil.DOT + imageFormat);
            while (f.isFile() && f.exists()) {
                serialNum++;
                f = new File(fileName, serialNum + StringUtil.DOT + imageFormat);
            }
            //将screenshot对象写入图像文件
            FileUtil.makeDirectory(f.getPath());
            ImageIO.write(screenShot, imageFormat, f);
            return f.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

    public static void main(String[] args) {
        GuiCamera cam = new GuiCamera("d:\\temp\\screen", "png");//
        cam.snapShot();
    }
}
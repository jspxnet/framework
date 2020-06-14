/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import java.awt.*;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;


public class ColorTranslate {
    ICC_Profile ICC_pf;
    ICC_ColorSpace ICC_ClSpace;
    //以下变量存储CMYK颜色值，取值为0到100
    int C = 9;
    int M = 9;
    int Y = 9;
    int K = 9;

    public ColorTranslate(String filename) {
        try {
            ICC_pf = ICC_Profile.getInstance(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ICC_ClSpace = new ICC_ColorSpace(ICC_pf);
    }

    //由RGB色彩空间变换到CMYK
    public float[] RGBtoCMYK(Color RGBColor) {
        float[] CMYKfloat = ICC_ClSpace.fromRGB(RGBColor.getRGBComponents(null));
        C = (int) (CMYKfloat[0] * 100);
        M = (int) (CMYKfloat[1] * 100);
        Y = (int) (CMYKfloat[2] * 100);
        K = (int) (CMYKfloat[3] * 100);
        return CMYKfloat;
    }

    //由CMYK色彩空间变换到RGB
    public Color CMYKtoRGB(float[] CMYKfloat) {
        return new Color(ICC_ClSpace, CMYKfloat, 1.0f);
    }

    public Color CMYKtoRGB() {
        float[] CMYKfloat = new float[4];
        CMYKfloat[0] = 0.01f * (float) C;
        CMYKfloat[1] = 0.01f * (float) M;
        CMYKfloat[2] = 0.01f * (float) Y;
        CMYKfloat[3] = 0.01f * (float) K;
        return new Color(ICC_ClSpace, CMYKfloat, 1.0f);
    }
}
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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-3-28
 * Time: 15:44:12
 * 色彩单元
 */
public final  class ColorUtil {
    private final static String[] DEEP_COLORS = new String[]{"#FF0000", "#00FF00", "#0000FF", "#6600CC", "#AFD8F8", "#F6BD0F", "#8BBA00", "#FF8E46", "#008E8E", "#D64646", "#8E468E", "#588526", "#B3AA00", "#008ED6", "#9D080D", "#A186BE", "#000000"};
    //private final static String[] shallowColors = new String[]{"#666666","#0099FF","#00FFFF","#FF00FF","#006600","#660000", "#F6BD0F", "#CC99FF","#FF66FF","#E1E9F4"};
    private final static Map<String, String> ZH_COLOR_MAP = new HashMap<String, String>();

    static {
        ZH_COLOR_MAP.put("黑", "#000000");
        ZH_COLOR_MAP.put("白", "#FFFFFF");
        ZH_COLOR_MAP.put("红", "#FF0000");
        ZH_COLOR_MAP.put("绿", "#00FF00");
        ZH_COLOR_MAP.put("蓝", "#0000FF");
        ZH_COLOR_MAP.put("黄", "#FFFF00");
        ZH_COLOR_MAP.put("紫", "#6600CC");
        ZH_COLOR_MAP.put("灰", "#666666");
        ZH_COLOR_MAP.put("淡蓝", "#0099FF");
        ZH_COLOR_MAP.put("冰绿", "#00FFFF");
        ZH_COLOR_MAP.put("粉红", "#FF00FF");
        ZH_COLOR_MAP.put("草绿", "#006600");
        ZH_COLOR_MAP.put("枣红", "#660000");
        ZH_COLOR_MAP.put("淡紫", "#CC99FF");
    }

    private ColorUtil() {

    }

    static public String toColorForZh(String zh) {
        return ZH_COLOR_MAP.get(zh);
    }

    public static Color[] getColorArray(int num) {
        Color[] result = new Color[num];
        String[] colorArray = getWebColorArray(num);
        for (int i = 0; i < num; i++) {
            result[i] = new Color(Integer.parseInt(HtmlUtil.deleteHtml(colorArray[i].replace("#", ""), 6, "").trim(), 16));
        }
        return result;
    }

    public static Color getRandomColor() {

        int i = RandomUtil.getRandomInt(0, DEEP_COLORS.length - 1);
        return new Color(Integer.parseInt(HtmlUtil.deleteHtml(DEEP_COLORS[i].replace("#", ""), 6, "").trim(), 16));
    }

    /**
     * 保证元素各不相同
     *
     * @param num 元素个数
     * @return 生成一个颜色数值
     */
    public static String[] getWebColorArray(int num) {
        String[] result = new String[num];
        int i;
        result[0] = getRandomWebColor();
        for (i = 1; i < num && i < DEEP_COLORS.length; i++) {
            result[i] = DEEP_COLORS[i - 1];
        }
        if (num > DEEP_COLORS.length) {
            while (i < num) {
                String nColor = getRandomWebColor();
                if (!ArrayUtil.inArray(result, nColor, true)) {
                    result[i] = nColor;
                    i++;
                }
            }
        }
        return result;
    }

    public static int randomNumber() {
        return (int) Math.floor(Math.random() * 256);
    }

    public static String getRandomWebColor() {
        StringBuilder sb = new StringBuilder("#");
        sb.append(Long.toHexString(randomNumber() - 1));
        sb.append(Long.toHexString(randomNumber() - 1));
        sb.append(Long.toHexString(randomNumber() - 1));
        return sb.toString().toUpperCase();
    }


    /**
     * @param color 原色
     * @return 安卓的整数转换为网页用的颜色
     * @throws Exception 异常
     */
    public static String toARGB(int color) throws Exception {

        String red = ObjectUtil.toString(ClassUtil.invokeStaticMethod("android.graphics.Color", "red", new Object[]{color}));  //android.graphics.Color.red(color));
        String green = ObjectUtil.toString(ClassUtil.invokeStaticMethod("android.graphics.Color", "green", new Object[]{color})); //Integer.toString(android.graphics.Color.green(color));
        String blue = ObjectUtil.toString(ClassUtil.invokeStaticMethod("android.graphics.Color", "blue", new Object[]{color})); //Integer.toString(android.graphics.Color.blue(color));
        return "#" + red + green + blue;
    }


}
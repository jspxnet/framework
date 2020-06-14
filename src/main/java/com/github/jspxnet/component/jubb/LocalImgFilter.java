/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.jubb;

import com.github.jspxnet.utils.StringUtil;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-4-1
 * Time: 12:26:39
 */
public class LocalImgFilter extends HTMLFilter {

    public LocalImgFilter(String s) {
        super(s);
    }

    public LocalImgFilter() {

    }


    private String[] img;

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    private String linkPage = "upload.jhtml?id=";

    public String getLinkPage() {
        return linkPage;
    }

    public void setLinkPage(String linkPage) {
        this.linkPage = linkPage;
    }

    @Override
    public String convertString() {
        return localImgeConverter();
    }


    //[local=400,300]0[/local]
    public String localImgeConverter() {
        if (img == null || img.length < 1) {
            return s;
        }
        Pattern pattern = compile("(\\[localimg=(.[^\\[]*)\\])(.[^\\[]*)(\\[\\/localimg\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String wh = matcher.group(2);
            int w = -1, h = -1;
            if (wh != null && wh.contains(",")) {
                w = StringUtil.toInt(StringUtil.substringBefore(wh, ","));
                h = StringUtil.toInt(StringUtil.substringAfterLast(wh, ","));
            } else if (wh != null && !wh.contains(",")) {
                w = StringUtil.toInt(wh);
                if (w > 0) {
                    h = w;
                }
            }
            if (w < 0) {
                w = h;
            }
            if (h < 0) {
                h = w;
            }
            int img_index = StringUtil.toInt(StringUtil.trim(matcher.group(3)));
            if (img_index >= img.length) {
                continue;
            }
            if (w > 700) {
                w = 600;
            }
            if (h > 700) {
                h = 600;
            }
            matcher.appendReplacement(stringbuffer, "<img src=\"" + img[img_index] + "\" width=\"" + w + "\" height=\"" + h + "\" border=\"0\"/>");
        }
        matcher.appendTail(stringbuffer);

        return stringbuffer.toString();
    }
}
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2004-4-21
 * Time: 19:57:44
 * JUBB
 */
public class FlashFilter extends HTMLFilter {
    private final static String PATTERN_FLASH_CONVERTER = "(\\[swf\\])(.[^\\[]*)(\\[\\/swf\\])";

    public FlashFilter(String s) {
        super(s);
    }

    public FlashFilter() {
    }

    @Override
    public String convertString() {
        return flashConverter();
    }

    private String flashConverter() {
        Pattern pattern = Pattern.compile(PATTERN_FLASH_CONVERTER, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String url = getSafetyUrl(matcher.group(2));
            if (!StringUtil.isNull(url)) {
                matcher.appendReplacement(stringbuffer, "<OBJECT codeBase=\"http://download.macromedia.com/pub/shockwave/cabs/msoa/swflash.cab#version=4,0,2,0\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=520 height=420><PARAM NAME=\"movie\" VALUE=\"" + url + "\"><PARAM NAME=\"quality\" VALUE=high><embed src=\"" + url + "\" quality=\"high\" pluginspage=\"http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash' type='application/x-shockwave-msoa\" width=520 height=420>" + url + "</embed></OBJECT>");
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}
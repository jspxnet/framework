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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2004-4-21
 * Time: 19:57:44
 * JUBB
 */
public class ObjectFilter extends HTMLFilter {
    public ObjectFilter(String s) {
        super(s);
    }

    public ObjectFilter() {
    }

    @Override
    public String convertString() {
        s = QTConverter();
        s = DIRConverter();
        s = MPConverter();
        s = RMConverter();
        return s;
    }

    private String RMConverter() {
        String s = "\\[rm=([0-5]?[0-9]{0,2}),([0-5]?[0-9]{0,2})\\]([http://|rtsp://].*)\\[\\/rm]";
        Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.s);
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = matcher.find();
        if (flag) {
            String url = getSafetyUrl(matcher.group(3));
            matcher.appendReplacement(stringbuffer, "<OBJECT classid=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA class=OBJECT id=RAOCX width=" + matcher.group(1) + " height=" + matcher.group(2) + "><PARAM NAME=SRC VALUE=" + url + "><PARAM NAME=CONSOLE VALUE=Clip1><PARAM NAME=CONTROLS VALUE=imagewindow><PARAM NAME=AUTOSTART VALUE=true><embed src=" + url + " type=audio/x-pn-realaudio-plugin width=" + matcher.group(1) + " height=" + matcher.group(2) + " controls=All console=cons></embed></OBJECT>");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

    private String MPConverter() {
        String s = "\\[rm=([0-5]?[0-9]{0,2}),([0-5]?[0-9]{0,2})\\]([http://|mms://|mmst://|mmsu://].*)\\[\\/rm]";
        Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.s);
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = matcher.find();
        if (flag) {
            String url = getSafetyUrl(matcher.group(3));
            matcher.appendReplacement(stringbuffer, "<object align=middle classid=CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95 class=OBJECT id=MediaPlayer width=" + matcher.group(1) + " height=" + matcher.group(2) + " ><param name=ShowStatusBar value=-1><param name=Filename value=" + url + "><embed type=application/x-oleobject codebase=http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701 flename=mp src=" + url + " width=" + matcher.group(1) + " height=" + matcher.group(2) + "></embed></object>");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

    private String QTConverter() {
        String s = "\\[qt=([0-5]?[0-9]{0,2}),([0-5]?[0-9]{0,2})\\](http://.*)\\[\\/qt]";
        Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.s);
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = matcher.find();
        if (flag) {
            String url = getSafetyUrl(matcher.group(3));
            matcher.appendReplacement(stringbuffer, "<embed src=" + url + " width=" + matcher.group(1) + " height=" + matcher.group(2) + " autoplay=true loop=false controller=true playeveryframe=false cache=false scale=TOFIT bgcolor=#000000 kioskmode=false targetcache=false pluginspage=http://www.apple.com/quicktime/>");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

    private String DIRConverter() {
        String s = "\\[dir=([0-5]?[0-9]{0,2}),([0-5]?[0-9]{0,2})\\](http://.*)\\[\\/dir]";
        Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.s);
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = matcher.find();
        if (flag) {
            String url = getSafetyUrl(matcher.group(3));
            matcher.appendReplacement(stringbuffer, "<object classid=clsid:166B1BCA-3F9C-11CF-8075-444553540000 codebase=http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=7,0,2,0 width=" + matcher.group(1) + " height=" + matcher.group(2) + "><param name=src value=" + url + "><embed src=" + url + " pluginspage=http://www.macromedia.com/shockwave/download/ width=" + matcher.group(1) + " height=" + matcher.group(2) + "></embed></object>");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}
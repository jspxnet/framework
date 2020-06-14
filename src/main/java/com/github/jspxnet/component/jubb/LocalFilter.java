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

import static java.util.regex.Pattern.compile;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 12-12-4
 * Time: 下午11:53
 * 载入本地上传的文件
 */
public class LocalFilter extends HTMLFilter {

    public LocalFilter(String s) {
        super(s);
    }

    public LocalFilter() {

    }


    private String downloadLink = "upload.jhtml?id=";
    private String fileType = StringUtil.empty;

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String convertString() {
        return localImgeConverter();
    }


    //[local=id]0[/local]
    public String localImgeConverter() {
        Pattern pattern = compile("(\\[local=(.[^\\[]*)\\])(.[^\\[]*)(\\[\\/local\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String wh = matcher.group(2);
            String str = StringUtil.empty;
            if (!StringUtil.isNull(fileType)) {
                str = "<img src=\"/pimg/filetype/" + fileType + ".gif\" border=\"0\"/>";
            }
            str = str + "<a href=\"" + downloadLink + wh + "\">" + matcher.group(3) + "</a>";
            matcher.appendReplacement(stringbuffer, str);
        }
        matcher.appendTail(stringbuffer);

        return stringbuffer.toString();
    }
}
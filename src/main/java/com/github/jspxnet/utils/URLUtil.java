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

import com.github.jspxnet.boot.environment.Environment;

import java.io.File;
import java.net.*;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-9-22
 * Time: 10:26:32
 */
public class URLUtil {
    private URLUtil() {

    }


    /**
     * 只转换中文部分
     *
     * @param text   url 参数
     * @param encode 编码
     * @return 转换编码
     */
    public static String getURLEncoder(String text, String encode) {
        if (text == null) {
            return StringUtil.empty;
        }
        if (StringUtil.isNull(encode)) {
            encode = Environment.defaultEncode;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sb.length(); i++) {
            String xx = sb.substring(i, i + 1);
            if (xx.matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    result.append(URLEncoder.encode(xx, encode));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                result.append(xx);
            }
        }
        sb.setLength(0);
        return StringUtil.replace(result.toString(), " ", "%20");
    }

    public static String getEncoder(String text, String encode) {
        if (text == null) {
            return StringUtil.empty;
        }
        try {
            return URLEncoder.encode(text, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String getURLDecoder(String text, String encode) {
        if (text == null) {
            return StringUtil.empty;
        }
        try {
            return URLDecoder.decode(text, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * @param url 路径
     * @return String  得到url文件类型
     */
    static public String getFileType(String url) {
        String fileName = getFileName(url);
        if (!url.contains(".")) {
            return StringUtil.empty;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * @param url     路径
     * @param newType 新的类型
     * @return 替换URL的文件类型
     */
    static public String replaceURLFileType(String url, String newType) {
        if (url == null) {
            return StringUtil.empty;
        }
        String oldType = getFileType(url);
        int post = url.lastIndexOf(oldType);
        if (post == -1) {
            return url;
        }
        return url.substring(0, post) + newType;
    }

    /**
     * @param url 路径
     * @return 文件名称  http://www.jspx.net/xxx/name.jsp  返回 name
     */
    static public String getFileName(String url) {
        if (url==null)
        {
            return StringUtil.empty;
        }
        int wHao = url.indexOf("?");
        if (wHao != -1) {
            url = url.substring(0, wHao);
        }
        int len = url.lastIndexOf("/");
        if (len != -1) {
            url = url.substring(len + 1);
        }
        return url;
    }

    /**
     * @param url 路径
     * @return 删除queryString 部分(删除参数部分)
     */
    public static String deleteQueryString(String url) {
        int w = url.indexOf("?");
        if (w != -1) {
            url = url.substring(0, w);
        }
        return url;
    }

    /**
     * @param url 路径
     * @return 得到URL名称部分 不代后缀 http://www.jspx.net/history/gfxw/2009/xxx.html"  返回xxx
     */
    static public String getFileNamePart(String url) {
        String fileName = getFileName(url);
        if (null == fileName) {
            return StringUtil.empty;
        }
        if (!fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * @param url 完整的URL路径
     * @return 返回URL 不代文件名称部分 例如:http://www.jspx.net/xxx/
     */
    public static String getURLPath(String url) {
        if (StringUtil.isNull(url)) {
            return StringUtil.empty;
        }
        int whao = url.indexOf("?");
        if (whao != -1) {
            url = url.substring(0, whao);
        }
        if (url.contains("://") && StringUtil.countMatches(url, "/") <= 2) {
            return url + "/";
        }
        return url.substring(0, url.lastIndexOf("/") + 1);
    }


    /**
     * @param str 完整的域名地址
     * @return 返回域名名称部分代http的 例如:http://www.jspx.net
     */
    static public String getHostURL(String str) {
        if (StringUtil.isNull(str)) {
            return StringUtil.empty;
        }
        String result = StringUtil.empty;
        if (str.startsWith("http://")) {
            result = str.substring(0, 7) + StringUtil.substringBefore(str.substring(7), "/");
        }
        if (str.startsWith("https://")) {
            result = str.substring(0, 8) + StringUtil.substringBefore(str.substring(8), "/");
        }
        return result;
    }

    /**
     * @param str 完整的url地址
     * @return 返回域名名称部分
     */
    static public String getHostName(String str) {
        if (StringUtil.isNull(str)) {
            return StringUtil.empty;
        }
        String result = StringUtil.empty;
        if (str.startsWith("http://")) {
            result = StringUtil.substringBefore(str.substring(7), "/").trim();
        }
        if (str.startsWith("https://")) {
            result = StringUtil.substringBefore(str.substring(8), "/").trim();
        }
        if (result.contains(":")) {
            result = StringUtil.substringBefore(result, ":");
        }
        return result;
    }

    /**
     * @param urlFileName url 路径
     * @param fileType    不转换文件类型
     * @param querystring url 参数
     * @return String
     */
    static public String getMakeHtmlFileName(String urlFileName, String fileType, String querystring) {
        if (urlFileName.endsWith(".css") || urlFileName.endsWith(".gif")
                || urlFileName.endsWith(".png") || urlFileName.endsWith(".zip")
                || urlFileName.endsWith(".rar")
                || urlFileName.endsWith(".jpg")
                || urlFileName.endsWith(".bmp")
                || urlFileName.endsWith(".swf")
                || urlFileName.endsWith(".js")
        ) {
            return urlFileName;
        }

        String result = urlFileName;
        if (!StringUtil.isNull(querystring)) {
            result = urlFileName + "?" + querystring;
        }

        result = StringUtil.replace(result, "?", ",");
        result = StringUtil.replace(result, "=", "_");
        result = StringUtil.replace(result, "&", ";");
        result = StringUtil.replace(result, ".", "-");
        return result + "." + fileType;
    }

    /**
     * @param urlFileName rul 路径
     * @param fileType    文件类型
     * @return String  转换为描述
     */
    static public String getDecryptFileName(String urlFileName, String fileType) {
        if (urlFileName == null) {
            return StringUtil.empty;
        }
        if (!urlFileName.endsWith("." + fileType)) {
            return urlFileName;
        }
        String result = urlFileName.substring(0, urlFileName.length() - 5);
        result = StringUtil.replace(result, "-", ".");
        result = StringUtil.replace(result, ";", "&");
        result = StringUtil.replace(result, "_", "=");
        return StringUtil.replace(result, ",", "?");
    }

    /**
     * @param file 路径
     * @return 以 file://d:/sss/xxx.html 的方式返回路径
     */
    static public String getFileURL(String file) {
        if (StringUtil.isNull(file)) {
            return "file:///";
        }
        File f = new File(file);
        return "file:///" + f.toURI().getPath();
    }

    /**
     * @param curl 必须以 http 的路径
     * @return 得到www的域名
     */
    public static String getDomain(String curl) {
        if (curl == null) {
            return StringUtil.empty;
        }
        if (!curl.startsWith("http") || curl.startsWith("ftl")) {
            curl = "http://" + curl;
        }
        URL url;
        String q = "";
        try {
            url = new URL(curl);
            q = url.getHost().toLowerCase();
        } catch (MalformedURLException e) {
            return StringUtil.empty;
        }
        return q;
    }

    /**
     * @param curl url
     * @return 得到子域名前缀，第一个点前边
     */
    public static String getSubdomainPrefix(String curl) {
        if (curl == null) {
            return StringUtil.empty;
        }
        String domain = getDomain(curl);
        return StringUtil.substringBefore(domain, StringUtil.DOT);
    }

    /**
     * @param url 必须以 http 的路径
     * @return 得到根域名
     */
    public static String getTopDomain(String url) {
        final String patternReg = "[^\\.]+(\\.com|\\.gov.cn|\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.net|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)";
        try {
            String host = getDomain(url);
            if (StringUtil.isIPAddress(host)) {
                return host;
            }
            host = new URL("http://" + host).getHost().toLowerCase();
            Pattern pattern = Pattern.compile(patternReg);
            Matcher matcher = pattern.matcher(host);
            while (matcher.find()) {
                return StringUtil.trim(matcher.group());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }


    /**
     * 判断是否为外部url路径
     * @param str 路径
     * @return   判断是否为外部url路径
     */
    static public boolean isUrl(String str) {
        if (StringUtil.isNull(str)) {
            return false;
        }
        return str.startsWith("http://") || str.startsWith("https://") || str.startsWith("ftp://") || str.startsWith("ftps://") || str.startsWith("file://");
    }
}
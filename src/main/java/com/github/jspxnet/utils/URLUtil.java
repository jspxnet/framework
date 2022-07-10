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
public final class URLUtil {
    private URLUtil() {

    }


    /**
     * 只转换中文部分
     *
     * @param text   url 参数
     * @param encode 编码
     * @return 转换编码
     */
    public static String getUrlEncoder(String text, String encode) {
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

    public static String getUrlDecoder(String text, String encode) {
        if (text == null) {
            return StringUtil.empty;
        }
        try {
            if (text.contains("%"))
            {
                text = text.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            }
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
        if (!url.contains(StringUtil.DOT)) {
            return StringUtil.empty;
        }
        return fileName.substring(fileName.lastIndexOf(StringUtil.DOT) + 1);
    }

    /**
     * @param url     路径
     * @param newType 新的类型
     * @return 替换URL的文件类型
     */
    static public String replaceUrlFileType(String url, String newType) {
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
        if (!fileName.contains(StringUtil.DOT)) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf(StringUtil.DOT));
    }

    /**
     * @param url 完整的URL路径
     * @return 返回URL 不代文件名称部分 例如:http://www.jspx.net/xxx/
     */
    public static String getUrlPath(String url) {
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
     *
     * @param url rul
     * @return 删除文件路径的后缀
     */
    public static String deleteUrlSuffix(String url) {
        if (StringUtil.isNull(url)) {
            return StringUtil.empty;
        }
        int whao = url.indexOf("?");
        if (whao != -1) {
            url = url.substring(0, whao);
        }
        if (url.contains(StringUtil.DOT))
        {
            url = url.substring(0, url.lastIndexOf(StringUtil.DOT));
        }
        return url;
    }
    /**
     * @param str 完整的域名地址
     * @return 返回域名名称部分代http的 例如:http://www.jspx.net
     */
    static public String getHostUrl(String str) {
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
        result = StringUtil.replace(result, StringUtil.EQUAL, "_");
        result = StringUtil.replace(result, StringUtil.AND, ";");
        result = StringUtil.replace(result, StringUtil.DOT, "-");
        return result + StringUtil.DOT + fileType;
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
        if (!urlFileName.endsWith(StringUtil.DOT + fileType)) {
            return urlFileName;
        }
        String result = urlFileName.substring(0, urlFileName.length() - 5);
        result = StringUtil.replace(result, "-", StringUtil.DOT);
        result = StringUtil.replace(result, ";", StringUtil.AND);
        result = StringUtil.replace(result, "_", StringUtil.EQUAL);
        return StringUtil.replace(result, ",", "?");
    }

    /**
     * @param file 路径
     * @return 以 file://d:/sss/xxx.html 的方式返回路径
     */
    static public String getFileUrl(String file) {
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
            if (matcher.find()) {
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



    /**
     *  没有后缀加后缀
     * @param hessianUrl rpc调用路径
     * @param suffix 调用路径
     * @return 返回
     */
    public static String getFixSuffix(String hessianUrl,String suffix)
    {
        if (!hessianUrl.toLowerCase().endsWith(StringUtil.DOT + suffix.toLowerCase()))
        {
            return hessianUrl + StringUtil.DOT + suffix;
        }
        return hessianUrl;
    }

    /**
     * 得到Hessian url的路由地址
     * @param hessianUrl rpc调用路径
     * @param domain  默认域名
     * @param namespace 默认路由网关
     * @param routesUrl  路由路径
     * @return 得到Hessian url的路由地址
     */
    public static String getFixHessianUrl(String hessianUrl,String domain,String namespace,String routesUrl)
    {
        String result;
        if (StringUtil.isEmpty(routesUrl))
        {
            if (domain.endsWith("/"))
            {
                return domain + StringUtil.replace(hessianUrl,"//","/");
            }
            return domain + StringUtil.replace( "/" + hessianUrl,"//","/");
        }

        if (hessianUrl.startsWith("/"))
        {
            result =  StringUtil.replaceOnce(hessianUrl,"/" + namespace,routesUrl);
        } else
        {
            result = StringUtil.replaceOnce(hessianUrl,namespace,routesUrl);
        }
        if (!result.startsWith("http"))
        {
            result = domain + StringUtil.replace("/" +  result,"//","/");
        }
        return result;
    }


    /**
     * 得到namespace
     *
     * @param servletPath 传入 request.servletPath
     * @return namespace 命名空间
     */
    public static String getNamespace(String servletPath) {
        String namespace = URLUtil.getUrlPath(servletPath);
        if (namespace.endsWith(StringUtil.ASTERISK)) {
            namespace = namespace.substring(0, namespace.length() - 1);
        }
        if (namespace.endsWith(StringUtil.BACKSLASH)) {
            namespace = namespace.substring(0, namespace.length() - 1);
        }
        if (namespace.startsWith(StringUtil.BACKSLASH)) {
            namespace = namespace.substring(1);
        }
        if (StringUtil.BACKSLASH.equals(namespace)) {
            return StringUtil.empty;
        }
        return namespace;
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param servletPath 得到方法
     * @return 得到更目录
     */
    public static String getRootNamespace(String servletPath) {
        if (servletPath == null) {
            return StringUtil.empty;
        }
        if (!servletPath.contains("/")) {
            return servletPath;
        }
        if (servletPath.startsWith("http")) {
            servletPath = StringUtil.substringAfter(servletPath, URLUtil.getHostUrl(servletPath));
        }
        String namespace = URLUtil.getUrlPath(servletPath);
        if (namespace.startsWith("/")) {
            namespace = namespace.substring(1);
        }
        if (namespace.contains("/")) {
            return StringUtil.substringBefore(namespace, "/");
        }
        return namespace;
    }
   public static void main(String[] args) {
        String str = "测试中文1%。此的AA";
        String out1 = getUrlEncoder(str,"UTF8");
        System.out.println(out1);
        System.out.println(getUrlDecoder(out1,"UTF8"));
    }
}
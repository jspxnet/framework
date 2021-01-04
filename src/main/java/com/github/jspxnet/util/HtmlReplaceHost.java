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

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.parse.html.*;
import com.github.jspxnet.utils.*;

import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-1-14
 * Time: 下午3:14
 * 使用在代码服务器上
 */
public class HtmlReplaceHost {
    private String proxyUrl = StringUtil.empty;
    private String oldUrl = StringUtil.empty;
    private String oldHost = StringUtil.empty;
    private String oldUrlPath = StringUtil.empty;
    private String html = StringUtil.empty;
    private String urlName = StringUtil.empty;
    private String htmlEncode = "BIG5";
    private boolean tranPhoto = false;
    private static final String PATTERN_ATTRIBUTE = "<.+?>";

    public HtmlReplaceHost() {

    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(final String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getOldUrl() {
        return oldUrl;
    }

    public boolean isTranPhoto() {
        return tranPhoto;
    }

    public void setTranPhoto(boolean tranPhoto) {
        this.tranPhoto = tranPhoto;
    }

    public void setOldUrl(String oldUrl) {

        this.oldUrl = oldUrl;
        this.urlName = URLUtil.getFileName(oldUrl);
        oldHost = URLUtil.getHostUrl(oldUrl);
        oldUrlPath = URLUtil.getURLPath(oldUrl);
        if ("http://".equalsIgnoreCase(oldUrlPath)) {
            oldUrlPath = oldHost;
        }
        if (!oldUrlPath.endsWith("/")) {
            oldUrlPath = oldUrlPath + "/";
        }
    }

    public String getHtmlEncode() {
        return htmlEncode;
    }

    public void setHtmlEncode(String htmlEncode) {
        this.htmlEncode = htmlEncode;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getReplacedString() throws Exception {
        html = HtmlUtil.deleteNotes(html);
        String result = html;

        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("img", ImgTag.class.getName());
        xmlEngine.putTag("meta", MetaTag.class.getName());
        xmlEngine.putTag("object", ObjectTag.class.getName());
        xmlEngine.putTag("a", ATag.class.getName());
        xmlEngine.putTag("link", LinkTag.class.getName());
        xmlEngine.putTag("script", ScriptTag.class.getName());
        List<TagNode> list = xmlEngine.getTagNodes(html);
        for (TagNode node : list) {
            if (node instanceof LinkTag) {
                LinkTag linkTag = (LinkTag) node;
                String linkUrl = linkTag.getHref();

                if (StringUtil.isNull(linkUrl) || "#".equals(linkUrl) || linkUrl.toLowerCase().startsWith("mailto")) {
                    continue;
                }

                String newURL = StringUtil.empty;
                if (linkUrl.startsWith("?")) {
                    newURL = proxyUrl + oldUrlPath + urlName + linkUrl;
                } else if (linkUrl.startsWith("http://")) {
                    newURL = linkUrl;
                } else if (linkUrl.startsWith("/")) {
                    newURL = oldHost + linkUrl;
                } else {
                    newURL = oldUrlPath + linkUrl;
                }
                String source = linkTag.getSource();
                String oldHtml = StringUtil.replace(source, linkUrl, newURL);
                result = StringUtil.replace(result, source, oldHtml);
            } else if (node instanceof ScriptTag) {
                ScriptTag scriptTag = (ScriptTag) node;
                String linkUrl = scriptTag.getSrc();
                String body = scriptTag.getBody();

                if (!StringUtil.isNull(body)) {
                    StringBuilder htmlOut = new StringBuilder(body);
                    int x = htmlOut.indexOf("window.open(");
                    while (x != -1 && x < htmlOut.length() - 20) {
                        for (int i = x + 12; i < htmlOut.length(); i++) {
                            String tempStart = htmlOut.substring(i, i + 1);
                            if ("'".equals(tempStart) || "\"".equals(tempStart)) {
                                String tempHead = htmlOut.substring(i + 1);
                                if (tempHead.startsWith("http")) {
                                    htmlOut = htmlOut.insert(i, "'" + proxyUrl + "'+");
                                } else if (tempHead.startsWith("/")) {
                                    htmlOut = htmlOut.insert(i, "'" + proxyUrl + oldHost + "'+");
                                } else {
                                    htmlOut = htmlOut.insert(i, "'" + proxyUrl + oldUrlPath + "'+");
                                }
                                x = i + proxyUrl.length();
                                break;
                            }
                            if (tempStart.equals(StringUtil.SEMICOLON)) {
                                x = i + proxyUrl.length();
                                break;
                            }
                        }
                        if (x > htmlOut.length()) {
                            break;
                        }
                        x = htmlOut.indexOf("window.open(", x);
                    }
                    result = StringUtil.replace(result, body, htmlOut.toString());
                    //result = StringUtil.replace(result, source, oldHtml);
                }
                if (!StringUtil.isNull(linkUrl)) {
                    String newURL = StringUtil.empty;
                    if (linkUrl.startsWith("http://")) {
                        newURL = linkUrl;
                    } else if (linkUrl.startsWith("/")) {
                        newURL = oldHost + linkUrl;
                    } else {
                        newURL = oldUrlPath + linkUrl;
                    }
                    String source = scriptTag.getSource();
                    String oldHtml = StringUtil.replace(source, linkUrl, proxyUrl + newURL);
                    result = StringUtil.replace(result, source, oldHtml);

                }


            } else if (node instanceof ImgTag) {
                ImgTag imgTag = (ImgTag) node;
                String linkUrl = imgTag.getSrc();
                if (StringUtil.isNull(linkUrl)) {
                    continue;
                }
                String newURL = StringUtil.empty;
                if (linkUrl.startsWith("http://")) {
                    newURL = linkUrl;
                } else if (linkUrl.startsWith("/")) {
                    newURL = oldHost + linkUrl;
                } else {
                    newURL = oldUrlPath + linkUrl;
                }
                if (tranPhoto) {
                    String photoFileName = URLUtil.getFileName(newURL);
                    String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                    String photoType = URLUtil.getFileType(photoFileName);
                    newURL = URLUtil.getURLPath(newURL) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                }

                String source = imgTag.getSource();
                String oldHtml = StringUtil.replace(source, linkUrl, newURL);
                result = StringUtil.replace(result, source, oldHtml);
            } else if (node instanceof ObjectTag) {
                ObjectTag objectTag = (ObjectTag) node;
                String linkUrl = objectTag.getData();
                String source = objectTag.getSource();
                List<TagNode> childObjects = objectTag.getObjectTags();
                if (!childObjects.isEmpty()) {

                    for (TagNode node2 : childObjects) {
                        ObjectTag objectTag2 = (ObjectTag) node2;
                        String source2 = objectTag2.getSource();
                        String linkUrlData = objectTag2.getData();
                        if (StringUtil.isNull(linkUrlData)) {
                            continue;
                        }
                        String newURL2 = StringUtil.empty;
                        if (linkUrlData.startsWith("http://")) {
                            newURL2 = linkUrlData;
                        } else if (linkUrlData.startsWith("/")) {
                            newURL2 = oldHost + linkUrlData;
                        } else {
                            newURL2 = oldUrlPath + linkUrlData;
                        }

                        if (tranPhoto) {
                            String photoFileName = URLUtil.getFileName(newURL2);
                            String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                            String photoType = URLUtil.getFileType(photoFileName);
                            newURL2 = URLUtil.getURLPath(newURL2) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                        }
                        String oldHtml = StringUtil.replace(source2, linkUrlData, newURL2);
                        result = StringUtil.replace(result, source2, oldHtml);
                    }
                }

                XmlEngineImpl xmlEngine2 = new XmlEngineImpl();
                xmlEngine2.putTag("param", ParamTag.class.getName());
                List<TagNode> list2 = xmlEngine2.getTagNodes(result);
                for (TagNode node2 : list2) {
                    ParamTag paramTag = (ParamTag) node2;
                    if (!("movie".equalsIgnoreCase(paramTag.getName()) || "expressinstall".equalsIgnoreCase(paramTag.getName()))) {
                        continue;
                    }
                    String body = paramTag.getSource();
                    String linkUrl2 = paramTag.getValue();
                    if (linkUrl2 == null || "#".equals(linkUrl2)) {
                        continue;
                    }
                    String newURL2 = StringUtil.empty;
                    if (linkUrl2.startsWith("http://")) {
                        newURL2 = linkUrl2;
                    } else if (linkUrl2.startsWith("/")) {
                        newURL2 = oldHost + linkUrl2;
                    } else {
                        newURL2 = oldUrlPath + linkUrl2;
                    }

                    if (tranPhoto) {
                        String photoFileName = URLUtil.getFileName(newURL2);
                        String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                        String photoType = URLUtil.getFileType(photoFileName);
                        newURL2 = URLUtil.getURLPath(newURL2) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                    }
                    String oldHtml = StringUtil.replace(body, linkUrl2, newURL2);
                    result = StringUtil.replace(result, body, oldHtml);
                }

                if (linkUrl == null || "#".equals(linkUrl)) {
                    continue;
                }
                String newURL = StringUtil.empty;
                if (linkUrl.startsWith("http://")) {
                    newURL = linkUrl;
                } else if (linkUrl.startsWith("/")) {
                    newURL = oldHost + linkUrl;
                } else {
                    newURL = oldUrlPath + linkUrl;
                }

                if (tranPhoto) {
                    String photoFileName = URLUtil.getFileName(newURL);
                    String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                    String photoType = URLUtil.getFileType(photoFileName);
                    newURL = URLUtil.getURLPath(newURL) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                }
                String oldHtml = StringUtil.replace(source, linkUrl, newURL);
                result = StringUtil.replace(result, source, oldHtml);

            } else if (node instanceof ParamTag) {
                ParamTag paramTag = (ParamTag) node;
                if (!("movie".equalsIgnoreCase(paramTag.getName()) || "expressinstall".equalsIgnoreCase(paramTag.getName()))) {
                    continue;
                }
                String linkUrl = paramTag.getValue();
                if (linkUrl == null || "#".equals(linkUrl)) {
                    continue;
                }
                String newURL = StringUtil.empty;
                if (linkUrl.startsWith("http://")) {
                    newURL = linkUrl;
                } else if (linkUrl.startsWith("/")) {
                    newURL = oldHost + linkUrl;
                } else {
                    newURL = oldUrlPath + linkUrl;
                }

                String source = paramTag.getSource();
                String oldHtml = StringUtil.replace(source, linkUrl, newURL);
                result = StringUtil.replace(result, source, oldHtml);
            } else if (node instanceof ATag) {
                ATag aTag = (ATag) node;
                String linkUrl = aTag.getHref();
                String steady = aTag.getStringAttribute("steady");
                if (StringUtil.toBoolean(steady)) {
                    continue;
                }
                if (linkUrl == null || "#".equals(linkUrl)) {
                    continue;
                }
                if (linkUrl.toLowerCase().startsWith("javascript")) {
                    continue;
                }

                String newURL = StringUtil.empty;
                if (linkUrl.startsWith("?")) {
                    newURL = oldUrlPath + urlName + linkUrl;
                } else if (linkUrl.startsWith("http://")) {
                    newURL = linkUrl;
                } else if (linkUrl.startsWith("/")) {
                    newURL = oldHost + linkUrl;
                } else {
                    newURL = oldUrlPath + linkUrl;
                }
                newURL = URLEncoder.encode(newURL, htmlEncode);
                String fileType = URLUtil.getFileType(linkUrl);
                if (!ArrayUtil.inArray(new String[]{"doc", "rar", "zip", "exe", "cab", "ocx", "docx", "xls", "gif", "jpg", "png", "7z", "7z"}, fileType, true)) {
                    newURL = proxyUrl + newURL;
                }
                String source = aTag.getSource();
                String oldHtml = StringUtil.replace(source, linkUrl, newURL);
                result = StringUtil.replace(result, source, oldHtml);
                //---------------------------
                XmlEngineImpl xmlEngine2 = new XmlEngineImpl();
                xmlEngine2.putTag("img", ImgTag.class.getName());
                List<TagNode> list2 = xmlEngine2.getTagNodes(source);
                for (TagNode node2 : list2) {

                    ImgTag imgTag = (ImgTag) node2;
                    String linkUrl2 = imgTag.getSrc();
                    if (StringUtil.isNull(linkUrl2)) {
                        continue;
                    }
                    String newURL2 = StringUtil.empty;
                    if (linkUrl2.startsWith("?")) {
                        newURL2 = proxyUrl + oldUrlPath + urlName + linkUrl2;
                    } else if (linkUrl2.startsWith("http://")) {
                        newURL2 = linkUrl2;
                    } else if (linkUrl2.startsWith("/")) {
                        newURL2 = oldHost + linkUrl2;
                    } else {
                        newURL2 = oldUrlPath + linkUrl2;
                    }
                    if (tranPhoto) {
                        String photoFileName = URLUtil.getFileName(newURL2);
                        String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                        String photoType = URLUtil.getFileType(photoFileName);
                        newURL2 = URLUtil.getURLPath(newURL) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                    }
                    String source2 = imgTag.getSource();
                    String oldHtml2 = StringUtil.replace(source2, linkUrl2, newURL2);
                    result = StringUtil.replace(result, source2, oldHtml2);
                }
            } else if (node instanceof MetaTag) {
                MetaTag metaTag = (MetaTag) node;
                if (!"Content-Type".equalsIgnoreCase(metaTag.getHttpEquiv())) {
                    continue;
                }
                String source = metaTag.getSource();
                result = StringUtil.replace(result, source, "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=" + htmlEncode + "\" />");
            }
        }
        list.clear();
        //flash有嵌入情况,在解析一次
        xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("object", ObjectTag.class.getName());
        list = xmlEngine.getTagNodes(result);
        for (TagNode node : list) {
            if (node instanceof ObjectTag) {
                ObjectTag objectTag = (ObjectTag) node;
                String linkUrl = objectTag.getData();
                //  String source = objectTag.getSource();
                List<TagNode> childObjects = objectTag.getObjectTags();
                if (!childObjects.isEmpty()) {

                    for (TagNode node2 : childObjects) {
                        ObjectTag objectTag2 = (ObjectTag) node2;
                        String source2 = objectTag2.getSource();
                        String linkUrlData = objectTag2.getData();
                        if (StringUtil.isNull(linkUrlData)) {
                            continue;
                        }
                        String newURL2 = StringUtil.empty;
                        if (linkUrlData.startsWith("http://")) {
                            newURL2 = linkUrlData;
                        } else if (linkUrlData.startsWith("/")) {
                            newURL2 = oldHost + linkUrlData;
                        } else {
                            newURL2 = oldUrlPath + linkUrlData;
                        }

                        if (tranPhoto) {
                            String photoFileName = URLUtil.getFileName(newURL2);
                            String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                            String photoType = URLUtil.getFileType(photoFileName);
                            newURL2 = URLUtil.getURLPath(newURL2) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                        }
                        String oldHtml = StringUtil.replace(source2, linkUrlData, newURL2);
                        result = StringUtil.replace(result, source2, oldHtml);
                    }
                }

                XmlEngineImpl xmlEngine2 = new XmlEngineImpl();
                xmlEngine2.putTag("param", ParamTag.class.getName());
                List<TagNode> list2 = xmlEngine2.getTagNodes(result);
                for (TagNode node2 : list2) {
                    ParamTag paramTag = (ParamTag) node2;
                    if (!("movie".equalsIgnoreCase(paramTag.getName()) || "expressinstall".equalsIgnoreCase(paramTag.getName()))) {
                        continue;
                    }
                    String body = paramTag.getSource();
                    String linkUrl2 = paramTag.getValue();
                    if (linkUrl2 == null || "#".equals(linkUrl2)) {
                        continue;
                    }
                    String newURL2 = StringUtil.empty;
                    if (linkUrl2.startsWith("http://")) {
                        newURL2 = linkUrl2;
                    } else if (linkUrl2.startsWith("/")) {
                        newURL2 = oldHost + linkUrl2;
                    } else {
                        newURL2 = oldUrlPath + linkUrl2;
                    }

                    if (tranPhoto) {
                        String photoFileName = URLUtil.getFileName(newURL2);
                        String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                        String photoType = URLUtil.getFileType(photoFileName);
                        newURL2 = URLUtil.getURLPath(newURL2) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                    }
                    String oldHtml = StringUtil.replace(body, linkUrl2, newURL2);
                    result = StringUtil.replace(result, body, oldHtml);
                }

                if (linkUrl == null || "#".equals(linkUrl)) {
                    continue;
                }
                String newURL = StringUtil.empty;
                if (linkUrl.startsWith("http://")) {
                    newURL = linkUrl;
                } else if (linkUrl.startsWith("/")) {
                    newURL = oldHost + linkUrl;
                } else {
                    newURL = oldUrlPath + linkUrl;
                }

                if (tranPhoto) {
                    String photoFileName = URLUtil.getFileName(newURL);
                    String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                    String photoType = URLUtil.getFileType(photoFileName);
                    newURL = URLUtil.getURLPath(newURL) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
                }
                String oldHtml = StringUtil.replace(objectTag.getSource(), linkUrl, newURL);
                result = StringUtil.replace(result, objectTag.getSource(), oldHtml);
            }
        }
        //-------------
        xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("form", FormTag.class.getName());
        list = xmlEngine.getTagNodes(result);
        for (TagNode node : list) {
            FormTag formTag = (FormTag) node;
            String linkUrl = formTag.getAction();
            if (linkUrl == null || "#".equals(linkUrl)) {
                continue;
            }
            String newURL = StringUtil.empty;
            if (linkUrl.startsWith("http://")) {
                newURL = linkUrl;
            } else if (linkUrl.startsWith("/")) {
                newURL = oldHost + linkUrl;
            } else {
                newURL = oldUrlPath + linkUrl;
            }
            String source = formTag.getSource();
            String oldHtml2 = StringUtil.replace(source, linkUrl, newURL);
            result = StringUtil.replace(result, source, oldHtml2);
        }
        //FormTag

        xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("iframe", IframeTag.class.getName());
        list = xmlEngine.getTagNodes(result);
        for (TagNode node : list) {
            IframeTag iframeTag = (IframeTag) node;
            String linkUrl = iframeTag.getSrc();
            if (linkUrl == null || "#".equals(linkUrl)) {
                continue;
            }
            String newURL = StringUtil.empty;
            if (linkUrl.startsWith("http://")) {
                newURL = linkUrl;
            } else if (linkUrl.startsWith("/")) {
                newURL = oldHost + linkUrl;
            } else {
                newURL = oldUrlPath + linkUrl;
            }
            newURL = URLEncoder.encode(newURL, htmlEncode);
            String source = iframeTag.getSource();
            String oldHtml2 = StringUtil.replace(source, linkUrl, proxyUrl + newURL);
            result = StringUtil.replace(result, source, oldHtml2);
        }
        //IframeTag


        //background
        Pattern pattern = Pattern.compile(PATTERN_ATTRIBUTE, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            String xml = matcher.group();
            if (xml.length() < 11) {
                continue;
            }
            String linkUrl = XMLUtil.getStringAttribute(xml, "background", '\\');
            if (linkUrl == null || "#".equals(linkUrl) || StringUtil.empty.equals(linkUrl)) {
                continue;
            }
            linkUrl = StringUtil.replace(linkUrl, "\"", StringUtil.empty);

            String newURL = StringUtil.empty;
            if (linkUrl.startsWith("http://")) {
                continue;
            } else if (linkUrl.startsWith("/")) {
                newURL = oldHost + linkUrl;
            } else {
                newURL = oldUrlPath + linkUrl;
            }

            if (tranPhoto) {
                String photoFileName = URLUtil.getFileName(newURL);
                String photoUrlName = URLUtil.getFileNamePart(photoFileName);
                String photoType = URLUtil.getFileType(photoFileName);
                newURL = URLUtil.getURLPath(newURL) + photoUrlName + "_" + htmlEncode.toLowerCase() + "." + photoType;
            }
            String oldHtml = StringUtil.replace(xml, linkUrl, newURL);
            result = StringUtil.replace(result, xml, oldHtml);
        }
        //修复
        result = StringUtil.replace(result, oldHost + "/" + oldHost, oldHost);
        return result;
    }
}
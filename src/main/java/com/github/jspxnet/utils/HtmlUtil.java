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

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.parse.html.ATag;
import com.github.jspxnet.scriptmark.parse.html.BodyTag;
import com.github.jspxnet.scriptmark.parse.html.ImgTag;
import com.itextpdf.text.pdf.BaseFont;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-27
 * Time: 16:49:17
 */
public final class HtmlUtil {

    final private static String[] extType =
            {
                    "jpg", "bmp", "gif", "doc", "rar", "exe", "arj", "avi", "wav", "rm",
                    "zip", "tar", "ppt", "png", "mp3", "mid", "psd", "dll", "cab", "swf", "wave"
            };


    final public static String safeFilter_delAllTags = "script,style,link,body,head,title,object,input,select,areatext,form,iframe";
    final public static String safeFilter_simpleDelTags = "meta,script,style,input,link,body,input,select,areatext,form";

    final public static String safeFilter_delTags = "meta,script,style,input,link,body,input,select,areatext,form,iframe";

    final private static String[] safeFilter_actions = new String[]{"onclick", "onblur", "ondblclick", "onfocus", "onkeydown", "onkeypress", "onkeyup", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup", "onload", "onError", "onresize", "onunload"};

    final private static String PATTERN_P_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    final private static String PATTERN_P_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";


    private static final HashMap<String, Character> unHtmlCharacter = new HashMap<String, Character>();
    private static final HashMap<Character, String> htmlCharacter = new HashMap<Character, String>();

    static {
        unHtmlCharacter.put("&lt;", '<');
        unHtmlCharacter.put("&gt;", '>');
        unHtmlCharacter.put("&amp;", '&');
        unHtmlCharacter.put("&quot;", '\"');
        unHtmlCharacter.put("&agrave;", 'à');
        unHtmlCharacter.put("&Agrave;", 'À');
        unHtmlCharacter.put("&acirc;", 'â');
        unHtmlCharacter.put("&auml;", 'ä');
        unHtmlCharacter.put("&Auml;", 'Ä');
        unHtmlCharacter.put("&Acirc;", 'Â');
        unHtmlCharacter.put("&aring;", 'å');
        unHtmlCharacter.put("&Aring;", 'Å');
        unHtmlCharacter.put("&aelig;", 'æ');
        unHtmlCharacter.put("&AElig;", 'Æ');
        unHtmlCharacter.put("&ccedil;", 'ç');
        unHtmlCharacter.put("&Ccedil;", 'Ç');
        unHtmlCharacter.put("&eacute;", 'é');
        unHtmlCharacter.put("&Eacute;", 'É');
        unHtmlCharacter.put("&egrave;", 'è');
        unHtmlCharacter.put("&Egrave;", 'È');
        unHtmlCharacter.put("&ecirc;", 'ê');
        unHtmlCharacter.put("&Ecirc;", 'Ê');
        unHtmlCharacter.put("&euml;", 'ë');
        unHtmlCharacter.put("&Euml;", 'Ë');
        unHtmlCharacter.put("&iuml;", 'ï');
        unHtmlCharacter.put("&Iuml;", 'Ï');
        unHtmlCharacter.put("&ocirc;", 'ô');
        unHtmlCharacter.put("&Ocirc;", 'Ô');
        unHtmlCharacter.put("&ouml;", 'ö');
        unHtmlCharacter.put("&Ouml;", 'Ö');
        unHtmlCharacter.put("&oslash;", 'ø');
        unHtmlCharacter.put("&Oslash;", 'Ø');
        unHtmlCharacter.put("&szlig;", 'ß');
        unHtmlCharacter.put("&ugrave;", 'ù');
        unHtmlCharacter.put("&Ugrave;", 'Ù');
        unHtmlCharacter.put("&ucirc;", 'û');
        unHtmlCharacter.put("&Ucirc;", 'Û');
        unHtmlCharacter.put("&uuml;", 'ü');
        unHtmlCharacter.put("&Uuml;", 'Ü');
        unHtmlCharacter.put("&nbsp;", ' ');
        unHtmlCharacter.put("&copy;", '\u00a9');
        unHtmlCharacter.put("&reg;", '\u00ae');
        unHtmlCharacter.put("&euro;", '\u20a0');
        for (String key : unHtmlCharacter.keySet()) {
            Character c = unHtmlCharacter.get(key);
            htmlCharacter.put(c, key);
        }
    }

    private HtmlUtil() {

    }

    public static String escapeDecodeHtml(String source) {
        int i, j;
        boolean continueLoop;
        int skip = 0;
        do {
            continueLoop = false;
            i = source.indexOf(StringUtil.AND, skip);
            if (i > -1) {
                j = source.indexOf(";", i);
                if (j > i && j - i < 9) {
                    String entityToLookFor = source.substring(i, j + 1);
                    if (unHtmlCharacter.containsKey(entityToLookFor)) {
                        Character value = unHtmlCharacter.get(entityToLookFor);
                        source = source.replaceAll(entityToLookFor, value.toString());
                        continueLoop = true;
                    } else {
                        skip = i + 1;
                        continueLoop = true;
                    }
                }
            }
        } while (continueLoop);
        return source;
    }


    /**
     * 转义 html 标签，让网页显示 html，而不执行
     *
     * @param source html代码
     * @return 转义后的html
     */
    public static String escapeEncoderHTML(String source) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            Character c = source.charAt(i);
            if (htmlCharacter.containsKey(c)) {
                result.append(htmlCharacter.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


    /**
     * @param str      格式说明:[标题]数据一行一个;项目名称=项目数据
     * @param showName 显示名称
     * @return 文本格式转换到FusionChart 的XML 格式
     */
    public static String getFusionChartXML(String str, int showName) {
        if (str == null) {
            return StringUtil.empty;
        }

        int decimal = 0;
        String[] array = StringUtil.split(str, StringUtil.CRLF);
        String title = null;
        Map<String, Float> dataMap = new LinkedHashMap<String, Float>();
        for (String line : array) {
            if (title == null && line.contains("[") && line.contains("]")) {
                title = StringUtil.substringBetween(line, "[", "]");
            } else {
                String name = StringUtil.substringBefore(line, StringUtil.EQUAL);
                String testValue = StringUtil.substringAfter(line, StringUtil.EQUAL);
                if (testValue != null && testValue.contains(StringUtil.DOT)) {
                    decimal = 2;
                }
                dataMap.put(StringUtil.trim(name), StringUtil.toFloat(testValue));
            }
        }

        String[] colors = ColorUtil.getWebColorArray(dataMap.size() + 1);
        float count = 0;
        for (Float value : dataMap.values()) {
            count = count + value;
        }

        StringBuilder result = new StringBuilder();
        result.append("<graph baseFont='宋体' baseFontSize='12' outCnvBaseFontSize='12' palette='10' caption='").append(title).append("' xAxisName='合计:").append(NumberUtil.getRound(count, 2)).append("' yAxisName='Y").append("' showNames='").append(0).append("' decimalPrecision='").append(decimal).append("' >\r\n");
        int i = 0;
        for (String name : dataMap.keySet()) {
            float num = NumberUtil.getRound(dataMap.get(name), 2);
            result.append("<set ").append("name='").append(name).append("' value='").append(num).append("' color='").append(colors[i]).append("' showName='").append(showName).append("'/>\r\n");
            i++;
        }
        if (count > 0) {

            result.append("<trendlines>\r\n");
            result.append("<line startValue='").append(NumberUtil.getRound(count / dataMap.size(), 2)).append("' color='#933' displayValue='平均' showOnTop ='1'/>");
            result.append("</trendlines>\r\n");
        }
        result.append("</graph>");
        return StringUtil.replace(result.toString(), StringUtil.CRLF, "");
    }


    private int getHaveType(String html, String type) {
        return StringUtil.indexIgnoreCaseOf(html, type);
    }

    /**
     * @param html HTMl 格式
     * @return 得到html页面里边包含的问卷类型
     */
    public String[] getFileTypeArray(String html) {
        String[] result = null;
        for (String type : extType) {
            if (getHaveType(html, StringUtil.DOT + type) != -1) {
                result = ArrayUtil.add(result, type);
            }
        }
        return result;
    }


    /**
     * @param str 删除动态脚本 [% %] 和 [? ?] []标识尖括号
     * @return String
     */
    public static String deleteScript(String str) {
        if (str == null) {
            return StringUtil.empty;
        }
        StringBuilder out = new StringBuilder(str.length());
        boolean start = false;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '<' && (str.charAt(i + 1) == '%' || str.charAt(i + 1) == '?')) {
                start = true;
            }
            if (start && (str.charAt(i) == '%' || str.charAt(i) == '?') && str.charAt(i + 1) == '>') {
                start = false;
                i = i + 2;
            }
            if (!start) {
                if (i < str.length() - 1 && (str.charAt(i) == '$' || str.charAt(i + 1) == '{')) {
                    out.append("\\");
                }
                out.append(str.charAt(i));
            }
        }
        return out.toString();
    }

    /**
     * 删除HTML,不会保留格式， web里边的标题输出使用在
     *
     * @param str  字符串
     * @param len  长度
     * @param sEnd 结束字符
     * @return String
     */
    static public String deleteHtml(String str, int len, String sEnd) {
        if (!StringUtil.hasLength(str)) {
            return StringUtil.empty;
        }
        String result = StringUtil.replace(deleteHtml(str), StringUtil.CRLF, "");
        if (len >= 0 && StringUtil.getLength(str) > len) {
            if (StringUtil.isNull(sEnd)) {
                result = StringUtil.csubstring(result, 0, len);
            } else {
                result = StringUtil.csubstring(result, 0, len) + sEnd;
            }
        }
        return StringUtil.trim(result);
    }


    /**
     * 删除字符传中的HTML标签,包括属性
     *
     * @param str HTML
     * @return String
     */

    static public String deleteHtml(String str) {
        if (str == null) {
            return StringUtil.empty;
        }
        //定义script的正则表达式
        Pattern p_script = compile(PATTERN_P_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(str);
        str = m_script.replaceAll(""); //过滤script标签

        //定义style的正则表达式
        Pattern p_style = compile(PATTERN_P_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(str);
        str = m_style.replaceAll(""); //过滤style标签

        Pattern pattern = compile("<.+?>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }


    /**
     * @param str html
     * @return 删除HTML 注释
     */
    static public String deleteNotes(String str) {
        if (str == null) {
            return StringUtil.empty;
        }
        StringBuilder out = new StringBuilder();
        StringBuilder tempZs = new StringBuilder();
        int q = 0;
        for (int i = 0; i < str.length(); i++) {
            if (i <= str.length() - 4 && "<!--".equals(str.substring(i, i + 4))) {
                q++;
                tempZs.setLength(0);
            }
            if (i >= 3 && "-->".equals(str.substring(i - 3, i))) {
                q = 0;
                if (i >= 5 && "//".equals(str.substring(i - 5, i - 3))) {
                    out.append(tempZs.toString());
                    tempZs.setLength(0);
                }
            }
            if (q > 0) {
                tempZs.append(str.charAt(i));
            } else {
                out.append(str.charAt(i));
            }
        }
        if (tempZs.toString().endsWith("//-->")) {
            out.append(tempZs.toString());
        }
        return out.toString();
    }


    static public String getBodyHtml(String html) throws Exception {
        if (StringUtil.isNull(html)) {
            return StringUtil.empty;
        }
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("body", BodyTag.class.getName());
        BodyTag bodyTag = (BodyTag) xmlEngine.createTagNode(html);
        if (bodyTag == null) {
            return html;
        }
        return bodyTag.getBody();
    }


    /**
     * @param s html字符串
     * @return 除去Html文件中的属性, 但是保留标签(td 等情况不能删除)
     */
    static public String deleteAttribute(String s) {
        if (s == null) {
            return StringUtil.empty;
        }
        StringBuffer stringbuffer = new StringBuffer();
        java.util.regex.Matcher matcher = compile("<[^/|a][^<^>]*>", Pattern.DOTALL).matcher(s);
        while (matcher.find()) {
            String r = matcher.group(0);
            if (!StringUtil.hasLength(r)) {
                continue;
            }
            String tag = StringUtil.substringBetween(r, "<", " ");
            if (!StringUtil.hasLength(tag)) {
                continue;
            }
            matcher.appendReplacement(stringbuffer, "<" + tag + ">");
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }

    /**
     * @param html html
     * @return 修复补全XML或HTML标签, 并且删除危险的html代码，样式
     * @throws Exception 异常
     */
    static public String getSafeFilter(String html) throws Exception {
        return getSafeFilter(html, safeFilter_delTags, safeFilter_actions);
    }

    static public String getSimpleSafeFilter(String html) throws Exception {
        return getSafeFilter(html, safeFilter_simpleDelTags, safeFilter_actions);
    }

    /**
     * @param html       html
     * @param delTag     要删除的html Tag
     * @param delActions 排除
     * @return 修复补全XML或HTML标签, 并且删除危险的html代码，样式
     */
    static public String getSafeFilter(String html, String delTag, String[] delActions) {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAdvancedXmlEscape(false);
        props.setRecognizeUnicodeChars(false);
        props.setTranslateSpecialEntities(false);
        props.setUseCdataForScriptAndStyle(false); //是否使用 <![CDATA[
        props.setOmitDeprecatedTags(true);
        props.setOmitXmlDeclaration(true);
        props.setOmitHtmlEnvelope(true);
        props.setOmitUnknownTags(false);
        props.setIgnoreQuestAndExclam(true);
        props.setNamespacesAware(false);
        props.setTransResCharsToNCR(false);
        props.setUseEmptyElementTags(true);
        props.setPruneTags(delTag);
        TagNode node = cleaner.clean(html);
        List set = node.getAllElementsList(true);
        for (Object tag : set) {
            if (tag == null) {
                continue;
            }
            TagNode tagNode = (TagNode) tag;
            for (String action : delActions) {
                String actionName = tagNode.getAttributeByName(action);
                if (actionName != null) {
                    tagNode.removeAttribute(action);
                }
            }
        }
        return new SimpleHtmlSerializer(props).getAsString(node);
    }

    /**
     * @param html      html字符串
     * @param tagName   标签名称
     * @param attribute 要得到的属性
     * @return 返回属性列表
     */
    static public String[] getTagAttribute(String html, String tagName, String attribute) {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(html);
        TagNode[] tagNodes = node.getElementsByName(tagName, true);
        String[] result = null;
        for (TagNode nod : tagNodes) {
            String value = nod.getAttributeByName(attribute);
            if (StringUtil.isNull(value)) {
                continue;
            }
            result = ArrayUtil.add(result, value);
        }

        if (!ArrayUtil.isEmpty(result)) {
            return new String[0];
        }
        return result;
    }

    static public String getMobileImageFilter(String html, String domain) {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(html);
        TagNode[] tagNodes = node.getElementsByName("img", true);
        for (TagNode nod : tagNodes) {
            String value = nod.getAttributeByName("src");
            if (StringUtil.isNull(value)) {
                continue;
            }
            if (value.startsWith("http") && !value.contains(domain)) {
                continue;
            }
            html = StringUtil.replaceOnce(html, value, FileUtil.getMobileFileName(value));
        }
        return html;
    }

    /**
     * @param content word html
     * @return 清空word代码
     */
    public static String cleanWord(String content) {
        // 段落替换为换行
        // <br><br/>替换为换行
        // 去掉其它的<>之间的东西
        content = content.replaceAll("<p .*?>", StringUtil.CRLF).replaceAll("<br\\s*/?>", StringUtil.CRLF).replaceAll("\\<.*?>", "");
        // 还原HTML
        content = StringUtil.replace(content, "\r\n\r\n", StringUtil.CRLF);
        return content;
    }


    public static String replaceUrl(String html, String oldUrl, String newUrl) {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAdvancedXmlEscape(false);
        props.setRecognizeUnicodeChars(false);
        props.setTranslateSpecialEntities(false);
        props.setUseCdataForScriptAndStyle(false); //是否使用 <![CDATA[

        props.setOmitDeprecatedTags(true);
        props.setOmitXmlDeclaration(true);
        props.setOmitHtmlEnvelope(true);
        props.setOmitUnknownTags(true);

        props.setIgnoreQuestAndExclam(true);
        props.setNamespacesAware(false);
        props.setTransResCharsToNCR(false);
        props.setUseEmptyElementTags(false);

        TagNode node = cleaner.clean(html);
        List<?> set = node.getAllElementsList(true);
        for (Object tag : set) {
            if (tag == null) {
                continue;
            }
            String tagName = tag.toString();

            ///////////////////////修复连接 A   link
            if ("a".equalsIgnoreCase(tagName) || "link".equalsIgnoreCase(tagName)) {
                List<?> tagList = node.getElementListByName(tagName, true);
                for (Object tagObj : tagList) {
                    TagNode tagNode = (TagNode) tagObj;
                    String linkUrl = tagNode.getAttributeByName("href");
                    if (linkUrl != null && linkUrl.startsWith(oldUrl)) {
                        tagNode.removeAttribute("href");
                        tagNode.addAttribute("href", StringUtil.replaceOnce(linkUrl, oldUrl, newUrl));
                    }
                }
            } else /////////////////////////////修复图片 img
                if ("img".equalsIgnoreCase(tagName) || "script".equalsIgnoreCase(tagName) || "javascript".equalsIgnoreCase(tagName)) {
                    List<?> tagList = node.getElementListByName(tagName, true);
                    for (Object tagObj : tagList) {
                        TagNode tagNode = (TagNode) tagObj;
                        String linkUrl = tagNode.getAttributeByName("src");
                        if (linkUrl != null && linkUrl.startsWith(oldUrl)) {
                            tagNode.removeAttribute("src");
                            tagNode.addAttribute("src", StringUtil.replaceOnce(linkUrl, oldUrl, newUrl));
                        }
                    }
                } else /////////////////////////////修复图片 img
                    if ("apple".equalsIgnoreCase(tagName)) {
                        List<?> tagList = node.getElementListByName(tagName, true);
                        for (Object tagObj : tagList) {
                            TagNode tagNode = (TagNode) tagObj;
                            String linkUrl = tagNode.getAttributeByName("codebase");
                            if (linkUrl != null && linkUrl.startsWith(oldUrl)) {
                                tagNode.removeAttribute("codebase");
                                tagNode.addAttribute("codebase", StringUtil.replaceOnce(linkUrl, oldUrl, newUrl));
                            }
                        }
                    } else /////////////////////////////修复多媒体 msoa rm mp
                        if ("object".equalsIgnoreCase(tagName)) {
                            List<?> tagList = node.getElementListByName(tagName, true);
                            for (Object tagObj : tagList) {
                                TagNode tagNode = (TagNode) tagObj;
                                String linkUrl = tagNode.getAttributeByName("data");
                                if (linkUrl != null && linkUrl.startsWith(oldUrl)) {
                                    tagNode.addAttribute("data", StringUtil.replaceOnce(linkUrl, oldUrl, newUrl));
                                }
                                List<?> paramList = tagNode.getElementListByName("param", true);
                                for (Object childTagObj : paramList) {
                                    TagNode paramTag = (TagNode) childTagObj;
                                    String name = paramTag.getAttributeByName("name");
                                    if ("src".equalsIgnoreCase(name) || "movie".equalsIgnoreCase(name)) {
                                        linkUrl = paramTag.getAttributeByName("value");
                                        if (linkUrl != null && linkUrl.startsWith(oldUrl)) {
                                            paramTag.removeAttribute("value");
                                            paramTag.addAttribute("value", StringUtil.replaceOnce(linkUrl, oldUrl, newUrl));
                                        }
                                    }
                                }
                                List<?> embedList = tagNode.getElementListByName("embed ", true);
                                for (Object childTagObj : embedList) {
                                    TagNode embedTag = (TagNode) childTagObj;
                                    linkUrl = embedTag.getAttributeByName("movie");
                                    if (linkUrl != null && linkUrl.startsWith(oldUrl)) {
                                        embedTag.removeAttribute("src");
                                        embedTag.addAttribute("src", StringUtil.replaceOnce(linkUrl, oldUrl, newUrl));
                                    }
                                }

                            }
                        }
        }
        return new SimpleHtmlSerializer(props).getAsString(node);

    }

    /**
     * @param html html
     * @return 返回 href 的地址列表,空或者#，或者void 开始的不返回
     */
    public static JSONArray getHrefUrl(String html) {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAdvancedXmlEscape(false);
        props.setRecognizeUnicodeChars(false);
        props.setTranslateSpecialEntities(false);
        props.setUseCdataForScriptAndStyle(false); //是否使用 <![CDATA[

        props.setOmitDeprecatedTags(true);
        props.setOmitXmlDeclaration(true);
        props.setOmitHtmlEnvelope(true);
        props.setOmitUnknownTags(true);

        props.setIgnoreQuestAndExclam(true);
        props.setNamespacesAware(false);
        props.setTransResCharsToNCR(false);
        props.setUseEmptyElementTags(false);

        TagNode node = cleaner.clean(html);

        JSONArray result = new JSONArray();

        TagNode[] tagNodes = node.getAllElements(true); //true 表示所有节点,false 表示第一级
        for (TagNode n : tagNodes) {
            if ("a".equalsIgnoreCase(n.getName())) {
                String href = n.getAttributeByName("href");
                if (StringUtil.isNull(href) || href.startsWith("#") || href.startsWith("javascript") || href.startsWith("mailto")) {
                    continue;
                }
                JSONObject json = new JSONObject();
                json.put("href",href);
                json.put("title",deleteHtml(n.getText().toString(),20,""));
                result.add(json);

            }
        }
        return result;
    }

    /**
     *
     * @param html html
     * @return 返回img src列表
     */
    public static JSONArray getImgSrc(String html) {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAdvancedXmlEscape(false);
        props.setRecognizeUnicodeChars(false);
        props.setTranslateSpecialEntities(false);
        props.setUseCdataForScriptAndStyle(false); //是否使用 <![CDATA[

        props.setOmitDeprecatedTags(true);
        props.setOmitXmlDeclaration(true);
        props.setOmitHtmlEnvelope(true);
        props.setOmitUnknownTags(true);

        props.setIgnoreQuestAndExclam(true);
        props.setNamespacesAware(false);
        props.setTransResCharsToNCR(false);
        props.setUseEmptyElementTags(false);

        TagNode node = cleaner.clean(html);

        JSONArray result = new JSONArray();

        TagNode[] tagNodes = node.getAllElements(true); //true 表示所有节点,false 表示第一级
        for (TagNode n : tagNodes) {
            if ("img".equalsIgnoreCase(n.getName())) {
                String src = n.getAttributeByName("src");
                if (StringUtil.isNull(src) || src.startsWith("#")) {
                    continue;
                }
                JSONObject json = new JSONObject();
                json.put("src",src);
                json.put("title",deleteHtml(n.getText().toString(),20,""));
                result.add(json);
            }
        }
        return result;
    }
    /**
     * html 十进制解码， 就是 html 里边{@code  &#8220; &#8221; &#2699;} 的编码
     *
     * @param str 十进制解码
     * @return 得到脂肪层
     */
    public static String unescapeHtml(String str) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char x = str.charAt(i);
            if (i < str.length() - 3 && '&' == x) {
                if ('#' == str.charAt(i + 1)) {
                    int pos = str.indexOf(';', i + 2);
                    if (pos != -1 && pos <= str.length()) {
                        String v = str.substring(i + 2, pos);
                        if (ValidUtil.isNumber(v)) {
                            sb.append((char) Integer.parseInt(v));
                            i = pos;
                            continue;
                        }
                    }
                } else if ("quot;".equals(str.substring(i + 1, i + 1 + 5))) {
                    sb.append("\"");
                    i = i + 5;
                    continue;
                }
            }
            sb.append(x);
        }
        return sb.toString();
    }

    /**
     * 将html里边的链接转换为markdown格式的文字表述
     * 作用就是避免外链
     * 图片 ![Alt text](/path/transfer/img.jpg)
     * 链接 [This link](http://example.net/)
     * 选转换图片，避免多层圈套
     *
     * @param html html
     * @return 转换后的结构
     * @throws Exception 异常
     */
    public static String linkToMarkdown(String html) throws Exception {

        String content = html;
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("img", ImgTag.class.getName());
        List<com.github.jspxnet.scriptmark.core.TagNode> list = xmlEngine.getTagNodes(content);
        for (com.github.jspxnet.scriptmark.core.TagNode node : list) {
            ImgTag imgTag = (ImgTag) node;
            String alt = imgTag.getStringAttribute("alt");
            String txt = "![Alt " + (StringUtil.isNull(alt) ? "IMG" : alt) + "](" + imgTag.getSrc() + ")";
            content = StringUtil.replace(content, imgTag.getSource(), txt);
        }
        xmlEngine.removeTag("img");
        list.clear();

        //这里可能会出现嵌套
        xmlEngine.putTag("a", ATag.class.getName());
        list = xmlEngine.getTagNodes(content);
        for (com.github.jspxnet.scriptmark.core.TagNode node : list) {
            ATag aTag = (ATag) node;
            String txt = "[" + StringUtil.trim(deleteHtml(aTag.getBody())) + "](" + aTag.getHref() + ")";
            content = StringUtil.replace(content, aTag.getSource(), txt);
        }
        return content;
    }


    /**
     * 将HTML转成PD格式的文件。html文件的格式比较严格
     * @param baseUrl 资源路径
     * @param html  html正文
     * @param os 输出流
     * @throws Exception 异常
     */
    public static void toPdf(URL baseUrl, String html, OutputStream os) throws Exception {
        EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
        String FONTS_PATH =  ENV_TEMPLATE.getString(Environment.fontsPath,"").endsWith("/")?ENV_TEMPLATE.getString(Environment.fontsPath):(ENV_TEMPLATE.getString(Environment.fontsPath)+"/");

        //"file:/E:/test/m/"
        ITextRenderer renderer = new ITextRenderer();
        renderer.getSharedContext().setPrint(true);
        // 解决中文支持问题
        ITextFontResolver fontResolver = renderer.getFontResolver();
        //fontResolver.addFontDirectory(FONTS_PATH,SystemUtil.OS==SystemUtil.WINDOWS?BaseFont.NOT_EMBEDDED:BaseFont.EMBEDDED);
        File f = new File(FONTS_PATH);
        if (f.isDirectory()) {
            File[] files = f.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".otf") || lower.endsWith(".ttf") || lower.endsWith(".ttc");
                }
            });

            for(int i = 0; i < Objects.requireNonNull(files).length; ++i) {
                fontResolver.addFont(files[i].getAbsolutePath(), BaseFont.IDENTITY_H,SystemUtil.OS==SystemUtil.WINDOWS?BaseFont.NOT_EMBEDDED:BaseFont.EMBEDDED);
            }
        }
        //解决图片的相对路径问题
        renderer.getSharedContext().setBaseURL(baseUrl.getPath());
        renderer.setDocumentFromString(html);
        Document documents = renderer.getDocument();
        documents.setStrictErrorChecking(false);
        renderer.layout();
        renderer.createPDF(os);
        os.close();
    }


}

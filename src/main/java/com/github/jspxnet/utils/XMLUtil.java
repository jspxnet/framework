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
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import com.github.jspxnet.util.XMLFormat;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utilities for dealing with XML
 *
 * @author tweber
 * @version 1.0
 */
@Slf4j
public final class XMLUtil {

    final private static char[] LT_ENCODE = "&lt;".toCharArray();
    final private static char[] GT_ENCODE = "&gt;".toCharArray();
    final private static char[] AMP_ENCODE = "&amp;".toCharArray();
    final private static char[] QUOT_ENCODE = "&quot;".toCharArray();
    final private static String XML_KEY_ENCODING = "encoding";
    final public static String CDATA_START_KEY = "<![CDATA[";
    final public static String CDATA_END_KEY = "]]>";



    private XMLUtil() {

    }


    /**
     * @param stringValue xml xml字符串
     * @return 删除XML注释
     */
    static public String deleteExegesis(String stringValue) {
        if (stringValue == null) {
            return StringUtil.empty;
        }
        if (!stringValue.contains("-->")) {
            return stringValue;
        }
        StringBuilder out = new StringBuilder(stringValue.length());
        String temp = stringValue;
        int x;
        while (temp.contains("-->")) {
            int pos = temp.indexOf("<!--");
            if (pos == -1) {
                out.append(temp);
                break;
            }
            out.append(temp, 0, pos);
            x = temp.indexOf("-->");
            if (x == -1) {
                continue;
            }
            temp = temp.substring(x + 3);
        }
        out.append(temp);
        return out.toString();
    }


    /**
     * @param fileName 文件名
     * @param typePath 路径
     * @return 得到文件类型
     */
    static public String getFileTypeHtml(String fileName, String typePath) {
        if (!fileName.contains(StringUtil.DOT)) {
            fileName = "folder";
        }
        return "<img src=" + typePath + FileUtil.getTypePart(fileName) + ".gif" + " border=0 />";
    }


    /**
     * @param s 字符串
     * @return 删除引号
     */
    public static String deleteQuote(String s) {
        if (!StringUtil.hasLength(s)) {
            return s;
        }
        s = StringUtil.trim(s);
        if (s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }


    /**
     * @param fileName 文件名
     * @return 解析XML从文件
     */
    static public String fileToXML(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return StringUtil.empty;
        }
        String fileExt = URLUtil.getFileType(fileName).toUpperCase();
        if ("WMV".equals(fileExt)) {
            return "<object id=\"mplayer\" width=\"460\" height=\"68\" classid=\"CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95\" codebase=\"http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,5,715\" align=\"baseline\" border=\"0\" standby=\"Loading Microsoft Windows Media Player components...\" type=\"application/x-oleobject\">\n" +
                    "            <param name=\"FileName\" value=\"" + fileName + "\">\n" +
                    "            <param name=\"ShowControls\" value=\"1\">\n" +
                    "            <param name=\"ShowAudioControls\" value=\"1\">\n" +
                    "            <param name=\"ShowPositionControls\" value=\"0\">\n" +
                    "            <param name=\"ShowTracker\" value=\"1\">\n" +
                    "            <param name=\"ShowStatusBar\" value=\"1\">\n" +
                    "            <param name=\"AutoStart\" value=\"1\">\n" +
                    "            <param name=\"EnableContextMenu\" value=\"1\">\n" +
                    "            <param name=\"InvokeURLs\" value=\"1\">\n" +
                    "            <param name=\"DefaultFrame\" value=\"datawindow\">\n" +
                    "</object>";
        }

        if ("SWF".equals(fileExt)) {
            return "<embed quality=\"high\" type=\"application/x-shockwave-flash\" src=\"" + fileName + "\" style=\"WIDTH:100%; HEIGHT: 480px\">";
        }

        if (ArrayUtil.inArray(new String[]{"RM", "RMVB"}, fileExt, true)) {
            return "<OBJECT ID=video CLASSID=\"clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA\" HEIGHT=288 WIDTH=352>\n" +
                    "<param name=\"_ExtentX\" value=\"9313\">\n" +
                    "<param name=\"_ExtentY\" value=\"7620\">\n" +
                    "<param name=\"AUTOSTART\" value=\"1\">\n" +
                    "<param name=\"SHUFFLE\" value=\"1\">\n" +
                    "<param name=\"PREFETCH\" value=\"0\">\n" +
                    "<param name=\"NOLABELS\" value=\"0\">\n" +
                    "<param name=\"SRC\" value=\"" + fileName + "\";>\n" +
                    "<param name=\"CONTROLS\" value=\"ImageWindow\">\n" +
                    "<param name=\"CONSOLE\" value=\"Clip1\">\n" +
                    "<param name=\"LOOP\" value=\"0\">\n" +
                    "<param name=\"NUMLOOP\" value=\"0\">\n" +
                    "<param name=\"CENTER\" value=\"0\">\n" +
                    "<param name=\"MAINTAINASPECT\" value=\"0\">\n" +
                    "<param name=\"BACKGROUNDCOLOR\" value=\"#000000\">\n" +
                    "</OBJECT>";
        }

        if ("QT".equals(fileExt)) {
            return "<object classid=clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B id=QTActiveXPlugin1 width=320 height=152><param name=_ExtentX value=0><param name=_ExtentY value=0><PARAM name=SRC VALUE=\"" + fileName + "\"></object>";
        }

        if ("MPG".equals(fileExt)) {
            return "<object classid=\"clsid:05589FA1-C356-11CE-BF01-00AA0055595A\" id=\"ActiveMovie1\" width=\"400\" height=\"350\">\n" +
                    "<param name=\"AutoStart\" value=\"-1\">\n" +
                    "<param name=\"AllowChangeDisplayMode\" value=\"-1\">\n" +
                    "<param name=\"AllowHideControls\" value=\"-1\">\n" +
                    "<param name=\"AutoRewind\" value=\"-1\">\n" +
                    "<param name=\"Enabled\" value=\"-1\">\n" +
                    "<param name=\"EnableContextMenu\" value=\"-1\">\n" +
                    "<param name=\"EnablePositionControls\" value=\"-1\">\n" +
                    "<param name=\"EnableSelectionControls\" value=\"0\">\n" +
                    "<param name=\"EnableTracker\" value=\"-1\">\n" +
                    "<param name=\"Filename\" value=\"" + fileName + "\" valuetype=\"ref\">\n" +
                    "<param name=\"PlayCount\" value=\"1\">\n" +
                    "<param name=\"Rate\" value=\"1\">\n" +
                    "<param name=\"SelectionStart\" value=\"-1\">\n" +
                    "<param name=\"SelectionEnd\" value=\"-1\">\n" +
                    "<param name=\"ShowControls\" value=\"-1\">\n" +
                    "<param name=\"ShowDisplay\" value=\"-1\">\n" +
                    "<param name=\"ShowTracker\" value=\"-1\">\n" +
                    "</object>";
        }
        if ("WAV".equals(fileExt)) {
            return "<object id=\"NSPlay\" width=300 height=300 classid=\"CLSID:22d6f312-b0f6-11d0-94ab-0080c74c7e95\" codebase=\"http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,5,715\" standby=\"Loading Microsoft Windows Media Player components...\" type=\"application/x-oleobject\" hspace=\"5\">\n" +
                    "<param name=\"AutoRewind\" value=1>\n" +
                    "<param name=\"FileName\" value=\"" + fileName + "\">\n" +
                    "<param name=\"ShowControls\" value=\"1\">\n" +
                    "<param name=\"ShowPositionControls\" value=\"0\">\n" +
                    "<param name=\"ShowAudioControls\" value=\"1\">\n" +
                    "</object>";
        }
        if (ArrayUtil.inArray(new String[]{"JPG", "GIF", "PNG", "BMP"}, fileExt, true)) {
            return "<img src=\"" + fileName + "\" border=\"0\"/>";
        }
        if (ArrayUtil.inArray(new String[]{"JPG", "GIF", "PNG", "BMP"}, fileExt, true)) {
            if ("FLV".equals(fileExt)) {
                return "<embed src=\"/script/vcastr2.swf\" FlashVars=\"vcastr_file=" + fileName + "\" quality=\"high\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" type=\"application/x-shockwave-flash\" width=\"550\" height=\"400\"></embed>";
            } else if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".rar")
                    || fileName.toLowerCase().endsWith(".jar") || fileName.toLowerCase().endsWith(".ace")
                    || fileName.toLowerCase().endsWith(".tar") || fileName.toLowerCase().endsWith(".html")) {
                //<a href="qqq">ggg</a>
                return "<a href=\"" + fileName + "\" >" + fileName + "</a>";
            }
        }
        return fileName;
    }

    static public DefaultHandler parseXmlFile(DefaultHandler handler, String f) throws IOException, SAXException {
        return parseXmlFile(handler, new File(f));
    }

    /**
     * 读sax2 的 xml 文件
     *
     * @param handler 解析方法
     * @param f       文件位置
     * @return 已经读好的方法
     * @throws java.io.IOException      io异常
     * @throws org.xml.sax.SAXException sax解析异常
     */
    static public DefaultHandler parseXmlFile(DefaultHandler handler, File f) throws IOException, SAXException {

        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
        if (f.isFile()) {
            xmlReader.parse(new InputSource(new FileReader(f)));
            return handler;
        }
        return null;
    }

    /**
     * 读XML 字符串
     *
     * @param handler 解析方法
     * @param xml     XML格式字符串
     * @return 已经读好的方法
     * @throws java.io.IOException io异常
     */
    static public boolean parseXmlString(DefaultHandler handler, String xml) throws IOException {
        if (StringUtil.isNull(xml)) {
            return true;
        }
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();

            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(xml.trim())));
        } catch (SAXException e) {
            log.error(xml, e);
            return false;
        }
        return true;
    }

    /**
     * @param handler 解析器
     * @param source  来源
     * @return 得到sax句柄
     * @throws IOException 异常
     */
    static public DefaultHandler parseXmlInputSource(DefaultHandler handler, InputSource source) throws IOException {
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(source);
        } catch (SAXException e) {
            log.error(source.toString(), e);
        }
        return handler;
    }

    /**
     * 读XML 字符串 中的 键的 值
     *
     * @param key XML格式 键
     * @param xml XML格式字符串
     * @return 已经读好的方法
     */
    static public String getKeyValue(String key, String xml) {
        try {
            BaseXML baseXml = new BaseXML(key, "");
            parseXmlString(baseXml, xml);
            return baseXml.getValue();
        } catch (Exception e) {
            log.error(xml, e);
        }
        return StringUtil.empty;
    }

    /**
     * @param key    属性名称
     * @param value  值
     * @param xmlstr xml字符串
     * @return 是否有此属性
     */
    static public boolean getInKey(String key, String value, String xmlstr) {
        try {
            ReadXML readXml = new ReadXML(key, value);
            parseXmlString(readXml, xmlstr);
            return readXml.getValue();
        } catch (Exception e) {
            log.error("parse Xml String:" + xmlstr + "  key=" + key + " value=" + value, e);
            return false;
        }
    }

    /**
     * @param string 字符串
     * @return 替换给xml表示
     */
    static public String escapeDecrypt(String string) {
        if (string == null) {
            return StringUtil.empty;
        }
        string = StringUtil.replace(string, "&amp;", StringUtil.AND);
        string = StringUtil.replace(string, "&lt;", "<");
        string = StringUtil.replace(string, "&gt;", ">");
        string = StringUtil.replace(string, "&quot;", "\"");
        return string;
    }

    /**
     * 去掉CDATA标签,CDATA内部数据保持不变, 外部的会转义 escapeDecrypt 里边的类容
     * @param stringValue xml字符串
     * @return 转义后的数据
     */
    static public String xmlCdataDecrypt(String stringValue) {
        if (stringValue == null) {
            return StringUtil.empty;
        }
        StringBuilder out = new StringBuilder(stringValue.length());
        String temp = stringValue;
        int x;
        while (temp.contains(CDATA_END_KEY)) {
            int pos = temp.indexOf(CDATA_START_KEY);
            if (pos == -1) {
                out.append(temp);
                break;
            }
            String xml = temp.substring(0,pos);
            out.append(escapeDecrypt(xml));
            x = temp.indexOf(CDATA_END_KEY);
            if (x == -1) {
                continue;
            }
            String txt = temp.substring(pos+CDATA_START_KEY.length(),x);
            out.append(txt);
            temp = temp.substring(x + CDATA_END_KEY.length());
        }
        out.append(escapeDecrypt(temp));
        return out.toString();
    }

    /**
     * @param in xml字符串
     * @return 还原xml替换符号
     */
    public static String escape(String in) {
        if (in == null) {
            return StringUtil.empty;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int) (len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            switch (ch) {
                case '<':
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(LT_ENCODE);
                    break;
                case '>':
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(GT_ENCODE);
                    break;
                case '&':
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(AMP_ENCODE);
                    break;
                case '"':
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(QUOT_ENCODE);
                    break;
                default:
                {
                    break;
                }
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * 返回表达式的属性,里边会匹配挂号,有修复功能
     * @param xml            href="http://v.youku.com/v_show/id_XMjUzOTUxMTMy.html" target="video"
     * @param name           href
     * @param escapeVariable 转换的字符
     * @return 返回href 即 name的属性
     */
    static public String getExpressionAttribute(String xml, String name, char escapeVariable) {
        if (!StringUtil.hasLength(name)) {
            return null;
        }
        if (escapeVariable == ' ') {
            escapeVariable = '\\';
        }
        String sk = name + StringUtil.EQUAL;
        int i = StringUtil.indexIgnoreCaseOf(xml, sk);
        if (i - 1 >= 0) {
            char f = xml.charAt(i - 1);
            if (f != '<' && f != ' ') {
                i = StringUtil.indexIgnoreCaseOf(xml, sk, i + 1);
            }
        }
        if (i == -1) {
            return null;
        }
        int yh = 0;
        int yf = 0;
        int yd = 0;
        int ys = 0;
        int yk = 0;
        StringBuilder sb = new StringBuilder();
        String s = xml.substring(i + sk.length()).trim();
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            char old = ' ';
            if (j > 0) {
                old = s.charAt(j - 1);
            }
            if (j == 0 && c == ' ') {
                continue;
            }
            if (c == '[' && old != escapeVariable) {
                yf++;
            }
            if (c == ']' && old != escapeVariable) {
                yf--;
            }
            if (c == '{' && old != escapeVariable) {
                yh++;
            }
            if (c == '}' && old != escapeVariable) {
                yh--;
            }
            if (c == '(' && old != escapeVariable) {
                yk++;
            }
            if (c == ')' && old != escapeVariable) {
                yk--;
            }
            if (c == '\"' && yd % 2 == 0 && old != escapeVariable) { // && old != escapeVariable
                ys++;
            }
            if (c == '\'' && ys % 2 == 0 && old != escapeVariable) //&& old != escapeVariable
            {
                yd++;
            }
            if (yh % 2 == 0 && yd % 2 == 0 && ys % 2 == 0 && yf == 0 && yk == 0 && (' ' == c || escapeVariable != old && '>' == c || (j > 1 && s.charAt(j - 2) != escapeVariable && '/' == old && '>' == c))) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 简单直接的返回,不做修复判断
     * @param xml            href="http://v.youku.com/v_show/id_XMjUzOTUxMTMy.html" target="video"
     * @param name           href
     * @param escapeVariable 转换的字符
     * @return 返回href 即 name的属性
     */
    static public String getStringAttribute(String xml, String name, char escapeVariable) {
        if (!StringUtil.hasLength(name)) {
            return null;
        }
        if (escapeVariable == ' ') {
            escapeVariable = '\\';
        }
        String sk = name + StringUtil.EQUAL;
        int i = StringUtil.indexIgnoreCaseOf(xml, sk);
        if (i - 1 >= 0) {
            char f = xml.charAt(i - 1);
            if (f != '<' && f != ' ') {
                i = StringUtil.indexIgnoreCaseOf(xml, sk, i + 1);
            }
        }
        if (i == -1) {
            return null;
        }


        int yd = 0;
        int ys = 0;

        StringBuilder sb = new StringBuilder();
        String s = xml.substring(i + sk.length()).trim();
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            char old = ' ';
            if (j > 0) {
                old = s.charAt(j - 1);
            }
            if (j == 0 && c == ' ') {
                continue;
            }

            if (c == '\"' && yd % 2 == 0 && old != escapeVariable) { // && old != escapeVariable
                ys++;
            }
            if (c == '\'' && ys % 2 == 0 && old != escapeVariable) //&& old != escapeVariable
            {
                yd++;
            }
            if ( yd % 2 == 0 && ys % 2 == 0 && (' ' == c || escapeVariable != old && '>' == c || (j > 1 && s.charAt(j - 2) != escapeVariable && '/' == old && '>' == c))) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    static public String format(String xml) {
        return XMLFormat.format(xml);
    }

    /**
     * 得到xml文件配置的编码,如果为空，就返回UTF-8
     * {@code <?xml version="1.0" encoding="gb2312"?>}
     *
     * @param xml xml
     * @return 得到xml文件配置的编码
     */
    static public String getHeadEncode(String xml) {
        xml = StringUtil.trim(StringUtil.substringBefore(xml, "?>")).toLowerCase();
        if ((xml.startsWith("<?xml")) && (xml.contains("?"))) {
            String tmp = XMLUtil.getStringAttribute(xml, XML_KEY_ENCODING, '\"');
            return StringUtil.trim(XMLUtil.deleteQuote(tmp));
        }
        return Environment.defaultEncode;
    }


    /**
     * xml节点转换到map
     * @param resData xml支付串
     * @param xpath 路径
     * @return xml节点转换到map
     */
    public static Map<String, Object> getMap(String resData, String xpath)
    {
        Map<String, Object> resultMap = new TreeMap<>();
        Document doc;
        try {
            doc = DocumentHelper.parseText(resData);
        } catch (DocumentException e) {
            log.error("xml 转换异常",e);
            return resultMap;
        }
        List<Node> selectNodes = doc.selectNodes(xpath);
        for (Node node : selectNodes) {
            resultMap.put(node.getName(), node.getText());
        }

        return resultMap;
    }

}
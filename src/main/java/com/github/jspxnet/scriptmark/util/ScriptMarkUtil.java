/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.util;

import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;
import com.github.jspxnet.scriptmark.exception.ScriptException;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

import java.util.LinkedList;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-27
 * Time: 15:28:52
 */
public class ScriptMarkUtil {
    private static final char[] ESCAPES = createEscapes();

    private static char[] createEscapes() {
        char[] escapes = new char['\\' + 1];
        for (int i = 0; i < 32; ++i) {
            escapes[i] = 1;
        }
        escapes['\\'] = '\\';
        escapes['\''] = '\'';
        escapes['"'] = '"';
        escapes['<'] = 'l';
        escapes['>'] = 'g';
        escapes['&'] = 'a';
        escapes['\b'] = 'b';
        escapes['\t'] = 't';
        escapes['\n'] = 'n';
        escapes['\f'] = 'f';
        escapes['\r'] = 'r';
        escapes['$'] = '$';
        return escapes;
    }

    private ScriptMarkUtil() {

    }

    public static int indexIgnoreCaseOf(String source, String target) {
        return indexIgnoreCaseOf(source.toCharArray(), 0, source.length(),
                target.toCharArray(), 0, target.length(), 0);
    }

    public static int indexIgnoreCaseOf(String source, String target, int begin) {
        return indexIgnoreCaseOf(source.toCharArray(), 0, source.length(),
                target.toCharArray(), 0, target.length(), begin);
    }

    /**
     * 得到初始化好的int数组。
     *
     * @param length 数组长度
     * @param value  初始值
     * @return 初始化后的int数组，各个元素的值都等于指定的value。
     * @since 0.5
     */
    public static Integer[] getInitIntArray(int length, int value) {
        Integer[] indexes = new Integer[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = i + value;
        }
        return indexes;
    }

    static int indexIgnoreCaseOf
            (
                    char[] source,
                    int sourceOffset,
                    int sourceCount,
                    char[] target,
                    int targetOffset,
                    int targetCount,
                    int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int i = sourceOffset + fromIndex;
        int max = sourceOffset + (sourceCount - targetCount);

        startSearchForFirstChar:
        while (true) {
            /* Look for first zhex. */
            while (i <= max && (toUpperCase(source[i]) != toUpperCase(first))) {
                i++;
            }
            if (i > max) {
                return -1;
            }

            /* Found first zhex, now look at the rest of v2 */
            int j = i + 1;
            int end = j + targetCount - 1;
            int k = targetOffset + 1;
            while (j < end) {
                if (toUpperCase(source[j++]) != toUpperCase(target[k++])) {
                    i++;
                    /* Look for str's first char again. */
                    continue startSearchForFirstChar;
                }
            }
            return i - sourceOffset;
            /* Found whole string. */
        }
    }

    static char toLowerCase(char ch) {
        String str = "" + ch;
        return str.toLowerCase().toCharArray()[0];
    }

    static char toUpperCase(char ch) {
        String str = "" + ch;
        return str.toUpperCase().toCharArray()[0];
    }

    public static String getFtlEnc(String s) {
        StringBuilder buf = null;
        int l = s.length();
        int el = ESCAPES.length;
        for (int i = 0; i < l; i++) {
            char c = s.charAt(i);
            if (c < el) {
                char escape = ESCAPES[c];
                switch (escape) {
                    case 0: {
                        if (buf != null) {
                            buf.append(c);
                        }
                        break;
                    }
                    case 1: {
                        if (buf == null) {
                            buf = new StringBuilder(s.length() + 3);
                            buf.append(s, 0, i);
                        }
                        // hex encoding for characters below 0x20
                        // that have no other escape representation
                        buf.append("\\x00");
                        int c2 = (c >> 4) & 0x0F;
                        c = (char) (c & 0x0F);
                        buf.append((char) (c2 < 10 ? c2 + '0' : c2 - 10 + 'A'));
                        buf.append((char) (c < 10 ? c + '0' : c - 10 + 'A'));
                        break;
                    }
                    default: {
                        if (buf == null) {
                            buf = new StringBuilder(s.length() + 2);
                            buf.append(s, 0, i);
                        }
                        buf.append('\\');
                        buf.append(escape);
                    }
                }
            } else {
                if (buf != null) {
                    buf.append(c);
                }
            }
        }
        return buf == null ? s : buf.toString();
    }

    public static String getFtlDec(String s) throws ScriptException {
        int idx = s.indexOf('\\');
        if (idx == -1) {
            return s;
        }
        int lidx = s.length() - 1;
        int bidx = 0;
        StringBuilder buf = new StringBuilder(lidx);
        do {
            buf.append(s, bidx, idx);
            if (idx >= lidx) {
                throw new ScriptException(s, "The last character of string literal is backslash", 0, 0);
            }
            char c = s.charAt(idx + 1);
            switch (c) {
                case '"':
                    buf.append('"');
                    bidx = idx + 2;
                    break;
                case '\'':
                    buf.append('\'');
                    bidx = idx + 2;
                    break;
                case '\\':
                    buf.append('\\');
                    bidx = idx + 2;
                    break;
                case 'n':
                    buf.append('\n');
                    bidx = idx + 2;
                    break;
                case 'r':
                    buf.append('\r');
                    bidx = idx + 2;
                    break;
                case 't':
                    buf.append('\t');
                    bidx = idx + 2;
                    break;
                case 'f':
                    buf.append('\f');
                    bidx = idx + 2;
                    break;
                case 'b':
                    buf.append('\b');
                    bidx = idx + 2;
                    break;
                case 'g':
                    buf.append('>');
                    bidx = idx + 2;
                    break;
                case 'l':
                    buf.append('<');
                    bidx = idx + 2;
                    break;
                case 'a':
                    buf.append('&');
                    bidx = idx + 2;
                    break;
                case '{':
                    buf.append('{');
                    bidx = idx + 2;
                    break;
                case 'x': {
                    idx += 2;
                    int x = idx;
                    int y = 0;
                    int z = lidx > idx + 3 ? idx + 3 : lidx;
                    while (idx <= z) {
                        char b = s.charAt(idx);
                        if (b >= '0' && b <= '9') {
                            y <<= 4;
                            y += b - '0';
                        } else if (b >= 'a' && b <= 'f') {
                            y <<= 4;
                            y += b - 'a' + 10;
                        } else if (b >= 'A' && b <= 'F') {
                            y <<= 4;
                            y += b - 'A' + 10;
                        } else {
                            break;
                        }
                        idx++;
                    }
                    if (x < idx) {
                        buf.append((char) y);
                    } else {
                        throw new ScriptException(s, "Invalid \\x escape in a string literal", 0, 0);
                    }
                    bidx = idx;
                    break;
                }
                default:
                    throw new ScriptException(s, "Invalid escape sequence (\\" + c + ") in a string literal", 0, 0);
            }
            idx = s.indexOf('\\', bidx);
        } while (idx != -1);
        buf.append(s.substring(bidx));

        return buf.toString();
    }


    public static String deleteQuote(String s) {
        return XMLUtil.deleteQuote(s);
    }

    /**
     * @param s       URL
     * @param charset 编码
     * @return 得到转码URL
     * @throws UnsupportedEncodingException 异常
     */
    public static String getUrlEnc(String s, String charset)
            throws UnsupportedEncodingException {
        int ln = s.length();
        int i;
        for (i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
                    || c >= '0' && c <= '9'
                    || c == '_' || c == '-' || c == '.' || c == '!' || c == '~'
                    || c >= '\'' && c <= '*')) {
                break;
            }
        }
        if (i == ln) {
            return s;
        }

        StringBuilder b = new StringBuilder(ln + ln / 3 + 2);
        b.append(s, 0, i);
        int encStart = i;
        for (i++; i < ln; i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
                    || c >= '0' && c <= '9'
                    || c == '_' || c == '-' || c == '.' || c == '!' || c == '~'
                    || c >= '\'' && c <= '*') {
                if (encStart != -1) {
                    byte[] o = s.substring(encStart, i).getBytes(charset);
                    for (byte anO : o) {
                        b.append('%');
                        byte bc;
                        bc = anO;
                        int c1 = bc & 0x0F;
                        int c2 = (bc >> 4) & 0x0F;
                        b.append((char) (c2 < 10 ? c2 + '0' : c2 - 10 + 'A'));
                        b.append((char) (c1 < 10 ? c1 + '0' : c1 - 10 + 'A'));
                    }
                    encStart = -1;
                }
                b.append(c);
            } else {
                if (encStart == -1) {
                    encStart = i;
                }
            }
        }
        if (encStart != -1) {
            byte[] o = s.substring(encStart, i).getBytes(charset);
            for (byte anO : o) {
                b.append('%');
                int c1 = anO & 0x0F;
                int c2 = (anO >> 4) & 0x0F;
                b.append((char) (c2 < 10 ? c2 + '0' : c2 - 10 + 'A'));
                b.append((char) (c1 < 10 ? c1 + '0' : c1 - 10 + 'A'));
            }
        }
        return b.toString();
    }

    //-----------------------------------------------------------------------------
    public static final String PROTOCOL_SEPARATOR = "://";

    public static final String PATH_SEPARATOR = "/";

    public static final char PATH_SEPARATOR_CHAR = '/';

    public static final String PARENT_PATH = "..";

    public static final String CURRENT_PATH = StringUtil.DOT;

    /**
     * 清理相对路径. 处理"../"和"./"相对于根目录"/"的正确路径.
     *
     * @param url 相对路径
     * @return 对根目录的绝对路径
     * @throws java.net.MalformedURLException 访问路径超越根目录时抛出
     * @throws NullPointerException           传入path为空时抛出
     */
    public static String cleanUrl(String url) throws MalformedURLException {
        if (url == null) {
            throw new MalformedURLException("url == null");
        }
        String domain = StringUtil.empty;
        int idx = getDomainIndex(url);
        if (idx > 0) {
            domain = url.substring(0, idx);
            url = url.substring(idx);
        }
        url = url.replace('\\', PATH_SEPARATOR_CHAR);
        String[] tokens = url.split(PATH_SEPARATOR);
        LinkedList<String> list = new LinkedList<String>();
        for (String token : tokens) {
            if (PARENT_PATH.equals(token)) {
                if (list.isEmpty()) {
                    throw new MalformedURLException("非法路径访问，不允许\"../\"访问根目录\"/\"以上的目录！");
                }
                list.removeLast();
            } else if (token != null && token.trim().length() > 0
                    && !CURRENT_PATH.equals(token)) {
                list.addLast(token);
            }
        }
        StringBuilder buf = new StringBuilder();
        for (String sTemp : list) {
            buf.append(PATH_SEPARATOR);
            buf.append(sTemp);
        }
        return domain + buf.toString();
    }

    /**
     * 获取URL域名的分割位置
     *
     * @param url 路径
     * @return 域名分割位置
     */
    public static int getDomainIndex(String url) {
        if (url != null) {
            int protocolIndex = url.indexOf(PROTOCOL_SEPARATOR);
            if (protocolIndex > 0) {
                int domainIndex = url.indexOf(PATH_SEPARATOR_CHAR, protocolIndex + PROTOCOL_SEPARATOR.length());
                if (domainIndex == -1) { // 只有域名的URL
                    return url.length();
                } else {
                    return domainIndex;
                }
            }
        }
        return -1;
    }


    /**
     * 语法参考:https://github.com/coreyti/showdown
     *
     * @param markdown Markdown格式字符串
     * @return html字符串
     */
    public static String getMarkdownHtml(String markdown) {
        if (StringUtil.isNull(markdown)) {
            return StringUtil.empty;
        }
        ScriptRunner scriptRunner = new TemplateScriptEngine();
        scriptRunner.put("markdownString", markdown);
        try {
            return (String) scriptRunner.eval("(new Showdown.converter()).makeHtml(markdownString)", 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scriptRunner.exit();
        }
        return StringUtil.empty;

    }

}
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

import com.github.jspxnet.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-3-31
 * Time: 11:21:23
 * <p>
 * 静态调用方法
 * Class stringUtil = ClassUtil.loadClass(class.getName());
 */
@Slf4j
public class StringUtil {
    public static final char[] BR_TAG = "<BR>".toCharArray();


    public static final char SINGLE_QUOTE_TAG = '\'';
    public static final char DOUBLE_QUOTE_TAG = '\"';
    public static final char UNDERLINE = '_';

    public static final String empty = "";
    public static final String space = " ";
    public static final String NULL = null;
    public static final char[] HexChars = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static final String AT = "@";

    public static final String ASTERISK = "*";

    public static final String SEMICOLON = ";";

    public static final String BACKSLASH = "/";

    public static final String TRANSFERRED = "\\ ".trim();

    public static final String COMMAS = ",";

    public static final String EQUAL = "=";

    public static final String COLON = ":";

    public static final String DOT = ".";

    public static final String AND = "&";

    public static final String CR = "\n";

    public static final String TAB = "\t";

    public static final String CRLF = "\r\n";

    //split 中需要转义的字符
    public static final String[] SPLIT_TRANSFERRED  = {"|",".","*","+"};


    /**
     * 判断是否为空
     * @param value 字符串
     * @return 是否为空
     */
    public static boolean isNull(String value) {
        return value == null || value.equals(StringUtil.empty) || "null".equals(value) || value.length() < 1;
    }
    /**
     * @param sqlText sql
     * @return 返回SQL 删除排序
     */
    public static String removeOrders(String sqlText) {
        if (sqlText == null) {
            return empty;
        }
        String sqlUpperCase = sqlText.toUpperCase();
        int pos = sqlUpperCase.lastIndexOf("LIMIT");
        if (pos != -1) {
            String tmpSql = sqlUpperCase.substring(pos);
            int endPos = tmpSql.indexOf(")");
            if (endPos == -1) {
                sqlUpperCase = StringUtil.substringBeforeLast(sqlUpperCase, "LIMIT");
                sqlText = sqlText.substring(0, sqlUpperCase.length());
            }
        }
        sqlUpperCase = sqlText.toUpperCase();
        pos = sqlUpperCase.lastIndexOf("ORDER");
        if (pos != -1) {
            String tmpSql = sqlUpperCase.substring(pos);
            int endPos = tmpSql.indexOf(")");
            if (endPos == -1) {
                sqlUpperCase = StringUtil.substringBeforeLast(sqlUpperCase, "ORDER");
                sqlText = sqlText.substring(0, sqlUpperCase.length());
            }
        }
        return sqlText;
    }

    /**
     * @param numStr 字符串
     * @return 删除字符串里边的数字
     */
    public static String removeNumber(String numStr) {
        if (numStr == null) {
            return empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numStr.length(); i++) {
            char c = numStr.charAt(i);
            if (!ValidUtil.isNumber(c + "")) {
                sb.append(c);
            }
        }
        return sb.toString();

    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    public static String substringBefore(String str, String separator, int times) {
        return substringBefore(str, separator, times, empty);
    }

    /**
     * @param str       字符串
     * @param separator 切分字符串
     * @param times     切分字符串出现次数
     * @param def       未空时默认返回
     * @return 子字符串
     */
    public static String substringBefore(String str, String separator, int times, String def) {
        if (times < 1) {
            return def;
        }
        if (str == null) {
            return def;
        }
        if (separator == null) {
            return empty;
        }
        int start = 0, pos = 0, x = 0, length = str.length();
        while (pos != -1 && pos < length) {
            x++;
            start = pos;
            pos = str.indexOf(separator, pos + separator.length());
            if (times == x) {
                break;
            }
        }
        if (pos == -1) {
            if (times == x) {
                pos = length;
            } else {
                return def;
            }
        }
        if (x <= 1) {
            return str.substring(0, pos);
        }
        return str.substring(start + separator.length(), pos);
    }

    /**
     * Gets the substring before the first occurrence of a separator.
     * The separator is not returned.
     * <p>
     * A  {@code null } string input will return  {@code null } .
     * An empty ("") string input will return the empty string.
     * A  {@code null } separator will return the input string.
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str       the String transfer get a substring from, may be null
     * @param separator the String transfer search for, may be null
     * @return the substring before the first occurrence of the separator,
     * {@code null } if null String input
     * @since 2.0
     */
    public static String substringBefore(String str, String separator) {
        if (StringUtil.isNull(str) || separator == null) {
            return empty;
        }
        if (separator.length() == 0) {
            return empty;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * Gets the substring after the first occurrence of a separator.
     * The separator is not returned.
     * <p>
     * A  {@code null } string input will return  {@code null } .
     * An empty ("") string input will return the empty string.
     * A  {@code null } separator will return the empty string if the
     * input string is not  {@code null } .
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str       the String transfer get a substring from, may be null
     * @param separator the String transfer search for, may be null
     * @return the substring after the first occurrence of the separator,
     * {@code null } if null String input
     * @since 2.0
     */
    public static String substringAfter(String str, String separator) {
        if (StringUtil.isNull(str)) {
            return str;
        }
        if (separator == null) {
            return empty;
        }

        int pos = str.indexOf(separator);
        if (pos == -1) {
            return empty;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * Gets the substring before the last occurrence of a separator.
     * The separator is not returned.
     * <p>
     * A  {@code null } string input will return  {@code null } .
     * An empty ("") string input will return the empty string.
     * An empty or  {@code null } separator will return the input string.
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str       the String transfer get a substring from, may be null
     * @param separator the String transfer search for, may be null
     * @return the substring before the last occurrence of the separator,
     * {@code null } if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(String str, String separator) {
        if (StringUtil.isNull(str) || StringUtil.isNull(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * Gets the substring after the last occurrence of a separator.
     * The separator is not returned.
     * <p>
     * A  {@code null } string input will return  {@code null } .
     * An empty ("") string input will return the empty string.
     * An empty or  {@code null } separator will return the empty string if
     * the input string is not  {@code null } .
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str       the String transfer get a substring from, may be null
     * @param separator the String transfer search for, may be null
     * @return the substring after the last occurrence of the separator,
     * {@code null } if null String input
     * @since 2.0
     */
    public static String substringAfterLast(String str, String separator) {
        if (str == null || separator == null) {
            return empty;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return empty;
        }
        return str.substring(pos + separator.length());
    }

    // Substring between
    //-----------------------------------------------------------------------

    /**
     * @param str 字符串
     * @param tag 开始 结束
     * @return 中间
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * Gets the String that is nested in between two Strings.
     * Only the first match is returned.
     *
     * @param str   字符串
     * @param open  开始
     * @param close 结束
     * @return 中间
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return empty;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * @param str   字符串
     * @param open  开始字符串
     * @param close 关闭字符串
     * @return 得到两个字符串外部字符串
     */
    public static String substringOutBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return empty;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.lastIndexOf(close);
            if (end != -1) {
                if (start < end) {
                    return str.substring(start + open.length(), end);
                } else {
                    return str.substring(end, start);
                }
            }

        }
        return null;
    }

    /**
     * Searches a String for substrings delimited by a start and end tag,
     * returning all matching substrings in an array.
     * <p>
     * A  {@code null } input String returns  {@code null } .
     * A  {@code null } open/close returns  {@code null } (no match).
     * An empty ("") open/close returns  {@code null } (no match).
     *
     * <pre>
     * StringUtils.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
     * StringUtils.substringsBetween(null, *, *)            = null
     * StringUtils.substringsBetween(*, null, *)            = null
     * StringUtils.substringsBetween(*, *, null)            = null
     * StringUtils.substringsBetween("", "[", "]")          = []
     * </pre>
     *
     * @param str   the String containing the substrings, null returns null, empty returns empty
     * @param open  the String identifying the start of the substring, empty returns null
     * @param close the String identifying the end of the substring, empty returns null
     * @return a String Array of substrings, or  {@code null } if no match
     * @since 2.3
     */
    public static String[] substringsBetween(String str, String open, String close) {
        if (str == null || StringUtil.isNull(open) || StringUtil.isNull(close)) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return new String[0];
        }
        int closeLen = close.length();
        int openLen = open.length();
        List<String> list = new ArrayList<String>();
        int pos = 0;
        while (pos < (strLen - closeLen)) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        final  int length = list.size();
        return list.toArray(new String[length]);
    }

    /**
     * Replaces a String with another String inside a larger String, once.
     * <p>
     * A  {@code null } reference passed transfer this method is a no-op.
     *
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("any", null, *)    = "any"
     * StringUtils.replaceOnce("any", *, null)    = "any"
     * StringUtils.replaceOnce("any", "", *)      = "any"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "ba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @param text text transfer search and replace in, may be null
     * @param repl the String transfer search for, may be null
     * @param with the String transfer replace with, may be null
     * @return the text with any replacements processed,
     * {@code null } if null String input
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * Replaces all occurrences of a String within another String.
     * <p>
     * A  {@code null } reference passed transfer this method is a no-op.
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("any", null, *)    = "any"
     * StringUtils.replace("any", *, null)    = "any"
     * StringUtils.replace("any", "", *)      = "any"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "b"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @param text text transfer search and replace in, may be null
     * @param repl the String transfer search for, may be null
     * @param with the String transfer replace with, may be null
     * @return the text with any replacements processed,
     * {@code null } if null String input
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }


    /**
     * Replaces a String with another String inside a larger String,
     * for the first  {@code max } values of the search String.
     * <p>
     * A  {@code null } reference passed transfer this method is a no-op.
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("any", null, *, *)     = "any"
     * StringUtils.replace("any", *, null, *)     = "any"
     * StringUtils.replace("any", "", *, *)       = "any"
     * StringUtils.replace("any", *, *, 0)        = "any"
     * StringUtils.replace("abaa", "a", null, -1) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text         text transfer search and replace in, may be null
     * @param searchString the String transfer search for, may be null
     * @param replacement  the String transfer replace it with, may be null
     * @param max          maximum number of values transfer replace, or  {@code -1 } if no maximum
     * @return the text with any replacements processed,
     * {@code null } if null String input
     */
    public static String replace(String text, String searchString, String replacement, int max) {
        if (!hasLength(text) || !hasLength(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = (increase < 0 ? 0 : increase);
        increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * @param str 字符串
     * @return 首字母小写
     */
    public static String uncapitalize(String str) {
        if (!hasLength(str)) {
            return str;
        }
        if (str.length() > 1) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
        return String.valueOf(Character.toLowerCase(str.charAt(0)));
    }

    /**
     * @param str 字符串
     * @return 得到spring注册的beanId格式
     */
    public static String getSpringBeanId(String str) {
        if (!hasLength(str) || !str.contains(StringUtil.DOT)) {
            return str;
        }
        return StringUtil.uncapitalize(substringAfterLast(str,StringUtil.DOT));
    }
    /**
     * @param str 字符串
     * @return 交换大小写
     */
    public static String swapCase(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuilder buffer = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    /**
     * @param str 字符串
     * @return 首字母大写
     */
    public static String capitalize(String str) {
        if (!hasLength(str)) {
            return str;
        }
        return Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * @param line      字符串
     * @param oldString 原来的字符串
     * @param newString 替换后的字符串
     * @return 不区分大小写替换
     */
    public static String replaceIgnoreCase(String line, String oldString,
                                           String newString) {
        if (line == null) {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * @param str 字符串
     * @return 判断是否存在长度
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     */
    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    /**
     * @param input 字符串
     * @return \r\n 转换为 {@code </br>}
     */
    public static String toBrLine(String input) {
        if (!hasLength(input)) {
            return empty;
        }
        char[] chars = input.toCharArray();
        int cur = 0;
        int len = chars.length;
        StringBuilder buf = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            // If we've found a Unix newline, add BR tag.
            if (chars[i] == '\n') {
                buf.append(chars, cur, i - cur).append(BR_TAG);
                cur = i + 1;
            }
            // If we've found a Windows newline, add BR tag.
            else if (chars[i] == '\r' && i < len - 1 && chars[i + 1] == '\n') {
                buf.append(chars, cur, i - cur).append(BR_TAG);
                i++;
                cur = i + 1;
            }
        }
        // Add whatever chars are left transfer buffer.
        buf.append(chars, cur, len - cur);
        return buf.toString();
    }

    /**
     * 插入字符串
     *
     * @param input  字符串
     * @param length 长度位置
     * @param fen    插入的数据
     * @return 返回结果字符串
     */
    public static String insertString(String input, int length, String fen) {
        if (StringUtil.isNull(input)) {
            return empty;
        }
        if (length <= 0) {
            return input;
        }
        StringBuilder buf = new StringBuilder(input);
        int len = buf.length() / length;
        if (len <= 1) {
            return input;
        }
        for (int i = len; i >= 1; i--) {
            buf.insert(length * i, fen);
        }
        return buf.toString();
    }

    /**
     * @param source sql 条件字符串
     * @return 判断sql是否合法
     */
    public static String checkSql(String source) {
        if (StringUtil.isNull(source)) {
            return empty;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    break;
                case '>':
                    break;
                case SINGLE_QUOTE_TAG:
                    break;
                case DOUBLE_QUOTE_TAG:
                    break;
                case '=':
                    break;
                case '{':
                    break;
                case '}':
                    break;
                default:
                    result.append(c);
                    break;
            }
        }
        return replaceIgnoreCase(replaceIgnoreCase(result.toString(), " or ", ""), " and ", " ");
    }

    /**
     * Tests whether a given zhex is alphabetic, numeric or
     *
     * @param c The zhex transfer be tested
     * @return whether the given zhex is alphameric or not
     */
    public static boolean isAlphaNumeric(char c) {
        return c == '_' ||
                (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }


    /**
     * @param numStr 字符串
     * @return 字符串中提出数字
     */
    public static String getNumber(String numStr) {
        return compile("[^0-9]").matcher(numStr).replaceAll("");
    }

    /**
     *
     * @param numStr 字符串
     * @return 去掉没有数字的字符串
     */
    public static String getNotNumber(String numStr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numStr.length(); i++) {
            char c = numStr.charAt(i);
            if (c != ' ' && !ValidUtil.isNumber(c + "")) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 是否为一个标准的数学数字Checks whether the String a valid Java number.
     * <p>
     * Valid numbers include hexadecimal marked with the  {@code 0x  }
     * qualifier, scientific notation and numbers marked with a type
     * qualifier (e.g. 123L).
     * <p>
     * {@code Null } and empty String will return
     * {@code false } .
     *
     * @param str the  {@code String } transfer check
     * @return {@code true } if the string is a correctly formatted number
     */
    public static boolean isStandardNumber(String str) {
        if (StringUtil.isNull(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                            && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want transfer loop transfer the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop transfer the next transfer last char or transfer the last char if we need another digit transfer
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (!allowSigns
                    && (
                    chars[i] == 'd'
                            || chars[i] == 'D'
                            || chars[i] == 'f'
                            || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                    || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last zhex is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it transfer make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }


    /**
     * UTF编码转换为真实的字符串
     *
     * @param str UTF 编码列表 \\u格式,支持中英文格式
     * @return UTF编码专字符串
     */
    public static String UTFToString(String str) {
        if (!str.contains("\\u")) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        try {
            while (str.length() > 0) {
                //4位长度
                if (str.startsWith("\\u") && str.length() >= 6 && !str.substring(2, 6).contains("\\")) {
                    sb.append((char) Integer.parseInt(str.substring(2, 6), 16));
                    str = str.substring(6);
                    continue;
                }
                //2位长度
                if (str.startsWith("\\u") && str.length() >= 4 && !str.substring(2, 4).contains("\\")) {
                    sb.append((char) Integer.parseInt(str.substring(2, 4), 16));
                    str = str.substring(4);
                    continue;
                }
                sb.append(str.charAt(0));
                str = str.substring(1);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return sb.toString();
    }

    /**
     * 切分字符串
     *
     * @param source 切分字符串
     * @param cut    切分表示
     * @return String[]  返回切分结果
     */
    public static String[] split(String source, String cut) {
        if (StringUtil.isNull(cut)) {
            cut = StringUtil.SEMICOLON;
        }
        if (isEmpty(source)) {
            return new String[0];
        }

        if (ArrayUtil.contains(SPLIT_TRANSFERRED,cut))
        {
            cut = TRANSFERRED + cut;
        }
        return source.split(cut);
    }


    /**
     * 此方法将给出的字符串source使用delim划分为单词数组。
     *
     * @param source 需要进行划分的原字符串
     * @param cut    单词的分隔字符
     * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组。
     * @since 0.2
     */
    public static String[] split(String source, char cut) {
        return split(source, String.valueOf(cut));
    }

    /**
     * 此方法将给出的字符串source使用逗号划分为单词数组。
     *
     * @param source 需要进行划分的原字符串
     * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组。
     * @since 0.1
     */

    public static String[] split(String source) {
        return split(source, StringUtil.SEMICOLON);
    }

    /**
     * 短信方式切分成指定长度的字符串
     *
     * @param source 字符串
     * @param length 长度
     * @return 切分的长度
     */
    public static String[] split(String source, int length) {
        if (source == null) {
            return new String[0];
        }
        String[] result = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (getLength(sb.toString()) >= length) {
                result = ArrayUtil.add(result, sb.toString());
                sb.setLength(0);
            }
            sb.append(c);
        }
        if (sb.length() > 0) {
            result = ArrayUtil.add(result, sb.toString());
        }

        return result;
    }

    /**
     * @param source URL
     * @return String 删除url文件名
     */
    public static String deleteURLFileName(String source) {
        if (StringUtil.isNull(source)) {
            return empty;
        }
        if (source.contains("?")) {
            source = substringAfter(source, "?");
        }
        String[] ls = split(source, "/");
        if (ls == null) {
            return empty;
        }
        return source.substring(0, source.length() - ls[ls.length - 1].length());
    }

    /**
     * 字符串数组中是否包含指定的字符串。
     *
     * @param strings       字符串数组
     * @param string        字符串
     * @param caseSensitive 是否大小写敏感
     * @return 包含时返回true，否则返回false
     * @since 0.4
     */
    public static boolean contains(String[] strings, String string, boolean caseSensitive) {
        for (String string1 : strings) {
            if (caseSensitive) {
                if (string1.equals(string)) {
                    return true;
                }
            } else {
                if (string1.equalsIgnoreCase(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 字符串数组中是否包含指定的字符串。大小写敏感。
     *
     * @param strings 字符串数组
     * @param string  字符串
     * @return 包含时返回true，否则返回false
     * @since 0.4
     */
    public static boolean contains
    (String[] strings, String
            string) {
        return contains(strings, string, true);
    }

    /**
     * 不区分大小写判定字符串数组中是否包含指定的字符串。
     *
     * @param strings 字符串数组
     * @param string  字符串
     * @return 包含时返回true，否则返回false
     * @since 0.4
     */
    public static boolean containsIgnoreCase(String[] strings, String string) {
        return contains(strings, string, false);
    }

    /**
     * 将字符串数组使用指定的分隔符合并成一个字符串。
     *
     * @param array 字符串数组
     * @param delim 分隔符，为null的时候使用""作为分隔符（即没有分隔符）
     * @return 合并后的字符串
     * @since 0.4
     */
    public static String combineStringArray
    (String[] array, String
            delim) {
        if (array == null) {
            return empty;
        }
        if (delim == null) {
            delim = empty;
        }
        StringBuilder result = new StringBuilder();
        for (String aArray : array) {
            result.append(aArray).append(delim);
        }
        return result.toString();
    }

    /**
     * @param c      指定的字符
     * @param length 指定的长度
     * @return 最终生成的字符串 以指定的字符和长度生成一个该字符的指定长度的字符串
     * @since 0.6
     */
    public static String fillString(char c, int length) {
        String ret = empty;
        for (int i = 0; i < length; i++) {
            ret += c;
        }
        return ret;
    }

    /**
     * 包括\t tab 建一起清除
     *
     * @param value 字符串
     * @return 清除空格
     */
    public static String trim(String value) {
        if (StringUtil.isNull(value)) {
            return empty;
        } else {
            return replaceOnce(value.trim(), "\t", StringUtil.empty);
        }
    }

    /**
     * 去除左边多余的空格。
     *
     * @param value 待去左边空格的字符串
     * @return 去掉左边空格后的字符串
     * @since 0.6
     */
    public static String trimLeft(String value) {
        if (value == null) {
            return StringUtil.empty;
        }
        String result = value;
        char[] ch = result.toCharArray();
        int index = -1;
        for (int i = 0; i < ch.length; i++) {
            if (Character.isWhitespace(ch[i])) {
                index = i;
            } else {
                break;
            }
        }
        if (index != -1) {
            result = result.substring(index + 1);
        }
        return result;
    }

    /**
     * 去除右边多余的空格。
     *
     * @param value 待去右边空格的字符串
     * @return 去掉右边空格后的字符串
     * @since 0.6
     */
    public static String trimRight(String value) {
        if (value == null) {
            return StringUtil.empty;
        }
        String result = value;
        char[] ch = result.toCharArray();
        int endIndex = -1;
        for (int i = ch.length - 1; i > -1; i--) {
            if (Character.isWhitespace(ch[i])) {
                endIndex = i;
            } else {
                break;
            }
        }
        if (endIndex != -1) {
            result = result.substring(0, endIndex);
        }
        return result;
    }

    /**
     * 根据转义列表对字符串进行转义。
     *
     * @param source        待转义的字符串
     * @param escapeCharMap 转义列表
     * @return 转义后的字符串
     * @since 0.6
     */
    public static String escapeCharacter(String source, Map escapeCharMap) {
        if (source == null || source.length() == 0) {
            return source;
        }
        if (escapeCharMap.size() == 0) {
            return source;
        }
        StringBuilder sb = new StringBuilder();
        StringCharacterIterator sci = new StringCharacterIterator(source);
        for (char c = sci.first(); c != StringCharacterIterator.DONE; c = sci.next()) {
            String character = String.valueOf(c);
            if (escapeCharMap.containsKey(character)) {
                character = (String) escapeCharMap.get(character);
            }
            sb.append(character);
        }
        return sb.toString();
    }

    /*
    得到字符的 16 位编码
    注意 encodeHex (ss.getBytes("GBK"))
    转时候 getBytes（参数）不同， 得到对应的 GBK 16 位编码
    */

    public static String encodeHex(byte[] bytes, String fen) {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < bytes.length; i++) {
            sb.append(HexChars[(bytes[i] >> 4) & 0xf]);
            sb.append(HexChars[bytes[i] & 0xf]);
            if (fen != null) {
                sb.append(fen);
            }
        }
        return sb.toString();
    }

    /*
    得到字符的 编码
    注意 encodeBytes (ss.getBytes("GBK"))
    转时候 getBytes（参数）不同， 得到对应的 编码 会不同
    */

    public static String encodeBytes(
            byte[] bytes) {
        if (bytes == null) {
            return empty;
        }
        StringBuilder buff = new StringBuilder();
        for (byte aByte : bytes) {
            buff.append(aByte).append(" ");
        }
        return buff.toString();
    }

    /*
    将String  转为 Unicode 编码
    */

    public static String toUTFString(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            result.append("\\u").append(toHexString(value.charAt(i), 4, '0'));
        }
        return result.toString();
    }

    /**
     * @param value 字符串
     * @return 得到手机短信格式的UTF编码
     */
    public static String toMobileUTFString(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            result.append(toHexString(value.charAt(i), 4, '0'));
        }
        return result.toString().toUpperCase();
    }

    /**
     * UTF编码转换为真实的字符串
     * 字符串格式，每4个为一组
     *
     * @param str UTF 编码列表 \\u格式,支持中英文格式
     * @return UTF编码专字符串
     */
    public static String mobileUTFToString(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i + 4 <= length; i = i + 4) {
            sb.append((char) Integer.parseInt(str.substring(i, i + 4), 16));
        }
        return sb.toString();
    }

    /**
     * @param value char 基本类型 得到的int
     * @param len   返回保持的长度
     * @param pad   不够填补
     * @return 得到字符的 16 位编码
     */
    public static String toHexString(long value, int len, char pad) {
        StringBuilder sb = new StringBuilder(Long.toHexString(value));
        int pos = len - sb.length();
        while (pos-- > 0) {
            sb.insert(0, pad);
        }
        return new String(sb);
    }

    /**
     * @param b 要转换的byte
     * @return 得到 16 位编码，一般看的时候是按照两位来看，但计算的时候不分开
     */
    public static String toHexString(byte[] b) {
        if (b == null) {
            return empty;
        }
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HexChars[(aB & 0xf0) >>> 4]);
            sb.append(HexChars[aB & 0x0f]);
        }
        return sb.toString();
    }


    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 字符串
     * @return 安c++的方式得到字符串长度
     */
    public static int getLength(String s) {
        if (s==null)
        {
            return 0;
        }
        int len = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int highByte = c >>> 8;
            len += highByte == 0 ? 1 : 2;
        }
        return len;
    }

    /**
     * @param value 字符串
     * @return 判断是否为中文
     */
    public static boolean isChinese(String value)
    {
        if (isNull(value)) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (value.substring(i, i + 1).matches("[\\u4E00-\\u9FA5]+")) {

                return true;
            }
        }
        return false;
    }

    /**
     * @param value 字符串
     * @return 删除中文
     */
    public static String deleteChinese(String value) {
        if (StringUtil.isNull(value)) {
            return empty;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (!value.substring(i, i + 1).matches("[\\u4E00-\\u9FA5]+")) {

                result.append(value, i, i + 1);
            }
        }
        return result.toString();
    }

    /**
     * @param value 文本
     * @param del   行  从1开始
     * @return 删除行
     */
    public static String deleteLine(String value, int del) {
        if (StringUtil.isNull(value)) {
            return empty;
        }
        StringBuilder result = new StringBuilder();
        StringTokenizer st = new StringTokenizer(StringUtil.replace(value, "\r\n", "\n"), "\n");
        if (st.countTokens() > 0) {
            int i = 0;
            while (st.hasMoreElements()) {
                i++;
                String line = st.nextToken();
                if (del != i) {
                    result.append(line).append("\r\n");
                }
            }
        }
        return trimRight(result.toString());
    }


    public static String replaceLine(String value, int del, String newLine) {
        if (StringUtil.isNull(value)) {
            return empty;
        }
        StringBuilder result = new StringBuilder();
        StringTokenizer st = new StringTokenizer(StringUtil.replace(value, "\r\n", "\n"), "\n");
        if (st.countTokens() > 0) {
            int i = 0;
            while (st.hasMoreElements()) {
                i++;
                String line = st.nextToken();
                if (del == i) {
                    result.append(StringUtil.trim(newLine)).append("\r\n");
                } else {
                    result.append(line).append("\r\n");
                }
            }
        }
        return trimRight(result.toString());
    }

    public static boolean isEmpty(String value) {
        return value == null || value.equals(empty) || value.length() < 1;
    }


    public static boolean isBlank(String value) {
        if (StringUtil.isNull(value)) {
            return true;
        }
        String tmp = replace(value, "\t", "");
        tmp = replace(value, "\n", "");
        tmp = replace(value, "\f", "");
        tmp = replace(value, "\r", "");
        return hasLength(tmp);
    }


    public static Map<String, String> toMap(String str) {
        return toMap(str, ":", StringUtil.SEMICOLON);
    }

    public static Map<String, String> toMap(String str, String keySplit, String lineSplit) {

        Map<String, String> result = new HashMap<String, String>();
        String[] attachmentsArray = split(str, lineSplit);
        for (String value : attachmentsArray) {
            if (StringUtil.isNull(value)) {
                continue;
            }
            if (!value.contains(keySplit)) {
                continue;
            }
            result.put(substringBefore(value, keySplit), substringAfter(value, keySplit));
        }
        return result;
    }

    public static boolean isHttp(String http) {
        return !StringUtil.isNull(http) && http.length() >= 2 && http.toLowerCase().startsWith("http");
    }

    /**
     * 修复HTTP 判断用户是否输入了 http:// 如果没有就增加 http://
     *
     * @param http url
     * @return 修复HTTP nurl
     */
    public static String mendHttp(String http) {
        if (StringUtil.isNull(http) || http.length() < 2) {
            return "http://";
        }
        if (http.toLowerCase().startsWith("http://")) {
            return http;
        } else {
            return "http://" + http;
        }
    }

    public static String toScript(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '"' || c == '\'' || c == '\\' || c == '>' || c < 0x20) {
                StringBuilder b = new StringBuilder(ln + 4);
                b.append(s, 0, i);
                while (true) {
                    if (c == '"') {
                        b.append("\\\"");
                    } else if (c == '\'') {
                        b.append("\\'");
                    } else if (c == '\\') {
                        b.append("\\\\");
                    } else if (c == '>') {
                        b.append("\\>");
                    } else if (c < 0x20) {
                        switch (c) {
                            case '\n':
                                b.append("\\n");
                                break;
                            case '\r':
                                b.append("\\r");
                                break;
                            case '\f':
                                b.append("\\f");
                                break;
                            case '\b':
                                b.append("\\b");
                                break;
                            case '\t':
                                b.append("\\t");
                                break;
                            default:
                                b.append("\\x");
                                int x = c / 0x10;
                                b.append((char)
                                        (x < 0xA ? x + '0' : x - 0xA + 'A'));
                                x = c & 0xF;
                                b.append((char)
                                        (x < 0xA ? x + '0' : x - 0xA + 'A'));
                                break;
                        }
                    } else {
                        b.append(c);
                    }
                    i++;
                    if (i >= ln) {
                        return b.toString();
                    }
                    c = s.charAt(i);
                }
            } // if has transfer be escaped
        } // for each characters
        return s;
    }


    public static String getElementName(String element) {
        if (StringUtil.isNull(element)) {
            return empty;
        }
        if (element.startsWith("[:")) {
            return element;
        }
        return substringBetween(element, "[", ":");
    }


    public static String getElementValue(String element) {
        if (StringUtil.isNull(element)) {
            return empty;
        }
        if (!element.contains("[")) {
            return element;
        }
        return substringBetween(element, ":", "]");
    }

    public static String getMemberId(String element) {
        if (StringUtil.isNull(element)) {
            return empty;
        }
        if (element.startsWith("[:")) {
            return element;
        }
        return substringBetween(element, "[", ":");
    }

    public static String getMemberName(String element) {
        if (StringUtil.isNull(element)) {
            return empty;
        }
        if (!element.contains("[")) {
            return element;
        }
        return substringBetween(element, ":", "]");
    }

    /**
     * @param str 字符串
     * @return String 删除xml 中多的文字
     */
    static public String deleteText(String str) {
        if (str == null) {
            return empty;
        }
        if (!str.contains(">")) {
            return str;
        }
        StringBuilder out = new StringBuilder(str.length());
        while (str.contains("<")) {
            out.append(str, str.indexOf("<"), str.indexOf(">") + 1);
            if (!str.contains("<")) {
                continue;
            }
            str = str.substring(str.indexOf(">") + 1);
        }
        out.append(str);
        return out.toString().trim();
    }

    /**
     * @param str   字符串
     * @param ibein 开始
     * @param iend  结束
     * @return 中文按照两个的长处理返回
     */
    static public String csubstring(String str, int ibein, int iend) {
        if (str == null) {
            return empty;
        }
        StringBuilder result = new StringBuilder();
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches("[\\u4E00-\\u9FA5]+")) {
                j = j + 2;
            } else {
                j++;
            }
            if (j >= ibein && j <= iend) {
                result.append(str, i, i + 1);
            }
        }
        return result.toString();
    }


    static char toLowerCase(char ch) {
        return ("" + ch).toLowerCase().toCharArray()[0];
    }

    static char toUpperCase(char ch) {
        return (ch + "").toUpperCase().toCharArray()[0];
    }

    /**
     * @param source       字符串
     * @param sourceOffset 启始位置
     * @param sourceCount  长度
     * @param target       查找
     * @param targetOffset 查找偏移
     * @param targetCount  查找
     * @param fromIndex    开始位置
     * @return indexOf(value, offset, count, str.value, str.offset, str.count, fromIndex); 不区分大小写的 indexOf
     */
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

    /**
     * @param str 查找的字符串
     * @param ind 要查询的数组
     * @return 判断字符串中是否存在数组中的字符
     */
    public static boolean indexOfArray(String str, String[] ind) {
        if (str == null) {
            return false;
        }
        if (ind == null) {
            return false;
        }
        for (String ix : ind) {
            if (str.contains(ix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param source 字符串
     * @param target 查询的字符串
     * @return int    接口简化的不区分大小写的 indexOf  返回位置
     */
    public static int indexIgnoreCaseOf
    (String
             source, String
             target) {
        return indexIgnoreCaseOf(source.toCharArray(), 0, source.length(),
                target.toCharArray(), 0, target.length(), 0);
    }

    public static int indexIgnoreCaseOf(String source, String target, int begin) {
        return indexIgnoreCaseOf(source.toCharArray(), 0, source.length(),
                target.toCharArray(), 0, target.length(), begin);
    }

    public static boolean toBoolean(String str) {
        if (str == null) {
            return false;
        }
        return ("TRUE".equalsIgnoreCase(str.toUpperCase()) || "yes".equalsIgnoreCase(str) || "on".equalsIgnoreCase(str) || "y".equalsIgnoreCase(str) || "T".equalsIgnoreCase(str) || "是".equalsIgnoreCase(str) || "ok".equalsIgnoreCase(str)) || (ValidUtil.isNumber(str) && toInt(str) > 0) || !(StringUtil.isNull(str) || ("FALSE".equalsIgnoreCase(str.toUpperCase()) || "no".equalsIgnoreCase(str) || "undefined".equalsIgnoreCase(str) || "f".equalsIgnoreCase(str) || "否".equalsIgnoreCase(str) || "不".equalsIgnoreCase(str)) || (ValidUtil.isNumber(str) && toInt(str) <= 0));
    }


    /**
     * 提供把字符串转为整数
     *
     * @param value 转换的数字
     * @return 四舍五入后的结果
     */
    public static int toInt(String value) {
        if (value == null || "".equals(value)) {
            return 0;
        }
        if ("true".equalsIgnoreCase(value) || "t".equalsIgnoreCase(value)) {
            return 1;
        }
        if ("false".equalsIgnoreCase(value) || "f".equalsIgnoreCase(value)) {
            return 0;
        }
        if (value.contains("pixels")) {
            value = trim(replace(value, "pixels", ""));
        }
        return toInt(value, 0);
    }

    /**
     * 提供把字符串转为整数
     *
     * @param value 转换的数字
     * @param nint  空备用返回
     * @return 四舍五入后的结果
     */
    public static int toInt(String value, int nint) {
        if (value == null) {
            return nint;
        }
        value = trim(value);
        if (!isStandardNumber(value)) {
            return nint;
        }
        if (value.contains(".")) {
            try {
                return (int) Double.parseDouble(replace(value, ",", ""));
            } catch (NumberFormatException e) {
                return nint;
            }
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return nint;
        }
    }

    /**
     * 提供把字符串转为精度数
     *
     * @param sdouble 转换的数字
     * @return 结果
     */
    public static double toDouble(String sdouble) {
        return toDouble(sdouble, 0, 0);
    }

    /**
     * 提供把字符串转为精度数
     *
     * @param sdouble 转换的数字
     * @param nint    空备用返回
     * @param bint    出错备用返回
     * @return 结果
     */
    public static double toDouble(String sdouble, double nint, double bint) {
        if (sdouble == null) {
            return nint;
        }
        if ("null".equalsIgnoreCase(sdouble)) {
            return nint;
        }
        if ("".equals(sdouble)) {
            return nint;
        }
        try {
            return Double.parseDouble(replace(sdouble, ",", ""));
        } catch (NumberFormatException e) {
            return bint;
        }
    }

    /**
     * @param sdouble 数字字符串
     * @return float 转换为Float
     */
    public static float toFloat(String sdouble) {
        return toFloat(sdouble, 0);
    }

    /**
     * @param sdouble 数字字符串
     * @param nint    异常默认
     * @return float  转换为Float
     */
    public static float toFloat(String sdouble, long nint) {
        if (sdouble == null) {
            return nint;
        }
        try {
            return Float.parseFloat(replace(sdouble, ",", ""));
        } catch (NumberFormatException e) {
            return nint;
        }
    }

    /**
     * @param sdouble 数字字符串
     * @return long  转换为 long
     */
    public static long toLong(String sdouble) {
        return toLong(sdouble, 0);
    }


    /**
     * @param sdouble 字符串数字
     * @param nint    保留小数
     * @return long 提供把字符串转为Long
     */
    public static long toLong(String sdouble, long nint) {
        if (sdouble == null) {
            return nint;
        }
        try {
            return Long.parseLong(replace(sdouble, ",", ""));
        } catch (NumberFormatException e) {
            return nint;
        }
    }

    public static Date getDate(String date) {
        return getDate(date, (Date) null);
    }

    /**
     * @param date        字符串日期
     * @param defaultDate 默认日期
     * @return 转换后的日期
     */
    public static Date getDate(String date, Date defaultDate) {
        if (StringUtil.isNull(date)) {
            return defaultDate;
        }
        date = trim(fullToHalf(date));
        String format = null;
        if (countMatches(date, "-") == 2 && countMatches(date, ":") == 2 && date.contains("T")) {
            //2014-06-25T05:01:04.595Z
            if (date.length() > 20 && date.contains(DOT))
            {
                format = DateUtil.UTC_ST_FORMAT;
            } else
            {
                //2014-06-25T05:01:04
                format = DateUtil.UTC_SHORT_FORMAT;
            }
        }
        else if (date.length() > 14 && date.length() <= 19 && countMatches(date, "-") == 2 && countMatches(date, ":") == 2) {
            format = DateUtil.FULL_ST_FORMAT;
        } else if (date.length() > 14 && date.length() <= 17 && countMatches(date, "-") == 2 && countMatches(date, ":") == 1) {
            format = DateUtil.CURRENCY_ST_FORMAT;
        } else if (date.length() > 14 && date.length() <= 19 && countMatches(date, "/") == 2 && countMatches(date, ":") == 2) {
            format = DateUtil.FULL_J_FORMAT;
        } else if (date.length() > 14 && date.length() <= 17 && countMatches(date, "/") == 2 && countMatches(date, ":") == 1) {
            format = DateUtil.CURRENCY_J_FORMAT;
        } else if (date.contains("年") && date.contains("月") && date.contains("日") && countMatches(date, ":") == 2) {
            format = "yy年MM月dd日 HH:mm:ss";
        } else if (date.contains("年") && date.contains("月") && date.contains("日") && countMatches(date, ":") == 1) {
            format = DateUtil.CN_FORMAT;
        } else if (date.length() >= 8 && date.length() < 11 && countMatches(date, "-") == 2) {
            format = DateUtil.DAY_FORMAT;
        } else if (date.length() >= 8 && date.length() < 11 && countMatches(date, "/") == 2) {
            format = "yyyy/MM/dd";
        } else if (date.length() >= 8 && date.length() < 11 && countMatches(date, ".") == 2) {
            format = "yyyy.MM.dd";
        } else if (date.length() > 5 && date.length() < 9 && countMatches(date, "-") == 2) {
            format = DateUtil.SHORT_DATE_FORMAT;
        } else if (date.length() == DateUtil.UTC_FTP_FORMAT.length() && countMatches(date, "-") == 0 && countMatches(date, ":") == 0) {
            format = DateUtil.UTC_FTP_FORMAT;
        }

        if (StringUtil.isNull(format) && (countMatches(date, "-") == 1 || countMatches(date, ".") == 1)) {
            date = replace(replace(date, ".", "-") + "-01", "--", "-");
            format = "yyyy-MM-dd";
        }

        if (!StringUtil.isNull(format)) {
            Date result = getDate(date, format);
            return result == null ? defaultDate : result;
        }
        String sDate = getNumber(date);
        int iLen = sDate.length();

        if (iLen == 4 && ValidUtil.isNumber(date)) {
            format = "MMdd";
        } else if (iLen == 6 && ValidUtil.isNumber(date)) {
            format = "yyMMdd";
        } else if (iLen == 8 && ValidUtil.isNumber(date)) {
            format = "yyyyMMdd";
        } else if (iLen == 10 && ValidUtil.isNumber(date)) {
            format = "yyyyMMddHH";
        } else if (iLen == 12 && ValidUtil.isNumber(date)) {
            format = "yyyyMMddHHmm";
        }
        if (StringUtil.isNull(format)) {
            return defaultDate;
        }
        Date result = getDate(date, format);
        return result == null ? defaultDate : result;

    }

    /**
     * @param date          日期字符串
     * @param dateFormatStr 日期格式
     * @return Date 转换为日期
     */
    public static Date getDate(String date, String dateFormatStr) {
        if (StringUtil.isNull(dateFormatStr)) {
            return getDate(date);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            log.error(date + " getDate format " + dateFormatStr, e);

        }
        return null;
    }

    /**
     * @param date   日期字符串
     * @param format 日期格式
     * @return 判断是否为日期格式
     */
    public static boolean isDate(String date, String format) {
        if (date == null || format == null) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dateFormat.parse(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * @param s ip字符串
     * @return 判断是否为IP
     */
    static public boolean isIPAddress(String s) {
        if (StringUtil.isNull(s)) {
            return false;
        }
        String[] ips = s.split("\\.");
        if (ips.length != 4) {
            return false;
        }
        for (String ip : ips) {
            if (!ValidUtil.isNumber(ip)) {
                return false;
            }
            int xx = toInt(ip);
            if (xx < 0 || xx > 255) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param sip IP 专 IP 数字
     * @return String String 返回
     */
    static public long toIpNumber(String sip) {

        if (ValidUtil.isMacAddress(sip)) {
            String[] ip = sip.split("-");
            long a = Integer.parseInt(ip[0], 16);
            long b = Integer.parseInt(ip[1], 16);
            long c = Integer.parseInt(ip[2], 16);
            long d = Integer.parseInt(ip[3], 16);
            return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        } else {
            if (!isIPAddress(sip)) {
                sip = "127.0.0.1";
            }
            String[] ip = sip.split("\\.");
            long a = Integer.parseInt(ip[0]);
            long b = Integer.parseInt(ip[1]);
            long c = Integer.parseInt(ip[2]);
            long d = Integer.parseInt(ip[3]);
            return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;

        }
    }




    public static String getPolicyName(String str, int maxLength, final char[] nameIncertitudeChars) {
        if (str == null) {
            return StringUtil.empty;
        }
        int centerLen = str.length();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < centerLen; i++) {
            char c = str.charAt(i);
            if (!ArrayUtil.contains(nameIncertitudeChars, c)) {
                result.append(c);
            }
            if (result.length() > maxLength) {
                break;
            }
        }
        return result.toString();
    }


    /**
     * @param text 字符串
     * @return boolean 编码是否有效
     */
    static public boolean UTF8CodeCheck(String text) {
        String sign = empty;
        if (text.startsWith("%e")) {
            for (int p = 0; p != -1; ) {
                p = text.indexOf("%", p);
                if (p != -1) {
                    p++;
                }
                sign += p;
            }
        }
        return "147-1".equals(sign);
    }

    /**
     *
     * @param txt 字符串
     * @param length 长度
     * @param send 切断修补的尾巴
     * @return 切断
     */
    static public String cut(String txt, int length, String send) {
        if (send == null) {
            send = empty;
        }
        if (txt == null) {
            return empty;
        }
        if (length < 0) {
            return txt;
        }
        if (txt.length() < length) {
            return txt;
        }
        String out = csubstring(txt, 0, length);
        int vLen = length - send.length();
        if (length > 0 && vLen > 0 && vLen < out.length()) {
            out = out.substring(0, vLen) + send;
        }
        return out;
    }

    /**
     * @param txt 文本
     * @param fen 分割
     * @return 切断显示分割最后部分，如果没有分割就显示所有，用于caption
     */
    static public String cutBefore(String txt, String fen) {
        if (txt == null) {
            return empty;
        }
        if (!txt.contains(fen)) {
            return txt;
        }
        return substringAfterLast(txt, fen);
    }


    /**
     * @param txt    字符串
     * @param length 保留长度
     * @param send   结尾填补
     * @return 保留长度的切取字符串
     */
    static public String leftCut(String txt, int length, String send) {
        if (send == null) {
            send = empty;
        }
        if (txt == null) {
            return empty;
        }
        if (length < 0) {
            return txt;
        }
        if (txt.length() < length) {
            return txt;
        }
        int startPos = txt.length() - length;
        if (startPos < 0) {
            return empty;
        }
        String out = csubstring(txt, startPos, txt.length());
        int vLen = length - send.length();
        if (length > 0 && vLen > 0) {
            out = out.substring(0, vLen) + send;
        }
        return out;
    }


    /**
     * @param start  标题
     * @param center 分割线符号
     * @param end    页数
     * @param length 长度
     * @return 目录索引对齐算法
     */
    static public String alignment(String start, String center, String end, int length) {
        if (start == null) {
            start = empty;
        }
        if (center == null) {
            return ".";
        }
        if (end == null) {
            return empty;
        }
        int centerLen = length - getLength(start) - getLength(end);
        StringBuilder result = new StringBuilder();
        result.append(start);
        for (int i = 0; i < centerLen; i++) {
            result.append(center);
        }
        result.append(end);
        return result.toString();
    }

    /**
     * 字符串切分为Map  格式  1111=bbb\r\n222=cccc\r\n
     *
     * @param text 字符串
     * @param fen  切分号
     * @param fix  修复第一个的路径 \ 为 /
     * @return 切分后的内容放入map方式返回
     */
    static public Map<String, String> splitToMap(String text, String fen, boolean fix) {
        if (StringUtil.isNull(text)) {
            return new HashMap<>();
        }
        String[] xx = split(convertCR(text), CR);
        /////////////处理每一行
        Map<String, String> result = new HashMap<String, String>();
        for (String line : xx) {
            if (fix) {
                result.put(FileUtil.mendPath(substringAfter(line, fen)), substringAfterLast(line, fen));
            } else {
                result.put(substringAfter(line, fen), substringAfterLast(line, fen));
            }
        }
        return result;
    }


    /**
     * @param text  字符串
     * @param left  比较的字符串
     * @param right 比较的字符串2
     * @return 判断支付数量是否相等   一般用在比较刮号验证上
     */
    static public boolean charCountEquals
    (String
             text, String
             left, String
             right) {
        return countMatches(text, left) == countMatches(text, right);
    }

    /**
     * Counts how many times the substring appears in the larger String.
     * <p>
     * A  {@code null } or empty ("") String input returns  {@code 0 } .
     *
     * <pre>
     * StringUtils.countMatches(null, *)       = 0
     * StringUtils.countMatches("", *)         = 0
     * StringUtils.countMatches("abba", null)  = 0
     * StringUtils.countMatches("abba", "")    = 0
     * StringUtils.countMatches("abba", "a")   = 2
     * StringUtils.countMatches("abba", "ab")  = 1
     * StringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str the String transfer check, may be null
     * @param sub the substring transfer count, may be null
     * @return the number of occurrences, 0 if either String is  {@code null  }
     */
    public static int countMatches(String str, String sub) {
        if (StringUtil.isNull(str) || StringUtil.isNull(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * 如果str 为空返回 defaultStr
     *
     * @param str        字符串
     * @param defaultStr 默认
     * @return 如果str 为空返回 defaultStr
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return !StringUtil.isNull(str) ? str : defaultStr;
    }

    /**
     * 修复namespace
     *
     * @param namespace 命名恐惧
     * @return 修复命名空间
     */
    public static String fixedNamespace(String namespace) {
        if (namespace == null) {
            return null;
        }
        if (namespace.endsWith("/")) {
            namespace = namespace.substring(0, namespace.length() - 1);
        }
        if (namespace.startsWith("/")) {
            namespace = namespace.substring(1);
        }
        return namespace;
    }

    /**
     * 得到Freemarker 的变量列表
     * 并且清除相同的变量
     *
     * @param str 字符串
     * @return 得到变量列表
     */
    public static String[] getFreeMarkerVar(String str) {

        StringBuilder sb = new StringBuilder();
        int length = str.length();
        boolean isVar = false;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c == '$' && i < length && str.charAt(i + 1) == '{') {
                isVar = true;
            }
            if (isVar && c == '}') {
                isVar = false;
                sb.append(StringUtil.SEMICOLON);
            }
            if (isVar && str.charAt(i) != '$' && str.charAt(i) != '{') {
                sb.append(c);
            }
        }

        String[] nameArray = split(sb.toString(), StringUtil.SEMICOLON);
        for (int i = 0; i < nameArray.length; i++) {
            if (nameArray[i].contains("#(")) {
                nameArray[i] = StringUtil.substringBeforeLast(nameArray[i], "#(");
            }
        }
        //过滤重复
        Set<String> set = new LinkedHashSet<String>(Arrays.asList(nameArray));
        return set.toArray(new String[set.size()]);
    }


    /**
     * 判断都是半角
     *
     * @param str 字符串
     * @return 判断都是半角 0:半角    1:混合 2:全部是全角
     */
    public static int getCompareHalf(String str) {
        if (StringUtil.isNull(str)) {
            return 0;
        }
        if (str.getBytes().length == str.length()) {
            return 0;
        }
        if (str.getBytes().length == str.length() * 2) {
            return 2;
        }
        if (str.getBytes().length < str.length() * 2 && str.getBytes().length > str.length()) {
            return 1;
        }
        return 0;
    }

    /**
     * 默认将 /  ; 空 修复
     *
     * @param str 字符串
     * @return String 修复切分
     */
    public static String convertSemicolon(String str) {
        if (str == null) {
            return empty;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if ('/' == str.charAt(i) || ' ' == str.charAt(i) || ',' == str.charAt(i)) {
                sb.append(SEMICOLON);
            } else {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * @param str 替换\r\n 到  \n
     * @return 替换到兼容\n
     */
    public static String convertCR(String str) {
        if (str == null) {
            return empty;
        }
        return StringUtil.replace(str, CRLF, CR);
    }

    /**
     * 半角转全角
     *
     * @param input 半角字符串
     * @return 全角
     */
    public static String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127) {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }


    /**
     * @param input 全角 字符串
     * @return 半角
     */
    public static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    public static String analyzerTitle(String str) {
        if (str == null) {
            return empty;
        }
        if (!str.contains("\n")) {
            return cut(str, 120, "");
        }
        while (str.contains("\n")) {
            String result = trim(str.substring(0, str.indexOf("\n")));
            if (!StringUtil.isNull(result)) {
                return result;
            }
            if (!str.contains("\n")) {
                break;
            }
            str = str.substring(str.indexOf("\n"));
        }
        return null;
    }

    /**
     *
     * @param input  输入
     * @return 加生成一个单引号
     */
    public static String quote(String input)
    {
        return quote( input, false);
    }
    /**
     * @param input 输入
     * @param dou   true:双引号;false:单引号
     * @return 生成一个引用, 引号
     */
    public static String quote(String input, boolean dou)
    {
        if (input == null) {
            return StringUtil.empty;
        }
        StringBuilder filtered = new StringBuilder(input.length() * 50);
        if (dou) {
            filtered.append("\"");
        } else {
            filtered.append("'");
        }

        char prevChar = '\u0000';
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            switch (c) {
                case '"':
                    if (dou) {
                        filtered.append("\\\"");
                    } else {
                        filtered.append(c);
                    }
                    break;
                case '\'':
                    if (!dou) {
                        filtered.append("\\'");
                    } else {
                        filtered.append(c);
                    }
                    break;
                case '\\':
                    filtered.append("\\\\");
                    break;
                case '\t':
                    filtered.append("\\t");
                    break;
                case '\n':
                    if (prevChar != '\r') {
                        filtered.append("\\n");
                    }
                    break;
                case '\r':
                    filtered.append("\\n");
                    break;
                case '\f':
                    filtered.append("\\f");
                    break;
                default:
                    filtered.append(c);
                    break;
            }
            prevChar = c;

        }
        if (dou) {
            filtered.append("\"");
        } else {
            filtered.append("'");
        }
        return filtered.toString();
    }

    public static String quoteSql(String input)
    {
        if (input == null) {
            return StringUtil.empty;
        }
        StringBuilder filtered = new StringBuilder(input.length() * 50);
        filtered.append("'");
        char prevChar = '\u0000';
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            switch (c) {
                case '\'':
                    filtered.append("''");
                    break;
                case '\t':
                    filtered.append("\\t");
                    break;
                case '\n':
                    if (prevChar != '\r') {
                        filtered.append("\\n");
                    }
                    break;
                case '\r':
                    filtered.append("\\n");
                    break;
                case '\f':
                    filtered.append("\\f");
                    break;
                default:
                    filtered.append(c);
                    break;
            }
            prevChar = c;

        }
        filtered.append("'");
        return filtered.toString();
    }

    public static String escape(String src) {
        int i;
        char j;
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j)) {
                tmp.append(j);
            } else if (j < 256) {
                tmp.append("%");
                if (j < 16) {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src, lastPos, pos);
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 转换邮件中title的乱码,例如：
     * (=?GB2312?Q?Fw: 2002=D1=D0=CC=D6=BB=E1=B6=AF=D4=B1=B4=F3=BB=E1=F4=DF=C7=
     * E9=BF=F6=CD=A8=B1=A8=A3=A8=BB=E1=D2=E9=BC=C7=C2=BC=A3=A9?=)
     *
     * @param str 待转换字符串
     * @return String 转换好的字符串
     */
    public static String mailTitleConverter(String str) {
        String temp = str;
        if (str != null) {
            String str2 = str.toUpperCase();
            if ((str2.contains("=?GB2312?Q?")) ||
                    (str2.contains("=?GB2312?B?")) ||
                    (str2.contains("=?ISO-8859-1?Q?")) ||
                    (str2.contains("=?ISO-8859-1?B?")) ||
                    (str2.contains("=?BIG5?Q?")) ||
                    (str2.contains("=?BIG5?B?")) ||
                    (str2.contains("=?US-ASCII?Q?")) ||
                    (str2.contains("=?US-ASCII?B?")) ||
                    (str2.contains("=?UNICODE-1-1-UTF-7?Q?")) ||
                    (str2.contains("=?UNICODE-1-1-UTF-7?B?"))) {
                str = temp;
                str = replaceIgnoreCase(str, "=?us-ascii?Q?", "");
                str = replaceIgnoreCase(str, "=?us-ascii?B?", "");
                str = replaceIgnoreCase(str, "=?big5?Q?", "");
                str = replaceIgnoreCase(str, "=?big5?B?", "");
                str = replaceIgnoreCase(str, "=?ISO-8859-1?Q?", "");
                str = replaceIgnoreCase(str, "=?ISO-8859-1?B?", "");
                str = replaceIgnoreCase(str, "=?GB2312?Q?", "");
                str = replaceIgnoreCase(str, "=?GB2312?B?", "");
                str = replaceIgnoreCase(str, "=?UNICODE-1-1-UTF-7?Q?", "");
                str = replaceIgnoreCase(str, "=?UNICODE-1-1-UTF-7?B?", "");
                str = replaceIgnoreCase(str, "?=", "");

                byte[] main = str.getBytes();
                byte[] remain = new byte[main.length];
                int index = 0;
                int i = 0;
                while (index < main.length) {
                    if (main[index] == '=') {

                        index++;
                        byte a1 = main[index];
                        if (a1 >= 65) {
                            a1 -= 55;
                        } else {
                            a1 -= 48;
                        }
                        index++;
                        byte a2 = main[index];
                        if (a2 >= 65) {
                            a2 -= 55;
                        } else {
                            a2 -= 48;
                        }
                        remain[i] = (byte) (a1 * 16 + a2);
                        index++;
                        i++;
                    } else {
                        remain[i] = main[index];
                        i++;
                        index++;
                    }
                }
                str = new String(remain).substring(0, i);
            }
        }
        return str;
    }


    /**
     * @param str 字符串
     * @return 解析文档中的下载和连接
     */
    public static Map<String, String> toAttachMap(String str) {
        Map<String, String> result = new HashMap<String, String>();
        String[] attachmentsArray = split(convertCR(str), CR);
        for (String value : attachmentsArray) {
            if (StringUtil.isNull(trim(value))) {
                continue;
            }
            if (value.startsWith("[link=") && value.endsWith("[/link]")) {
                String link = substringBetween(value, "[link=", "]");
                String title = substringBetween(value, "]", "[/link]");
                result.put("[" + link + "]", title);
            } else if (value.contains("=") && ValidUtil.isNumber(substringBefore(value, "="))) {
                result.put(substringBefore(value, "="), substringAfter(value, "="));
            }
        }
        return result;
    }

    /**
     * toAttachMap 的显示算法
     *
     * @param str 字符串
     * @return 过滤标签后显示文本, 否则影响美观
     */
    public static String getAttachCaption(String str) {
        String[] attachmentsArray = split(convertCR(str), CR);
        String result = "";
        for (String value : attachmentsArray) {
            if (StringUtil.isNull(trim(value))) {
                continue;
            }
            if (value.startsWith("[link=") && value.endsWith("[/link]") || value.contains("=") && ValidUtil.isNumber(substringBefore(value, "="))) {
                //...

            } else {
                if (StringUtil.isNull(result)) {
                    result = value;
                } else {
                    result = result + " " + value;
                }
            }
        }
        if (!StringUtil.isNull(result)) {
            return result;
        }
        for (String value : attachmentsArray) {
            if (StringUtil.isNull(trim(value))) {
                continue;
            }
            if (value.startsWith("[link=") && value.endsWith("[/link]")) {
                String title = substringBetween(value, "]", "[/link]");
                if (!StringUtil.isNull(title)) {
                    return title;
                }
            } else {
                if (value.contains("=") && ValidUtil.isNumber(substringBefore(value, "="))) {
                    String title = substringAfter(value, "=");
                    if (!StringUtil.isNull(title)) {
                        return title;
                    }
                }
            }
        }
        return empty;
    }

    /**
     * 2-7;9-10;20;30;40-43;50
     *
     * @param str 数组表达式表示的数组转换为真实的数组
     * @return 将arrayUtil里边的 getArrayExpression 数组表达式表示的数组转换为真实的数组
     */
    public static int[] expressionArray(String str) {
        int[] result = null;
        String[] lines = split(trim(str), StringUtil.SEMICOLON);
        for (String value : lines) {
            if (StringUtil.isNull(trim(value))) {
                continue;
            }
            if (value.contains("-")) {
                int start = toInt(substringBefore(value, "-"));
                int end = toInt(substringAfter(value, "-"));
                if (start < end) {
                    for (int i = start; i <= end; i++) {
                        result = ArrayUtil.add(result, i);
                    }
                }
            } else {
                result = ArrayUtil.add(result, toInt(value));
            }
        }
        return result;
    }

    /**
     * @param replaceChar 替换字符串
     * @param length      长度
     * @return 获取替换字符串
     */
    public static String replaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;
        for (int i = 1; i < length; i++) {
            resultReplace += replaceChar;
        }
        return resultReplace;
    }

    /*
     * 在使用本方法前，请先验证号码的合法性 规则：
     * 中国移动拥有号码段为:139,138,137,136,135,134,147,159,158,157(3G),151,152,150,182(3G),188(3G),187(3G);16个号段
     * 中国联通拥有号码段为:130,131,132,145,155,156(3G),186(3G),185(3G);8个号段
     * 中国电信拥有号码段为:133,1349,153,189(3G),180(3G);5个号码段
     * @param mobile要判断的号码
     * @return 返回相应类型：1代表联通；2代表移动；3代表电信
     */
    public static int getMobileType(String mobile) {
        if (mobile == null) {
            return 0;
        }
        if (mobile.startsWith("0") || mobile.startsWith("+860")) {
            mobile = mobile.substring(mobile.indexOf("0") + 1);
        }
        String cm = "^((13[4-9])|(147)|(15[0-2,7-9])|(18[2-3,7-8]))\\d{8}$";
        String cu = "^((13[0-2])|(145)|(15[5-6])|(186))\\d{8}$";
        String ct = "^((133)|(153)|(18[0,9]))\\d{8}$";

        int flag = 0;
        if (mobile.matches(cm)) {
            flag = 1;
        } else if (mobile.matches(cu)) {
            flag = 2;
        } else if (mobile.matches(ct)) {
            flag = 3;
        } else {
            flag = 4;
        }
        return flag;
    }

    public static String getMobileTypeName(int x) {
        String[] name = new String[]{"未知", "移动", "联通", "电信"};
        if (x <= 0 || x > 3) {
            return name[0];
        }
        return name[x];
    }


    /**
     * @param s 字符串
     * @return 转换到javascript ，最外部还包含引号
     */
    public static String toJavaScriptQuote(String s) {
        if (s == null) {
            return "null";
        }
        int ln = s.length();
        StringBuilder b = new StringBuilder(ln + 4);
        b.append('"');
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '"') {
                b.append("\\\"");
            } else if (c == '\\') {
                b.append("\\\\");
            } else if (c < 0x20) {
                if (c == '\n') {
                    b.append("\\n");
                } else if (c == '\r') {
                    b.append("\\r");
                } else if (c == '\f') {
                    b.append("\\f");
                } else if (c == '\b') {
                    b.append("\\b");
                } else if (c == '\t') {
                    b.append("\\t");
                } else {
                    b.append("\\u00");
                    int x = c / 0x10;
                    b.append((char) (x < 0xA ? x + '0' : x - 0xA + 'A'));
                    x = c & 0xF;
                    b.append((char) (x < 0xA ? x + '0' : x - 0xA + 'A'));
                }
            } else {
                b.append(c);
            }
        } // for each characters
        b.append('"');
        return b.toString();
    }


    /**
     * @param s js
     * @return 转换为js字符串
     */
    public static String toJavaScriptString(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '"' || c == '\\' || c < 0x20) {
                StringBuilder b = new StringBuilder(ln + 4);
                b.append(s, 0, i);
                while (true) {
                    if (c == '"') {
                        b.append("\\\"");
                    } else if (c == '\\') {
                        b.append("\\\\");
                    } else if (c < 0x20) {
                        if (c == '\n') {
                            b.append("\\n");
                        } else if (c == '\r') {
                            b.append("\\r");
                        } else if (c == '\f') {
                            b.append("\\f");
                        } else if (c == '\b') {
                            b.append("\\b");
                        } else if (c == '\t') {
                            b.append("\\t");
                        } else {
                            b.append("\\u00");
                            int x = c / 0x10;
                            b.append((char)
                                    (x < 0xA ? x + '0' : x - 0xA + 'a'));
                            x = c & 0xF;
                            b.append((char)
                                    (x < 0xA ? x + '0' : x - 0xA + 'a'));
                        }
                    } else {
                        b.append(c);
                    }
                    i++;
                    if (i >= ln) {
                        return b.toString();
                    }
                    c = s.charAt(i);
                }
            } // if has transfer be escaped
        } // for each characters
        return s;
    }

/*
    public static String comp(String str) {
        int i = 1;
        StringBuilder buf = new StringBuilder();
        int count = 1;
        char ch = str.charAt(0);
        for(;;){
            char c = i==str.length() ? '\10':str.charAt(i);
            if(c==ch){
                count++;
            }else{
                if(count == 1)
                    buf.append(ch);
                else
                    buf.append(count).append(ch);
                count=1;
                ch = c;
            }
            i++;
            if(i==str.length()+1){
                break;
            }
        }
        return buf.toString();
    }

 */

    /**
     * 判断是否为json数组
     *
     * @param str 字符串
     * @return 判断是否为json数组
     */
    public static boolean isJsonArray(String str) {
        int a = StringUtil.countMatches(str, "\"");
        int b = StringUtil.countMatches(str, "'");
        return str.startsWith("[") && str.endsWith("]") && (a > 0 || b > 0);
    }

    /**
     * @param str 字符串
     * @return 判断是否为json格式
     */
    public static boolean isJsonObject(String str) {
        int a = StringUtil.countMatches(str, "\"");
        int b = StringUtil.countMatches(str, "'");
        String t = trim(str);
        return t.startsWith("{") && t.endsWith("}") && (a > 0 || b > 0) && t.contains(":");
    }

    /**
     * 读取字符串中指定行数
     *
     * @param str  字符串
     * @param line 行
     * @return 读取字符串中指定行数
     * @throws IOException 异常
     */
    public static String getTextLine(String str, int line) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
        String lineTxt = empty;
        int counter = 0;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            counter++;
            if (counter == line) {
                return lineTxt;
            }
        }
        bufferedReader.close();
        return StringUtil.empty;
    }

    /**
     * @param value 字符串
     * @return 判断是否是xml结构
     */
    public static boolean isXml(String value) {
        if (StringUtil.isNull(value)) {
            return false;
        }
        value = value.trim();
        if (!(value.startsWith("<") && value.endsWith(">"))) {
            return false;
        }
        try {
            DocumentHelper.parseText(value);
        } catch (DocumentException e) {
            return false;
        }
        return true;
    }

    /**
     * 得到字符串中偶数个数
     *
     * @param str 字符串
     * @return 偶数个数
     */
    public static int getNumberEvenCount(String str) {
        int je = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Integer.parseInt(c + "") % 2 == 0) {   //偶数
                je++;
            }
        }
        return je;
    }

    /**
     * 得到｛｝ 种的内容，提供解析站位号的方便
     *
     * @param str 字符串
     * @return 得到｛｝ 列表
     */
    public static String[] getBraceTokens(String str) {
        String regex = "\\{([^}]*)\\}";
        Pattern pattern = compile(regex);
        Matcher matcher = pattern.matcher(str);
        String[] result = null;
        while (matcher.find()) {
            result = ArrayUtil.add(result, matcher.group());
        }
        return result;
    }


    /**
     *
     * @param value 字符串
     * @param lower 统一小写
     * @return  驼峰格式字符串转换为下划线格式字符串
     */
    public static String camelToUnderline(String value,boolean lower) {
        if (StringUtil.isNull(value)) {
            return empty;
        }
        String param = value;
        if (lower)
        {
            param = value.toLowerCase();
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param 字符串
     * @return 驼峰格式字符串转换为下划线格式字符串
     */
    public static String camelToUnderline(String param) {
        if (StringUtil.isNull(param)) {
            return empty;
        }
        return camelToUnderline(param,true);

    }
    /**
     * 下划线格式字符串转换为驼峰格式字符串
     *
     * @param value 字符串
     * @return 下划线格式字符串转换为驼峰格式字符串
     */
    public static String underlineToCamel(String value) {
        if (isNull(value)) {
            return empty;
        }
        String param= value.toLowerCase();
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //---------------------------------

    /**
     * 通配符方式比对路径
     *
     * @param url  路径
     * @param find 通配符路径
     * @return 是否相同
     */
    public static boolean getPatternFind(String url, String find) {
        if (find == null || url == null) {
            return false;
        }
        if (url.equals(find))
        {
            return true;
        }
        String s = find.replace(".", "#");
        s = s.replaceAll("#", "\\\\.");
        s = s.replace('*', '#');
        s = s.replaceAll("#", ".*");
        s = s.replace("?", "#");
        s = s.replaceAll("#", ".?");
        s = "^" + s + "$";
        return Pattern.matches(s, url);
    }

    public static String deleteAny(String inString, String charsToDelete) {
        if (hasLength(inString) && hasLength(charsToDelete)) {
            StringBuilder sb = new StringBuilder(inString.length());
            for (int i = 0; i < inString.length(); ++i) {
                char c = inString.charAt(i);
                if (charsToDelete.indexOf(c) == -1) {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return inString;
        }
    }


    /**
     * 注意map 的值中不能有=号,否则会出错误
     * 系统原生的map.toString()  { 1=聊天室,  2=公告,  3=应用提示, -1=未知,  4=草稿,  5=收件箱,  6=已发邮件,  7=垃圾箱,  8=管理员消息,  9=IM私人消息}
     * @param str map 字符串
     * @return 转换为map
     */
    public static Map<String,String> mapStringToMap(String str){
        StringMap<String,String> map = new StringMap<>();
        map.setKeySplit(StringUtil.EQUAL);
        map.setLineSplit(StringUtil.COMMAS);
        map.setString(StringUtil.substringOutBetween(str,"{","}"));
        return map.getValueMap();
    }

    /**
     *
     * @param str 字符串
     * @return 转换到cvs格式
     */
    public static String csvString(String str){
        if (str==null||"null".equals(str))
        {
            return empty;
        }
        if (str.startsWith("+")||str.startsWith("-")||str.startsWith("@"))
        {
            str = " " + str;
        }
        if(str.contains(",")){
            str = str.replace("\"","\"\"");
            str ="\"" + str + "\"";
        }
        return str;
    }

    /**
     *
     * @param str 字符串
     * @return 判断是否为浮点数
     */
    public static boolean isFloat(String str)
    {
        if (str==null)
        {
            return false;
        }
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }


    public static String toLowerCase(String str)
    {
        if (str==null)
        {
            return null;
        }
        return str.toLowerCase();
    }

    public static void main(String[] args) {
        Date data = StringUtil.getDate("2006-09-01T00:00:01.099 Z");
        System.out.println(DateUtil.toString(data,DateUtil.FULL_ST_FORMAT));

    }


}
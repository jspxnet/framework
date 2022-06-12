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


import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.scriptmark.ListIterator;
import com.github.jspxnet.scriptmark.core.script.ScriptTypeConverter;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;
import com.github.jspxnet.json.*;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionFactory;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.utils.*;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import org.mozilla.javascript.NativeArray;

import java.text.DecimalFormat;
import java.util.*;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-11
 * Time: 17:45:19
 * com.github.jspxnet.jspx.test.scriptmark.util.ScriptConverter
 * extends ScriptableObject
 * extends ScriptableObject
 */
@Slf4j
public class ScriptConverter {


    final static char[] NAME_INCERTITUDE_CHARS = {
            '\\', '/', '\r', '\n', '$', '&', '\'', '(', ')', '&', '#', '!', '=', '\"', '<', '>', '.'
    };


    final static public String var_converter = "converter";
    static private final ScriptConverter INSTANCE = new ScriptConverter();
    private static final String[] imgTypes = new String[]{"jpg", "png", "gif", "bmp"};

    public static ScriptConverter getInstance() {
        return INSTANCE;
    }


    private ScriptConverter() {

    }

    public String getClassName() {
        return var_converter;
    }

    public static String trim(Object o) {
        if (o == null) {
            return StringUtil.empty;
        }
        return ((String) o).trim();
    }

    public static String replaceAll(String o, String a, String b) {
        return StringUtil.replace(o, a, b);
    }

    /**
     * 对象转换为json
     *
     * @param o 参数
     * @return 返回
     * @throws JSONException 异常
     */
    public static String toJson(Object o) throws JSONException {
        if (ClassUtil.isStandardProperty(o.getClass())) {
            return o.toString();
        }
        if (o instanceof NativeArray) {
            NativeArray jsObj = (NativeArray) o;
            Object[] array = new Object[(int) jsObj.getLength()];
            for (int i = 0; i < jsObj.getLength(); i++) {
                array[i] = jsObj.get(i, jsObj.getPrototype());
            }
            o = array;
        }
        if (o instanceof Collection || o.getClass().isArray()) {
            return new JSONArray(o).toString();
        }
        return new JSONObject(o, true).toString();
    }

    /**
     * @param o 对象
     * @return Bean对象转换为XML
     */
    public static String toXml(Object o) {
        if (o == null) {
            return "<null/>";
        }
        return XML.toString(o, o.getClass().getSimpleName());
    }


    /**
     * @param string 字符串
     * @param dub    单双引号
     * @return 字符串加引号
     */
    public static String quote(String string, boolean dub) {
        return StringUtil.quote(string, dub);
    }


    /**
     * @param o      数字,或者 字符串
     * @param format 格式
     * @return 格式化输出数字
     */
    public static String format(Object o, String format) {
        return new DecimalFormat(format).format(StringUtil.toDouble(o.toString()));
    }

    /**
     * @param o 转换为 boolean
     * @return 转换为 boolean
     */
    public static boolean toBoolean(Object o) {
        return ObjectUtil.toBoolean(o);
    }


    /**
     * @param str 字符串
     * @return XML转换为Json
     * @throws JSONException 异常
     */
    public static String xmlToJson(String str) throws JSONException {
        return JSONML.toJSONObject(str).toString();
    }

    /**
     * @param o 对象
     * @return 对象转换为Json 形式的 XML
     * @throws JSONException 异常
     */
    public static String toJsonXml(Object o) throws JSONException {
        JSONObject json = new JSONObject(o, true);
        return XML.toString(json);
    }

    /**
     * @param str 字符串
     * @return UBB 代码 转换
     */
    public static String toUbb(String str) {


        try {
            return (String) ClassUtil.callStaticMethod(ClassUtil.loadClass("com.github.jspxnet.component.jubb.UBBFilter"), "decode", str);
        } catch (ClassNotFoundException e) {
            log.error("no com.github.jspxnet.component.jubb.UBBFilter", e);
        }
        return StringUtil.empty;
    }

    /**
     * 添加引号,中间的引号转换为通配符
     *
     * @param input 字符串
     * @return 转换为javascript的字符串
     */
    public static String toScript(String input) {
        return StringUtil.toScript(input);
    }

    /*
     * <pre>{@code
     * <OPTION value="o">保密</OPTION>
     * <OPTION value="z1">白羊座(3月21--4月19日)</OPTION>
     * <OPTION value="z2">金牛座(4月20--5月20日)</OPTION>
     * <OPTION value="z3">双子座(5月21--6月21日)</OPTION>
     * <OPTION value="z4">巨蟹座(6月22--7月22日)</OPTION>
     * <OPTION value="z5">狮子座(7月23--8月22日)</OPTION>
     * <OPTION value="z6">处女座(8月23--9月22日)</OPTION>
     * <OPTION value="z7">天秤座(9月23--10月23日)</OPTION>
     * <OPTION value="z8">天蝎座(10月24--11月21日)</OPTION>
     * <OPTION value="z9">射手座(11月22--12月21日)</OPTION>
     * <OPTION value="z10">魔羯座(12月22--1月19日)</OPTION>
     * <OPTION value="z11">水瓶座(1月20--2月18日)</OPTION>
     * <OPTION value="z12">双鱼座(2月19--3月20日)</OPTION>
     * }</pre>
     */
    public static int getBirthStar(Date date) {
        return DateUtil.getBirthStar(date);
    }


    /**
     * 转换为中文金额
     *
     * @param num 数字
     * @return 中文金额
     */
    public static String toChineseCurrency(String num) {
        return NumberUtil.toChineseCurrency(new BigDecimal(num));
    }

    public static String toChineseNumber(String num) {
        return toChineseNumber(num, 0);
    }

    public static String toChineseNumber(String num, int type) {
        return NumberUtil.toChineseNumber(new BigDecimal(num), type);
    }

    public static Date getDate(String dateStr) {
        return StringUtil.getDate(dateStr);
    }

    public static String dateFormat(Date date) {
        return dateFormat(date, DateUtil.FULL_ST_FORMAT);
    }

    public static String dateFormat(Date date, String format) {
        return dateFormat(date, format, StringUtil.empty);
    }

    /**
     * 日期格式化
     *
     * @param date    日期
     * @param format 格式
     * @param def     默认
     * @return 日期格式化后的字符串
     */
    public static String dateFormat(Date date, String format, String def) {
        if (DateUtil.toString(DateUtil.empty, DateUtil.ST_FORMAT).equals(DateUtil.toString(date, DateUtil.ST_FORMAT))) {
            return def;
        }
        if ("undefined".equals(format)||StringUtil.isNull(format))
        {
            format = DateUtil.ST_FORMAT;
        }
        return DateUtil.toString(date, format);
    }

    public static String dateFormatQuote(Date date, String format) {
        return StringUtil.quote(DateUtil.toString(date, format));
    }
    /**
     * 判断是否为当天
     *
     * @param date 日期
     * @return 判断是否为当天
     */
    public static boolean isToDay(Date date) {
        return DateUtil.isToDay(date);
    }

    public static String getTimeFormatText(Date date) {
        return DateUtil.getTimeFormatText(date);
    }

    public static String getTimeFormatText(Date date, String lan) {
        return DateUtil.getTimeFormatText(date, lan);
    }

    public static String getFileType(String str) {
        return FileUtil.getTypePart(str);
    }

    public static String[] toArray(String string, String fen) {
        if (fen.equals(StringUtil.CR)) {
            string = StringUtil.convertCR(string);
        }
        return StringUtil.split(string, fen);
    }

    public static boolean isJsonArray(String str) {
        return StringUtil.isJsonArray(str);
    }

    public static boolean isJsonObject(String str) {
        return StringUtil.isJsonObject(str);
    }

    public static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    public static String getHtmlOptions(Object obj, String sel, String keyF) {

        if (obj == null) {
            return StringUtil.empty;
        }
        if (keyF == null || "undefined".equalsIgnoreCase(keyF)) {
            keyF = StringUtil.COLON;
        }
        if (StringUtil.isEmpty(sel) || "undefined".equalsIgnoreCase(sel)) {
            sel = StringUtil.SEMICOLON;
        }

        if (obj instanceof String || obj.getClass().getName().contains("NativeString")) {
            String str = obj.toString();
            if (isJsonArray(str)) {
                try {

                    AtomicReference<StringBuffer> sb = new AtomicReference<StringBuffer>(new StringBuffer());
                    JSONArray jsonArray = JSONArray.parse(str);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String line = jsonArray.getString(i);
                        if (line == null) {
                            continue;
                        }
                        int hav = line.indexOf(keyF);
                        if (hav == -1) {
                            sb.get().append("<option value=\"").append(line).append("\"");
                            if (line.equals(sel)) {
                                sb.get().append(" selected=\"selected\"");
                            }
                            sb.get().append(">").append(line).append("</option>");
                        } else {
                            String keys = line.substring(0, hav);
                            String vars = line.substring(hav + 1);

                            sb.get().append("<option value=\"").append(keys).append("\"");
                            if (keys.equals(sel)) {
                                sb.get().append(" selected=\"selected\"");
                            }
                            sb.get().append(">").append(vars).append("</option>");
                        }
                    }
                    return sb.get().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (isJsonObject(str)) {
                try {

                    StringBuilder sb = new StringBuilder();
                    JSONObject jsonObject = new JSONObject(str);
                    for (String key : jsonObject.keySet()) {
                        String vars = jsonObject.getString(key);
                        if (vars == null) {
                            continue;
                        }
                        sb.append("<option value=\"").append(key).append("\"");
                        if (key.equals(sel)) {
                            sb.append(" selected=\"selected\"");
                        }
                        sb.append(">").append(vars).append("</option>");
                    }
                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            StringMap<String, String> stringMap = new StringMap();
            stringMap.setString(str);
            StringBuilder sb = new StringBuilder();
            for (String key : stringMap.keySet()) {
                String vars = stringMap.get(key);
                sb.append("<option value=\"").append(key).append("\"");
                if (key.equals(sel)) {
                    sb.append(" selected=\"selected\"");
                }
                sb.append(">").append(vars).append("</option>");
            }
            return sb.toString();
        }
        if (obj instanceof String[]) {
            String[] strings = (String[]) obj;
            StringBuilder sb = new StringBuilder();
            for (String line : strings) {
                sb.append("<option value=\"").append(line).append("\"");
                if (line.equals(sel)) {
                    sb.append(" selected=\"selected\"");
                }
                sb.append(">").append(line).append("</option>");
            }
            return sb.toString();
        }
        if (obj instanceof Map) {
            Map stringMap = (Map) obj;
            StringBuilder sb = new StringBuilder();
            for (Object key : stringMap.keySet()) {
                String vars = (String) stringMap.get(key);
                sb.append("<option value=\"").append(key).append("\"");
                if (key.equals(sel)) {
                    sb.append(" selected=\"selected\"");
                }
                sb.append(">").append(vars).append("</option>");
            }
            return sb.toString();
        }
        if (obj instanceof Collection) {
            Collection cols = (Collection) obj;
            StringBuilder sb = new StringBuilder();
            Iterator iterator = cols.iterator();
            while (iterator.hasNext()) {
                String line = (String) iterator.next();
                sb.append("<option value=\"").append(line).append("\"");
                if (line.equals(sel)) {
                    sb.append(" selected=\"selected\"");
                }
                sb.append(">").append(line).append("</option>");
            }
            return sb.toString();
        }
        return "unknow type:" + obj.getClass();
    }


    public static String show(Object obj, Object selObj, Object key) {
        if (key == null || "undefined".equalsIgnoreCase(key.toString())) {
            key = StringUtil.COLON;
        }
        String keyF = key.toString();

        if (selObj == null || "undefined".equalsIgnoreCase(selObj.toString())) {
            selObj = StringUtil.SEMICOLON;
        }
        String sel = selObj.toString();
        if (obj instanceof String || obj.getClass().getName().contains("NativeString")) {
            String str = obj.toString();
            if (isJsonArray(str)) {
                try {
                    JSONArray jsonArray = JSONArray.parse(str);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String line = jsonArray.getString(i);
                        if (line == null) {
                            continue;
                        }
                        int hav = line.indexOf(keyF);
                        if (hav == -1) {
                            if (line.equals(sel)) {
                                return line;
                            }
                        } else {
                            String keys = line.substring(0, hav);
                            String vars = line.substring(hav + 1);
                            if (keys.equals(sel)) {
                                return vars;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return StringUtil.empty;
            }
            StringMap<String, String> stringMap = new StringMap<>();
            stringMap.setString(str);
            for (String k : stringMap.keySet()) {
                if (k.equals(sel)) {
                    return stringMap.getString(k);
                }
            }
            return StringUtil.empty;
        }
        if (obj instanceof String[]) {
            String[] strings = (String[]) obj;
            for (String line : strings) {
                if (line.equals(sel)) {
                    return line;
                }
            }
            return StringUtil.empty;
        }
        if (obj instanceof Map) {
            Map stringMap = (Map) obj;
            for (Object keyx : stringMap.keySet()) {
                if (keyx.equals(sel)) {
                    return (String) stringMap.get(keyx);
                }
            }
            return StringUtil.empty;
        }
        if (obj instanceof Collection) {
            Collection cols = (Collection) obj;
            Iterator iterator = cols.iterator();
            while (iterator.hasNext()) {
                String line = (String) iterator.next();
                if (line.equals(sel)) {
                    return line;
                }
            }
            return StringUtil.empty;
        }
        return "unknow type:" + obj.getClass();
    }


    /**
     * @param src 字符串
     * @return md5 加密字符串
     */
    public static String md5(String src) {
        return EncryptUtil.getMd5(src);
    }

    /**
     * @param src 字符串
     * @return sha 加密字符串
     */
    public static String sha(String src) {
        if (src == null) {
            return StringUtil.empty;
        }
        return EncryptUtil.getSha(src);
    }

    public static String getPinYin(String src) {
        return getPinYin(src, "");
    }

    public static String getPinYin(String src, String fen) {
        if (src == null) {
            return StringUtil.empty;
        }
        return ChineseUtil.getFullSpell(src, fen);
    }

    /**
     * 删除html 并且限制长度,不够的时候 并且补充
     *
     * @param str  处理的字符串
     * @param len  长度
     * @param send 补充
     * @return 删除后的结果
     */
    public static String deleteHtml(String str, int len, String send) {
        return HtmlUtil.deleteHtml(str, len, send);
    }

    /**
     * @param str   字符串
     * @param begin 开始
     * @param end   结束
     * @return 中文按照两个的长处理
     */
    static public String substring(String str, int begin, int end) {
        return StringUtil.csubstring(str, begin, end);
    }

    /**
     * @param str   字符串
     * @param begin 开始
     * @param end   结束
     * @return 切取字符串
     */
    static public String substringBetween(String str, String begin, String end) {
        return StringUtil.substringBetween(str, begin, end);
    }

    /**
     * @param str 字符串
     * @return 按照C方式得到长度
     */
    static public int getCLength(String str) {
        return StringUtil.getLength(str);
    }


    /**
     * @param str   字符串
     * @param world 单词
     * @return 高亮显示
     */
    static public String getHighlight(String str, String world) {
        String[] worlds = StringUtil.split(world, " ");
        for (String w : worlds) {
            str = StringUtil.replaceIgnoreCase(str, w, "<span class=\"matching\">" + w + "</span>");
        }
        return str;
    }

    static public String getHttp(String http, String encode) {
        if (StringUtil.isNull(http)) {
            return http;
        }
        try {
            return IoUtil.autoReadText(http,encode);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }


    /**
     * @param str      格式说明:[标题]数据一行一个;项目名称=项目数据
     * @param showName 名称
     * @return 文本格式转换到FusionChart 的XML 格式
     */
    static public String getFusionChartXML(String str, int showName) {
        return HtmlUtil.getFusionChartXML(str, showName);
    }

    /**
     * @param str 字符串
     * @return \r\n  转换为 br 显示
     */
    static public String toBrLine(Object str) {
        return StringUtil.toBrLine((String) str);
    }

    /**
     * @param str 字符串
     * @return 转换为xml 标记符号
     */
    static public String escape(String str) {
        return XMLUtil.escape(str);
    }

    /**
     * @param txt    字符串
     * @param length 长度
     * @param send   结尾
     * @return 切断
     */
    static public String cut(String txt, int length, String send) {
        return StringUtil.cut(txt, length, send);
    }

    /**
     * @param txt 文本
     * @param fen 分割
     * @return 切断显示分割最后部分，如果没有分割就显示所有，用于caption
     */

    static public String cutBefore(String txt, String fen) {
        return StringUtil.cutBefore(txt, fen);
    }


    /**
     * @param txt 字符串
     * @return 加密
     * @throws Exception 异常
     */
    static public String getEncode(String txt) throws Exception {
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        return encrypt.getEncode(txt);
    }

    /**
     * @param txt 字符串
     * @return 解密
     * @throws Exception 异常
     */
    static public String getDecode(String txt) throws Exception {
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        return encrypt.getDecode(txt);
    }

    /**
     * @param file 文件名
     * @return 判断问卷类型是否为图片
     */
    public static boolean isImage(String file) {
        return ArrayUtil.inArray(imgTypes, FileUtil.getTypePart(file), true);
    }

    /**
     * @param text 字符串
     * @return 解析附件
     */
    public static Map<String, String> toAttachMap(String text) {
        return StringUtil.toAttachMap(text);
    }

    /**
     * json 格式的数组转换为java 的数组
     *
     * @param s json格式的数组
     * @return 返回java的数组
     * @throws Exception 异常
     */
    public static String[] jsonToArray(String s) throws Exception {
        if (s != null && s.contains("[") && s.contains("]")) {
            TemplateScriptEngine scriptEngine = new TemplateScriptEngine();
            try {
                Object oo = scriptEngine.eval(s, 0);
                ListIterator iterator = ScriptTypeConverter.getCollection(oo);
                String[] result = new String[iterator.getLength()];
                while (iterator.hasNext()) {
                    result[iterator.getIndex()] = (String) iterator.next();
                }
                return result;
            } finally {
                scriptEngine.exit();
            }
        }
        return null;
    }

    /**
     * @param html html字符串
     * @return 修复补全XML或HTML标签, 后期考虑改成 HtmlCleaner,目前HtmlCleaner在javascript部分有bug
     * @throws Exception 异常
     */
    public static String getSafeHtmlFilter(String html) throws Exception {
        return HtmlUtil.getSafeFilter(html);
    }

    /**
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 比较的结果为，年，月，日 ,主要为满足机场需要
     */
    public static int[] getCompareDate(Date startDate, Date endDate) {
        return DateUtil.getCompareDate(startDate, endDate);
    }

    /**
     * @param startDate 开始日期，和当前日期比较
     * @return 比较日期
     */
    public static int[] getCompareDate(Date startDate) {
        return DateUtil.getCompareDate(startDate, new Date());
    }

    /**
     * @param startDate 得到开始日期
     * @return 比较日期
     */
    public static long getTimeInMillis(Date startDate) {
        return DateUtil.getTimeInMillis(startDate, new Date());
    }

    /**
     * @param v      值
     * @param length 长度
     * @return 保留字符串长度
     */
    public static String getKeepLength(String v, int length) {
        return NumberUtil.getKeepLength(v, length);
    }

    /**
     * @param v      值
     * @param length 长度
     * @return 保留字符串长度
     */
    public static String getKeepLength(int v, int length) {
        return NumberUtil.getKeepLength(v, length);
    }

    /**
     * @param v      值
     * @param length 长度
     * @return 保留字符串长度
     */
    public static String getKeepLength(long v, int length) {
        return NumberUtil.getKeepLength(v, length);
    }

    public static Date toDate(Object date) {
        if (date instanceof Long)
        {
           return new Date(((Long)date));
        }
        if (date instanceof String)
        {
            return StringUtil.getDate((String)date);
        }
        return StringUtil.getDate(date.toString());
    }


    /**
     * @param arguments 参赛
     * @return 创建一个新的bean对象
     * @throws Exception 异常
     */
    public static Object create(Object[] arguments) throws Exception {
        String src = arguments[0].toString();
        Object object = ClassUtil.newInstance(src);
        for (int i = 1; i < arguments.length; i++) {
            String termData = arguments[i].toString();
            if (termData != null && termData.contains(StringUtil.EQUAL)) {
                String mName = StringUtil.substringBefore(termData, StringUtil.EQUAL);
                String vName = termData.substring(mName.length() + 1);
                BeanUtil.setSimpleProperty(object, "set" + StringUtil.capitalize(mName), vName);
            }
        }
        return object;
    }

    /**
     * @param arguments 参数
     * @return 得到ioc对象
     */
    public static Object getIoc(Object[] arguments) {
        String className = arguments[0].toString();
        String txWeb_namespace = null;
        if (className.contains(TXWebUtil.AT)) {
            String tmpClassName = className;
            className = className.substring(0, tmpClassName.indexOf(TXWebUtil.AT));
            txWeb_namespace = StringUtil.substringAfter(tmpClassName, TXWebUtil.AT);
        }
        if (!StringUtil.hasLength(txWeb_namespace)) {
            txWeb_namespace = TXWeb.global;
        }
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        Object object = beanFactory.getBean(className, txWeb_namespace);

        for (int i = 1; i < arguments.length; i++) {
            String termData = arguments[i].toString();
            if (termData != null && termData.contains(StringUtil.EQUAL)) {
                String mName = StringUtil.substringBefore(termData, StringUtil.EQUAL);
                String vName = termData.substring(mName.length() + 1);
                BeanUtil.setSimpleProperty(object, "set" + StringUtil.capitalize(mName), vName);
            }
        }
        return object;
    }

    /**
     * 得到action,并装置参数
     *
     * @param arg action className 类名称 arguments 参数
     * @return 得到action action
     * @throws Exception 异常 一次
     */
    public static Object getActon(Object[] arg) throws Exception {
        if (arg.length < 2) {
            return null;
        }
        Action action = (Action) arg[0];
        String className = arg[1].toString();
        List<Object> list = new ArrayList<>();
        if (arg.length > 2) {
            list.addAll(Arrays.asList(arg).subList(2, arg.length));
        }
        return ActionFactory.getActon(action, className, list);
    }

    /**
     * @param obj 对象
     * @return 得到字段选项, 用于查询
     */
    public static String getFields(Object obj) {
        if (obj instanceof Class) {
            return getFields((Class) obj);
        } else if (obj instanceof String) {
            try {
                return getFields(ClassUtil.loadClass((String) obj));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return getFields(obj.getClass());
    }

    /**
     * @param cla 类
     * @return 得到字段选项, 用于查询
     */
    public static String getFields(Class cla) {
        List<SoberColumn> soberColumns = AnnotationUtil.getColumnList(cla);
        StringBuilder sb = new StringBuilder();
        for (SoberColumn column : soberColumns) {
            if (column.isHidden() || column.getLength() > 250) {
                continue;
            }
            sb.append(column.getName()).append(":").append(column.getCaption()).append(StringUtil.SEMICOLON);
        }
        if (sb.toString().endsWith(StringUtil.SEMICOLON)) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @param obj   对象
     * @param field 字段
     * @return 选项
     */
    public static String getOptions(Object obj, String field) {
        if (obj instanceof Class) {
            return getOptions((Class) obj, field);
        } else {
            return getOptions(obj.getClass(), field);
        }
    }


    public static String getThumbnailFileName(String name) {
        return FileUtil.getThumbnailFileName(name);
    }

    public static String getMobileFileName(String name) {
        return FileUtil.getMobileFileName(name);
    }

    /**
     * @param cla   类
     * @param field 字段
     * @return 选项
     */
    public static String getOptions(Class cla, String field) {
        List<SoberColumn> soberColumns = AnnotationUtil.getColumnList(cla);
        for (SoberColumn column : soberColumns) {
            if (column.getName().equalsIgnoreCase(field)) {
                return column.getOption();
            }
        }
        return StringUtil.empty;
    }


    /**
     * @param cla   类
     * @param field 字段
     * @param clear 清空括号
     * @return 选项
     */
    public static String getOptions(Class cla, String field, boolean clear) {
        String option = getOptions(cla, field);
        if (option.startsWith("(") && option.endsWith(")") && clear) {
            return StringUtil.substringBetween(option, "(", ")");
        }
        return StringUtil.empty;
    }

    /**
     * 特殊字符转换
     *
     * @param in 字符
     * @return 过滤后的字符串
     */
    public static String escapeEncoderHTML(String in) {
        return HtmlUtil.escapeEncoderHTML(in);
    }

    /**
     * {@code  &amp;  }解码
     *
     * @param in 进入
     * @return 解码
     */
    public static String escapeDecodeHtml(String in) {
        return HtmlUtil.escapeDecodeHtml(in);
    }


    /**
     * @param txt 中文
     * @return 创建一个拼音的ID号，不能保证不重复
     */
    public static String makePinYinName(String txt) {
        String text = StringUtil.getPolicyName(StringUtil.fullToHalf(txt), 100, NAME_INCERTITUDE_CHARS);
        text = StringUtil.replace(text, " ", "");
        return getPinYin(text);
    }


    /**
     * @param str md 格式
     * @return 转换到md格式
     */
    public static String getMarkdownHtml(String str) {
        return ScriptMarkUtil.getMarkdownHtml(str);
    }
}
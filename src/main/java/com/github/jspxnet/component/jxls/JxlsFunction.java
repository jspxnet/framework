package com.github.jspxnet.component.jxls;


import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class JxlsFunction {

    private static final String[] IMG_TYPE = new String[]{"jpg", "png", "gif", "bmp"};

    /**
     * @param date 日期
     * @param fmt  格式
     * @return 日期格式化
     */
    public String dateFormat(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        return DateUtil.toString(date, fmt);
    }

    /**
     *
     * @param f 数字
     * @return 格式化输入数字,js 默认会在单精度后多个 .0
     */
    public String getNumberStdFormat(Number f) {
        return NumberUtil.getNumberStdFormat(f);
    }

    /**
     *
     * @param f 数字
     * @return 格式化输入数字,js 默认会在单精度后多个 .0
     */
    public String getNumberStdFormat(String f) {
        return NumberUtil.getNumberStdFormat(f);
    }

    /**
     * @param o      数字,或者 字符串
     * @param format 格式
     * @return 格式化输出数字
     */
    public String format(Object o, String format) {
        return NumberUtil.format(o, format);
    }

    /**
     * @param o 转换为 boolean
     * @return 转换为 boolean
     */
    public boolean toBoolean(Object o) {
        return ObjectUtil.toBoolean(o);
    }

    /**
     * @param date 日期
     * @return 判断是否为当天
     */
    public boolean isToDay(Date date) {
        return DateUtil.isToDay(date);
    }

    /**
     * @param src 字符串
     * @return 得到拼音
     */
    public String getPinYin(String src) {
        return getPinYin(src, "");
    }

    /**
     * @param src 字符串
     * @param fen 分割
     * @return 得到拼音
     */
    public String getPinYin(String src, String fen) {
        return ChineseUtil.getFullSpell(src,fen);
    }

    /**
     * @param b  对象
     * @param o1 true返回
     * @param o2 false 返回
     * @return if判断
     */
    public Object ifElse(boolean b, Object o1, Object o2) {
        return b ? o1 : o2;
    }


    /**
     *
     * @param b 判断对象
     * @param o1  true返回
     * @param o2 false 返回
     * @return 返回数据
     */
    public Object ifNull(Object b, Object o1, Object o2) {
        return ObjectUtil.isEmpty(b) ? o1 : o2;
    }
    /**
     *  单元格合并,注意这里是倒起来合并的,
     *  是用方法:${jspx:mergeCell(d.acs98,d.mergerRows)}
     *  mergerRows 是 JxlsUtil.getMergeValue(list,"acs98","mergerRows") 创建出来的字段
     * @param value 值
     * @param mergerRows  当前行向上合并几个单元
     * @return 单元格合并
     */
    public MergeCell mergeCell(Object value, Integer mergerRows) {
        return new MergeCell(value, mergerRows);
    }

    public String trim(Object o) {
        if (o == null) {
            return StringUtil.empty;
        }
        return ((String) o).trim();
    }

    /**
     * 替换
     * @param o 原对象
     * @param a 替换
     * @param b 替换为
     * @return 返回替换后的
     */
    public String replace(String o, String a, String b) {
        return StringUtil.replace(o, a, b);
    }


    /**
     * @param string 字符串
     * @param dub    单，双引号
     * @return 字符串加引号
     */
    public String quote(String string, boolean dub) {
        return StringUtil.quote(string, dub);
    }

    /**
     *
     * @param num 数字
     * @return 中文数字
     */
    public String getChineseNumber(String num) {
        return getChineseNumber(num, 0);
    }

    /**
     *
     * @param num 数字
     * @param type  数值型，中文型
     * @return 中文数字
     */
    public String getChineseNumber(String num, int type) {
        return NumberUtil.toChineseNumber(new BigDecimal(num), type);
    }


    /**
     * @param src 字符串
     * @return md5 加密字符串
     */
    public String md5(String src) {
        return EncryptUtil.getMd5(src);
    }


    /**
     * 删除html 并且限制长度,不够的时候 并且补充
     *
     * @param str  处理的字符串
     * @param len  长度
     * @param send 补充
     * @return 删除后的结果
     */
    public String deleteHtml(String str, int len, String send) {
        return HtmlUtil.deleteHtml(str, len, send);
    }

    /**
     * @param str   字符串
     * @param begin 开始
     * @param end   结束
     * @return 中文按照两个的长处理
     */
    public String substring(String str, int begin, int end) {
        return StringUtil.csubstring(str, begin, end);
    }

    /**
     * @param str   字符串
     * @param begin 开始
     * @param end   结束
     * @return 切取字符串
     */
    public String substringBetween(String str, String begin, String end) {
        return StringUtil.substringBetween(str, begin, end);
    }

    /**
     * @param str 字符串
     * @return 按照C方式得到长度
     */
    public int getCLength(String str) {
        return StringUtil.getLength(str);
    }

    /**
     * @param file 文件名
     * @return 判断问卷类型是否为图片
     */
    public boolean isImage(String file) {
        return ArrayUtil.inArray(IMG_TYPE, FileUtil.getTypePart(file), true);
    }

    /**
     * @param text 字符串
     * @return 解析附件
     */
    public Map<String, String> toAttachMap(String text) {
        return StringUtil.toAttachMap(text);
    }



}

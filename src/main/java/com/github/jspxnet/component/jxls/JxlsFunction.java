package com.github.jspxnet.component.jxls;


import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        if (src == null) {
            return StringUtil.empty;
        }
        try {
            return (String) ClassUtil.callStaticMethod(ClassUtil.loadClass("com.github.jspxnet.component.zhex.spell.ChineseUtil"), "fullSpell", src, fen);
        } catch (ClassNotFoundException e) {
            log.error("no fount jspx-zhex-x.jar", e);
        }
        return StringUtil.empty;
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
     *  单元格合并,注意这里是倒起来合并的,
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

    public String getChineseNumber(String num) {
        return getChineseNumber(num, 0);
    }

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

    /**
     * 合并算法
     *
     * @param objectList 对象列表
     * @param field      字段
     * @return 合并算法
     */
    public List<CellProxy> getMergeValue(List<CellProxy> objectList, String field) {
        if (objectList == null || objectList.isEmpty()) {
            return new ArrayList<>();
        }
        int[] rowValue = new int[objectList.size()];
        int j = 0;
        rowValue[0] = 0;
        String name = (String) BeanUtil.getFieldValue(objectList.get(0), field);
        for (int i = 1; i < objectList.size(); i++) {
            if (name != null && name.equals(BeanUtil.getFieldValue(objectList.get(i), field))) {
                j++;
                rowValue[i] = 0;
            } else {
                rowValue[i - 1] = j;
                j = 0;
            }
            name = (String) BeanUtil.getFieldValue(objectList.get(i), field);
        }
        for (int i = 0; i < objectList.size(); i++) {
            objectList.get(i).setMergerRows(rowValue[i]);
        }
        return objectList;
    }

    /**
     * @param objectList 数据对象
     * @param field      合并对象
     * @return 自动计算合并单元
     */
    public List<CellProxy> getMerge(List<Object> objectList, String field) {
        List<CellProxy> list = new ArrayList<>();
        for (Object obj : objectList) {
            CellProxy cellProxy = (CellProxy) Proxy.newProxyInstance(CellProxy.class.getClassLoader(),
                    new Class[]{CellProxy.class}, new CellHandler(obj));
            list.add(cellProxy);
        }
        return getMergeValue(list, field);
    }


}

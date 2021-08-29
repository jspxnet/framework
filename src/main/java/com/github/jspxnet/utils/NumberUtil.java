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


import java.math.BigDecimal;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
 * 确的浮点数运算，包括加减乘除和四舍五入。
 *
 * @author chenyuan
 */
@Slf4j
final public class NumberUtil {
    //默认除法运算精度
    private static final int DEFAULT_DIV_SCALE = 2;

    //这个类不能实例化
    private NumberUtil() {

    }

    static public final String LING_STRING = "000000000000000000000000000000000000";

    //中文金额单位数组
    final private static String[] STR_CHINESE_UNIT = new String[]{"分", "角", "圆", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆"};
    //中文数字字符数组
    final private static String[] CHINESE_NUMBER = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "点"};
    //单位数组
    final private static String[] NUMBER_UNITS = new String[]{"", "", "点", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千", "兆"};
    // 中文大写数字数组
    final private static String[] NUMERIC_CHINESE = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "点"};


    /**
     * 得到英文的第几个表示,
     * 1-10：第1 first 1st 第2 second 2nd 第3 third 3rd 第4 fourth 4th 第5 fifth 5th 第6 sixth 6th 第7 seventh 7th 第8 eighth 8th 第9 ninth 9th 第10 tenth 10th
     *
     * @param num 数字
     * @return 返回
     */
    public static String getEnglishTh(int num) {
        if (num == 1) {
            return "1st";
        }
        if (num == 2) {
            return "2nd";
        }
        if (num == 3) {
            return "3rd";
        }
        return num + "th";
    }

    /**
     * Description 将数字金额转换为中文金额
     * BigDecimal bigdMoneyNumber 转换前的数字金额
     * 调用：myToChineseCurrency("101.89")="壹佰零壹圆捌角玖分"
     * myToChineseCurrency("100.89")="壹佰零捌角玖分"
     * myToChineseCurrency("100")="壹佰圆整"
     *
     * @param bigMoneyNumber 数字
     * @return String 数字金额转换为中文金额
     */
    public static String toChineseCurrency(BigDecimal bigMoneyNumber) {
        String strChineseCurrency = StringUtil.empty;
        //零数位标记
        boolean bZero = true;
        //中文金额单位下标
        int ChineseUnitIndex = 0;
        try {
            if (bigMoneyNumber.intValue() == 0) {
                return "零圆整";
            }
            //处理小数部分，四舍五入
            double doubMoneyNumber = Math.round(bigMoneyNumber.doubleValue() * 100);
            //是否负数
            boolean bNegative = doubMoneyNumber < 0;
            //取绝对值
            doubMoneyNumber = Math.abs(doubMoneyNumber);
            //循环处理转换操作
            while (doubMoneyNumber > 0) {
                //整的处理(无小数位)
                if (ChineseUnitIndex == 2 && strChineseCurrency.length() == 0) {
                    strChineseCurrency = strChineseCurrency + "整";
                }
                //非零数位的处理
                if (doubMoneyNumber % 10 > 0) {
                    strChineseCurrency = CHINESE_NUMBER[(int) doubMoneyNumber % 10] + STR_CHINESE_UNIT[ChineseUnitIndex] + strChineseCurrency;
                    bZero = false;
                }
                //零数位的处理
                else {
                    //元的处理(个位)
                    if (ChineseUnitIndex == 2) {
                        //段中有数字
                        if (doubMoneyNumber > 0) {
                            strChineseCurrency = STR_CHINESE_UNIT[ChineseUnitIndex] + strChineseCurrency;
                            bZero = true;
                        }
                    }
                    //万、亿数位的处理
                    else if (ChineseUnitIndex == 6 || ChineseUnitIndex == 10) {
                        //段中有数字
                        if (doubMoneyNumber % 1000 > 0) {
                            strChineseCurrency = STR_CHINESE_UNIT[ChineseUnitIndex] + strChineseCurrency;
                        }
                    }

                    //前一数位非零的处理
                    if (!bZero) {
                        strChineseCurrency = CHINESE_NUMBER[0] + strChineseCurrency;
                    }
                    bZero = true;
                }
                doubMoneyNumber = Math.floor(doubMoneyNumber / 10);
                ChineseUnitIndex++;
            }
            //负数的处理
            if (bNegative) {
                strChineseCurrency = "负" + strChineseCurrency;
            }
        } catch (Exception e) {
            log.error(bigMoneyNumber.toString(), e);
            return StringUtil.empty;
        }
        return strChineseCurrency;
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);

    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal div(int v1, int v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal div(double v1, double v2, int scale) {
        if (v2 == 0) {
            return new BigDecimal("0");
        }
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);

    }

    public static float getRound(float v, int scale) {
        return getRound((double) v, scale).floatValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static BigDecimal getRound(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @param bigdMoneyNumber 数值
     * @param type            数值型，中文型
     * @return 转换为中文的数字
     */
    public static String toChineseNumber(BigDecimal bigdMoneyNumber, int type) {
        String[] numeric;
        if (type == 0) {
            numeric = NUMERIC_CHINESE;
        } else {
            numeric = CHINESE_NUMBER;
        }
        String strChineseCurrency = StringUtil.empty;
        //零数位标记
        boolean bZero = true;
        //中文金额单位下标
        int ChineseUnitIndex = 0;
        try {
            if (bigdMoneyNumber.intValue() == 0) {
                return "零";
            }
            //处理小数部分，四舍五入
            double doubMoneyNumber = Math.round(bigdMoneyNumber.doubleValue() * 100);
            //是否负数
            boolean bNegative = doubMoneyNumber < 0;
            //取绝对值
            doubMoneyNumber = Math.abs(doubMoneyNumber);
            //循环处理转换操作
            while (doubMoneyNumber > 0) {
                //整的处理(无小数位)
                if (ChineseUnitIndex == 2 && strChineseCurrency.length() == 0) {
                    strChineseCurrency = strChineseCurrency + StringUtil.empty;
                }
                //非零数位的处理
                if (doubMoneyNumber % 10 > 0) {
                    strChineseCurrency = numeric[(int) doubMoneyNumber % 10] + NUMBER_UNITS[ChineseUnitIndex] + strChineseCurrency;
                    bZero = false;
                }
                //零数位的处理
                else {
                    //元的处理(个位)
                    if (ChineseUnitIndex == 2) {
                        //段中有数字
                        if (doubMoneyNumber > 0) {
                            strChineseCurrency = NUMBER_UNITS[ChineseUnitIndex] + strChineseCurrency;
                            bZero = true;
                        }
                    }
                    //万、亿数位的处理
                    else if (ChineseUnitIndex == 6 || ChineseUnitIndex == 10) {
                        //段中有数字
                        if (doubMoneyNumber % 1000 > 0) {
                            strChineseCurrency = NUMBER_UNITS[ChineseUnitIndex] + strChineseCurrency;
                        }
                    }

                    //前一数位非零的处理
                    if (!bZero) {
                        strChineseCurrency = numeric[0] + strChineseCurrency;
                    }
                    bZero = true;
                }
                doubMoneyNumber = Math.floor(doubMoneyNumber / 10);
                ChineseUnitIndex++;
            }
            //负数的处理
            if (bNegative) {
                strChineseCurrency = "负" + strChineseCurrency;
            }
        } catch (Exception e) {
            log.error(bigdMoneyNumber.toString(), e);
            return StringUtil.empty;
        }
        if (strChineseCurrency.endsWith(NUMBER_UNITS[2])) {
            strChineseCurrency = strChineseCurrency.substring(0, strChineseCurrency.length() - 1);
        }
        return strChineseCurrency;
    }


    /**
     * 提供把数串转为字符
     *
     * @param value 转换的字符
     * @return 结果
     */
    public static String toString(int value) {
        try {
            return Integer.toString(value);
        } catch (NumberFormatException e) {
            return "0";
        }
    }


    public static String toString(float value) {
        try {
            return Float.toString(value);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    public static String toString(long value) {
        try {
            return Long.toString(value);
        } catch (NumberFormatException e) {
            return "0";
        }
    }


    /**
     * 提供把数串转为字符
     *
     * @param value 转换的字符
     * @return 结果
     */
    public static String toString(double value) {
        try {
            return Double.toString(value);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    /**
     * 数字对象转字符串
     * @param value 值
     * @return 字符串
     */
    public static String toString(Object value) {
        try {
            if (value instanceof  Double)
            {
                return toString((double)value);
            }
            if (value instanceof  Float)
            {
                return toString((float)value);
            }
            if (value instanceof  Integer)
            {
                return toString((int)value);
            }
            if (value instanceof  Long)
            {
                return toString((long)value);
            }
            if (value instanceof  Short)
            {
                return toString((short)value);
            }

        } catch (NumberFormatException e) {
            log.error("toString value:{},error:{}",value,e.getMessage());
        }
        return "0";
    }
    /**
     * @param value 数字
     * @return boolean 判断一个数是否为偶数
     */
    public static boolean isEven(int value) {
        return value % 2 == 0;
    }

    /**
     * 格式化输出数字
     *
     * @param d      数字
     * @param format 格式   eg: "####.00"
     * @return String
     */
    public static String format(Object d, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(d);
    }

    /**
     * 判断值的域是否在范围内
     *
     * @param number 要判断的数字
     * @param min    最小数
     * @param max    最大数
     * @return true 是 false 否
     */
    public static boolean isBetween(double number, double min, double max) {
        return min <= number && number <= max;
    }

    public static boolean isBetween(long number, long min, long max) {
        return min <= number && number <= max;
    }

    public static boolean isBetween(int number, int min, int max) {
        return min <= number && number <= max;
    }

    /**
     * @param array 数组
     * @return 得到数组中最小的
     */
    public static int getMin(int[] array) {
        int iMin = array[0];
        for (int x : array) {
            if (iMin > x) {
                iMin = x;
            }

        }
        return iMin;
    }

    /**
     * @param array 数组
     * @return 得到数组中最小的
     */
    public static long getMin(long[] array) {
        long iMin = array[0];
        for (long x : array) {
            if (iMin > x) {
                iMin = x;
            }
        }
        return iMin;
    }

    public static float getMax(float[] array) {
        float imx = array[0];
        for (float x : array) {
            if (imx < x) {
                imx = x;
            }

        }
        return imx;
    }

    /**
     * @param array 数组
     * @return 得到数组中最大的
     */
    public static int getMax(int[] array) {
        int imx = array[0];
        for (int x : array) {
            if (imx < x) {
                imx = x;
            }

        }
        return imx;
    }

    /**
     * @param array 数组
     * @return 得到数组中最大的
     */
    public static long getMax(long[] array) {
        long imx = array[0];
        for (long x : array) {
            if (imx < x) {
                imx = x;
            }

        }
        return imx;
    }


    public static String getKeepLength(long value, int keyLength) {
        return getKeepLength((Long.valueOf(value)).toString(), keyLength);
    }

    public static String getKeepLength(int value, int keyLength) {
        return getKeepLength((Integer.valueOf(value)).toString(), keyLength);
    }

    public static String getKeepLength(String value, int keyLength) {
        StringBuilder temp = new StringBuilder();
        temp.append(NumberUtil.LING_STRING).append(value);
        return temp.substring(temp.length() - keyLength, temp.length());
    }

    /**
     * Converts a number transfer gigabytes/megabytes/kilobytes/bytes
     *
     * @param aBytes 格式化转换流量显示
     * @return nice representation of bytes
     */
    public static String fromBytes(long aBytes) {
        long gigabytes = aBytes / (1024 * 1024 * 1024);
        aBytes = aBytes % (1024 * 1024 * 1024);

        long megabytes = aBytes / (1024 * 1024);
        aBytes = aBytes % (1024 * 1024);

        long kilobytes = aBytes / (1024);
        aBytes = aBytes % (1024);

        if (gigabytes > 0) {
            return String.format("%dgb %dmb %dkb %db", gigabytes, megabytes, kilobytes, aBytes);
        } else if (megabytes > 0) {
            return String.format("%dmb %dkb %db", megabytes, kilobytes, aBytes);
        } else if (kilobytes > 0) {
            return String.format("%dkb %db", kilobytes, aBytes);
        } else {
            return String.format("%db", aBytes);
        }
    }


    /**
     * converts the given byte size in a textual representation
     *
     * @param bytes the bytes transfer convert
     * @return the formated String representation of the bytes
     */
    public static String toFormatBytesSize(long bytes) {
        if (bytes > (5 * 1000 * 1000)) {
            return (bytes / 1000000) + " MB";

        } else if (bytes > (10 * 1000)) {
            return (bytes / 1000) + " KB";

        } else {
            return bytes + " bytes";
        }
    }


    /**
     * 例如:36进制的互换
     * String str32 = NumberUtil.getRadix(Integer.toString(i),10,36);
     * NumberUtil.getRadix(str32,36,10));
     *
     * @param num  数字字符串
     * @param form 从 某进制转换到
     * @param to   to结果进制表示
     * @return 任意进制转换
     */
    public static String getRadix(String num, int form, int to) {
        return new java.math.BigInteger(num, form).toString(to);
    }


    /**
     * 去掉结尾无用的0  如 2.00  返回2
     * 简化 标准 数字格式
     * @param number 数字
     * @return 标准格式
     */
    public static String getNumberStdFormat(Number number)
    {
        if (number==null|| Double.compare(number.doubleValue(),0)==0)
        {
            return "0";
        }
        String s = number.toString();
        if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(StringUtil.DOT)) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    public static String getNumberStdFormat(String number)
    {
        if (number==null|| Double.compare(StringUtil.toDouble(number),0)==0)
        {
            return "0";
        }
        String s = number;
        if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(StringUtil.DOT)) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }

    /**
     * 得到数字是几位小数
     * @param bigDecimal 数字
     * @return 得到数字是几位小数
     */
    public static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        if (bigDecimal==null)
        {
            return 0;
        }
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(StringUtil.DOT);
        return index < 0 ? 0 : string.length() - index - 1;
    }


}
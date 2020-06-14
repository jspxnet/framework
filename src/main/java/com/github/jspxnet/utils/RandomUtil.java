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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-1
 * Time: 19:20:55
 */
public class RandomUtil {
    private static final Random RANDOM = new Random();

    private RandomUtil() {

    }

    public static boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

    /**
     * @return 得到一个随机整数，最大限度的不重复,用在随机id上
     */
    public static int getRandomInt() {
        StringBuilder temp = new StringBuilder();
        if (RANDOM.nextBoolean()) {
            temp.append(System.currentTimeMillis()).append(getRandom(5, false, true));
        } else {
            temp.append(System.currentTimeMillis()).append(getRandom(4, false, true));
        }
        return StringUtil.toInt(temp.substring(temp.length() - 9, temp.length()));
    }

    /**
     * @return 返回默认14位的一个随机数字
     */
    public static long getRandomLong() {
        return getRandomLong(14);
    }

    /**
     * @param count 长度
     * @return 得到一个指定长度的随机整数，最大限度的不重复
     */
    public static long getRandomLong(int count) {
        if (count < 0 || count > 16) {
            count = 15;
        }
        StringBuilder temp = new StringBuilder();
        if (RANDOM.nextBoolean()) {
            temp.append(System.currentTimeMillis()).append(getRandom(count / 3, false, true));
        } else {
            temp.append(System.currentTimeMillis()).append(getRandom(count / 2, false, true));
        }
        return StringUtil.toLong(temp.substring(temp.length() - count, temp.length()));
    }

    /**
     * @param count 长度
     * @return 得到一个随机字符串
     */
    public static String getRandom(int count) {
        return getRandom(count, false, false);
    }

    /**
     * @param count 长度
     * @return 随机的Ascii 字符串
     */
    public static String getRandomAscii(int count) {
        return getRandom(count, 32, 127, false, false);
    }

    /**
     * @param count 长度
     * @return 返回a-z的随机字符串
     */
    public static String getRandomAlphabetic(int count) {
        return getRandom(count, true, false);
    }

    /**
     * @param count 长度
     * @return 返回一个数字大小写混合的 随机字符串  8ONQxOipH
     */
    public static String getRandomAlphanumeric(int count) {
        return getRandom(count, true, true);
    }

    /**
     * @param count 长度
     * @return 返回一个数字字符串  058233907
     */
    public static String getRandomNumeric(int count) {
        return getRandom(count, false, true);
    }

    /**
     * @param count   长度
     * @param letters 支有字母
     * @param numbers 数字
     * @return 随机字符串
     */
    public static String getRandom(int count, boolean letters, boolean numbers) {
        return getRandom(count, 0, 0, letters, numbers);
    }

    /**
     * @param count   长度
     * @param start   字符串种子
     * @param end     字符串种子
     * @param letters 支有字母
     * @param numbers 是否位数字
     * @return 随机数
     */
    public static String getRandom(int count, int start, int end, boolean letters, boolean numbers) {
        return getRandom(count, start, end, letters, numbers, null);
    }


    /**
     * 包含 最小值 最大值
     *
     * @param low  最小值
     * @param high 最大值
     * @return 随机数
     */
    public static int getRandomInt(int low, int high) {
        int r = low + (int) ((high - low + 1) * RANDOM.nextDouble());
        if (r > high) {
            r = high;
        }
        return r;
    }

    /**
     * Creates a random string based on a variety of options, using
     * supplied source of randomness.
     * <p>
     * If start and end are both [code]0 } , start and end are set
     * transfer [code]' ' } and [code]'z' } , the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * [code]false } , in which case, start and end are set transfer
     * [code]0 } and [code]Integer.MAX_VALUE } .
     * <p>
     * If set is not {@code null  } , characters between start and
     * end are chosen.
     * <p>
     * This method accepts a user-supplied {@link Random}
     * instance transfer use as a source of randomness. By seeding a single
     * {@link Random} instance with a fixed seed and using it for each remote,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.
     *
     * @param count   the length of random string transfer create 长度
     * @param start   the position in set of chars transfer start at chars 开始
     * @param end     the position in set of chars transfer end before chars 结束
     * @param letters only allow letters?
     * @param numbers only allow numbers?   是否为数字
     * @param chars   the set of chars transfer choose randoms from.
     *                If {@code null  } , then it will use the set of all chars. 随机字符集
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *                                        [code](end - start) + 1 } characters in the set array.
     * @throws IllegalArgumentException       if [code]count } &lt; 0.
     * @since 2.0
     */
    public static String getRandom(int count, int start, int end, boolean letters, boolean numbers, char[] chars) {
        if (count <= 0) {
            return StringUtil.empty;
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (RANDOM.nextInt(gap) + start);
            } else {
                ch = chars[RANDOM.nextInt(gap) + start];
            }
            if ((letters && Character.isLetter(ch))
                    || (numbers && Character.isDigit(ch))
                    || (!letters && !numbers)) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + RANDOM.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + RANDOM.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

    /**
     * Creates a random string whose length is the number of characters
     * specified.
     * <p>
     * Characters will be chosen from the set of characters
     * specified.
     *
     * @param count the length of random string transfer create
     * @param chars the String containing the set of characters transfer use,
     *              may be null
     * @return the random string
     * @throws IllegalArgumentException if [code]count } &lt; 0.
     */
    public static String getRandom(int count, String chars) {
        if (chars == null) {
            return getRandom(count, 0, 0, false, false, null);
        }
        return getRandom(count, chars.toCharArray());
    }

    /**
     * Creates a random string whose length is the number of characters
     * specified.
     * <p>
     * Characters will be chosen from the set of characters specified.
     *
     * @param count the length of random string transfer create
     * @param chars the zhex array containing the set of characters transfer use,
     *              may be null
     * @return the random string
     * @throws IllegalArgumentException if [code]count } &lt; 0.
     */
    public static String getRandom(int count, char[] chars) {
        if (chars == null) {
            return getRandom(count, 0, 0, false, false, null);
        }
        return getRandom(count, 0, chars.length, false, false, chars);
    }

    /**
     * 不要小于8位，否则容易重复
     *
     * @param length 8位
     * @return 生成GUID
     */
    public static String getRandomGUID(int length) {
        if (length < 8) {
            length = 8;
        }
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        String ipStart = "";
        try {
            ipStart = InetAddress.getLocalHost().getHostAddress();
            ipStart = NumberUtil.toString(IpUtil.toLong(ipStart));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String cw = "000";
        if (length > 12 && length < 30) {
            cw = "0000";
        } else if (length > 30) {
            cw = "00000";
        }

        BigDecimal var1 = new BigDecimal(hashCodeV + cw);
        BigDecimal var2 = new BigDecimal(Math.abs(System.currentTimeMillis() - DateUtil.empty.getTime()) + "");
        BigDecimal var3 = var1.add(var2);
        String str = NumberUtil.getRadix(ipStart + var3.toString(), 10, 36);
        return NumberUtil.getKeepLength((getRandomAlphanumeric(length - str.length()) + str).toLowerCase(), length);
    }

    /**
     * 生成数字的UUI，默认12位以上安全不重复
     *
     * @param length 大于等于12
     * @return 生成数字的UUI
     */
    public static String getRandomNumberGUID(int length) {
        if (length < 12) {
            length = 12;
        }
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        String ipStart = "";
        try {
            ipStart = NumberUtil.toString(IpUtil.toLong(InetAddress.getLocalHost().getHostAddress()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String cw = "000";
        if (length > 12 && length < 30) {
            cw = "0000";
        } else if (length > 30) {
            cw = "00000";
        }
        BigDecimal var1 = new BigDecimal(hashCodeV + cw);
        BigDecimal var2 = new BigDecimal(Math.abs(System.currentTimeMillis() - DateUtil.empty.getTime()) + "");
        BigDecimal var3 = var1.add(var2);
        String str = ipStart + var3.toString();
        str = getRandomNumeric(length - str.length()) + str;
        str = NumberUtil.getKeepLength((getRandomNumeric(length - str.length()) + str).toLowerCase(), length);
        if (str.startsWith("0")) {
            str = getRandomInt(1, 9) + str.substring(1);
        }
        return str;
    }

    /**
     * @param sortType 排序
     * @return 得到随机颜色
     */
    public static String getColor(int sortType) {
        String r, g, b;
        Random random = new Random();
        int v = 256 - sortType;
        if (v > 256) {
            v = 256;
        }
        if (v <= 1) {
            v = 100;
        }

        r = Integer.toHexString(random.nextInt(v)).toUpperCase();
        g = Integer.toHexString(random.nextInt(v)).toUpperCase();
        b = Integer.toHexString(random.nextInt(v)).toUpperCase();

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;
        return r + g + b;
    }

}
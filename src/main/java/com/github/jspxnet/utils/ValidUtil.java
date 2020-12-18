package com.github.jspxnet.utils;


import com.github.jspxnet.util.ValidateIdCard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/3/4 14:00
 * description: 统一的验证单元
 */
public class ValidUtil {
    private ValidUtil()
    {

    }
    /**
     * This method accepts name with char, number or '_' or '.'
     * <p>
     * This method should be used transfer impl all LoginName input from user for sdk.security.
     *
     * @param str 用户名
     * @return 判断是否为合法的用户名
     */
    final static char[] incertitudeChars = {
            '\\', '/', '$', '\'', '#', '!', '\"', '<', '>', '!', '!', '~', ';', '^', ';', '*', '(', ')', '[', ']', '{', '}'
    };

    /**
     * 判断是否为空
     * @param value 字符串
     * @return 是否为空
     */
    public static boolean isNull(Object value) {
        return ObjectUtil.isEmpty(value);
    }
    /**
     *
     * @param cardNumber  身份证号
     * @return 是否有效
     */
    public static boolean isIdCard(String cardNumber)
    {
        return ValidateIdCard.validateCard(cardNumber);
    }

    /**
     * 判断一字段值是否数字都是字符
     * @param numStr 数字字符串
     * @return  判断一字段值是否数字都是字符
     */
    public static boolean isNumber(String numStr) {
        if (StringUtil.isNull(numStr)) {
            return false;
        }
        String s = numStr.replaceAll("[0-9;]+", "");
        return "".equals(s.trim());
    }


    /**
     *
     * @param str 字符串
     * @return 是否为邮箱
     */
    public static boolean isMail(String str) {
        return !(str == null || str.length() < 2) && str.contains("@") && str.contains(".");
    }

    /**
     * @param mobiles 字符串
     * @return 判断是否为电话号码
     */
    public static boolean isPhone(String mobiles) {
        //if (!hasLength(str) || str.length() < 10) return false;
        Pattern p = compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$|^([0-9]{3}-?[0-9]{8})|([0-9]{4}-?[0-9]{7})$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * @param mobiles 判断是否为手机号
     * @return 判断是否为手机
     */
    public static boolean isMobile(String mobiles) {
        Pattern p = compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     *
     * @param str 用户名
     * @return  合规的用户名
     */
    public static boolean isGoodName(String str)
    {
        return isGoodName(str,3,24);
    }

    public static boolean isGoodName(String str, int minLength, int maxLength) {
        if (!StringUtil.hasLength(str) || str.length() < minLength || str.length()>maxLength)
        {
            return false;
        }
        for (char c : incertitudeChars) {
            if (str.indexOf(c) != -1) {
                return false;
            }
        }
        //用户名称不能全是数字
        if (isNumber(str)) {
            return false;
        }
        //不要使用邮箱 方便区分那种方式登录
        if (isMail(str)) {
            return false;
        }
        //不要电话 方便区分那种方式登录
        return !isPhone(str);
    }

    /**
     *
     * @param macAddressCandidate 地址字符串
     * @return 是否为mac地址
     */
    public static boolean isMacAddress(String macAddressCandidate) {
        if (macAddressCandidate == null || macAddressCandidate.length() < 10) {
            return false;
        }
        Pattern macPattern = compile("[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}");
        Matcher m = macPattern.matcher(macAddressCandidate);
        return m.matches();
    }


}

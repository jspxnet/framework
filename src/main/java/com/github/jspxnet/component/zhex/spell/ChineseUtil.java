/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.spell;


import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.component.zhex.WordFilter;
import com.github.jspxnet.component.zhex.filter.FJFilter;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-3-21
 * Time: 16:56:38
 */
public class ChineseUtil {


    private ChineseUtil() {

    }


    /**
     * 返回字符串的全拼,是汉字转化为全拼,其它字符不进行转换
     *
     * @param src       字符串
     * @param separator 分割
     * @return 转换成全拼后的字符串
     */
    public static String fullSpell(String src, String separator) {
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder result = new StringBuilder();
        try {
            for (char aT1 : t1) {
                // 判断是否为汉字字符
                if (Character.toString(aT1).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(aT1, t3);
                    result.append(StringUtil.capitalize(t2[0])).append(separator);
                } else {
                    result.append(aT1).append(separator);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return result.toString();
    }


    /**
     * 返回字符串拼音的首字母,是汉字转化为拼音,其它字符不进行转换
     *
     * @param str       字符串
     * @param separator 转换成拼音后的字符串（全大写）
     * @return 拼音的首字母
     */
    static public String firstSpell(String str, String separator) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                result.append(pinyinArray[0].charAt(0)).append(separator);
            } else {
                result.append(word).append(separator);
            }
        }
        return result.toString();
    }

    public static String getFullSpell(String str, String separator) {
        return fullSpell(str, separator);
    }

    public static String getFirstSpell(String str, String separator) {
        return firstSpell(str, separator);
    }

    public static String getFirstSpell(String str) {
        return firstSpell(str, "");
    }

    public static String getFJFilter(String str) {
        return getFJFilter(str, null);
    }

    public static String getFJFilter(String str, String type) {
        if (StringUtil.isNull(type)) {
            type = FJFilter.GB_BIG5;
        }
        WordFilter wordFilter = FJFilter.getInstance();
        try {
            return wordFilter.doFilter(str, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }


}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.jubb;

import com.github.jspxnet.security.symmetry.AbstractEncrypt;

import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.Date;
import java.util.Map;

/**
 * @author chenyuan
 *
 */
public class UBBFilter extends HTMLFilter {
    final static public String URLFilter = "URLFilter";
    final static public String MailFilter = "MailFilter";
    final static public String ColorFilter = "ColorFilter";
    final static public String CssFilter = "CssFilter";
    final static public String TextStyleFilter = "TextStyleFilter";
    final static public String QuteFilter = "QuoteFilter";
    final static public String ImgFilter = "ImgFilter";
    final static public String SmileFilter = "SmileFilter";
    final static public String FlashFilter = "FlashFilter";
    final static public String ObjectFilter = "ObjectFilter";
    final static public String SoundFilter = "SoundFilter";
    final static public String FontFilter = "FontFilter";
    final static public String CodeFilter = "CodeFilter";
    final static public String AutoFilter = "AutoFilter";
    final static public String LocalFilter = "LocalFilter";

    static final public String DefaultFilter = QuteFilter + CodeFilter + TextStyleFilter + FontFilter + CssFilter + URLFilter + MailFilter + ColorFilter + ImgFilter + ObjectFilter + LocalFilter;
    static final public String BBSFilter = QuteFilter + CodeFilter + TextStyleFilter + FontFilter + CssFilter + URLFilter + MailFilter + ColorFilter + ImgFilter;
    static final public String MediaFilter = LocalFilter + SoundFilter + FlashFilter + ObjectFilter;

    static final public String SimpleFilter = ColorFilter + TextStyleFilter;
    private String[] as = null;


    static private String downloadLink = "download.jhtml?id=";
    static private AbstractEncrypt encrypt = null;

    public static void setEncrypt(AbstractEncrypt encrypt) {
        UBBFilter.encrypt = encrypt;
    }

    /**
     * ubb过滤工厂
     *
     * @param s1   过滤的字符串
     * @param term 条件
     */

    public UBBFilter(String s1, String term) {
        s = s1;
        String Term = term;
        if (StringUtil.isNull(Term)) {
            Term = DefaultFilter;
        }
        Term = StringUtil.replace(Term, "Filter", "Filter/");
        as = StringUtil.split(Term, "/");
    }

    public static String getDownloadLink() {
        return downloadLink;
    }

    public static void setDownloadLink(String link) {
        downloadLink = link;
    }

    @Override
    public void setInputString(String s) {
        this.s = s;
    }

    @Override
    public String getInputString() {
        return s;
    }

    /**
     * @return 返回结果
     */
    @Override
    public String getFilterString() {

        String outString = s;
        try {
            for (String a : as) {
                String className =  "com.github.jspxnet.component.jubb." + a;
                Filter filter = (Filter) Class.forName(className).newInstance();
                filter.setInputString(outString);
                if (a.equals(LocalFilter)) {
                    BeanUtil.setSimpleProperty(filter, "downloadLink", downloadLink);
                }
                outString = filter.getFilterString();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return s;
        }
        return outString;
    }


    /**
     * @param filterString 要过滤的字符串
     * @param grade        当前用户等级
     * @param gradeTip     等等提示
     * @return 结果字符串
     */
    public static String getHideFilter(String filterString, int grade, String gradeTip) {
        if (StringUtil.isNull(gradeTip)) {
            gradeTip = "指定用户角色才能查看";
        }

        HideFilter filter = new HideFilter();
        filter.setGrade(grade);
        filter.setGradeTip(gradeTip);
        filter.setInputString(filterString);
        return filter.getFilterString();
    }

    /**
     * @param filterString 要过滤的字符串
     * @param reply        是否回复
     * @param replyTip     说明信息
     * @return 回复可见
     */
    public static String getReplyFilter(String filterString, boolean reply, String replyTip) {
        ReplyFilter filter = new ReplyFilter();
        filter.setReply(reply);
        filter.setReplyTip(replyTip);
        filter.setInputString(filterString);
        return filter.getFilterString();
    }

    /**
     * @param filterString 过滤的字符串
     * @param payMap       支付了多少
     * @param valueMap     变量
     * @param moneyTip     提示信息
     * @return 消费金钱可见
     */
    public static String getMoneyFilter(String filterString, Map<Integer, Double> payMap, Map<String, Object> valueMap, String moneyTip) {
        MoneyFilter moneyFilter = new MoneyFilter();
        moneyFilter.setPayMap(payMap);
        moneyFilter.setValueMap(valueMap);
        moneyFilter.setMoneyTip(moneyTip);
        moneyFilter.setInputString(filterString);
        return moneyFilter.getFilterString();
    }


    /**
     * @param filterString 过滤的字符串
     * @param payMap       支付了多少
     * @param valueMap     变量
     * @param pointsTip    提示信息
     * @return 消费积分可见
     */
    public static String getPointsFilter(String filterString, Map<Integer, Integer> payMap, Map<String, Object> valueMap, String pointsTip) {
        PointsFilter pointsFilter = new PointsFilter();
        pointsFilter.setPayMap(payMap);
        pointsFilter.setValueMap(valueMap);
        pointsFilter.setPointsTip(pointsTip);
        pointsFilter.setInputString(filterString);
        return pointsFilter.getFilterString();
    }

    /**
     * @param filterString 过滤的字符串
     * @param startDate    开启时间 数字，表示天数，或者是一个日期格式
     * @param timingTip    提示信息
     * @return 定时开启显示内容
     */
    public static String getTimingFilter(String filterString, Date startDate, String timingTip) {
        TimingFilter timingFilter = new TimingFilter();
        timingFilter.setStartDate(startDate);
        timingFilter.setTimingTip(timingTip);
        timingFilter.setInputString(filterString);
        return timingFilter.getFilterString();
    }


    /**
     * [localimg=400,300]0[/localimg]
     *
     * @param filterString 字符串
     * @param imgArray     图片列表
     * @return 得到积分
     */
    public static String getInsertLocalImg(String filterString, String[] imgArray) {
        LocalImgFilter imgFilter = new LocalImgFilter();
        imgFilter.setInputString(filterString);
        imgFilter.setImg(imgArray);
        return imgFilter.getFilterString();
    }

    /**
     * @param html html
     * @return ubb转换
     */
    public static String decode(String html) {
        return decode(html, DefaultFilter);
    }

    /**
     * @param html html
     * @param term 需要过滤的标签
     * @return ubb转换
     */
    public static String decode(String html, String term) {
        UBBFilter factoryFilter = new UBBFilter(html, term);
        return factoryFilter.getFilterString();
    }

}
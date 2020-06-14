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


import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ValidUtil;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-12-19
 * Time: 上午10:16
 * 时间定时
 * [timing=2017-04-20]要显示的内容[/timing]
 */
public class TimingFilter extends HTMLFilter {
    private Date startDate = new Date();
    private String timingTip = "定时{0}显示内容";

    public TimingFilter(String s) {
        super(s);
    }

    public TimingFilter() {

    }

    public String getTimingTip() {
        return timingTip;
    }

    public void setTimingTip(String timingTip) {
        this.timingTip = timingTip;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String convertString() {
        return hideConverter();
    }

    public String hideConverter() {
        Pattern pattern = compile("(\\[timing=([0-9]|[0-9]{4}-[0-9]{2}-[0-9]{2})\\])(.[^\\[]*)(\\[\\/timing\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            String lan = matcher.group(2);
            boolean openView = false;
            Date date = new Date();
            if (ValidUtil.isNumber(lan)) {
                date = DateUtil.addDate(StringUtil.toInt(lan), startDate);
                openView = date.before(new Date());
            } else {
                try {
                    date = StringUtil.getDate(lan);
                    openView = date.before(new Date());
                } catch (Exception e) {
                    openView = true;
                }
            }
            if (openView) {
                matcher.appendReplacement(stringbuffer, matcher.group(3));
            } else {
                matcher.appendReplacement(stringbuffer, StringUtil.replace(timingTip, "{0}", DateUtil.toString(date, DateUtil.DAY_FORMAT)));
            }
        }
        matcher.appendTail(stringbuffer);
        //-----------------------------------------------------------------
        pattern = compile("(\\[timing\\])([^\\[]*)(\\[\\/timing\\])", Pattern.DOTALL);
        matcher = pattern.matcher(stringbuffer.toString());
        StringBuffer result = new StringBuffer();
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            if (DateUtil.addDate(1, startDate).before(new Date())) {
                matcher.appendReplacement(result, matcher.group(2));
            } else {
                matcher.appendReplacement(stringbuffer, StringUtil.replace(timingTip, "{0}", DateUtil.toString(DateUtil.addDate(3, startDate), DateUtil.DAY_FORMAT)));
            }
        }
        matcher.appendTail(result);

        return result.toString();

    }
}
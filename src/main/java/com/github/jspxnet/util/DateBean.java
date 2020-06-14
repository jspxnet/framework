/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-30
 * Time: 10:48:00
 */
public class DateBean {
    private int date = 0;
    private int chineseYear = 0;
    private int chineseMonth = 0;
    private int chineseDate = 0;
    private String cyclicalYear = StringUtil.empty;
    private String cyclicalMonth = StringUtil.empty;
    private String cyclicalDate = StringUtil.empty;
    private int count = 0;
    private String cssName = StringUtil.empty;
    private String hint = StringUtil.empty;
    private String explain = StringUtil.empty;

    public DateBean() {

    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    private int star = 0;

    public boolean isEmpty() {
        return year <= 0 && month <= 0 && date <= 0;
    }

    public int getWeek() {
        return week;
    }

    public int getWeekString() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    private int week = 0;

    private int year = 0;

    private int month = 0;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getChineseYear() {
        return chineseYear;
    }

    public void setChineseYear(int chineseYear) {
        this.chineseYear = chineseYear;
    }

    public int getChineseMonth() {
        return chineseMonth;
    }

    public void setChineseMonth(int chineseMonth) {
        this.chineseMonth = chineseMonth;
    }

    public int getChineseDate() {
        return chineseDate;
    }

    public void setChineseDate(int chineseDate) {
        this.chineseDate = chineseDate;
    }

    public String getCyclicalYear() {
        return cyclicalYear;
    }

    public void setCyclicalYear(String cyclicalYear) {
        this.cyclicalYear = cyclicalYear;
    }

    public String getCyclicalMonth() {
        return cyclicalMonth;
    }

    public void setCyclicalMonth(String cyclicalMonth) {
        this.cyclicalMonth = cyclicalMonth;
    }

    public String getCyclicalDate() {
        return cyclicalDate;
    }

    public void setCyclicalDate(String cyclicalDate) {
        this.cyclicalDate = cyclicalDate;
    }

    public int getCount() {
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    public String getCssName() {
        return cssName;
    }

    public void setCssName(String cssName) {
        this.cssName = cssName;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getFullDate() {
        String smonth = month + StringUtil.empty;
        String sdate = date + StringUtil.empty;
        if (month < 10) {
            smonth = "0" + month;
        }
        if (date < 10) {
            sdate = "0" + date;
        }
        return year + StringUtil.empty + smonth + StringUtil.empty + sdate;
    }

    public void loginDuty(long uid) {
        System.out.println("--uid=" + uid);
    }
}
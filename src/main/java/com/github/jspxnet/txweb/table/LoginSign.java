/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.DateUtil;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-10-23
 * Time: 下午6:15
 * 如果是公司内部使用，可以使用此表统计出考勤表
 */

@Table(name = "jspx_login_sign", caption = "登录考勤记录")
public class LoginSign extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "uid", length = 50, notNull = true)
    private long uid = 0;


    /**
     *  为了统计方便 作为日历使用
     */
    @Column(caption = "日期", length = 50, notNull = true)
    private String dateStr = DateUtil.getDateST();

    /**
     * 为了统计方便
     */
    @Column(caption = "月份", length = 50, notNull = true)
    private String monthStr = DateUtil.toString(new Date(), "yyyy-MM");

    /**
     * 为了统计方便
     */
    @Column(caption = "年份", length = 50, notNull = true)
    private String yearStr = DateUtil.toString(new Date(), "yyyy");


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getMonthStr() {
        return monthStr;
    }

    public void setMonthStr(String monthStr) {
        this.monthStr = monthStr;
    }

    public String getYearStr() {
        return yearStr;
    }

    public void setYearStr(String yearStr) {
        this.yearStr = yearStr;
    }
}
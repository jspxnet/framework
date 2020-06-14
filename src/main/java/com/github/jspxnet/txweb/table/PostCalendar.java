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

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-10-6
 * Time: 15:07:36
 * 记录每天发布的数量
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_post_calendar", caption = "提交日历")
public class PostCalendar extends OperateTable {
    public PostCalendar() {

    }

    @Id
    @Column(caption = "ID", notNull = true)
    private int id;

    @Column(caption = "计数", notNull = true)
    private long postCount = 0;

    @Column(caption = "最后日期", notNull = true)
    private Date postDate = new Date();

    @Column(caption = "字符串日期", length = 40, notNull = true)
    private String shortDate = DateUtil.toString(DateUtil.DAY_FORMAT);

    @Column(caption = "年份", length = 5, notNull = true)
    private int dateYear = DateUtil.getYear();

    @Column(caption = "月份", length = 3, notNull = true)
    private int dateMonth = DateUtil.getMonth();

    @Column(caption = "日", length = 3, notNull = true)
    private int dateDay = DateUtil.getDate();

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;


    private String hint = StringUtil.empty;

    private String cssName = StringUtil.empty;

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
        shortDate = DateUtil.toString(this.postDate, DateUtil.DAY_FORMAT);
        dateYear = StringUtil.toInt(DateUtil.toString(this.postDate, "yyyy"));
        dateMonth = StringUtil.toInt(DateUtil.toString(this.postDate, "MM"));
        dateDay = StringUtil.toInt(DateUtil.toString(this.postDate, "dd"));
    }

}
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
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-4-19
 * Time: 上午10:41
 * 支付公司信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_payment_firm", caption = "支付公司信息")
public class PaymentFirm extends OperateTable {
    public PaymentFirm() {

    }

    @Id
    @Column(caption = "ID", notNull = true)
    private int id = 0;

    @Column(caption = "名称", length = 100, notNull = true)
    private String caption = StringUtil.empty;

    @Column(caption = "公司网址", length = 100)
    private String siteUrl = StringUtil.empty;

    @Column(caption = "显示页面", length = 100)
    private String showUrl = StringUtil.empty;

    @Column(caption = "返回页面", length = 100)
    private String returnUrl = StringUtil.empty;

    @Column(caption = "事件页面", length = 100)
    private String notifyUrl = StringUtil.empty;

    @Column(caption = "公司网址", length = 100)
    private String actionUrl = StringUtil.empty;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

}
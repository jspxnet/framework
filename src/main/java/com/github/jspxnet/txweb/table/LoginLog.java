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
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-1-13
 * Time: 下午2:52
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Table(name = "jspx_login_log", caption = "登录历史记录表")
public class LoginLog extends OperateTable {

    @Id
    @Column(caption = "ID",notNull = true)
    private long id;

    @Column(caption = "token", length = 512, notNull = true)
    private String token = null;

    @Column(caption = "sessionId", length = 256)
    private String sessionId = null;

    //登录客户端方式，app:开放接口;web:网页;weiXin:微信
    @Column(caption = "名称", length = 20, defaultValue = "web")
    private String client = "web";

    //记录的是第几次登录
    @Column(caption = "登录次数", notNull = true)
    private long loginTimes = 0;

    @Column(caption = "应用ID", notNull = true)
    private long appId = 0;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "排序日期", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "名称", dataType = "isLengthBetween(1,250)", length = 250)
    private String url = StringUtil.empty;

    @Column(caption = "浏览器", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String browser = "unknown";

    @Column(caption = "操作系统", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String system = "unknown";

    @Column(caption = "网络类型", dataType = "isLengthBetween(1,20)", length = 20)
    private String netType = "unknown";


}
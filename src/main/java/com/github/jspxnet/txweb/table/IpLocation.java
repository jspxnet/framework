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

import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-6-23
 * Time: 17:08:03
 * 陈原
 */

@Data
@Table(name = "jspx_ip_location", caption = "IP表")
public class IpLocation implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "行数", notNull = true)
    private int lineNumber = 0;

    @Column(caption = "起始IP", notNull = true)
    private long beginIp = 0;

    @Column(caption = "结束IP", notNull = true)
    private long endIp = 0;

    @Column(caption = "地区", length = 100, notNull = true)
    private String country = StringUtil.empty;

    @Column(caption = "城市", length = 100)
    private String city = StringUtil.empty;

}
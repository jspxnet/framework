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

import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-6-23
 * Time: 17:08:03
 * 陈原
 */

@Table(name = "jspx_ip_location", caption = "IP表", cache = true)
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

    @Column(caption = "城市", length = 100, notNull = false)
    private String city = StringUtil.empty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public long getBeginIp() {
        return beginIp;
    }

    public void setBeginIp(long beginIp) {
        this.beginIp = beginIp;
    }

    public long getEndIp() {
        return endIp;
    }

    public void setEndIp(long endIp) {
        this.endIp = endIp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return country;
    }

}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-2
 * Time: 12:11:48
 *
 */
@Data
@Table(name = "jspx_sequences")
public class Sequences implements Serializable {

    @Id(type = IDType.none)
    @Column(caption = "name", length = 100, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "当前计数", notNull = true, defaultValue = "0")
    private long keyValue = 0;

    @Column(caption = "最大计数", notNull = true, defaultValue = "0")
    private long keyMax = 99999;

    @Column(caption = "最小计数", notNull = true, defaultValue = "0")
    private int keyMin = 0;

    @Column(caption = "间隔计数", notNull = true, defaultValue = "0")
    private int keyNext = 1;

    @Column(caption = "计数长度", notNull = true, defaultValue = "0")
    private int keyLength = 5;

    @Column(caption = "添加日期", option = "0:否;1:是", notNull = true, defaultValue = "1")
    private int dateStart = 0;

    @Column(caption = "单号头", length = 20)
    private String headChars = StringUtil.empty;

    //加前缀配置 'BXD'yyyyMMdd
    @Column(caption = "日期格式", length = 20, notNull = true, defaultValue = "yyMMdd")
    private String dateFormat = "yyMMddhh";

    //添加IP硬件信息，确保分布式可合并
    @Column(caption = "添加MAC", option = "0:否;1:是", notNull = true, defaultValue = "1")
    private int mac = 0;

    public String getNextKey(long value) {
        keyValue = value;
        if (keyValue <= keyMin) {
            keyValue = keyMin;
        }
        if (keyValue > keyMax) {
            keyValue = keyMin;
        }

        if (StringUtil.isNull(dateFormat)) {
            dateFormat = "yyMMddhh";
        }
        String start = StringUtil.empty;
        if (dateStart == 1 && mac == 0)
        {
            start = DateUtil.toString(dateFormat);
        } else
        if (dateStart == 0 && mac == 1) {
            start = IpUtil.getIpEnd();
        } else  if (dateStart == 1 && mac == 1)
        {
            start = DateUtil.toString(dateFormat) + IpUtil.getIpEnd();
        }
        if (keyLength-start.length()<5)
        {
            return NumberUtil.getKeepLength( start+NumberUtil.toString(keyValue), keyLength);
        }
        return start + NumberUtil.getKeepLength( NumberUtil.toString(keyValue), keyLength-start.length());
    }

    public String getNextKey() {
        if (keyValue <= keyMin) {
            keyValue = keyMin;
        }
        if (keyValue > keyMax) {
            keyValue = keyMin;
        }
        keyValue = keyValue + keyNext;
        if (StringUtil.isNull(dateFormat)) {
            dateFormat = "yyMMddhh";
        }
        String start = StringUtil.empty;
        if (dateStart == 1 && mac == 0)
        {
            start = DateUtil.toString(dateFormat);
        } else
        if (dateStart == 0 && mac == 1) {
            start = IpUtil.getIpEnd();
        } else  if (dateStart == 1 && mac == 1)
        {
            start = DateUtil.toString(dateFormat) + IpUtil.getIpEnd();
        }
        if (keyLength-start.length()<5)
        {
            return NumberUtil.getKeepLength( start+NumberUtil.toString(keyValue), keyLength);
        }
        return start + NumberUtil.getKeepLength( NumberUtil.toString(keyValue), keyLength-start.length());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<sequences value=\"").append(keyValue).append("\" name=\"").append(name).append("\" " + "keyMax=\"").append(keyMax).append("\" keyMin=\"").append(keyMax).append("\" keyNext=\"").append(keyNext).append("\" " + "length=\"").append(keyLength).append("\" dateFormat=\"").append(dateFormat).append("\" dateStart=\"").append(dateStart).append("\"/>\r\n");
        return sb.toString();
    }
}
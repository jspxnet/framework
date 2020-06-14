/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 15:23:37
 */
@Table(caption = "操作方法")
public class ResultConfigBean implements Serializable {
    @Column(caption = "返回值", length = 200, notNull = true)
    private String name = StringUtil.ASTERISK;
    @Column(caption = "返回类型", length = 200, notNull = true)
    private String type = ActionSupport.TEMPLATE;
    @Column(caption = "路径", length = 200, notNull = true)
    private String value = StringUtil.empty;
    @Column(caption = "http状态", notNull = true)
    private int status = 200;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "<result name=\"" + name + "\" type=\"" + type + "\" status=\"" + status + "\"><![CDATA[" + value + "]]></result>\r\n";
    }
}
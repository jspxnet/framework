/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.postalcode;

import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-16
 * Time: 9:42:54
 * 国别（地区）代码表
 * 国家
 */
public class Country implements Serializable {
    @Id(auto = false)
    @Column(caption = "ID号", length = 50, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "国名", length = 160, notNull = true)
    private String name = StringUtil.empty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Country() {

    }
}
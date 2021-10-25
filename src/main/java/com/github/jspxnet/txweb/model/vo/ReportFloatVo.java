/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.model.vo;

import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-11-9
 * Time: 下午8:57
 * 报表图形VO对象
 */
@Data
@Table(caption = "小数统计报表", create = false,cache = false)
public class ReportFloatVo {
    private String name = StringUtil.empty;
    private float num = 0;

    public void setName(float name) {
        this.name = Float.valueOf(name).toString();
    }

    public void setName(double name) {
        this.name = Double.valueOf(name).toString();
    }

}
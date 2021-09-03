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

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: ChenYuan
 * date: 12-4-8
 * Time: 下午9:39
 */

@Data
@Table(caption = "整数统计报表", create = false)
public class ReportIntVo implements Serializable {

    private String name = StringUtil.empty;
    private int num = 0;
    private int tempNum = 0;

    public void setName(Object name) {
        if (name instanceof Integer || name instanceof Long || name instanceof Float) {
            this.name = name + "";

        } else {
            this.name = (String) name;
        }
    }
}
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
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * author chenyuan
 * @author Administrator
 */
@Data
public abstract class OperateTable implements Serializable {
    @Column(caption = "操作人", length = 50, notNull = true)
    protected String putName = StringUtil.empty;

    @Column(caption = "操作人ID", notNull = true)
    protected long putUid = 0;

    @Column(caption = "IP地址", length = 48, notNull = true, defaultValue = "127.0.0.1")
    protected String ip = "127.0.0.1";

    @Column(caption = "创建时间", notNull = true)
    protected Date createDate = new Date();


    public void setIp(String ip) {
        if (ip != null && ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        this.ip = ip;
    }

}
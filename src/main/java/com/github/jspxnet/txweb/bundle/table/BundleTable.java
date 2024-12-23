/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle.table;

import java.io.Serializable;

import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-13
 * Time: 16:59:33
 */
@Data
@Table(name = "jspx_bundle_table", caption = "资源绑定表")
public class BundleTable implements Serializable {

    public BundleTable() {

    }

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "键值", length = 50, notNull = true)
    private String idx = StringUtil.empty;

    @Column(caption = "内容", length = 1000, notNull = true)
    private String context = StringUtil.empty;

    //todo 新版本在加入
    //@Column(caption = "描述", length = 100)
    private String caption = StringUtil.empty;

    @Column(caption = "类型", length = 50, notNull = true)
    private String dataType = StringUtil.empty;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @Column(caption = "加密", length = 2, option = "0:否;1:是", notNull = true)
    private int encrypt = 0;

}
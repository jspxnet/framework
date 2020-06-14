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

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-28
 * Time: 下午4:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_bundle", caption = "用户配置")
public class MemberBundle extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "键值", length = 50, notNull = true)
    private String idx = StringUtil.empty;

    @Column(caption = "内容", length = 250, notNull = true)
    private String context = StringUtil.empty;

    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

}
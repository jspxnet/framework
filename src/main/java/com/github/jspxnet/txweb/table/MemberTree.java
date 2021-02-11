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
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-4-13
 * Time: 10:44:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_tree", caption = "栏目权限")
public class MemberTree extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "栏目ID", length = 50, notNull = true)
    private String nodeId = StringUtil.empty;

    @Column(caption = "用户ID", length = 50, notNull = true)
    private long uid = 0;

    @Column(caption = "树名", length = 50, notNull = true)
    private String namespace = StringUtil.empty;

    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    @Column(caption = "树ID", length = 50, notNull = true)
    private String treeId = StringUtil.empty;
}
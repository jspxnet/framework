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

import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-12-2
 * Time: 15:19:36
 * 因为有多个软件，和多个角色的对应关系，所以要单独做一个角色表，保存用户和角色的关闭
 * 角色和用户不是一对一关系，而是一对多的关系
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_role", caption = "用户角色")
public class MemberRole extends OperateTable {
    @Id(auto = true, length = 24, type = IDType.uuid)
    @Column(caption = "ID", length = 32, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "用户ID", notNull = true)
    private long uid = 0;

    @Column(caption = "权限角色ID", length = 32, notNull = true)
    private String roleId = StringUtil.empty;

    @Nexus(mapping = MappingType.OneToOne, field = "roleId", targetField = "id", targetEntity = Role.class,chain = true, delete = false, where = "${roleId!=0}",term="congealType:eq[0];auditingType:eq[1]")
    private Role role = new Role();

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

}
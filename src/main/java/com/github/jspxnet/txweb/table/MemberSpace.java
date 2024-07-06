/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.table;


import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Created by yuan on 14-3-1.
 * 用户所属关系表，是一个总表，各个软件里边的用户域
 * 第一个子用户就是自己的创建者，查询子用户的时候不用 memberId
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_space", caption = "用户域")
public class MemberSpace extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "父用户ID", notNull = true)
    private long memberId = 0;

    //只是第一次进入的名称,后期不要是用这个名称,也不用维护,如果操作记录
    @Column(caption = "父用户名", length = 50, dataType = "isLengthBetween(0,50)")
    private String memberName = StringUtil.empty;

    //创建者自己，被邀请的人来创建
    @Column(caption = "子用户ID", notNull = true)
    private long childId = 0;

    //只是第一次进入的名称,后期不要是用这个名称,也不用维护,如果操作记录
    @Column(caption = "子用户名", length = 50, dataType = "isLengthBetween(0,50)")
    private String childName = StringUtil.empty;

    @Column(caption = "机构ID", length = 32, notNull = true)
    private String organizeId = StringUtil.empty;

    @Column(caption = "组织名称", length = 200, dataType = "isLengthBetween(0,200)", notNull = true)
    private String organize = StringUtil.empty;

    @Column(caption = "部门节点", length = 32)
    private String nodeId = StringUtil.empty;

    @Column(caption = "是否被冻结", length = 2, option = "0:有效;1:冻结", notNull = true)
    private int congealType = CongealEnumType.NO_CONGEAL.getValue();

    @Column(caption = "冻结时间")
    private Date congealDate = new Date();

    @Column(caption = "原因", length = 250)
    private String reason = StringUtil.empty;

    @Column(caption = "备注", length = 250)
    private String remark = StringUtil.empty;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @Column(caption = "职位id", length = 32)
    private String positionNodeId;
}
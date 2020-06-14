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

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-15
 * Time: 22:34:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_action_log", caption = "动作日志")
public class ActionLog extends OperateTable {

    @Id(auto = true, length = 32, type = IDType.uuid, dateStart = true)
    @Column(caption = "ID", length = 32, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "名称", length = 200, dataType = "isLengthBetween(2,200)", notNull = true)
    private String caption = StringUtil.empty;

    @Column(caption = "类名", length = 200, dataType = "isLengthBetween(2,200)", notNull = true)
    private String className = StringUtil.empty;

    @Column(caption = "方法名称", length = 200, dataType = "isLengthBetween(2,200)", notNull = true)
    private String methodCaption = StringUtil.empty;

    @Column(caption = "类名", length = 200, dataType = "isLengthBetween(2,200)", notNull = true)
    private String classMethod = StringUtil.empty;

    @Column(caption = "返回", length = 200, dataType = "isLengthBetween(2,100)", notNull = true)
    private String actionResult = StringUtil.empty;

    @Column(caption = "说明", length = 250, dataType = "isLengthBetween(2,250)", notNull = false)
    private String title = StringUtil.empty;

    @Column(caption = "正文", length = 20000, dataType = "isLengthBetween(2,20000)", notNull = false)
    private String content = StringUtil.empty;

    @Column(caption = "类型", length = 100)
    private String objectType = StringUtil.empty;

    @Column(caption = "操作对象", length = 200)
    private String objectId = StringUtil.empty;

    //根据用户配置设置
    @Column(caption = "显示", option = "0:显示;1:隐藏")
    private int showType = 0;

    @Column(caption = "地址", length = 250, dataType = "isLengthBetween(2,250)", notNull = true)
    private String url = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;


}
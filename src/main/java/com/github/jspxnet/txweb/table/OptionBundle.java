/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Created by yuan on 14-2-16.
 * <p>
 * 字典表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_option_bundle", caption = "备选表")
public class OptionBundle extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "编码", length = 50, dataType = "isLengthBetween(0,50)", notNull = false)
    private String code = StringUtil.empty;

    //昵称，中文名称方式登录
    @Column(caption = "名称", length = 80, dataType = "isLengthBetween(0,50)", notNull = true)
    private String name = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "拼音", length = 100, hidden = true, notNull = true)
    private String spelling = StringUtil.empty;

    @Column(caption = "描述", length = 200)
    private String description = StringUtil.empty;

    @Column(caption = "默认选择", enumType = YesNoEnumType.class)
    private int selected = YesNoEnumType.NO.getValue();

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "父编码", length = 50, dataType = "isLengthBetween(0,50)", notNull = false)
    private String parentCode = StringUtil.empty;

    @Column(caption = "分组编码", length = 50, dataType = "isLengthBetween(0,50)", notNull = false)
     private String groupCode;

    @Column(caption = "命名空间", length = 50, notNull = true)
    private String namespace = StringUtil.empty;
}
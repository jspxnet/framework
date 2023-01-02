package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.enums.ControlTypeEnumType;
import com.github.jspxnet.enums.PlatformEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.table.meta.ControlEvent;
import com.github.jspxnet.txweb.table.meta.ControlProperty;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ControlDto implements Serializable {

    @Column(caption = "ID")
    private long id;

    @Column(caption = "控件类型", length = 100,option = "",notNull = true)
    private String groupName = StringUtil.empty;


    //控件名称
    @Column(caption = "名称")
    private String name = StringUtil.empty;

    //控件中文名称
    @Column(caption = "描述名称")
    private String caption = StringUtil.empty;

    /**
     * 它类似于传统html中的div，用于包裹各种元素内容。
     * 如果使用nvue
     * 则需注意，包裹文字应该使用 text组件。
     */
    @Column(caption = "控件使用说明")
    private String desc = StringUtil.empty;

    @Column(caption = "控件例子")
    private String demo = StringUtil.empty;

    @Column(caption = "控件属性")
    private List<ControlProperty> propertyList = new ArrayList<>();

    @Column(caption = "事件列表")
    private List<ControlEvent> eventList = new ArrayList<>();

    @Column(caption = "适用平台类型",enumType = PlatformEnumType.class)
    private int platformType = PlatformEnumType.ANY.getValue();

    @Column(caption = "控件类型",enumType = ControlTypeEnumType.class)
    private int controlType = ControlTypeEnumType.BASE.getValue();

    @Column(caption = "用户类型", notNull = true)
    private int userType = 0;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "创建时间", notNull = true)
    private Date createDate = new Date();


}

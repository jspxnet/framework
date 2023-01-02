package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.enums.ControlTypeEnumType;
import com.github.jspxnet.enums.PlatformEnumType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.BaseParam;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ControlParam extends BaseParam {

    @Param(caption = "ID")
    private long id;

    //控件名称
    @Param(caption = "名称", max = 50,required = true)
    private String name = StringUtil.empty;

    //控件中文名称
    @Param(caption = "描述名称", max = 200,required = true)
    private String caption = StringUtil.empty;

    /**
     * 它类似于传统html中的div，用于包裹各种元素内容。
     * 如果使用nvue
     * 则需注意，包裹文字应该使用text组件。
     */
    @Param(caption = "控件使用说明", max = 20000)
    private String desc = StringUtil.empty;

    @Param(caption = "控件例子", max = 20000)
    private String demo = StringUtil.empty;

    @Param(caption = "控件属性")
    private List<ControlPropertyParam> propertyList = new ArrayList<>();

    @Param(caption = "控件事件")
    private List<ControlEventParam> eventList = new ArrayList<>();

    @Param(caption = "适用平台类型",enumType = PlatformEnumType.class)
    private int platformType = PlatformEnumType.ANY.getValue();

    @Param(caption = "控件类型",enumType = ControlTypeEnumType.class)
    private int controlTyp = ControlTypeEnumType.BASE.getValue();

    @Param(caption = "用户类型")
    private int userType = 0;


    @Param(caption = "排序")
    private int sortType = 0;

    @Param(caption = "排序时间")
    private Date sortDate = new Date();

}

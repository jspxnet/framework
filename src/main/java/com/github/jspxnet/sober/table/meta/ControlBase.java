package com.github.jspxnet.sober.table.meta;

import com.github.jspxnet.enums.ControlTypeEnumType;
import com.github.jspxnet.enums.PlatformEnumType;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.enums.MappingEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 这里是基础控件信息，是控件的配置模板
 */
@Data
@Table(name = "jspx_control_base", caption = "控件模板")
public class ControlBase implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    //实体名
    @Column(caption = "名称", length = 50, notNull = true)
    private String name = StringUtil.empty;

    //控件中文名称
    @Column(caption = "描述名称", length = 200, notNull = true)
    private String caption = StringUtil.empty;

    //它类似于传统html中的div，用于包裹各种元素内容。如果使用vue则需注意，包裹文字应该使用text组件。
    @Column(caption = "控件使用说明", length = 20000)
    private String describe = StringUtil.empty;

    @Column(caption = "控件例子", length = 20000)
    private String demo = StringUtil.empty;

    //属性列表
    @Nexus(mapping = MappingEnumType.OneToMany, field = "name", targetField = "name", targetEntity = ControlProperty.class,chain = true,save = true, delete = true,update = true)
    private List<ControlProperty> propertyList = new ArrayList<>();

    //事件列表
    @Nexus(mapping = MappingEnumType.OneToMany, field = "name", targetField = "name", targetEntity = ControlEvent.class,chain = true,save = true, delete = true,update = true)
    private List<ControlEvent> eventList = new ArrayList<>();

    //用于控制权限,是否可用
    @Column(caption = "适用平台类型",enumType = PlatformEnumType.class, notNull = true)
    private int platformType = PlatformEnumType.ANY.getValue();

    @Column(caption = "控件类型",enumType = ControlTypeEnumType.class, notNull = true)
    private int controlType = ControlTypeEnumType.BASE.getValue();

    @Column(caption = "父控件ID", length = 100, notNull = true)
    private String parentId = StringUtil.empty;

    @Column(caption = "控件代码", length = 1000, notNull = true)
    private String content = StringUtil.empty;

    //用于控制权限,是否可用,放权值
    @Column(caption = "用户类型", notNull = true)
    private int userType = 0;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "创建时间", notNull = true)
    private Date createDate = new Date();
}

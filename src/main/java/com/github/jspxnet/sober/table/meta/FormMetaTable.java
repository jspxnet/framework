package com.github.jspxnet.sober.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.sober.enums.MappingEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_form_table", caption = "表单")
public class FormMetaTable extends BaseBillType {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "编码", length = 50, notNull = true)
    private String code = StringUtil.empty;

    @Column(caption = "表单名", length = 50, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "标题", length = 50, notNull = true)
    private String title = StringUtil.empty;

    //主表,只有一个，实体里边可以嵌套树结构表
    @Nexus(mapping = MappingEnumType.OneToOne, field = "name", targetField = "tableName",term = "entityType:eq[main]", targetEntity = SoberTable.class, chain = true,update = true, delete = true)
    private SoberTable entity = new SoberTable();

    @Column(caption = "布局", length = 1000)
    private String layout = StringUtil.empty;

    @Column(caption = "修改时间", notNull = true)
    protected Date lastDate = new Date();

    @Column(caption = "创建时间", notNull = true)
    protected Date createDate = new Date();

    @Column(caption = "操作人", length = 50, notNull = true)
    protected String user = StringUtil.empty;

    @Column(caption = "版本号", notNull = true)
    private float version = 0;

    @Column(caption = "修改时间", notNull = true)
    protected ControlBase controlBase = new ControlBase();

    //表单属性
    @Nexus(mapping = MappingEnumType.OneToMany, field = "id", targetField = "formId", targetEntity = ControlProperty.class,chain = true,save = true, delete = true,update = true)
    private List<ControlProperty> propertyList = new ArrayList<>();

    //插件列表
    @Nexus(mapping = MappingEnumType.OneToMany, field = "id", targetField = "formId", targetEntity = OperatePlug.class)
    private List<OperatePlug> operatePlugList = new LinkedList<>();

    //控件配置
    @Nexus(mapping = MappingEnumType.OneToMany, field = "id", targetField = "formId", targetEntity = FormControl.class)
    private List<FormControl> controlList = new LinkedList<>();


    //所有的界面功能脚本
    @Column(caption = "功能脚本", length = 20000)
    private String actionScript = StringUtil.empty;

    //所有的界面脚本放这里 比如:http://120.92.142.115:81/vform3pro/  上边导出的json
    @Column(caption = "布局脚本", length = 20000)
    private String viewScript = StringUtil.empty;
}

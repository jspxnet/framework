package com.github.jspxnet.txweb.table;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.model.container.PropertyContainer;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户部门信息表，保持用户所在岗位部门信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_dept", caption = "用户部门信息表",cache = false)
public class MemberDept extends PropertyContainer {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "用户ID", notNull = true)
    protected long uid = 0;

    //昵称，中文名称方式登录
    @Column(caption = "昵称", length = 50, dataType = "isLengthBetween(2,32)", notNull = true)
    private String name = StringUtil.empty;

    //办公 或者店铺begin
    @Column(caption = "部门ID", length = 20)
    private String departmentId = StringUtil.empty;

    @Column(caption = "部门名称", length = 100)
    private String department = StringUtil.empty;
    //办公 end

    @Column(caption = "级别", option = "正厅级;副厅级;正处级;副处级;正科级;副科级;办事员;助工", length = 60)
    private String superior = StringUtil.empty;

    //通过配置支持
    @Column(caption = "岗位", length = 50)
    private String position = StringUtil.empty;

    @Column(caption = "默认", notNull = true)
    private int defaultType = 0;


    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    public String getDepartmentCaption() {
        if (StringUtil.isNull(department) || !department.contains("/")) {
            return department;
        }
        return StringUtil.substringAfterLast(department, "/");
    }

}

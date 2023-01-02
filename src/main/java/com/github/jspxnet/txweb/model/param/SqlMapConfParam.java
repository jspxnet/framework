package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class SqlMapConfParam implements Serializable {

    @Param(caption = "ID")
    private long id;

    @Param(caption = "执行类型",enumType= ExecuteEnumType.class)
    private int executeType = 0;

    @Param(caption = "名称", min = 1,max = 200,required = true)
    private String name = StringUtil.empty;

    @Param(caption = "描述", min = 0,max = 250)
    private String caption = StringUtil.empty;

    @Param(caption = "返回类型", min = 0,max = 250)
    private String resultType = DataMap.class.getName();

    @Param(caption = "数据库类型",min = 0,max = 250)
    private String databaseType = StringUtil.empty;

    @Param(caption = "sql",min = 0,max = 4000,level = SafetyEnumType.NONE)
    private String context = StringUtil.empty;

    @Param(caption = "载入关联映射",enumType = YesNoEnumType.class)
    private int nexus = 0;

    @Param(caption = "载入关联映射",enumType = QueryModelEnumType.class)
    private int queryModel = 0;

    /**
     *  作用修改进入的参数,修复返回的类型
     */
    @Param(caption = "拦截器",min = 0, max = 1000)
    private String interceptor = StringUtil.empty;

    //当前页变量名称
    @Param(caption = "分页变量",min = 0, max =50,value = "currentPage")
    private String currentPage = "currentPage";

    @Param(caption = "分页行数",min = 0, max =50,value = "count")
    private String count = "count";

    @Param(caption = "命名空间",min = 1, max = 100)
    private String namespace = StringUtil.empty;

}

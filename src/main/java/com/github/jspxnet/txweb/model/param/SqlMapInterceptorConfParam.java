package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class SqlMapInterceptorConfParam implements Serializable {

    @Param(caption = "ID")
    private long id;

    @Param(caption = "名称", max = 250)
    private String name = StringUtil.empty;

    @Param(caption = "描述", max = 250)
    private String caption = StringUtil.empty;

    @Param(caption = "条件",max = 250,level = SafetyEnumType.NONE)
    private String term = StringUtil.empty;

    @Param(caption = "排序")
    private int sortType = 0;

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Param(caption = "开启",enumType = YesNoEnumType.class)
    private int enable = YesNoEnumType.YES.getValue();

    /**
     * 每次修改更新+1,查询只用最新的一条
     */
    @Param(caption = "版本号",value = "1")
    private int version = 1;

    @Param(caption = "命名空间", max = 50)
    public String namespace = StringUtil.empty;
}
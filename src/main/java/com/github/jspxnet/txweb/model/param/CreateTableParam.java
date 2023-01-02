package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


@Data
public class CreateTableParam implements Serializable  {

    @Param(caption = "表名称",min=1,max = 100,level = SafetyEnumType.VAR_HEIGHT)
    private String tableName;

    //中文的表明描述，可以作为关键字识别
    @Param(caption = "表名描述", min=1,max = 100,level = SafetyEnumType.HEIGHT)
    private String caption = StringUtil.empty;

    @Param(caption = "是否使用缓存")
    private boolean useCache = true;

    //如果没用配置将变成动态对象
    @Param(caption = "实体对象")
    private String entityClass;

    @Param(caption = "父表", min=1,max = 100,level = SafetyEnumType.HEIGHT)
    private String parentTableName = StringUtil.empty;

    //一般默认为id
    @Param(caption = "关键字名")
    private String primary = StringUtil.empty;

    @Param(caption = "字段表")
    private List<SoberColumn> columns = new LinkedList<>();

}

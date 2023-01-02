package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class SoberColumnParam implements Serializable {

    //同时也是关联关系
    @Param(caption = "表名称")
    private String tableName;

    @Param(caption = "字段名称",required = true)
    private String name = StringUtil.empty;

    @Param(caption = "字段类型",required = true)
    private String typeString = "string";

    @Param(caption = "类对象",min = 2,max = 200,required = true)
    private String className;

    @Param(caption = "非空")
    private boolean notNull = false;

    @Param(caption = "默认值")
    private String defaultValue = StringUtil.empty;

    @Param(caption = "描述")
    private String caption = StringUtil.empty;

    @Param(caption = "选项")
    private String option = StringUtil.empty;

    @Param(caption = "验证")
    private String dataType = StringUtil.empty;

    //和WebComponent 名称对应
    @Param(caption = "输入框")
    private String input = "text";

    @Param(caption = "长度")
    private int length = 0;

    //true 的时候导出屏蔽
    @Param(caption = "隐藏")
    private boolean hidden = false;

}

package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GenericPageParam extends PageParam {

    @Param(caption = "模型ID",required = true,level = SafetyEnumType.MIDDLE,max = 200,min = 20)
    private String modelId;
}

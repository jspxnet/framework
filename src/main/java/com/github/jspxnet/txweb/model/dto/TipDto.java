package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.enums.BoolEnumType;
import com.github.jspxnet.enums.TipStatusEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

/**
 * TipStatus
 */
@Data
public class TipDto implements Serializable {
    @Column(caption = "ID")
    private String id = StringUtil.empty;

    //控件中文名称
    @Column(caption = "提示信息")
    private String message = StringUtil.empty;


    @Column(caption = "状态")
    private int status = TipStatusEnumType.START.getValue();

    @Column(caption = "百分百")
    private float percent = 0;


    @JsonIgnore(isNull = true)
    @Column(caption = "异常数据定位")
    private String position = null;

    //当多条提示信息合并的时候用
    @Column(caption = "是否成功")
    private int success = BoolEnumType.YES.getValue();


    @Column(caption = "序号")
    private int sort = 0;
}

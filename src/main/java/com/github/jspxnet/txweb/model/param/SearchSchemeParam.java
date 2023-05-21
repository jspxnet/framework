package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.BoolEnumType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchSchemeParam implements Serializable {
    @Param(caption = "ID")
    private long id = 0;

    @Param(caption = "搜索名称")
    private String name = StringUtil.empty;

    //保存需要显示的字段
    @Param(caption = "显示字段")
    private String fields = StringUtil.empty;

    @Param(caption = "条件")
    private String term = StringUtil.empty;

    @Param(caption = "排序")
    private String orderBy = StringUtil.empty;

    //用json保存
    @Param(caption = "快捷过滤")
    private String fastFilter = StringUtil.empty;

    @Param(caption = "共享")
    private int share = BoolEnumType.NO.getValue();

    //多个用分号分隔
    @Param(caption = "共享用户")
    private String shareUser = StringUtil.empty;
}

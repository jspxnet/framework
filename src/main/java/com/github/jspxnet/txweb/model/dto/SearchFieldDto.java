package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chenyuan
 */
@Data
public class SearchFieldDto implements Serializable {
    @Column(caption = "字段名称")
    private String name = StringUtil.empty;

    @Column(caption = "描述")
    private String caption = StringUtil.empty;

    @Column(caption = "选项")
    private JSONArray optionEnum;

    @Column(caption = "隐藏")
    private boolean hidden = false;

    @Column(caption = "不允许搜索")
    private boolean searchHidden = false;
}

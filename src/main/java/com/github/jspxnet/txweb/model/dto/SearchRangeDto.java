package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;

public class SearchRangeDto implements Serializable {
    //关键字名
    private String primary = StringUtil.empty;

    @Column(caption = "描述",length = 200)
    private String caption = StringUtil.empty;
}

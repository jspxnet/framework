package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_page_code_maker_version", caption = "页面代码表历史版本")
public class PageCodeMakerVersion extends PageCodeMaker{

    @Column(caption = "修改时间", notNull = true)
    protected Date modifyDate = new Date();
}

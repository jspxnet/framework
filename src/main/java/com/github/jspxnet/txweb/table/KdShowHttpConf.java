package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;


/**
 * 配置金蝶按钮打开页面
 */
@Data
@Table(name = "jspx_kdshowhttp_conf", caption = "金蝶按钮打开页面配置")
public class KdShowHttpConf implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "名称", length = 250, notNull = true)
    private String name = StringUtil.empty;

    //外部通过这个来匹配
    @Column(caption = "表单名称", length = 250, notNull = true)
    private String fromId = StringUtil.empty;

    @Column(caption = "按钮名称", length = 250, notNull = true)
    private String buttonName = StringUtil.empty;

    @Column(caption = "对应字段", length = 250)
    private String linkField = StringUtil.empty;

    @Column(caption = "FROMID表名", length = 250, notNull = true)
    private String tableName = StringUtil.empty;


}
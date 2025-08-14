package com.github.jspxnet.txweb.table;


import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;


/**
 * 金蝶配置多选转换到普通输入框配置
 */
@Data
@Table(name = "jspx_multibase_conf", caption = "多选转换配置")
public class MultiBaseConvertConf implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "名称", length = 250, notNull = true)
    private String name = StringUtil.empty;

    //外部通过这个来匹配
    @Column(caption = "表单名称", length = 250, notNull = true)
    private String fromId = StringUtil.empty;

    @Column(caption = "多选框", length = 250, notNull = true)
    private String multiBase = StringUtil.empty;

    @Column(caption = "多选框对应表", length = 250)
    private String multiBaseTable = StringUtil.empty;

    @Column(caption = "输入框", length = 20, notNull = true)
    private String inputBox = StringUtil.empty;

}

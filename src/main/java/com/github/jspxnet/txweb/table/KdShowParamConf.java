package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
@Table(name = "jspx_kdshow_param_conf", caption = "金蝶开页窗体配置")
public class KdShowParamConf implements Serializable {
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

    @Column(caption = "要打开的对应表单ID", length = 250, notNull = true)
    private String openFromId = StringUtil.empty;

    @Column(caption = "打开类型",option = "Dynamic:动态表单;Bill:单据;List:列表;SysReport:简单账表;SQLReport:SQL账表;MoveReport:系统标准账表",length = 50, notNull = true)
    private String openType = StringUtil.empty;


    @Column(caption = "过滤条件", length = 250, notNull = true)
    private String filter = StringUtil.empty;

}

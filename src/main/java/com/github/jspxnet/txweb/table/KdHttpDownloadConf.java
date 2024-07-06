package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
@Table(name = "jspx_kd_download_conf", caption = "金蝶按钮打配置")
public class KdHttpDownloadConf implements Serializable {
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

    @Column(caption = "多选框对应表", length = 250, notNull = true)
    private String url = StringUtil.empty;

    @Column(caption = "参数", length = 4000)
    private String postData = StringUtil.empty;

}
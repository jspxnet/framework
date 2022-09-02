package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by chenyuan on 2016/1/14.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_download_file_client", caption = "附件下载记录")
public class DownloadFileClient extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "附件ID", notNull = true)
    private long fid = 0;

    @Column(caption = "名称", dataType = "isLengthBetween(1,250)", length = 250, notNull = true)
    private String url = StringUtil.empty;

    @Column(caption = "浏览器", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String browser = StringUtil.empty;

    @Column(caption = "操作系统", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String system = StringUtil.empty;

    @Column(caption = "位置", dataType = "isLengthBetween(1,100)", length = 100)
    private String location = StringUtil.empty;

    @Column(caption = "网络类型", dataType = "isLengthBetween(1,20)", length = 20)
    private String netType = StringUtil.empty;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

}

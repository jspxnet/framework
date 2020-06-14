package com.github.jspxnet.txweb.vo;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(caption = "附件", create = false, cache = false)
public class AttachmentsVo implements Serializable {

    @Column(caption = "ID")
    private long id = 0;

    //用来判断是否已经存在，也是唯一ID
    @Column(caption = "Hash")
    private String hash = StringUtil.empty;

    @Column(caption = "标题")
    private String title = StringUtil.empty;

    @Column(caption = "关键字")
    private String tags = StringUtil.empty;

    @Column(caption = "属性")
    private String attributes = StringUtil.empty;

    @Column(caption = "描述")
    private String content = StringUtil.empty;

    //有可能是url ,本地路径是相对路径
    @Column(caption = "文件路径")
    private String fileName = StringUtil.empty;

    @Column(caption = "类型")
    private String fileType = StringUtil.empty;

    @Column(caption = "大小")
    private long fileSize = 0;

    @Column(caption = "下载次数")
    private int downTimes = 0;

    @Column(caption = "排序日期")
    private Date sortDate = new Date();

    @Column(caption = "关联ID")
    private long pid = 0;
}

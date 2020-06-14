package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * Created by chenyuan on 14-6-27.
 * com.github.jspxnet.txweb.table.PhotoPattern
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_photo_pattern", caption = "图片样板")
public class PhotoPattern extends OperateTable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "标题", dataType = "isLengthBetween(2,100)", length = 100, notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "拼音", dataType = "isLengthBetween(2,100)", length = 100)
    private String spelling = StringUtil.empty;

    @Column(caption = "图片数量", notNull = true)
    private int photoNumber = 1;

    @Column(caption = "缩图", length = 200, dataType = "isLengthBetween(1,200)", notNull = true)
    private String button = StringUtil.empty;

    @Column(caption = "内容", length = 8000, dataType = "isLengthBetween(1,99999)", notNull = true)
    private String content = StringUtil.empty;

    /**
     * img*=300x300
     * img1=300x300
     * img1=500x500
     */
    @Column(caption = "切图", length = 250, dataType = "isLengthBetween(0,250)", notNull = true)
    private String cutScript = StringUtil.empty;

    @Column(caption = "是否审核", length = 2, notNull = true)
    private int auditingType = 0;

    @Column(caption = "审核时间", notNull = true)
    private Date auditingDate = new Date();

    @Column(caption = "排序", option = "0:默认;2:固顶;4:置顶;8:结对置顶;-1:下沉", notNull = true)
    private int sortType = 0;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "域名", length = 50, notNull = true)
    private String namespace = StringUtil.empty;

}

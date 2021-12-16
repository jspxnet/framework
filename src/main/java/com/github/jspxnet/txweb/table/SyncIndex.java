package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/12/4 12:30
 * description: 同步的时候记录key的表,确保识别的被删除的数据
 **/
@Data
@Table(name = "jspx_sync_index", caption = "同步关键字表")
public class SyncIndex {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "", length = 50, dataType = "isLengthBetween(0,50)", notNull = true)
    private String keyValue = StringUtil.empty;

    @Column(caption = "", length = 100, dataType = "isLengthBetween(0,50)", notNull = true)
    private String className = StringUtil.empty;

}

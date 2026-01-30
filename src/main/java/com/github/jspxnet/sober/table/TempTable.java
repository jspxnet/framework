package com.github.jspxnet.sober.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 这个表是保存临时的表信息，到一定时间后删除
 */
@Data
@Table(name = "jspx_temp_table", caption = "临时表")
public class TempTable implements Serializable {

    @Id
    @Column(caption = "id",notNull = true)
    private long id;

    @Column(caption = "表名称",length = 100,notNull = true)
    private String tableName;

    @Column(caption = "保留小时", notNull = true)
    private int hour = 3;

    @Column(caption = "创建时间", notNull = true)
    private Date createDate = new Date();

}

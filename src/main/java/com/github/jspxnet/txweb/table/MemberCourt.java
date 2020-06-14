package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_court", caption = "用户小区信息")
public class MemberCourt extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "用户ID", notNull = true)
    protected long uid = 0;

    //昵称，中文名称方式登录
    @Column(caption = "昵称", length = 50, dataType = "isLengthBetween(2,32)", notNull = true)
    private String name = StringUtil.empty;

    @Column(caption = "小区名称", length = 50, dataType = "isLengthBetween(2,32)", notNull = true)
    private String court = StringUtil.empty;

    @Column(caption = "组团", length = 50, dataType = "isLengthBetween(2,32)", notNull = true)
    private String cluster = StringUtil.empty;

    @Column(caption = "栋", length = 30, dataType = "isLengthBetween(2,32)", notNull = true)
    private String unit = StringUtil.empty;

    @Column(caption = "楼层", length = 10, dataType = "isLengthBetween(2,10)", notNull = true)
    private String build = StringUtil.empty;

    @Column(caption = "号", length = 10, dataType = "isLengthBetween(2,10)", notNull = true)
    private String no = StringUtil.empty;

    //标识当前所在
    @Column(caption = "默认", notNull = true)
    private int defaultType = 0;

}

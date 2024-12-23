package com.github.jspxnet.txweb.model.vo;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by chenyuan on 2016/09/05         .
 * com.github.jspxnet.txweb.transfer.MemberStatVo
 */
@Data
@Table(caption = "证书比例视图", create = false,cache = false)
public class MemberStatVo implements Serializable {
    @Column(caption = "用户ID", notNull = true)
    private long uid;

    @Column(caption = "统计数据", notNull = true)
    private int num = 0;

}

package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2020/12/6 21:29
 * description: 分享连接
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_promote_link", caption = "分销连接")
public class PromoteLink extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "分享类型",option = "0:用户;1:公众号;2:小程序;3:URL页面;4:商品;5:活动;6:新闻;7:bolg;8:vlog")
    private int type = 0;

    @Column(caption = "入口页面", length = 300)
    private String url;

    @Column(caption = "图片", length = 300)
    private String logo;

    @Column(caption = "对象Id", length = 64)
    private String objectId;

    @Column(caption = "内容", length = 300)
    private String content ;

    @Column(caption = "来源标识", length = 60)
    private String origin;

    @Column(caption = "机构ID", length = 65)
    private String organizeId;

    @Column(caption = "引入数量")
    private int attention = 0;

}

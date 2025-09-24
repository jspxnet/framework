package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
@Table(name = "jspx_kd_api_conf", caption = "金蝶API事件")
public class KdHttpApiConf implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "名称", length = 250, notNull = true)
    private String name = StringUtil.empty;

    //外部通过这个来匹配
    @Column(caption = "表单ID", length = 100, notNull = true)
    private String fromId = StringUtil.empty;

    @Column(caption = "按钮名称", length = 250, notNull = true)
    private String buttonName = StringUtil.empty;

    //url地址,如果有多个使用@来
    @Column(caption = "url地址", length = 250, notNull = true)
    private String url = StringUtil.empty;

    @Column(caption = "参数", length = 4000)
    private String postData = StringUtil.empty;

    //如果有多个使用多个参数用 ; 来风格
    @Column(caption = "变量", length = 200)
    private String param = StringUtil.empty;


    //SCRK00042651 PARAM
    //101	生产入库单	PRD_INSTOCK	SAVE	http://127.0.0.1:8095/hykjgx/stkmis/savenotice.jwc	{id:"${Id}","key":"fffc257c504507942c82a3e34832150"}	Id
}
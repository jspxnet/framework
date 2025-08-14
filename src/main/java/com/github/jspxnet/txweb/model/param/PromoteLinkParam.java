package com.github.jspxnet.txweb.model.param;


import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/6 22:09
 * description: jspbox
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PromoteLinkParam extends BaseParam {

    @Param(caption = "分享类型",required = true)
    private int type = 0;

    @Param(caption = "入口页面",required = true)
    private String url;

    @Param(caption = "图片",required = true)
    private String logo;

    @Param(caption = "对象Id")
    private String objectId;

    @Param(caption = "内容")
    private String content ;

    @Param(caption = "来源标识")
    private String origin;

    @Param(caption = "机构ID")
    private String organizeId;

    @Param(caption = "platformNo")
    private String platformNo;
}

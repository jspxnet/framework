package com.github.jspxnet.txweb.apidoc;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.txweb.annotation.Describe;
import lombok.Data;

import java.io.Serializable;

@Data
@Table(name = "api_action_vo", caption = "API文档索引对象", create = false)
public class ApiAction implements Serializable {
    //是用类名md5生成
    @Column(caption = "ID")
    private String id = "";

    @Column(caption = "配置方法")
    private String confMethod = "";

    @Column(caption = "namespace")
    private String namespace = "";

    @Column(caption = "URL")
    private String url = "";

    @Column(caption = "标题")
    private String title = "";

    @Column(caption = "类对象")
    private String className = "";



}

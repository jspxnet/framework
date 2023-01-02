package com.github.jspxnet.network.consul;

import com.github.jspxnet.sober.annotation.Column;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/19 17:47
 * description: 简单的注册类型
 **/
@Data
public class DiscoveryService implements Serializable {

    @Column(caption = "服务id")
    private String id;
    @Column(caption = "服务名称")
    private String name;
    @Column(caption = "tags")
    private String tags;
    @Column(caption = "ip地址")
    private String address;
    @Column(caption = "端口")
    private int port;
    @Column(caption = "健康监控路径")
    private String path;
    @Column(caption = "script")
    private String script;
    @Column(caption = "shell")
    private String shell;
    @Column(caption = "shell")
    private String interval = "10s";
}

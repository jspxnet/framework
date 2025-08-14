package com.github.jspxnet.network.consul;

import com.github.jspxnet.sober.annotation.Column;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/1/5 13:07
 * description: Consul配置
 **/
@Data
public class ConsulConfig implements Serializable {
    @Column(caption = "Consul 服务器IP")
    private String ip = "127.0.0.1";
    @Column(caption = "Consul 服务器端口")
    private int port = 8500;

    @Column(caption = "是否是用ssl")
    private boolean ssl = false;

    @Column(caption = "证书类型",option = "JKS, JCEKS, PKCS12, PKCS11, DKS")
    private String certType;

    @Column(caption = "证书路径包含文件名")
    private String certificatePath;
    @Column(caption = "证书密码")
    private String certificatePassword;
    @Column(caption = "证书key容器路径")
    private String keyStorePath;
    @Column(caption = "证书key容器密码")
    private String keyStorePassword;
}

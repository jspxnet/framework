package com.github.jspxnet.component.k3cloud;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/30 0:45
 * description: thermo-model
 **/
@Data
public class K3TableConf implements Serializable {
    private String key;
    private String tableId;
    private String caption;
    private String className;
    private String content;
}

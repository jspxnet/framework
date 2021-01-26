package com.github.jspxnet.util;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/1/26 17:26
 * @description: jspbox
 **/
@Data
public class PortInfo implements Serializable {
    private String protocol;
    private String scheme;
    private int port;
}

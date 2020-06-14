package com.github.jspxnet.network.rpc.model.transfer;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 21:26
 * description: jspbox
 **/
@Data
public class IocResponse  implements Serializable {


    private Throwable error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

}

package com.github.jspxnet.sioc.rpc;

import com.github.jspxnet.sioc.annotation.RpcClient;
import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 23:20
 * description: jspbox
 **/
public interface RpcClientProxy extends Serializable {

    Class<?> getTarge();

    RpcClient getRpcClient();

}

package com.github.jspxnet.sioc.rpc;

import com.github.jspxnet.sioc.annotation.RpcClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 23:20
 * description: jspbox
 **/
public interface RpcClientProxy extends Serializable {

    void setRequest(final HttpServletRequest request);

    void setResponse(final HttpServletResponse response);

    Class<?> getTarge();

    RpcClient getRpcClient();

}

package com.github.jspxnet.comm;

import java.util.Map;

/**
 * Created by chenyuan on 2015/8/25.
 */
public interface Router {

    void setGatewayList(Map<String, SerialComm> gatewayList);

    SerialComm getRouter();

}

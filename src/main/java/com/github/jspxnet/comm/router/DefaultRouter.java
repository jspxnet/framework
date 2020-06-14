package com.github.jspxnet.comm.router;


import com.github.jspxnet.comm.Router;
import com.github.jspxnet.comm.SerialComm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyuan on 2015/8/25.
 */
public class DefaultRouter implements Router {

    private static Map<String, SerialComm> gatewayList = Collections.synchronizedMap(new HashMap<>());
    private int current = 0;

    @Override
    public void setGatewayList(Map<String, SerialComm> gatewayList) {
        DefaultRouter.gatewayList = gatewayList;
    }

    /**
     * @return 得到路由计算
     */
    @Override
    public SerialComm getRouter() {
        if (gatewayList.size() == 1) {
            return gatewayList.get(0);
        }
        if (current > gatewayList.size()) {
            current = 0;
        }
        return gatewayList.get(current);
    }
}

package com.github.jspxnet.comm.router;


import com.github.jspxnet.comm.Router;
import com.github.jspxnet.comm.SerialComm;
import java.util.*;

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
            for (String key:gatewayList.keySet())
            {
                return gatewayList.get(key);
            }
            return gatewayList.values().toArray(new SerialComm[0])[0];
        }
        if (current > gatewayList.size()) {
            current = 0;
        }
        int i=0;
        for (String key:gatewayList.keySet())
        {
            if (i==current)
            {
                return gatewayList.get(key);
            }
            i++;
        }
        return gatewayList.values().toArray(new SerialComm[0])[current];
    }
}

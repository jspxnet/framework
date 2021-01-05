package com.github.jspxnet.network.consul;

import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import java.util.List;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/1/5 12:37
 * @description: jspbox
 **/
public interface ConsulService {

    void register(String serviceId,String serviceName,int port);

    void register(NewService newService);

    List<HealthService> getHealthServices(String serviceName);

    boolean put(String key, String value);

    List<String> getKeysOnly(String keyPrefix);

    String get(String key);

    List<String> getStatusPeers();

    String getStatusLeader();
}

package com.github.jspxnet.network.rpc.env;

import com.ecwid.consul.v1.health.model.HealthService;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.consul.ConsulService;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/19 20:01
 * description: 查询分布式调用适配器
 **/
@Slf4j
public  class DiscoveryServiceAddress {
    private DiscoveryServiceAddress()
    {

    }

    private static ConsulService consulService;
    private static String serviceDiscoverMode;
    public static InetSocketAddress getSocketAddress(String serviceName)
    {
        if (serviceDiscoverMode==null)
        {
            serviceDiscoverMode =EnvFactory.getEnvironmentTemplate().getString(Environment.serviceDiscoverMode);
            if (StringUtil.isNull(serviceDiscoverMode))
            {
                serviceDiscoverMode = Environment.defaultValue;
            }
        }

        if (Environment.consul.equalsIgnoreCase(serviceDiscoverMode))
        {
            if (consulService==null)
            {
                consulService = EnvFactory.getBeanFactory().getBean(ConsulService.class);
            }
            HealthService.Service service  = consulService.getRunServices(Environment.defaultValue);
            if (service==null)
            {
                service  = consulService.getRunServices(serviceName);
            }
            if (service==null)
            {
                log.error("consul 当前没有可用的访问");
                return null;
            }
            return new InetSocketAddress(service.getAddress(),service.getPort());
        }

        return MasterSocketAddress.getInstance().getSocketAddress(serviceName);
    }
}

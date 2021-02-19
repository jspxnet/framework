package com.github.jspxnet.network.consul.impl;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.consul.ConsulDiscoveryService;
import com.github.jspxnet.network.consul.ConsulService;
import com.github.jspxnet.network.consul.DiscoveryService;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.utils.IpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/2/19 17:30
 * @description: Consul启动注册服务
 *
 **/

@Slf4j
public class ConsulDiscoveryServiceImpl implements ConsulDiscoveryService {
    @Ref
    private ConsulService consulService;

    final private static List<String> REGISTER_ID_LIST = new ArrayList<>();

    private String config;
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * 注册服务
     */
    @Init
    public void register() throws IOException {
        if (StringUtil.isNull(config))
        {
            return;
        }
        File file = EnvFactory.getFile(config);
        if (file==null)
        {
            log.error("Consul Discovery register not found config file:{}",config);
            return;
        }
        String str = IoUtil.autoReadText(file);
        if (StringUtil.isNull(str)&&!StringUtil.isJsonObject(str))
        {
            log.error("Consul Discovery register not found config error:{}",str);
            return;
        }
        JSONObject json = new JSONObject(str);

        JSONArray array = json.getJSONArray("service");
        if (ObjectUtil.isEmpty(array))
        {
            log.error("Consul Discovery register not found config service:{}",str);
            return;
        }
        List<DiscoveryService> list = array.parseObject(DiscoveryService.class);
        if (ObjectUtil.isEmpty(list))
        {
            log.error("Consul Discovery register not found config error:{}",str);
            return;
        }
        for (DiscoveryService service:list)
        {
            if (StringUtil.isNull(service.getAddress()))
            {
                service.setAddress(IpUtil.publicIP().getHostAddress());
            }
            if (StringUtil.isNull(service.getId()))
            {
                String id = StringUtil.replace(service.getAddress(),".","") +"-"+ service.getPort() + "-" + service.getName();
                id = StringUtil.replace(id,"//","");
                service.setId(id);
            }
            if (!REGISTER_ID_LIST.contains(service.getId()))
            {
                REGISTER_ID_LIST.add(service.getId());
                log.info("Consul Discovery register:{}",ObjectUtil.toString(service));
                consulService.register(service);
            }
        }
    }

    /**
     * 卸载服务
     */
    @Destroy
    public void deregister()
    {
        for (String id:REGISTER_ID_LIST)
        {
            if (StringUtil.isNull(id))
            {
                continue;
            }
            consulService.deregister(id);
        }
    }

}

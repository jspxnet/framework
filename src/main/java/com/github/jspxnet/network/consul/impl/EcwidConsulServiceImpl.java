package com.github.jspxnet.network.consul.impl;


import com.ecwid.consul.transport.TLSConfig;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.github.jspxnet.network.consul.ConsulConfig;
import com.github.jspxnet.network.consul.ConsulService;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 12:37
 * description: Ecwid Consul 客户端,支持ssl证书
 *
 **/
@Slf4j
public class EcwidConsulServiceImpl implements ConsulService {

    private ConsulClient client;
    public EcwidConsulServiceImpl() {

    }

    private ConsulConfig consulConfig;
    public void setConsulConfig(ConsulConfig consulConfig) {
        this.consulConfig = consulConfig;
    }

    @Init
    public void createClient()
    {

        log.info("consulConfig={}", ObjectUtil.toString(consulConfig));

        if (!consulConfig.isSsl())
        {
            client = new ConsulClient(consulConfig.getIp(),consulConfig.getPort());
        } else
        {
            AssertException.isNull(consulConfig.getCertType(),"证书类型不能为空");
            TLSConfig tlsConfig = new TLSConfig(TLSConfig.KeyStoreInstanceType.valueOf(consulConfig.getCertType().toUpperCase()),
                    consulConfig.getCertificatePath(),consulConfig.getCertificatePassword(),
                    consulConfig.getKeyStorePath(),consulConfig.getKeyStorePassword());
            client = new ConsulClient(consulConfig.getIp(),consulConfig.getPort(), tlsConfig);
        }
    }

    @Override
    public void register(String serviceId,String serviceName,int port) {
        // register new service
        NewService newService = new NewService();
        newService.setId(serviceId);
        newService.setName(serviceName);
        newService.setTags(Arrays.asList("EU-West", "EU-East"));
        newService.setPort(port);
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp("http://127.0.0.1:" + port + "/health");
        serviceCheck.setInterval("10s");
        newService.setCheck(serviceCheck);
        client.agentServiceRegister(newService);
    }

    @Override
    public void register(NewService newService) {
        // register new service
        /*  NewService newService = new NewService();
        newService.setId(serviceId);
        newService.setName(serviceName);
        newService.setTags(Arrays.asList("EU-West", "EU-East"));
        newService.setPort(8080);
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp("http://127.0.0.1:8080/health");
        serviceCheck.setInterval("10s");
        newService.setCheck(serviceCheck);*/
        client.agentServiceRegister(newService);
    }

    @Override
    public List<HealthService> getHealthServices(String serviceName) {
        HealthServicesRequest consulRequest = HealthServicesRequest.newBuilder().build();
        Response<List<HealthService>> healthyServices = client.getHealthServices(serviceName,consulRequest);
        return healthyServices.getValue();
    }

    @Override
    public boolean put(String key, String value) {
        Response<Boolean> booleanResponse = client.setKVValue(key, value);
        return booleanResponse.getValue();
    }


    @Override
    public List<String> getKeysOnly(String keyPrefix) {
        Response<List<String>>  response = client.getKVKeysOnly(keyPrefix);
        return response.getValue();
    }


    @Override
    public String get(String key) {
        Response<GetValue> getValueResponse = client.getKVValue(key);
        return getValueResponse.getValue().getDecodedValue();
    }

    @Override
    public List<String> getStatusPeers() {
        Response<List<String>> listResponse = client.getStatusPeers();
        return listResponse.getValue();
    }

    @Override
    public String getStatusLeader() {
        Response<String> stringResponse = client.getStatusLeader();
        return stringResponse.getValue();
    }
}

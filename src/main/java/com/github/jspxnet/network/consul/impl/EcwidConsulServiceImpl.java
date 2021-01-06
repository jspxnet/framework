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
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 12:37
 * description: Ecwid Consul 客户端,支持ssl证书
 **/
@Slf4j
public class EcwidConsulServiceImpl implements ConsulService {

    private ConsulClient client;
    public EcwidConsulServiceImpl() {

    }

    @Ref
    private ConsulConfig consulConfig;

    /**
     * 初始化
     */
    @Init
    protected void createClient()
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

    /**
     *
     * @param serviceId 服务id
     * @param serviceName 服务名称
     * @param tags tag
     * @param ip ip地址
     * @param port  端口
     * @param path 访问路径
     */
    @Override
    public void register(String serviceId,String serviceName,String tags,String ip,int port,String path) {
        // register new service
        NewService newService = new NewService();
        newService.setId(serviceId);
        newService.setName(serviceName);
        newService.setTags(Arrays.asList(StringUtil.split(tags," ")));
        newService.setPort(port);
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp("http://"+ip+":" + port + "/" + path);
        serviceCheck.setTlsSkipVerify(false);
        serviceCheck.setInterval("10s");
        newService.setCheck(serviceCheck);
        client.agentServiceRegister(newService);
    }

    /**
     *
     * @param newService 注册参数
     */
    @Override
    public void register(NewService newService) {
        client.agentServiceRegister(newService);
    }

    /**
     *  服务停止时取消注册
     * @param id 服务id
     */
    @Override
    public void deregister(String id)
    {
        client.agentServiceDeregister(id);//服务停止时取消注册
    }


    /**
     *
     * @param serviceName 服务名
     * @return 得到健康的服务组
     */
    @Override
    public List<HealthService> getHealthServices(String serviceName) {
        HealthServicesRequest consulRequest = HealthServicesRequest.newBuilder().build();
        Response<List<HealthService>> healthyServices = client.getHealthServices(serviceName,consulRequest);
        return healthyServices.getValue();
    }

    /**
     *
     * @param key key
     * @param value 值
     * @return 放入是否成功
     */
    @Override
    public boolean put(String key, String value) {
        Response<Boolean> booleanResponse = client.setKVValue(key, value);
        return booleanResponse.getValue();
    }


    /**
     *
     * @param keyPrefix 关键字
     * @return 查询值
     */
    @Override
    public List<String> getKeysOnly(String keyPrefix) {
        Response<List<String>>  response = client.getKVKeysOnly(keyPrefix);
        return response.getValue();
    }


    /**
     *
     * @param key key
     * @return  值
     */
    @Override
    public String get(String key) {
        Response<GetValue> getValueResponse = client.getKVValue(key);
        return getValueResponse.getValue().getDecodedValue();
    }

    /**
     *
     * @return 状态
     */
    @Override
    public List<String> getStatusPeers() {
        Response<List<String>> listResponse = client.getStatusPeers();
        return listResponse.getValue();
    }

    /**
     *
     * @return  竞选状态
     */
    @Override
    public String getStatusLeader() {
        Response<String> stringResponse = client.getStatusLeader();
        return stringResponse.getValue();
    }
}

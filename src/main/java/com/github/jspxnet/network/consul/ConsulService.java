package com.github.jspxnet.network.consul;

import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/5 12:37
 * description: jspbox
 **/
public interface ConsulService {
    /**
     *
     * @param serviceId 服务id
     * @param serviceName 服务名称
     * @param tags tag
     * @param ip ip地址
     * @param port  端口
     * @param path 访问路径
     */
    void register(String serviceId,String serviceName,String tags,String ip,int port,String path);
    /**
     *
     * @param newService 注册参数
     */
    void register(NewService newService);
    /**
     *  服务停止时取消注册
     * @param id 服务id
     */
    void deregister(String id);
    /**
     *
     * @param serviceName 服务名
     * @return 得到健康的服务组
     */
    List<HealthService> getHealthServices(String serviceName);
    /**
     *
     * @param key key
     * @param value 值
     * @return 放入是否成功
     */
    boolean put(String key, String value);
    /**
     *
     * @param keyPrefix 关键字
     * @return 查询值
     */
    List<String> getKeysOnly(String keyPrefix);
    /**
     *
     * @param key key
     * @return  值
     */
    String get(String key);

    /**
     *
     * @return 状态
     */
    List<String> getStatusPeers();
    /**
     *
     * @return  竞选状态
     */
    String getStatusLeader();
}

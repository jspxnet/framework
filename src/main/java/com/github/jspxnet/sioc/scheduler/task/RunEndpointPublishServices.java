package com.github.jspxnet.sioc.scheduler.task;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;


import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyuan on 2015-7-8.
 * 开启单独端口发布soap
 * <p>
 * [bean class="com.github.jspxnet.sioc.scheduler.task.RunEndpointPublishServices" singleton="false"]
 * [string name="host"]http://xxxxxxxxxxxxxxxxxxxxxxx[&gt;]/string]
 * [/bean]
 * 这种方式不能公用端口host
 * 需要，tomcat的扩展包支持
 * http://mirrors.hust.edu.cn/apache/tomcat/tomcat-8/v8.5.35/bin/extras/catalina-ws.jar
 */
@Slf4j
@Bean
public class RunEndpointPublishServices {
    private static boolean isRun = false;

    private Map<String, String> serviceMap = new HashMap<String, String>();

    public void setServiceMap(Map<String, String> serviceMap) {
        this.serviceMap = serviceMap;
    }

    private String host;

    public void setHost(String host) {
        this.host = host;
    }

    @Scheduled(once = true)
    public String run() {
        if (isRun) {
            return Environment.none;
        }
        isRun = true;
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        for (String key : serviceMap.keySet()) {
            String service = serviceMap.get(key);
            log.info("WebService open " + host + "/" + key);
            Endpoint.publish(host + "/" + key, beanFactory.getBean(service));
        }
        return Environment.SUCCESS;
    }

}

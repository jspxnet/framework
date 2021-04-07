package com.github.jspxnet.boot.conf;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.network.vcs.VcsClient;
import com.github.jspxnet.network.vcs.VcsFactory;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class VcsBootConfig {
    public void bind(Properties properties)
    {
        JspxConfiguration jspxConfiguration = EnvFactory.getBaseConfiguration();
        try {
            log.info("配置检测到是用vcs分布式配置");

            String url = StringUtil.trim(properties.getProperty(Environment.VCS_URL));
            String localPath = StringUtil.trim(properties.getProperty(Environment.VCS_LOCAL_PATH));
            String name = StringUtil.trim(properties.getProperty(Environment.VCS_USER_NAME));
            String password = StringUtil.trim(properties.getProperty(Environment.VCS_USER_PASSWORD));
            Map<String, Object> valueMap =  new HashMap<String, Object>((Map) properties);
            valueMap.put(Environment.defaultPath, jspxConfiguration.getDefaultPath());
            localPath = EnvFactory.getPlaceholder().processTemplate(valueMap, localPath);
            VcsClient vcsClient = VcsFactory.createClient(url, localPath, name, password);
            if (vcsClient != null) {
                String vcsVersion = vcsClient.download();
                log.info("下载vcs配置版本：{}", vcsVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("下载vcs配置发生错误", e);
        } finally {
            jspxConfiguration.setDefaultConfigFile(Environment.jspx_properties_file);
        }
    }
}

package com.github.jspxnet.boot.conf;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
import com.ctrip.framework.apollo.core.dto.ServiceDTO;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.internals.ConfigRepository;
import com.ctrip.framework.apollo.internals.RemoteConfigRepository;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.enums.BootConfigEnumType;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class AppolloBootConfig {

    public void bind(Map<String,String> properties)
    {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
<<<<<<< HEAD
        String namespace = properties.getProperty(Environment.APOLLO_BOOTSTRAP_NAMESPACES,"application");
        System.setProperty(Environment.APOLLO_ENV,properties.getProperty(Environment.APOLLO_ENV));
        System.setProperty(Environment.APOLLO_APP_ID,properties.getProperty(Environment.APOLLO_APP_ID));
        System.setProperty(Environment.APOLLO_BOOTSTRAP_ENABLED,properties.getProperty(Environment.APOLLO_BOOTSTRAP_ENABLED));
        System.setProperty(Environment.APOLLO_BOOTSTRAP_NAMESPACES,namespace);
        System.setProperty(Environment.APOLLO_BOOTSTRAP_EAGERLOAD_ENABLED,properties.getProperty(Environment.APOLLO_BOOTSTRAP_EAGERLOAD_ENABLED));
        System.setProperty(Environment.APOLLO_META,properties.getProperty(Environment.APOLLO_META));
        envTemplate.put(Environment.BOOT_CONF_MODE, BootConfigEnumType.APPOLLO.getName());
        log.debug("Apollo bootstrap namespaces: {},env: {}", namespace,properties.getProperty(Environment.APOLLO_ENV));
=======
        String namespace = properties.getOrDefault(Environment.APOLLO_BOOTSTRAP_NAMESPACES,"application");
        System.setProperty(Environment.APOLLO_ENV,properties.get(Environment.APOLLO_ENV));
        System.setProperty(Environment.APOLLO_APP_ID,properties.get(Environment.APOLLO_APP_ID));
        System.setProperty(Environment.APOLLO_BOOTSTRAP_ENABLED,properties.get(Environment.APOLLO_BOOTSTRAP_ENABLED));
        System.setProperty(Environment.APOLLO_BOOTSTRAP_NAMESPACES,namespace);
        System.setProperty(Environment.APOLLO_BOOTSTRAP_EAGERLOAD_ENABLED,properties.get(Environment.APOLLO_BOOTSTRAP_EAGERLOAD_ENABLED));
        System.setProperty(Environment.APOLLO_META,properties.get(Environment.APOLLO_META));
        envTemplate.put(Environment.BOOT_CONF_MODE, BootConfigEnumType.APPOLLO.getName());
        log.debug("Apollo bootstrap namespaces: {},env: {}", namespace,properties.get(Environment.APOLLO_ENV));
>>>>>>> dev
        //"application" apollo.ip

        Config config = ConfigService.getConfig(namespace);
        Set<String> names = config.getPropertyNames();
        for (String key:names)
        {
            envTemplate.put(key,config.getProperty(key, StringUtil.empty));
        }
        //ConfigUtil
        config.addChangeListener(changeEvent -> {
            log.info("Changes for namespace {}", changeEvent.getNamespace());
            for (String key : changeEvent.changedKeys()) {
                ConfigChange change = changeEvent.getChange(key);
                log.info("Change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
                        change.getPropertyName(), change.getOldValue(), change.getNewValue(),
                        change.getChangeType());
                if (PropertyChangeType.ADDED.equals(change.getChangeType())||PropertyChangeType.MODIFIED.equals(change.getChangeType()))
                {
                    envTemplate.put(change.getPropertyName(),change.getNewValue());
                }
                if (PropertyChangeType.DELETED.equals(change.getChangeType()))
                {
                    envTemplate.deleteEnv(change.getPropertyName());
                }
            }
        });
    }
}

package com.github.jspxnet.boot.conf;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.enums.BootConfigEnumType;
import com.github.jspxnet.utils.StringUtil;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class AppolloBootConfig {

    public void bind(Properties properties)
    {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();

        System.setProperty(Environment.APOLLO_ENV,properties.getProperty(Environment.APOLLO_ENV));
        System.setProperty(Environment.APOLLO_APP_ID,properties.getProperty(Environment.APOLLO_APP_ID));
        System.setProperty(Environment.APOLLO_BOOTSTRAP_ENABLED,properties.getProperty(Environment.APOLLO_BOOTSTRAP_ENABLED));
        System.setProperty(Environment.APOLLO_BOOTSTRAP_NAMESPACES,properties.getProperty(Environment.APOLLO_BOOTSTRAP_NAMESPACES));
        System.setProperty(Environment.APOLLO_META,properties.getProperty(Environment.APOLLO_META));
        envTemplate.put(Environment.BOOT_CONF_MODE, BootConfigEnumType.APPOLLO.getName());


        Config config = ConfigService.getAppConfig();
        Set<String> names = config.getPropertyNames();
        for (String key:names)
        {
            envTemplate.put(key,config.getProperty(key, StringUtil.empty));
        }

        ConfigChangeListener changeListener = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
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
            }
        };
        config.addChangeListener(changeListener);
    }
}

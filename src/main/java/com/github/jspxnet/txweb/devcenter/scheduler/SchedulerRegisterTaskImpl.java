package com.github.jspxnet.txweb.devcenter.scheduler;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.cron4j.Scheduler;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.devcenter.service.SchedulerTaskService;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.SchedulerControl;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Bean(namespace = Environment.DEV_CENTER, singleton = true)
@Slf4j
public class SchedulerRegisterTaskImpl implements SchedulerRegisterTask {


    private GenericDAO genericDAO;

    @Ref
    public void setGenericDAO(GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
        //载入默认数据源begin
        if (genericDAO.getSoberFactory() == null) {
            String soberFactoryName = EnvFactory.getEnvironmentTemplate().getString("soberFactory", "jspxSoberFactory");
            BeanFactory beanFactory = EnvFactory.getBeanFactory();
            SoberFactory soberFactory = (SoberFactory) beanFactory.getBean(soberFactoryName);
            genericDAO.setSoberFactory(soberFactory);
        }
        //载入默认数据源end
    }


    private SchedulerTaskService schedulerTaskService;

    /**
     * 载入配置中的定时设置
     */
    @Scheduled(name = "载入配置定时任务设置", once = true, force = true)
    @Override
    public void loadSchedulerControlConfig() {

        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        if (!environmentTemplate.getBoolean(Environment.USE_SCHEDULER_REGISTER)) {
            return;
        }

        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        List<SchedulerDto> list = schedulerTaskManager.getList(null, 1, 1000);
        for (SchedulerDto dto : list) {
            SchedulerControl control = BeanUtil.copy(dto, SchedulerControl.class);
            SchedulerControl oldControl = genericDAO.get(SchedulerControl.class, control.getGuid());
            if (oldControl == null) {
                continue;
            }
            try {

                if (!StringUtil.isNull(control.getGuid())) {
                    String newPattern = control.getPattern();
                    String oldPattern = oldControl.getPattern();
                    if (newPattern != null && !newPattern.equals(oldPattern)) {
                        Scheduler scheduler = schedulerTaskManager.get(control.getGuid());
                        scheduler.reschedule(control.getGuid(), oldControl.getPattern());
                    }
                    if (oldControl.getStarted() == YesNoEnumType.NO.getValue()) {
                        Scheduler scheduler = schedulerTaskManager.get(control.getGuid());
                        if (scheduler.isStarted()) {
                            scheduler.stop();
                        }
                    }
                }
            } catch (Exception e) {
                log.error("定时任务载入上次配置异常:{},{}", ObjectUtil.toString(control), e.getMessage());
            }
        }
        //注册服务
        run();
    }

    /**
     * 注册服务
     */
    private void registerClient() {
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        if (!environmentTemplate.getBoolean(Environment.USE_SCHEDULER_REGISTER)) {
            return;
        }
        JSONObject json = new JSONObject();
        json.put(Environment.USE_SCHEDULER_REGISTER, environmentTemplate.getBoolean(Environment.USE_SCHEDULER_REGISTER));
        json.put(Environment.SCHEDULER_REGISTER_API, environmentTemplate.getString(Environment.SCHEDULER_REGISTER_API));
        json.put(Environment.SCHEDULER_REGISTER_NAME, environmentTemplate.getString(Environment.SCHEDULER_REGISTER_NAME));
        json.put(Environment.SCHEDULER_REGISTER_URL, environmentTemplate.getString(Environment.SCHEDULER_REGISTER_URL));
        json.put(Environment.SCHEDULER_REGISTER_TOKEN, environmentTemplate.getString(Environment.SCHEDULER_REGISTER_TOKEN));

        String url = environmentTemplate.getString(Environment.SCHEDULER_REGISTER_URL);
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(url);
        String out = null;
        try {
            out = httpClient.post(json);
            JSONObject result = new JSONObject(out);
            RocResponse<?> response = result.parseObject(RocResponse.class);
            if (!response.isSuccess()) {
                log.error("注册任务服务失败URL:{},info:{}", url, out);
            }
        } catch (Exception e) {
            log.info("注册任务服务json:{}", json.toString(4));
            log.error("注册任务服务失败,请检查注册定时任务配置,和服务器是否启动:{},error:{}", out, e.getMessage());
        }
    }


    /**
     * 定时刷新任务缓存
     * ScheduledCron
     */
    @Scheduled(name = "更新定时任务信息", cron = "${schedulerCheckCron}", delayed = 1000)
    @Override
    public void run() {
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        if (!environmentTemplate.getBoolean(Environment.USE_SCHEDULER_REGISTER)) {
            return;
        }
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        List<SchedulerDto> list = schedulerTaskManager.getList(null, 1, 1000);
        for (SchedulerDto dto : list) {
            SchedulerControl newControl = BeanUtil.copy(dto, SchedulerControl.class);
            SchedulerControl oldControl = genericDAO.get(SchedulerControl.class, newControl.getGuid());
            if (StringUtil.isNull(newControl.getRegisterName())) {
                newControl.setRegisterName(environmentTemplate.getString(Environment.SCHEDULER_REGISTER_NAME));
            }
            try {
                if (oldControl == null) {
                    genericDAO.save(newControl);
                } else {
                    newControl.setCreateDate(oldControl.getCreateDate());
                    genericDAO.update(newControl);
                }
            } catch (Exception e) {
                log.error("定时任务保存异常:{},{}", ObjectUtil.toString(newControl), e.getMessage());
            }
        }

        registerClient();
    }
}

package com.github.jspxnet.sioc.scheduler.task;

import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 监控redis启动否
 */
@Data
@Slf4j
public class RedisRunTask {
    private String cmd = "net start Redis";

    private boolean enable = false;

    //每小时一次,检查redis是否正常
    @Scheduled(cron = "1 */1 * * *")
    public void run()  {

        if (enable && !StringUtil.isNull(cmd)) {
            //SystemUtil.shell(cmd);
        }

    }
}
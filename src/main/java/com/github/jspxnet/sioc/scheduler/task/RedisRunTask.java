package com.github.jspxnet.sioc.scheduler.task;

import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 监控redis启动否
 */
@Slf4j
public class RedisRunTask {
    private String cmd = "net start Redis";

    private boolean enable = false;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    //每小时一次,检查redis是否正常
    @Scheduled(cron = "1 */1 * * *")
    public void run()  {

        if (enable && !StringUtil.isNull(cmd)) {
            //SystemUtil.shell(cmd);
        }

    }
}
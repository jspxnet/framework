package com.github.jspxnet.sober.queue;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.sober.queue.cmd.SaveObjectCmd;
import com.github.jspxnet.sober.queue.cmd.UpdateObjectCmd;
import com.github.jspxnet.sober.queue.cmd.UpdateSqlCmd;
import com.github.jspxnet.txweb.dao.GenericDAO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 这里将数据保存到数据库
 */
@Slf4j
@Bean(bind = RedisStoreQueueServer.class, singleton = true)
public class RedisStoreQueueServer extends BaseRedisStoreQueue  {

    final private static Map<String, CmdRun> CMD_RUN_MAP = new HashMap<>();

    static {
        CMD_RUN_MAP.put(BaseRedisStoreQueue.CMD_SAVE, new SaveObjectCmd());
        CMD_RUN_MAP.put(BaseRedisStoreQueue.CMD_UPDATE, new UpdateObjectCmd());
        CMD_RUN_MAP.put(BaseRedisStoreQueue.CMD_UPDATE_SQL, new UpdateSqlCmd());
    }


    /**
     * 保存成功日志记录，生产环境没必要保存
     */
    private boolean saveSucceedLog = false;

    public void setSaveSucceedLog(boolean saveSucceedLog) {
        this.saveSucceedLog = saveSucceedLog;
    }

    private int second = 1;

    public void setSecond(int second) {
        this.second = second;
    }

    @Ref
    private GenericDAO genericDAO;

    private static int debug_times = 0;

    /**
     * 一个长线程，间隔10秒的保存数据，主要使用在分布式上，单机服务器也可以利用换成来提高性能，
     * 避免数据库卡死，这里只是分离出了保存，不能执行更新
     */
    @Scheduled
    public void run() {
        if (!EnvFactory.getEnvironmentTemplate().getBoolean(Environment.useCache))
        {
            return;
        }
        try {
            //锁定单线程begin
            RBucket<Integer> bucket = redissonClient.getBucket(LOCK_SERVER_KEY);
            if (bucket.isExists()) {
                return;
            }
            bucket.set(second, second, TimeUnit.SECONDS);
            //锁定单线程end

            if (debug_times < 3) {
                log.debug("存储队列运行次数{}", (debug_times++));
            }

            //锁定为单线程允许,不要并行，数据库压力太大 begin
            RQueue<?> queue = redissonClient.getQueue(STORE_KEY);
            if (queue == null || queue.isEmpty()) {
                return;
            }
            //null异常情况
            CmdContainer cmdContainer = null;
            Object cmdObj = queue.poll();
            if (cmdObj == null) {
                return;
            }

            if (!(cmdObj instanceof CmdContainer)) {
                return;
            }

            cmdContainer = (CmdContainer) cmdObj;
            if (!cmdContainer.isValid()) {
                return;
            }
            log.debug("存储队列queue长度:{}", queue.size());
            //有效性判断
            CmdRun cmdRun = CMD_RUN_MAP.get(cmdContainer.getCmd());
            if (!(cmdRun instanceof CmdRun)) {
                return;
            }
            cmdRun.setCmdContainer(cmdContainer);
            cmdRun.setGenericDAO(genericDAO);
            cmdRun.setSaveSucceedLog(saveSucceedLog);
            cmdRun.execute();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("存储队列保存数据发生异常:", e);

        }
        log.info("存储队列服务退出");
    }
}

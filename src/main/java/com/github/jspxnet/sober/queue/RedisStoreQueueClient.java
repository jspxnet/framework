package com.github.jspxnet.sober.queue;


import com.github.jspxnet.json.GsonUtil;
import com.github.jspxnet.sioc.annotation.Bean;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;

/**
 * 这里将需要保存到数据库的数据保存的redis
 */
@Bean(bind = RedisStoreQueueClient.class, singleton = true)
@Slf4j
public class RedisStoreQueueClient extends BaseRedisStoreQueue {
    private Gson gson = GsonUtil.createGson();

    public boolean save(Object o) {
        if (o == null) {
            return false;
        }
        RQueue<CmdContainer> queue = redissonClient.getQueue(STORE_KEY);
        return queue.add(new CmdContainer(CMD_SAVE, gson.toJson(o),o.getClass().getName()));
    }

    public boolean update(Object o) {
        if (o == null) {
            return false;
        }
        RQueue<CmdContainer> queue = redissonClient.getQueue(STORE_KEY);
        return queue.add(new CmdContainer(CMD_UPDATE, gson.toJson(o),o.getClass().getName()));
    }


    public boolean updateSql(String sql) {
        log.debug("updateSQL:{}", sql);
        RQueue<CmdContainer> queue = redissonClient.getQueue(STORE_KEY);
        return queue.add(new CmdContainer(CMD_UPDATE_SQL, sql,String.class.getName()));
    }


    public boolean useRedisson()
    {
        return redissonClient!=null;
    }



}

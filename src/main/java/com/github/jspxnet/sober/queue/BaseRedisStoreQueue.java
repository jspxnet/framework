package com.github.jspxnet.sober.queue;

import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.utils.StringUtil;
import org.redisson.api.RedissonClient;

public abstract class BaseRedisStoreQueue  {
    final static public String CMD_SAVE = "save";
    final static public String CMD_UPDATE = "update";
    final static public String CMD_UPDATE_SQL = "updateSql";

    final static String LOCK_SERVER_KEY = StringUtil.replace(BaseRedisStoreQueue.class.getName(), ".", ":") + ":lock";
    final static String STORE_KEY = StringUtil.replace(BaseRedisStoreQueue.class.getName(), ".", ":") + ":data";

    @Ref(bind = RedissonClientConfig.class)
    protected transient RedissonClient redissonClient;

}

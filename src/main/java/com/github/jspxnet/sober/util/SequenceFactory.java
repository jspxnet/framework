package com.github.jspxnet.sober.util;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.queue.RedisStoreQueueClient;
import com.github.jspxnet.sober.table.Sequences;
import com.github.jspxnet.utils.BooleanUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

/**
 * chenYuan
 *
 */
@Bean
@Slf4j
public class SequenceFactory {

    @Ref(bind = RedissonClientConfig.class)
    protected RedissonClient redissonClient;

    @Ref(bind = RedisStoreQueueClient.class)
    protected RedisStoreQueueClient redisStoreQueueClient;

    private boolean useSchedule = true;


    private static final String SEQUENCE_KEY = "sober:sequence:%s";

    public void fixCache(String key,long value) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(String.format(SEQUENCE_KEY,key));
        atomicLong.set(value);
    }

    public long generate(String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(String.format(SEQUENCE_KEY,key));
        return atomicLong.incrementAndGet();
    }

    private void setGenerate(String key,long value) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(String.format(SEQUENCE_KEY,key));
        atomicLong.set(value);
    }

    /**
     *
     * @param keyName 建名
     * @param idf 配置
     * @param type 类型
     * @param jdbcOperations 数据库对象
     * @return 返回key
     */
     public String getNextKey(String keyName, Id idf, Class<?> type, JdbcOperations jdbcOperations)
    {
        Sequences tableSequences = (Sequences)JSCacheManager.get(Sequences.class,keyName);
        if (tableSequences==null||tableSequences.getKeyValue()==idf.min())
        {
            synchronized (this)
            {
                EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
                useSchedule = environmentTemplate.getBoolean(Environment.USE_SCHEDULE);
                tableSequences = jdbcOperations.get(Sequences.class, keyName);
                if (tableSequences==null)
                {
                    tableSequences = createSequences(keyName, idf,type);
                    long keyValue  = generate(tableSequences.getName());
                    tableSequences.setKeyValue(Math.max(tableSequences.getKeyValue(),keyValue));
                    try {
                        jdbcOperations.save(tableSequences);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                JSCacheManager.put(Sequences.class,tableSequences.getName(),tableSequences);
                setGenerate(tableSequences.getName(),tableSequences.getKeyValue());
            }
        }
        long keyValue  = generate(tableSequences.getName());
        TableModels sequencesTable = jdbcOperations.getSoberTable(Sequences.class);
        String sql = "UPDATE " + sequencesTable.getName() + " SET keyValue="+keyValue+" WHERE " + sequencesTable.getPrimary() + "=" + StringUtil.quoteSql(tableSequences.getName());
        if (useSchedule&&redisStoreQueueClient!=null&&redisStoreQueueClient.useRedisson()&&tableSequences.getKeyValue()>=idf.min())
        {
            redisStoreQueueClient.updateSql(sql);
        } else
        {
            jdbcOperations.update(sql);
        }
        return tableSequences.getNextKey(keyValue);
    }

    /**
     * 创建一个序列生成模型
     * @param keyName 名称
     * @param idf 方式
     * @param type 类型
     * @return 对象
     */
    private Sequences createSequences(String keyName, Id idf, Class type)
    {
        Sequences tableSequences = new Sequences();
        tableSequences.setName(keyName);
        if (IDType.serial.equalsIgnoreCase(idf.type())) {
            //数据库不支持，自动切换过来的情况
            if (type.equals(Long.class) || type.getName().contains("long")) {
                tableSequences.setKeyMax(9999999999999999L);
                tableSequences.setKeyLength(16);
                tableSequences.setKeyMin(1);
                tableSequences.setKeyNext(1);
                tableSequences.setKeyValue(idf.min());
                tableSequences.setDateStart(0);
                tableSequences.setDateFormat(idf.dateFormat());
                tableSequences.setMac(0);
            } else
            if (type.equals(Integer.class) || type.getName().contains("int")) {
                tableSequences.setKeyMax(2000000000);
                tableSequences.setKeyLength(10);
                tableSequences.setKeyMin(1);
                tableSequences.setKeyNext(1);
                tableSequences.setKeyValue(idf.min());
                tableSequences.setDateStart(0);
                tableSequences.setDateFormat(idf.dateFormat());
                tableSequences.setMac(idf.mac() ? 1 : 0);
            } else
            if (type.equals(String.class) || type.getName().contains("String")) {
                tableSequences.setKeyMax(Long.MAX_VALUE);
                tableSequences.setKeyLength(idf.length());
                tableSequences.setKeyMin(1);
                tableSequences.setKeyNext(1);
                tableSequences.setKeyValue(idf.min());
                tableSequences.setDateStart(1);
                tableSequences.setDateFormat(idf.dateFormat());
                tableSequences.setMac(idf.mac() ? 1 : 0);
            }
        } else {
            tableSequences.setKeyMax(idf.max());
            tableSequences.setKeyLength(idf.length());
            tableSequences.setKeyMin(idf.min());
            tableSequences.setKeyNext(idf.next());
            tableSequences.setKeyValue(idf.min());
            tableSequences.setDateStart(BooleanUtil.toInt(idf.dateStart()));
            tableSequences.setDateFormat(idf.dateFormat());
            tableSequences.setMac(idf.mac() ? 1 : 0);
        }
        return tableSequences;
    }
}

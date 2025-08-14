package com.github.jspxnet.cache.redis;

import com.github.jspxnet.cache.container.StringEntry;
import com.github.jspxnet.cache.ValidateCodeCache;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Bean(singleton = true)
public class ValidateCodeCacheService implements ValidateCodeCache {

    /**
     * 短信验证
     */
    public final static String SMS_STORE_KEY = "jspx:validate:sms:code:map";
    /**
     * 图片验证
     */
    public final static String IMG_STORE_KEY = "jspx:validate:img:code:map";
    /**
     *
     */
    public final static String GENERAL_VERIFY_KEY = "jspx:validate:general:code:map";
    /**
     * 验证次数记录
     */
    public final static String VALIDATE_TIMES_KEY = "jspx:validate:times:%s";

    //肖佳后期补充
    public final static String VALIDATE_REMAINING_KEY = "jspx:validate:remaining:%s";


    @Ref(bind = RedissonClientConfig.class)
    static RedissonClient redissonClient;

    /**
     * 默认为3分钟
     */
    @Setter
    @Getter
    private int smsTimeOutSecond = 900;
    @Setter
    @Getter
    private int imgTimeOutSecond = 600;
    /**
     * 默认时间
     */
    private final int generalTimeOutSecond = 900;

    private String getTimesKey(String id) {
        return String.format(VALIDATE_TIMES_KEY, id);
    }

    /**
     * 添加手机验证信息
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 添加是否成功
     */
    @Override
    public boolean addSmsCode(String mobile, String code) {

        StringEntry entry = new StringEntry();
        entry.setKey(mobile);
        entry.setValue(code);
        RMap<String, StringEntry> map = redissonClient.getMap(SMS_STORE_KEY);
        map.put(entry.getKey(), entry);
        return map.expire(Instant.now().plusSeconds(smsTimeOutSecond));
        //return map.expire(smsTimeOutSecond, TimeUnit.SECONDS);
    }


    /**
     * 验证短信，验证限时
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 添加是否成功
     */
    @Override
    public boolean validateSms(String mobile, String code) {
        String timeKey = getTimesKey(mobile);
        boolean result = validateSmsCheck(mobile, code);
        if (result) {
            RBucket<Integer> bucket = redissonClient.getBucket(timeKey);
            if (bucket != null) {
                bucket.delete();
            }
        } else {
            RBucket<Integer> bucket = redissonClient.getBucket(timeKey);
            Integer times = bucket.get();
            if (times == null) {
                times = 1;
            }
            times++;
            bucket.set(times);
            bucket.expire(Instant.now().plusSeconds(10000));
        }
        return result;
    }

    /**
     * 为了方便复杂的验证方式
     *
     * @param mobile 电话
     * @return 验证码
     */
    @Override
    public String querySms(String mobile) {
        RMap<String, StringEntry> map = redissonClient.getMap(SMS_STORE_KEY);
        StringEntry entry = map.get(mobile);
        if (entry == null) {
            return StringUtil.empty;
        }
        return entry.getValue();
    }

    /**
     * 正式的验证
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 添加是否成功
     */
    private boolean validateSmsCheck(String mobile, String code) {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        RMap<String, StringEntry> map = redissonClient.getMap(SMS_STORE_KEY);
        StringEntry entry = map.remove(mobile);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired(smsTimeOutSecond)) {
            return false;
        }
        String value = entry.getValue();
        if (StringUtil.isNull(value)) {
            return false;
        }
        if (value.contains("[") && value.contains("]")) {
            return code.equalsIgnoreCase(StringUtil.substringBetween(value, "[", "]"));
        }
        return code.equalsIgnoreCase(entry.getValue());
    }

    /**
     * 图片方式验证码
     *
     * @param sessionId 用户sessionId
     * @param code      代码
     * @return 添加是否成
     */
    @Override
    public boolean addImgCode(String sessionId, String code) {
        if (sessionId == null) {
            return false;
        }
        StringEntry entry = new StringEntry();
        entry.setKey(sessionId);
        entry.setValue(code);
        RMap<String, StringEntry> map = redissonClient.getMap(IMG_STORE_KEY);
        map.put(entry.getKey(), entry);
        return map.expire(Instant.now().plusSeconds(imgTimeOutSecond));
        //map.expire(imgTimeOutSecond, TimeUnit.SECONDS);
    }

    /**
     * 验证图片方式验证码,删除方式
     *
     * @param sessionId 用户sessionId 默认方式 EncryptUtil.getMd5(userSession.getId())
     * @param code      验证码
     * @return 验证是否成功
     */
    @Override
    public boolean validateImg(String sessionId, String code) {
        return validateImg(sessionId, code, true);
    }

    /**
     * 验证图片方式验证码
     *
     * @param sessionId 用户sessionId
     * @param code      验证码
     * @param delete    验证后是否删除
     * @return 验证是否成功
     */
    @Override
    public boolean validateImg(String sessionId, String code, boolean delete) {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        if (!delete) {
            //注册的时候只验证不删除
            RMap<String, StringEntry> map = redissonClient.getMap(IMG_STORE_KEY);
            StringEntry entry = map.get(sessionId);
            if (entry == null) {
                return false;
            }
            if (entry.isExpired(imgTimeOutSecond)) {
                return false;
            }
            return code.equalsIgnoreCase(entry.getValue());
        } else {

            String timeKey = getTimesKey(sessionId);
            boolean result = validateImgCheck(sessionId, code);
            if (result) {
                RBucket<Integer> bucket = redissonClient.getBucket(timeKey);
                if (bucket != null) {
                    bucket.delete();
                }
            } else {
                RBucket<Integer> bucket = redissonClient.getBucket(timeKey);
                Integer times = bucket.get();
                if (times == null) {
                    times = 1;
                }
                times++;
                bucket.set(times);
                bucket.expire(Instant.now().plusSeconds(10* DateUtil.MINUTE));
            }
            return result;
        }
    }


    private boolean validateImgCheck(String sessionId, String code) {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        RMap<String, StringEntry> map = redissonClient.getMap(IMG_STORE_KEY);
        StringEntry entry = map.remove(sessionId);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired(imgTimeOutSecond)) {
            return false;
        }
        return code.equalsIgnoreCase(entry.getValue());
    }

    @Override
    public boolean addGeneralCode(String type, String id, String code) {
        if (id == null) {
            return false;
        }
        String key = EncryptUtil.getMd5(type + id);
        StringEntry entry = new StringEntry();
        entry.setKey(key);
        entry.setValue(code);
        RMap<String, StringEntry> map = redissonClient.getMap(GENERAL_VERIFY_KEY);
        map.put(entry.getKey(), entry);
        return map.expire(Instant.now().plusSeconds(generalTimeOutSecond));
    }

    @Override
    public boolean validateGeneralCheck(String type, String id, String code) {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        String key = EncryptUtil.getMd5(type + id);

        RMap<String, StringEntry> map = redissonClient.getMap(GENERAL_VERIFY_KEY);
        StringEntry entry = map.remove(key);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired(generalTimeOutSecond)) {
            return false;
        }
        String value = entry.getValue();
        if (StringUtil.isNull(value)) {
            return false;
        }
        if (value.contains("[") && value.contains("]")) {
            return code.equalsIgnoreCase(StringUtil.substringBetween(value, "[", "]"));
        }
        boolean check = code.equalsIgnoreCase(entry.getValue());
        if (!check) {
            log.info("code验证失败1:{}\r\n2:{}", code, entry.getValue());
        }
        return check;
    }

    @Override
    public int getTimes(String id) {
        String timeKey = getTimesKey(id);
        RBucket<Integer> bucket = redissonClient.getBucket(timeKey);
        Integer times = bucket.get();
        if (times == null) {
            times = 0;
            bucket.set(times,10,TimeUnit.MINUTES);
        }
        return times;
    }

    /**
     * @return 返回登入剩余时长
     */
    @Override
    public long getTimeRemaining(String loginId) {
        String key = String.format(VALIDATE_REMAINING_KEY, EncryptUtil.getMd5(loginId));
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (!bucket.isExists()) {
            bucket.set(loginId, 1800, TimeUnit.SECONDS);
        }
        long expireTime = bucket.remainTimeToLive();
        if (expireTime <= 0) {
            bucket.delete();
            // 删除一个getTimesKey
            redissonClient.getBucket(String.format(VALIDATE_REMAINING_KEY, EncryptUtil.getMd5(loginId))).delete();
        }
        // 倒计时返回
        return expireTime / 1000;
    }

    /**
     * @param id 更新次数
     */
    @Override
    public void updateTimes(String id) {
        String timeKey = getTimesKey(id);
        RBucket<Integer> bucket = redissonClient.getBucket(timeKey);
        Integer value = bucket.get();
        if (value == null) {
            value = 0;
        }
        bucket.set(value + 1, 10, TimeUnit.MINUTES);
    }
}

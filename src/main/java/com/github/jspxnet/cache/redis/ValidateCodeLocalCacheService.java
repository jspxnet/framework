package com.github.jspxnet.cache.redis;

import com.github.jspxnet.cache.ValidateCodeCache;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.store.LRUStore;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/8/11 20:47
 * @description: jspbox
 **/
@Slf4j
@Bean(singleton = true)
public class ValidateCodeLocalCacheService implements ValidateCodeCache {

    /**
     * 短信验证
     */
    final static String SMS_STORE_KEY = "jspx:validate:sms:code:map";
    /**
     * 图片验证
     */
    final static String IMG_STORE_KEY = "jspx:validate:img:code:map";
    /**
     *
     */
    final static String GENERAL_VERIFY_KEY = "jspx:validate:general:code:map";
    /**
     * 验证次数记录
     */
    final static String VALIDATE_TIMES_KEY = "jspx:validate:times:%s";



   private final static LRUStore CACHE = new LRUStore();

    /**
     * 默认为3分钟
     */
    private int smsTimeOutSecond = 900;
    private int imgTimeOutSecond = 600;

    public int getSmsTimeOutSecond() {
        return smsTimeOutSecond;
    }

    public void setSmsTimeOutSecond(int smsTimeOutSecond) {
        this.smsTimeOutSecond = smsTimeOutSecond;
    }

    public int getImgTimeOutSecond() {
        return imgTimeOutSecond;
    }

    public void setImgTimeOutSecond(int imgTimeOutSecond) {
        this.imgTimeOutSecond = imgTimeOutSecond;
    }

    private String getTimesKey(String id) {
        return String.format(VALIDATE_TIMES_KEY,id);
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
        CacheEntry entry = new CacheEntry();
        try {
            entry.setKey(mobile);
            entry.setValue(code);
            entry.setTimeToLive(smsTimeOutSecond* DateUtil.SECOND);
            CACHE.put(entry);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
            CACHE.remove(mobile);
        } else {
            CacheEntry entry  = CACHE.get(timeKey);
            Integer times = (Integer)entry.getValue();
            if (times == null) {
                times = 1;
            }
            times++;
            entry.setValue(times);
            entry.setTimeToLive(10 * DateUtil.MINUTE);
            CACHE.put(entry);
        }
        return result;
    }

    /**
     * 为了方便复杂的验证方式
     * @param mobile  电话
     * @return 验证码
     */
    @Override
    public String querySms(String mobile)
    {
        CacheEntry entry = CACHE.get(SMS_STORE_KEY);
        if (entry == null) {
            return StringUtil.empty;
        }
        return (String)entry.getValue();
    }
    /**
     * 正式的验证
     * @param mobile 手机号
     * @param code   验证码
     * @return 添加是否成功
     */
    private boolean validateSmsCheck(String mobile, String code) {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        CacheEntry entry = CACHE.remove(mobile);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            return false;
        }
        String value = (String)entry.getValue();
        if (StringUtil.isNull(value))
        {
            return false;
        }
        if (value.contains("[")&&value.contains("]"))
        {
            return code.equalsIgnoreCase(StringUtil.substringBetween(value,"[","]"));
        }
        return code.equalsIgnoreCase(value);
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
        if (sessionId==null)
        {
            return false;
        }
        CacheEntry entry = new CacheEntry();
        try {
            entry.setKey(sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        entry.setValue(code);
        entry.setTimeToLive(imgTimeOutSecond*DateUtil.SECOND);
        CACHE.put(entry);
        return true;
    }

    /**
     *  验证图片方式验证码,删除方式
     *
     * @param sessionId 用户sessionId 默认方式 EncryptUtil.getMd5(userSession.getId())
     * @param code 验证码
     * @return 验证是否成功
     */
    @Override
    public boolean validateImg(String sessionId, String code)
    {
        return validateImg(sessionId, code,true);
    }
    /**
     *  验证图片方式验证码
     * @param sessionId 用户sessionId
     * @param code 验证码
     * @param delete  验证后是否删除
     * @return 验证是否成功
     */
    @Override
    public boolean validateImg(String sessionId, String code,boolean delete)
    {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        if (!delete)
        {
            //注册的时候只验证不删除
            CacheEntry entry = CACHE.get(sessionId);
            if (entry == null) {
                return false;
            }
            if (entry.isExpired()) {
                return false;
            }
            return code.equalsIgnoreCase((String)entry.getValue());
        } else
        {
            String timeKey = getTimesKey(sessionId);
            boolean result = validateImgCheck(sessionId, code);
            if (result) {
                CACHE.remove(timeKey);
            } else {
                CacheEntry entry = CACHE.get(timeKey);
                if (entry==null)
                {
                    entry = new CacheEntry();
                    try {
                        entry.setKey(timeKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Integer times = (Integer)entry.getValue();
                if (times == null) {
                    times = 1;
                }
                times++;
                entry.setValue(times);
                entry.setTimeToLive(10*DateUtil.MINUTE);
                CACHE.put(entry);
            }
            return result;
        }
    }


    private boolean validateImgCheck(String sessionId, String code) {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        CacheEntry entry = CACHE.get(sessionId);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            return false;
        }
        return code.equalsIgnoreCase((String)entry.getValue());
    }

    @Override
    public boolean addGeneralCode(String type, String id, String code) {
        if (id==null)
        {
            return false;
        }
        String key = EncryptUtil.getMd5(type+id);
        CacheEntry entry = new CacheEntry();
        try {
            entry.setKey(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        entry.setValue(code);
        /**
         * 默认时间
         */
        int generalTimeOutSecond = 900;
        entry.setTimeToLive(generalTimeOutSecond *DateUtil.SECOND);
        CACHE.put(entry);
        return true;
    }

    @Override
    public boolean validateGeneralCheck(String type, String id, String code)
    {
        if (StringUtil.isEmpty(code)) {
            return false;
        }
        String key = EncryptUtil.getMd5(type+id);
        CacheEntry entry = CACHE.get(GENERAL_VERIFY_KEY + key);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            return false;
        }
        String value = (String)entry.getValue();
        if (StringUtil.isNull(value))
        {
            return false;
        }
        if (value.contains("[")&&value.contains("]"))
        {
            return code.equalsIgnoreCase(StringUtil.substringBetween(value,"[","]"));
        }
        boolean check = code.equalsIgnoreCase(value);
        if (!check)
        {
            log.info("code验证失败1:{}\r\n2:{}",code,entry.getValue());
        }
        return check;
    }

    @Override
    public int getTimes(String id) {
        String timeKey = getTimesKey(id);
        CacheEntry entry = CACHE.get(timeKey);
        if (entry==null)
        {
             entry = new CacheEntry();
            try {
                entry.setKey(timeKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Integer times = (Integer)entry.getValue();
        if (times == null) {
            times = 0;
            entry.setTimeToLive(10*DateUtil.MINUTE);
            CACHE.put(entry);
        }
        return times;
    }

    /**
     * @param id 更新次数
     */
    @Override
    public void updateTimes(String id) {
        String timeKey = getTimesKey(id);
        CacheEntry entry = CACHE.get(timeKey);
        if (entry==null)
        {
            entry = new CacheEntry();
            try {
                entry.setKey(timeKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Integer value = (Integer)entry.getValue();
        if (value==null)
        {
            value = 0;
        }
        entry.setValue(value + 1);
        entry.setTimeToLive(10*DateUtil.MINUTE);
        CACHE.put(entry);
    }


}

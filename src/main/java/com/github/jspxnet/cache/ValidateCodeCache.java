package com.github.jspxnet.cache;

public interface ValidateCodeCache {

    boolean addImgCode(String sessionId, String code);

    boolean validateSms(String mobile, String code);

    boolean addSmsCode(String mobile, String code);

    boolean validateImg(String sessionId, String code);

    boolean validateImg(String sessionId, String code, boolean delete);

    int getTimes(String id);

    void setTimes(String id, int times);

    void updateTimes(String id);

    String querySms(String mobile);

}

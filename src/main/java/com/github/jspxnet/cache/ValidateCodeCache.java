package com.github.jspxnet.cache;

public interface ValidateCodeCache {

    boolean addImgCode(String sessionId, String code);

    boolean validateSms(String mobile, String code);

    boolean addSmsCode(String mobile, String code);

    boolean validateImg(String sessionId, String code);

    boolean validateImg(String sessionId, String code, boolean delete);


    boolean addGeneralCode(String type, String id, String code);

    boolean validateGeneralCheck(String type, String id, String code);

    int getTimes(String id);

    String querySms(String mobile);

    void updateTimes(String id);
}

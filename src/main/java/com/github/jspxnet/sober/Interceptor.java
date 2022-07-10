package com.github.jspxnet.sober;


import java.io.Serializable;
import java.util.Map;

/**
 * SqlMap 拦截器
 */
public interface Interceptor extends Serializable {

    void destroy();

    void init();

    void before(SoberSupport soberSupport, Map<String, Object> valueMap) throws Exception;

    Object after(SoberSupport soberSupport, Map<String, Object> valueMap,final Object result) throws Exception;


}
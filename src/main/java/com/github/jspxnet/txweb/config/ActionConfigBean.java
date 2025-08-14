/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 20:05:27
 * com.github.jspxnet.txweb.config.ActionConfigBean
 * TXWeb action 配置对象
 */
@Table(name = "jspx_action", caption = "TXWeb配置", create = false)
public class ActionConfigBean implements ActionConfig, Serializable {
    @Setter
    @Column(caption = "名称", length = 200, notNull = true)
    private String caption = StringUtil.empty;
    @Setter
    @Column(caption = "对象名称", length = 200, notNull = true)
    private String actionName;
    @Column(caption = "ioc对象", length = 200, notNull = true)
    private String iocBean;
    @Setter
    @Column(caption = "操作类", length = 200, dataType = "isLengthBetween(2,200)", notNull = true)
    private String className = StringUtil.empty;
    @Setter
    @Column(caption = "执行方法", length = 200)
    private String method = "";
    @Setter
    @Column(caption = "手机支持", notNull = true)
    private boolean mobile = false;
    @Column(caption = "页面缓存", notNull = true)
    private boolean cache = false;
    @Column(caption = "页面缓存名称", notNull = true)
    private String cacheName = DefaultCache.class.getName();
    @Setter
    @Column(caption = "所在命名空间", length = 250)
    private String namespace =  StringUtil.empty;

    @Setter
    @Getter
    @Column(caption = "动态载入", notNull = true)
    private boolean register = false;


    private final Map<String, Object> param = new HashMap<String, Object>();
    private final List<String> interceptors = new LinkedList<String>();
    private final List<ResultConfigBean> resultConfigs = new ArrayList<ResultConfigBean>();
    private String[] passInterceptor = null;


    public ActionConfigBean() {

    }


    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public String getIocBean() {
        return iocBean;
    }

    void setIocBean(String iocBean) {
        this.iocBean = iocBean;
    }


    @Override
    public String getClassName() {
        return className;
    }


    @Override
    public String getMethod() {
        return method;
    }

    void addParam(String name, Object value) {
        param.put(name, value);
    }

    public Object getParam(String name) {
        return param.get(name);
    }

    @Override
    public Map<String, Object> getParam() {
        return param;
    }

    @Override
    public String[] getPassInterceptor() {
        return passInterceptor;
    }

    void setPassInterceptor(String[] passInterceptor) {
        this.passInterceptor = passInterceptor;
    }

    @Override
    public List<String> getInterceptors() {
        return interceptors;
    }

    @Override
    public void setCache(boolean cache) {
        this.cache = cache;
    }
    @Override
    public boolean isCache() {
        return cache;
    }

    void addInterceptors(String interceptorName) {
        interceptors.add(interceptorName);
    }

    @Override
    public ResultConfigBean getResultConfig(String name) {
        if (StringUtil.isNull(name)) {
            name = StringUtil.ASTERISK;
        }
        for (ResultConfigBean resultConfigBean : resultConfigs) {
            if (resultConfigBean.getName().equals(name)) {
                return resultConfigBean;
            }
        }

        if (!StringUtil.ASTERISK.equals(name)) {
            for (ResultConfigBean resultConfigBean : resultConfigs) {
                if (StringUtil.ASTERISK.equals(resultConfigBean.getName())) {
                    return resultConfigBean;
                }
            }
        }
        return null;
    }


    @Override
    public List<ResultConfigBean> getResultConfigs() {
        return resultConfigs;
    }

    void addResultConfig(ResultConfigBean resultConfig) {
        resultConfigs.add(resultConfig);
    }

    @Override
    public boolean isMobile() {
        return mobile;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }
    @Override
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<action name=\"").append(actionName).append("\" ");
        if (!StringUtil.isNull(caption)) {
            sb.append("caption=\"").append(caption).append("\" ");
        }
        sb.append(" class=\"").append(iocBean).append("\" ");
        if (!StringUtil.isNull(method)) {
            sb.append("method=\"").append(method).append("\" ");
        }
        if (mobile) {
            sb.append("mobile=\"").append(mobile).append("\" ");
        }
        if (cache) {
            sb.append("cache=\"").append(cache).append("\" ");
        }
        if (!StringUtil.isEmpty(namespace)) {
            sb.append("namespace=\"").append(namespace).append("\" ");
        }

        sb.append(">\r\n");
        for (String pkey : param.keySet()) {
            sb.append("<param name=\"").append(pkey).append("\">").append(param.get(pkey)).append("</param>\r\n");
        }
        for (ResultConfigBean resultConfigBean : resultConfigs) {
            sb.append(resultConfigBean.toString());
        }
        for (String interceptor : interceptors) {
            sb.append("<interceptor-ref name=\"").append(interceptor).append("\" />\r\n");
        }
        sb.append("</action>\r\n");
        return sb.toString();
    }
}
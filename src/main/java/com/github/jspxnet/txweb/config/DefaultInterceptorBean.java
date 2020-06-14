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

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-9-19
 * Time: 下午3:14
 * 因为默认配置有两种情况,一直需要继承,一种不需要继承,本来可以直接配置action里边,
 * 但为了优化配置效果，所以提供默认拦截器可以，配置设置是否支持拦截器
 */
@Table(name = "jspx_defaultInterceptor", caption = "TXWeb默认拦截器", cache = true)
public class DefaultInterceptorBean {

    @Column(caption = "名称", length = 200, notNull = true)
    private String caption = StringUtil.empty;

    @Column(caption = "对象名称", length = 200, notNull = true)
    private String name;

    @Column(caption = "是否继承", notNull = true)
    private boolean extend = false;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExtend() {
        return extend;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }
}
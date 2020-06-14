/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.service;


import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-27
 * Time: 下午11:43
 */
@WebService
public interface WebBeanFactory {

    @WebMethod
    void setToken(String token);

    @WebMethod
    boolean isOnline() throws Exception;

    @WebMethod
    String process(String call);

}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-12
 * Time: 18:30:29
 */
public class CacheException extends RuntimeException {


    public CacheException() {
        super();
    }


    public CacheException(String message) {
        super(message);
    }


    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }


    public CacheException(Throwable cause) {
        super(cause);
    }


    public final Throwable getInitialCause() {
        return super.getCause();
    }
}
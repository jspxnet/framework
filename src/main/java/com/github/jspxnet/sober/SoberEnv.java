/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 22:32:18
 */
public class SoberEnv {
    private SoberEnv() {

    }


    //////////替代关键字,sql 处使用

    //读写分离，支持负载均衡  begin
    final public static int READ_WRITE = 0;

    final public static int READ_ONLY = 1;

    final public static int WRITE_ONLY = 2;

    final public static int THREAD_LOCAL = 3;
    //读写分离，支持负载均衡  end

    final public static String notTransaction = "-1";

}
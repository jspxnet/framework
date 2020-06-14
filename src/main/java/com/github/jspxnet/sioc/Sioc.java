/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc;

import com.github.jspxnet.sioc.annotation.RpcClient;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 15:44:56
 */
public class Sioc {
    private Sioc() {

    }

    final public static String global = "global";
    final public static String IocLoad = "siocLoad:";

    final public static String KEY_RPC_CLIENT = "rpcClient";

    final public static String IocFen = "$";
    final public static String IocRef = "ref";
    final public static String IocClass = "class";

    final public static String IocNamespace = "@namespace";
    final public static String IocRootNamespace = "@rootNamespace";

}
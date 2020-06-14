/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dispatcher;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-11-16
 * Time: 上午9:56
 */
public interface DispatcherListener {
    /**
     * Called when the dispatcher is initialized
     *
     * @param du The dispatcher instance
     */
    void dispatcherInitialized(Dispatcher du);

    /**
     * Called when the dispatcher is destroyed
     *
     * @param du The dispatcher instance
     */
    void dispatcherDestroyed(Dispatcher du);
}
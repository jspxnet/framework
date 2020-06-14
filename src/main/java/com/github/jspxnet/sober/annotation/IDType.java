/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.annotation;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-10-21
 * Time: 15:15:31
 */
public class IDType {
    private IDType() {

    }

    final public static String seq = "seq";

    final public static String uuid = "uuid";

    final public static String serial = "serial";

    //用户自己自定
    final public static String none = "none";
    /*
    seq:系统默认生成 yyyyMMddhhss + 序列,类型可以是long 和 String
    seq:的时候使用下边的配置创建系列
    字符串类型，长度大于18会加入日期

    uid:jdk1.5 默认生成 根据长度生成 ，如果字段类型是数字类型，那么长度14位默认位安全不重复的
    uuid  如果是字符串类型8位以上位默认位安全不重复的，16位已经能保证非常安全稳定
    serial:数据库 自动增加，如果数据库不支持，切换到自动增加
    */


}
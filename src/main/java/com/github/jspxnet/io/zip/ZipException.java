/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io.zip;

/**
 * Copyright: Copyright (c) 2002-2003
 * Company: JavaResearch(http://www.javaresearch.org)
 * 最后更新日期:2003年1月9日
 *
 * @author Cherami, Barney, Brain
 * @version 0.8
 * 陈原修改版
 */

/**
 * 这个程序需要自行处理的高层错误异常，引入的目的是方便进行自己的高层异常处理。
 * 目前这个类仅仅还是一个空壳，没有实际的用处，以后会扩展并完善
 */
public class ZipException
        extends Exception {
    public static final int ENTRYEXIST = 1; //异常的常量定义
    private int type = 0; //异常的类型

    /**
     * 构造方法。
     *
     * @param type 异常类型
     */
    public ZipException(int type) {
        this.type = type;
    }

    /**
     * 得到异常的类型。
     *
     * @return 异常的类型
     */
    public int getType() {
        return type;
    }
}
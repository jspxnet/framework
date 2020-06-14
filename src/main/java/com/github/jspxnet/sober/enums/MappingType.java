/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.enums;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-7
 * Time: 18:16:21
 */
public class MappingType {
    private MappingType() {

    }

    //多对一
    public static final String ManyToOne = "ManyToOne";
    //一对一
    public static final String OneToOne = "OneToOne";
    //一对多
    public static final String OneToMany = "OneToMany";


}
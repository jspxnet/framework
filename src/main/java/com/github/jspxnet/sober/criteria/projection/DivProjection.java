/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.criteria.projection;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-10
 * Time: 10:28:25
 */
public class DivProjection implements Projection {
    //字段1
    private Projection projection1;
    //字段2
    private Projection projection2;

    //字段1除字段2

    protected DivProjection(Projection projection1, Projection projection2) {
        this.projection1 = projection1;
        this.projection2 = projection2;
    }

    @Override
    public String toSqlString(String databaseName) {
        return "(" + projection1.toSqlString(databaseName) + " / " + projection2.toSqlString(databaseName) + ")";
    }

}
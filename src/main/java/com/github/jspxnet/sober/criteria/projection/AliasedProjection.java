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
 * Time: 9:15:26
 */
public class AliasedProjection implements Projection {
    final private Projection projection;
    final private String alias;

    @Override
    public String toString() {
        return projection.toString() + " AS " + alias;
    }

    protected AliasedProjection(Projection projection, String alias) {
        this.projection = projection;
        this.alias = alias;
    }

    @Override
    public String toSqlString(String databaseName) {
        return projection.toSqlString(databaseName) + " AS " + alias;
    }

    public String[] getAliases() {
        return new String[]{alias};
    }
}
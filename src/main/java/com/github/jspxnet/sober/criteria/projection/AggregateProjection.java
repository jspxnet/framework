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
 * Time: 8:45:45
 */
public class AggregateProjection extends SimpleProjection {


    protected String propertyName;
    final private String aggregate;

    protected AggregateProjection(String aggregate, String propertyName) {
        this.aggregate = aggregate;
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return aggregate + "(" + propertyName + ')';
    }

    @Override
    public String toSqlString(String databaseName) {
        return aggregate + "(" + propertyName + ") AS " + propertyName;
    }

}
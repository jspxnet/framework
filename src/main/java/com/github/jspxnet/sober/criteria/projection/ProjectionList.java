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

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-10
 * Time: 9:01:08
 */
public class ProjectionList implements Projection {
    private List<Projection> elements = new ArrayList<Projection>();

    protected ProjectionList() {
    }

    static public ProjectionList create() {
        return new ProjectionList();
    }

    public ProjectionList add(Projection projection, String alias) {
        elements.add(Projections.alias(projection, alias));
        return this;
    }

    @Override
    public String toSqlString(String databaseName) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            Projection proj = elements.get(i);
            buf.append(proj.toSqlString(databaseName));
            if (i < elements.size() - 1) {
                buf.append(", ");
            }
        }
        return buf.toString();

    }

    @Override
    public String toString() {
        return elements.toString();
    }

}
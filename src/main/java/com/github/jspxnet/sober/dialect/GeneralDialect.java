/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.sober.TableModels;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 23:49:14
 * 简单数据库 SQL 匹配
 */
public class GeneralDialect extends Dialect {
    @Override
    public boolean supportsSequenceName() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return false;
    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        return sql;
    }

    @Override
    public boolean supportsConcurReadOnly() {
        return false;
    }

    @Override
    public boolean commentPatch() {
        return false;
    }

}
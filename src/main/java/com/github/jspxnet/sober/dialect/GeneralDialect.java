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
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.utils.ClassUtil;
import java.io.InputStream;
import java.sql.Time;
import java.util.Date;

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
    public String getLimitString(String sql, int begin, int end,TableModels soberTable) {
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

    @Override
    public String getFieldType(SoberColumn soberColumn) {

        if (ClassUtil.isNumberType(soberColumn.getClassType()))
        {
            if (soberColumn.getClassType()==int.class || soberColumn.getClassType()==Integer.class)
            {
                if (soberColumn.getLength()<3)
                {
                    return "tinyint("+soberColumn.getLength()+")";
                }
                return "integer";
            }

            if (soberColumn.getClassType()==long.class || soberColumn.getClassType()==Long.class)
            {
                if (soberColumn.getLength()>8)
                {
                    return "bigint("+soberColumn.getLength()+")";
                }
                return "bigint(16)";
            }

            if (soberColumn.getClassType()==float.class || soberColumn.getClassType()==Float.class||soberColumn.getClassType()==double.class || soberColumn.getClassType()==Double.class)
            {
                if (soberColumn.getLength()>8)
                {
                    return "decimal("+soberColumn.getLength()+",2)";
                }
                return "decimal";
            }
        }
        if (soberColumn.getClassType()==boolean.class || soberColumn.getClassType()==Boolean.class)
        {
            return "int(1)";
        }
        if (soberColumn.getClassType()==String.class)
        {
            if (soberColumn.getLength()<512)
            {
                return "varchar("+soberColumn.getLength()+")";
            }
            if (soberColumn.getLength()<3000)
            {
                return "mediumtext";
            }
            return "text";
        }

        if (soberColumn.getClassType()== Date.class)
        {
            return "datetime";
        }

        if (soberColumn.getClassType()== Time.class)
        {
            return "time";
        }

        if (soberColumn.getClassType()== InputStream.class)
        {
            return "LONGBLOB";
        }

        if (soberColumn.getClassType()==char.class)
        {
            return "char("+soberColumn.getLength()+")";
        }
        return "varchar(512)";
    }

    @Override
    public String fieldQuerySql(String sql) {
        return "SELECT * FROM (" + sql + ") zs limit 1";
    }
}
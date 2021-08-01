package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.sober.TableModels;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/7/31 1:42
 * @description: jspbox
 **/
@Slf4j
public class MySQLLowDialect extends MySQLDialect {

    public MySQLLowDialect() {
        put(Date.class.getName(), "`${" + COLUMN_NAME + "}` datetime <#if where=" + COLUMN_NOT_NULL + ">NOT NULL DEFAULT '0000-00-00 00:00:00'</#if> COMMENT '${" + COLUMN_CAPTION + "}'");
    }

    @Override
    public String getLimitString(String sql, int begin, int end, TableModels soberTable) {
        int length = end - begin;
        if (length < 0) {
            length = 0;
        }
        return sql + " limit " + begin + "," + length;
    }


}
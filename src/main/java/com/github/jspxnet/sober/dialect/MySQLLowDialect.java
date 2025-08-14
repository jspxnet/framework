package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.sober.TableModels;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/7/31 1:42
 * description: 老版本mysql支持
 **/
@Slf4j
public class MySQLLowDialect extends MySQLDialect {

    public MySQLLowDialect() {
        put(Date.class.getName(), "`${" + COLUMN_NAME + "}` datetime <#if where=\"" + COLUMN_NOT_NULL + "\">NOT NULL DEFAULT '0000-00-00 00:00:00'</#if> COMMENT '${" + COLUMN_CAPTION + "}'");
    }

}
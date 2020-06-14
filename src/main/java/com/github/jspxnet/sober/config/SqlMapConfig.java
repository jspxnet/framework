/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.config;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 20:13:24
 */
@Data
public class SqlMapConfig implements Serializable {
    private String id = StringUtil.empty;

    private String database = Environment.defaultValue;

    private String resultType = DataMap.class.getName();

    private String context = StringUtil.empty;

    private String quote = StringUtil.empty;

    public String getResultClass() {
        if ("map".equalsIgnoreCase(resultType))
        {
            return null;
        }
        if ("list".equalsIgnoreCase(resultType))
        {
            return ArrayList.class.getName();
        }
        return resultType;
    }
}
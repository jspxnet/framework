/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.datasource;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-1-2
 * Time: 下午2:52
 */
public interface ReadWriteDataSource extends DataSource, Serializable {

    int getReadWrite();

    void setReadWrite(int readWrite);


}
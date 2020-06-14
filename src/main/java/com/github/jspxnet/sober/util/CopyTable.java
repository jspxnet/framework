/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.dialect.MySQLDialect;
import com.github.jspxnet.txweb.dao.GenericDAO;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-6-16
 * Time: 下午9:24
 */
public class CopyTable {

    //导入的sql解析器
    private Dialect dialect = new MySQLDialect();

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    private DataSource formDataSource;

    public DataSource getFormDataSource() {
        return formDataSource;
    }

    public void setFormDataSource(DataSource formDataSource) {
        this.formDataSource = formDataSource;
    }

    private DataSource toDataSource;

    public DataSource getToDataSource() {
        return toDataSource;
    }

    public void setToDataSource(DataSource toDataSource) {
        this.toDataSource = toDataSource;
    }


    public void copy(String sql, Class toTable) throws Exception {
        PreparedStatement accessState = formDataSource.getConnection().prepareStatement(sql);
        ResultSet resultSet = accessState.executeQuery();
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        SoberSupport genericDAO = beanFactory.getBean(GenericDAO.class);
        while (resultSet.next()) {
            genericDAO.save(JdbcUtil.getBean(resultSet, toTable, dialect));
        }
        JdbcUtil.closeResultSet(resultSet);
    }

}
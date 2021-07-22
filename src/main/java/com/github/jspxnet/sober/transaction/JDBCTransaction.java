/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.transaction;



import com.github.jspxnet.sober.util.JdbcUtil;
import com.github.jspxnet.sober.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;


import javax.sql.DataSource;
import java.sql.Savepoint;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-5
 * Time: 16:09:48
 */

@Slf4j
public class JDBCTransaction extends AbstractTransaction {
    public JDBCTransaction(DataSource dataSource) {
        setDataSource(dataSource);
    }

    private JDBCTransaction() {

    }


    private Savepoint savepoint = null;


    /**
     * JDBC事务开始,简单的设置 AutoCommit(false);
     *
     * @throws TransactionException 事务异常
     */
    @Override
    public void begin() throws TransactionException {
        if (connection==null)
        {
            throw new TransactionException(new NullPointerException(),null,"数据库链接为空");
        }
        isActive++;
        if (isActive == 1) {
            try {
                if (connection.getAutoCommit()) {
                    connection.setAutoCommit(false);
                }
                if (supportsSavePoints) {
                    savepoint = connection.setSavepoint(getTransactionId());
                }
            } catch (SQLException e) {
                try {
                    if (supportsSavePoints && savepoint != null) {
                        connection.releaseSavepoint(savepoint);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                savepoint = null;
                throw new TransactionException(e,null ,"Error Transaction begin not transfer get savepoint,jdbc事务不能够开启,可能是因为不能够得到连接");
            }
        }
    }

    /**
     * 提交,如果存在事务嵌套,那么以最外边的事务为准
     * @throws Exception 事务异常
     */
    @Override
    public void commit() throws Exception {
        isActive--;
        if (wasCommitted) {
            return;
        }
        if (isActive > 0) {
            return;
        }
        connection.commit();
        connection.setAutoCommit(true);
        isActive = 0;
        wasCommitted = true;
        TRANSACTION_MANAGER.remove(transactionId);
    }

    /**
     * 回滚,如果存在事务嵌套,回滚后下边的嵌套将不既有意义
     */
    @Override
    public void rollback() {
        isActive--;
        if (wasRolledBack) {
            return;
        }
        try {
            if (connection != null&&!connection.getAutoCommit()) {
                if (supportsSavePoints && savepoint != null) {
                    connection.rollback(savepoint);
                } else {
                    connection.rollback();
                }
            }
        } catch (Exception e) {
            log.error("Error Transaction rollback ,jdbc事务回滚错误", e);
        } finally {
            if (connection != null) {
                try {
                    connection.releaseSavepoint(savepoint);
                    savepoint = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isActive = 0;
            wasRolledBack = true;
            TRANSACTION_MANAGER.remove(transactionId);
            JdbcUtil.closeConnection(connection, true);
        }
    }


}
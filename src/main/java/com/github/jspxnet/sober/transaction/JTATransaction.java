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
import javax.transaction.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-5
 * Time: 16:15:52
 * @author chenYuan
 */
@Slf4j
public class JTATransaction extends AbstractTransaction {
    private JTATransaction() {

    }

    public JTATransaction(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    private UserTransaction userTransaction = null;

    public UserTransaction getUserTransaction() {
        return userTransaction;
    }

    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    @Override
    public void begin() throws TransactionException {
        isActive++;
        if (isActive == 1) {
            try {
                userTransaction.setTransactionTimeout(timeout);
                userTransaction.begin();
            } catch (Exception e) {
                throw new TransactionException(e, null,"Error Transaction begin,JTA事务不能够开启,检查系统环境");
            } finally {

                wasCommitted = false;
                wasRolledBack = false;
            }
        }

    }

    @Override
    public void commit() throws Exception {
        isActive--;
        if (isActive > 0) {
            return;
        }
        if (wasCommitted || wasRolledBack) {
            return;
        }

        if (connection == null || connection.isClosed()) {
            throw new Exception("Error Transaction commit,JTA事务不能提交,回滚出错");
        }

        userTransaction.commit();
        isActive = 0;
        wasCommitted = true;
        TRANSACTION_MANAGER.remove(transactionId);
    }

    @Override
    public void rollback() {
        if (wasRolledBack) {
            return;
        }
        isActive = 0;
        try {
            userTransaction.rollback();
        } catch (SystemException e) {
            log.error("Error Transaction rollback,JTA事务不能回滚,可能是环境配置错误", e);
        } finally {
            wasRolledBack = true;
            TRANSACTION_MANAGER.remove(transactionId);
            JdbcUtil.closeConnection(connection, true);
        }
    }
}
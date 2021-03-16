package com.github.jspxnet.sober.transaction;

import com.github.jspxnet.sober.Transaction;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import java.sql.SQLException;

/**
 * 事务控制器，提供外部统一控制事务的开始关闭
 */
public class TransactionController extends JdbcOperations {

    public TransactionController() {

    }

    /**
     * @return 保存提交点, 开始一个事务
     * @throws SQLException SQL 异常
     */
    public Transaction createTransaction() throws SQLException {
        return getSoberFactory().createTransaction();
    }

}

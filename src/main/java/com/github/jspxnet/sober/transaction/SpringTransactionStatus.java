package com.github.jspxnet.sober.transaction;

import org.springframework.transaction.support.AbstractTransactionStatus;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/16 1:50
 * description: sober 在spring中的事物状态
 **/
public class SpringTransactionStatus extends AbstractTransactionStatus {
    private final boolean newTransaction;
    private final String transactionId;

    public SpringTransactionStatus(String transactionId, boolean newTransaction) {
        this.newTransaction = newTransaction;
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean isNewTransaction() {
        return this.newTransaction;
    }
}

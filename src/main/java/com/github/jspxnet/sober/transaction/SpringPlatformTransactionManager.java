package com.github.jspxnet.sober.transaction;





/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/16 1:23
 * description: sober 在spring中的事物管理器
 **/
/*

public class SpringPlatformTransactionManager implements PlatformTransactionManager {
    private final com.github.jspxnet.sober.transaction.TransactionManager transactionManager = TransactionManager.getInstance();
    private final SoberFactory soberFactory;
    public SpringPlatformTransactionManager(SoberFactory soberFactory)
    {
        this.soberFactory = soberFactory;
    }


    @Override
    public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
        try {

            AbstractTransaction abstractTransaction = soberFactory.createTransaction();
            String transactionId = abstractTransaction.getTransactionId();
            abstractTransaction.begin();
            Object[] param = new Object[2];
            param[0]=transactionId;
            param[1]=!transactionManager.containsKey(transactionId);
            return (TransactionStatus)ClassUtil.newInstance("org.springframework.transaction.support.AbstractTransactionStatus",param);
        } catch (Throwable throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        SpringTransactionStatus springTransactionStatus = (SpringTransactionStatus)transactionStatus;
        AbstractTransaction transaction = transactionManager.get(springTransactionStatus.getTransactionId());
        if (transaction==null)
        {
            throw new NoTransactionException(springTransactionStatus.getTransactionId());
        }

        try {
            transaction.commit();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new TransactionSystemException(springTransactionStatus.getTransactionId(),throwable);
        }
    }

    @Override
    public void rollback(TransactionStatus transactionStatus) throws TransactionException {
        SpringTransactionStatus springTransactionStatus = (SpringTransactionStatus)transactionStatus;
        AbstractTransaction transaction = transactionManager.get(springTransactionStatus.getTransactionId());
        if (transaction==null)
        {
            throw new NoTransactionException(springTransactionStatus.getTransactionId());
        }

        try {
            transaction.rollback();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new UnexpectedRollbackException(springTransactionStatus.getTransactionId());
        }
    }
}
*/

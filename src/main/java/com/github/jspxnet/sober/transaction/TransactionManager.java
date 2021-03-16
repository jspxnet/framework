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


import com.github.jspxnet.util.ThreadHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-21
 * Time: 18:09:31
 * 事务池
 * @author chenYuan
 */
@Slf4j
public class TransactionManager extends ThreadHashMap<String, AbstractTransaction> {

    /**
     * 单列方式运行,这个类对于外部没有意义,内核使用.
     */
    final static private TransactionManager INSTANCE = new TransactionManager();

    public static TransactionManager getInstance() {
        return INSTANCE;
    }

    private TransactionManager() {

    }

    /**
     * 开始一个事务,当事务开始的时候jdbc连接将统一为一个
     *
     * @param transaction 事务对象
     */
    public void add(AbstractTransaction transaction) {
        super.put(transaction.getTransactionId(), transaction);
    }

    /**
     * 释放事务对象
     * @param tid 事务id
     */
    public void remove(String tid) {
        super.remove(tid);
    }

    /**
     * 外部得到事务对象,通过SoberFactory.hashCode
     *
     * @param tid 通过连接id得到事务对象
     * @return 返回事务对象
     */
    public AbstractTransaction get(String tid) {
        return super.get(tid);
    }

    public boolean containsKey(String tid) {
        return super.containsKey(tid);
    }

    public void checkTransactionOvertime() {
        for (AbstractTransaction t : super.values()) {
            //超时或关闭的事务对象将删除，
            if (t == null) {
                continue;
            }
            try {
                if (t.connection == null || t.connection.isClosed())
                {
                    super.remove(t.getTransactionId());
                    continue;
                }
                boolean isTimeOut = false;
                if (t.isActive()&&System.currentTimeMillis() - t.getCreateTimeMillis() > t.getTimeout()*2L) {
                    isTimeOut = true;
                } else
                if (!t.isActive()&&System.currentTimeMillis() - t.getCreateTimeMillis() > t.getTimeout()) {
                    isTimeOut = true;
                }
                if (isTimeOut)
                {
                    super.remove(t.getTransactionId());
                    if (!t.wasCommitted()&&t.isSupportsSavePoints()) {
                        t.rollback();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("事务异常,检查是否事务超时",e);
            }

        }
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AbstractTransaction t : super.values()) {
            //超时或关闭的事务对象将删除，
            sb.append(t.toString());
        }
        return sb.toString();
    }

}
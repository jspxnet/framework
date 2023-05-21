package com.github.jspxnet.sober.exception;

public class RepeatBillNoException extends Exception {
    private final String tableName;
    private final String billNo;


    public RepeatBillNoException(String tableName,String billNo) {
        this.tableName = tableName;
        this.billNo = billNo;

    }


    @Override
    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] ses = super.getStackTrace();
        System.arraycopy(ses, 0, ses, 0, ses.length);

        StackTraceElement st1 = new StackTraceElement(tableName, "msg", "单据号重复:" + billNo, 1);

        return ses;
    }

}

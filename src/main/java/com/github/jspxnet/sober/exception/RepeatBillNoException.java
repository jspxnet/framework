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
        StackTraceElement[] result = new StackTraceElement[ses.length+1];
        System.arraycopy(ses, 0, result, 0, ses.length);
        StackTraceElement st1 = new StackTraceElement(tableName, "msg", "单据号重复:" + billNo, 1);
        result[ses.length] = st1;
        return result;
    }

}

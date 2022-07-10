package com.github.jspxnet.sober;

public interface Invocation {
    /**
     * 这里采用异常终止的方式
     * @return 返回查询对象
     * @throws Exception 异常
     */
    Object invoke() throws Exception;
}

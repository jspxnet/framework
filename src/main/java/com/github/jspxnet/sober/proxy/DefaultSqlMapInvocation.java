package com.github.jspxnet.sober.proxy;


import com.github.jspxnet.sober.Invocation;
import lombok.extern.slf4j.Slf4j;


/**
 * @author chenYuan
 */
@Slf4j
public class DefaultSqlMapInvocation implements Invocation {
    final private InterceptorProxy proxy;
    public DefaultSqlMapInvocation(InterceptorProxy proxy)
    {
        this.proxy = proxy;

    }


    /**
     * 这里采用异常终止的方式
     * @return 返回查询对象
     * @throws Exception 异常
     */
    @Override
    public Object invoke() throws Exception {
        try {
            return proxy.invoke();
        } finally {
            proxy.clean();
        }
    }
}

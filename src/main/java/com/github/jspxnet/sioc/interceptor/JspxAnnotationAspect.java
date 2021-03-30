package com.github.jspxnet.sioc.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/30 23:26
 * description: 让spring支持jspx sqlMap注释功能
 * 使用方法在spring总载入就可以了
 * <pre>
 * {@code
 *  Import(JspxAnnotationAspect.class})
 * }</pre>
 **/
@Aspect
@Component
@Slf4j
public class JspxAnnotationAspect {
    @Pointcut("@annotation(com.github.jspxnet.sober.annotation.SqlMap) || @annotation(com.github.jspxnet.txweb.annotation.Transaction)")
    private void jspxAnnotMethod() {

    }

    /**
     * 捕获请求和响应
     *
     * @param call spring JoinPoint
     * @return 拦截返回
     * @throws Throwable 异常
     */
    @Around(value = "jspxAnnotMethod()")
    public Object doAround(ProceedingJoinPoint call) throws Throwable {
        return SpringMethodInterceptor.springInvoke(call);
    }
}

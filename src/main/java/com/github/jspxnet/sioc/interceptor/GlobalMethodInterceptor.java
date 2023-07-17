package com.github.jspxnet.sioc.interceptor;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.annotation.SqlMap;
import com.github.jspxnet.sober.enums.PropagationEnumType;
import com.github.jspxnet.sober.exception.TransactionException;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.transaction.TransactionController;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.txweb.annotation.Transaction;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/10/18 1:27
 * description: 因为多层嵌套代理问题比较多,这里就是用一个代理来处理
 * </p>
 **/
@Slf4j
public class GlobalMethodInterceptor implements MethodInterceptor {

    private Class<?> targetClass;

    /**
     * @param targetObject 对象
     * @return 代理对象
     */
    public Object getProxyInstance(Object targetObject) {
        return getProxyInstance(targetObject.getClass());
    }

    /**
     * @param cls 对象
     * @return 代理对象
     */
    public Object getProxyInstance(Class<?> cls) {
        //传入用户类
        this.targetClass = cls;
        //Enhancer是cglib的核心类
        Enhancer enhancer = new Enhancer();
        // 将用户类设为 Enhancer对象的superclass属性,,即设为 Enhancer对象的父类
        enhancer.setSuperclass(targetClass);
        // 设 Enhancer对象的Callbacks属性,要求必须是Callback接口类型
        enhancer.setCallback(this);
        enhancer.setUseFactory(true);
        return enhancer.create();  //生成代理对象
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy) throws Throwable {
        if ("toString".equals(method.getName())) {
            return targetClass.toString();
        }
        if ("getClass".equals(method.getName())) {
            return targetClass;
        }
        Method exeMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());

        SqlMap sqlMap = exeMethod.getAnnotation(SqlMap.class);
        com.github.jspxnet.sober.Transaction invokeTransaction = null;
        Transaction transaction = exeMethod.getAnnotation(Transaction.class);
        if (transaction != null) {
            invokeTransaction = getTransactionBegin(transaction, exeMethod);
        }
        Object result;
        try {
            if (sqlMap != null) {
                result = invokeSqlMap(targetClass, obj, arg, proxy, sqlMap, exeMethod);
            } else {
                result = proxy.invokeSuper(obj, arg);
            }
            if (transaction != null && invokeTransaction != null) {
                invokeTransaction.commit();
            }
        } catch (Exception e) {
            Exception newException = e;
            if (transaction != null && invokeTransaction != null) {
                invokeTransaction.rollback();
                if (!StringUtil.isNull(transaction.message())) {
                    newException = new TransactionException(e, transaction, transaction.message());
                }
            }
            throw newException;
        }


        return result;
    }


    private static com.github.jspxnet.sober.Transaction getTransactionBegin(Transaction transaction, Method exeMethod) throws Throwable {
        //没有配置的情况,直接提示执行返回begin
        if (transaction == null) {
            return null;
        }
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        TransactionController transactionController = beanFactory.getBean(TransactionController.class, Sioc.global);
        if (transactionController == null) {
            log.error("TransactionController 事务控制器没有配置,不能开启事务处理");
            return null;
        }
        //没有配置的情况,直接提示执行返回end
        //这里开始事务处理
        com.github.jspxnet.sober.Transaction invokeTransaction = transactionController.createTransaction();
        if (PropagationEnumType.NEW.equals(transaction.propagation())) {
            invokeTransaction.reset();
        }
        invokeTransaction.begin();
        return invokeTransaction;
    }

    private static Object invokeSqlMap(Class<?> targetClass, Object obj, Object[] arg, MethodProxy proxy, SqlMap sqlMap, Method exeMethod) throws Throwable {
        String exeId = sqlMap.id();
        if (StringUtil.isNull(exeId)) {
            exeId = ClassUtil.getImplements(targetClass).getName() + StringUtil.DOT + exeMethod.getName();
        }
        String namespace = sqlMap.namespace();
        if (StringUtil.isEmpty(namespace)) {
            Bean bean = targetClass.getAnnotation(Bean.class);
            if (bean != null) {
                namespace = bean.namespace();
            }
        }
        proxy.invokeSuper(obj, arg);
        SoberSupport soberSupport = (SoberSupport) obj;
        SqlMapConf sqlMapConf = soberSupport.getBaseSqlMap().getSqlMapConf(namespace,exeId,sqlMap.execute(),sqlMap);
        return SoberUtil.invokeSqlMapInvocation(soberSupport,arg,sqlMapConf,exeMethod);
    }
}

package com.github.jspxnet.sioc.interceptor;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.annotation.SqlMap;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.PropagationEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.sober.exception.TransactionException;
import com.github.jspxnet.sober.transaction.TransactionController;
import com.github.jspxnet.txweb.annotation.Transaction;
import com.github.jspxnet.txweb.result.RocException;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/18 1:27
 * description: 因为多层嵌套代理问题比较多,这里就是用一个代理来处理
 **/
@Slf4j
public class GlobalMethodInterceptor implements MethodInterceptor  {

    private Class<?> targetClass;
    /**
     *
     * @param targetObject 对象
     * @return 代理对象
     */
    public Object getProxyInstance(Object targetObject){
        //传入用户类
        this.targetClass = targetObject.getClass();
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
    public Object intercept(Object obj, Method method, Object[] arg,MethodProxy proxy) throws Throwable {
        Method exeMethod = targetClass.getMethod(method.getName(),method.getParameterTypes());
        Transaction transaction = exeMethod.getAnnotation(Transaction.class);
        SqlMap sqlMap = exeMethod.getAnnotation(SqlMap.class);
        Object result;
        com.github.jspxnet.sober.Transaction invokeTransaction = null;
        if (transaction!=null)
        {
            invokeTransaction = getTransactionBegin(transaction,exeMethod);
        }
        try
        {
            if (sqlMap!=null)
            {
                result = invokeSqlMap(targetClass, obj,  arg, proxy,sqlMap,exeMethod);
            } else
            {
                result = proxy.invokeSuper(obj, arg);
            }
            if (transaction!=null&&invokeTransaction!=null)
            {
                invokeTransaction.commit();
            }
        } catch (Throwable e)
        {
            if (transaction!=null&&invokeTransaction!=null)
            {
                invokeTransaction.rollback();
            }
            if (e instanceof RocException)
            {
                throw e;
            }
            throw new TransactionException(e,transaction,exeMethod.getName());
        }
        return result;
    }


    private static com.github.jspxnet.sober.Transaction getTransactionBegin(Transaction transaction,Method exeMethod) throws Throwable {
        //没有配置的情况,直接提示执行返回begin
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        TransactionController transactionController = beanFactory.getBean(TransactionController.class, Sioc.global);
        if (transactionController==null)
        {
            log.error("TransactionController 事务控制器没有配置,不能开启事务处理");
            return null;
        }
        //没有配置的情况,直接提示执行返回end
        //这里开始事务处理
        com.github.jspxnet.sober.Transaction invokeTransaction = null;
        invokeTransaction = transactionController.createTransaction();
        if (PropagationEnumType.NEW.equals(transaction.propagation())) {
            invokeTransaction.reset();
        }
        invokeTransaction.begin();
        return invokeTransaction;
    }

    private static Object invokeSqlMap(Class<?> targetClass,Object obj, Object[] arg,MethodProxy proxy,SqlMap sqlMap,Method exeMethod) throws Throwable {
        String exeId = sqlMap.id();
        if (StringUtil.isNull(exeId))
        {
            exeId = ClassUtil.getImplements(targetClass).getName() + "." +exeMethod.getName();
        }
        proxy.invokeSuper(obj, arg);
        //这里开始事务处理
        SoberSupport soberSupport = (SoberSupport)obj;
        if (ExecuteEnumType.QUERY.equals(sqlMap.execute()))
        {
            Class<?> cls = null;
            Map<String,Object> valueMap = null;
            Object currentPageObj = ClassUtil.getParameterValue(exeMethod,sqlMap.currentPage(),arg);
            Object countObj = ClassUtil.getParameterValue(exeMethod,sqlMap.count(),arg);
            Integer currentPage = currentPageObj!=null?ObjectUtil.toInt(currentPageObj):null;
            Integer totalCount = countObj!=null?ObjectUtil.toInt(countObj):null;
            if (!ObjectUtil.isEmpty(arg))
            {
                for (Object o:arg)
                {
                    if ((o instanceof Map))
                    {
                        if (valueMap==null)
                        {
                            valueMap = ( Map)o;
                        } else
                        {
                            valueMap.putAll(( Map)o);
                        }
                    } else
                    if (currentPage==null&&( o instanceof Number))
                    {
                        log.debug("分页参数,页数请是用标准名称:" + sqlMap.currentPage());
                        currentPage = ((Number)o).intValue();
                    } else
                    if (totalCount==null&&(o instanceof Number))
                    {
                        log.debug("分页参数,返回行数请是用标准名称:" + sqlMap.count());
                        totalCount =  ((Number)o).intValue();
                    }
                    if (cls==null&&(o instanceof Class))
                    {
                        cls = (Class<?>)o;
                    }
                }
                //对象方式
                if (valueMap==null)
                {
                    for (Object o:arg)
                    {
                        if (o ==null)
                        {
                            continue;
                        }
                       if (o instanceof Serializable)
                       {
                           valueMap = ObjectUtil.getMap(o);
                           break;
                       }
                    }
                }
            }
            if (cls==null)
            {
                cls = exeMethod.getReturnType();
                if (Collection.class.isAssignableFrom(cls))
                {
                    cls = null;
                }
            }
            if (QueryModelEnumType.SINGLE.equals(sqlMap.mode()))
            {
                if (cls!=null&&ClassUtil.isStandardType(cls))
                {
                    Object result = soberSupport.buildSqlMap().getUniqueResult(sqlMap.namespace(),exeId,valueMap);
                    return BeanUtil.getTypeValue(result,cls);
                }
                List<?> list = soberSupport.buildSqlMap().query(sqlMap.namespace(),exeId,valueMap,1,1,sqlMap.nexus(),false,cls);
                if (list==null||list.isEmpty())
                {
                    return null;
                }
                return list.get(0);
            }

            if (QueryModelEnumType.COUNT.equals(sqlMap.mode()))
            {
                Object result = soberSupport.buildSqlMap().queryCount(sqlMap.namespace(),exeId,valueMap);
                if (cls!=null&&ClassUtil.isStandardType(cls))
                {
                    return BeanUtil.getTypeValue(result,cls);
                }
                return result;
            }
            if (currentPage==null)
            {
                currentPage  = 1;
            }
            if (totalCount==null)
            {
                totalCount = soberSupport.getMaxRows();
            }
            return soberSupport.buildSqlMap().query(sqlMap.namespace(),exeId,valueMap,currentPage,totalCount,sqlMap.nexus(),cls);
        }

        if (ExecuteEnumType.UPDATE.equals(sqlMap.execute()))
        {
            Map<String,Object> valueMap = null;
            if (!ObjectUtil.isEmpty(arg))
            {
                for (Object o:arg)
                {
                    if (valueMap==null &&(o instanceof Map))
                    {
                        valueMap = (Map)o;
                    }
                }
            }
            return soberSupport.buildSqlMap().update(sqlMap.namespace(),exeId,valueMap);
        }

        if (ExecuteEnumType.EXECUTE.equals(sqlMap.execute()))
        {
            Map<String,Object> valueMap = null;
            if (!ObjectUtil.isEmpty(arg))
            {
                for (Object o:arg)
                {
                    if (valueMap==null &&(o instanceof Map))
                    {
                        valueMap = (Map)o;
                    }
                }
            }
            return soberSupport.buildSqlMap().execute(sqlMap.namespace(),exeId,valueMap);
        }
        return null;
    }



}

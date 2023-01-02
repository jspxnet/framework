package com.github.jspxnet.sober.proxy;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sober.Interceptor;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.dialect.Dialect;
import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class InterceptorProxy implements Serializable {
    public static final String KEY_NAMESPACE = "namespace";
    public static final String KEY_EXE_ID = "exeId";
    public static final String KEY_CURRENT_PAGE = "currentPage";
    public static final String KEY_COUNT = "count";
    //public static final String KEY_NEXUS = "nexus";
    public static final String KEY_RETURN_CLASS = "returnClass";
    public static final String KEY_QUERY_MODEL_ENUM_TYPE = "QueryModelEnumType";
    public static final String KEY_LOAD_CHILD = "loadChild";
    public static final String KEY_ROLL_ROWS = "rollRows";


    private final LinkedList<Interceptor> interceptors;
    private final SoberSupport soberSupport;
    private final Map<String, Object> valueMap = new HashMap<>();
    private final SqlMapConf sqlMapConf;



    public InterceptorProxy(SoberSupport soberSupport, SqlMapConf sqlMapConf,Map<String, Object> valueMap)
    {
        this.soberSupport = soberSupport;
        this.sqlMapConf = sqlMapConf;
        interceptors = builderInterceptor(sqlMapConf.getInterceptorConfList());
        this.valueMap.putAll(valueMap);

        this.valueMap.put(KEY_QUERY_MODEL_ENUM_TYPE, sqlMapConf.getQueryModel());
        this.valueMap.put(KEY_NAMESPACE, sqlMapConf.getNamespace());
        this.valueMap.put(KEY_EXE_ID, sqlMapConf.getName());
        this.valueMap.put(KEY_LOAD_CHILD, ObjectUtil.toBoolean(sqlMapConf.getNexus()));
        this.valueMap.put(Dialect.KEY_DATABASE_NAME, soberSupport.getSoberFactory().getDatabaseName());
    }

    public LinkedList<Interceptor> builderInterceptor(LinkedList<SqlMapInterceptorConf> confList)
    {
        LinkedList<Interceptor> list = new LinkedList<>();
        if (!ObjectUtil.isEmpty(confList))
        {
            BeanFactory beanFactory = EnvFactory.getBeanFactory();
            for (SqlMapInterceptorConf conf:confList)
            {
                if (conf==null)
                {
                    continue;
                }
                Interceptor interceptor;
                if (conf.getName()!=null&&conf.getName().contains(StringUtil.AT))
                {
                    interceptor = (Interceptor)beanFactory.getBean(conf.getName());
                } else
                {
                    interceptor = (Interceptor)beanFactory.getBean(conf.getName(),conf.getNamespace());
                }
                if (interceptor!=null)
                {
                    list.addLast(interceptor);
                }
            }
        }
        return list;
    }

    /**
     *
     * @param soberSupport 数据库对象
     * @param arg  参赛
     * @param sqlMapConf sql配置
     * @param exeMethod  执行方法
     */
    public InterceptorProxy(SoberSupport soberSupport, Object[] arg, SqlMapConf sqlMapConf, Method exeMethod)
    {
        this.soberSupport = soberSupport;
        this.sqlMapConf = sqlMapConf;
        interceptors = builderInterceptor(sqlMapConf.getInterceptorConfList());

        valueMap.put(KEY_QUERY_MODEL_ENUM_TYPE, sqlMapConf.getQueryModel());
        valueMap.put(KEY_NAMESPACE, sqlMapConf.getNamespace());
        valueMap.put(KEY_EXE_ID, sqlMapConf.getName());
        valueMap.put(KEY_LOAD_CHILD, ObjectUtil.toBoolean(sqlMapConf.getNexus()));

        if (ExecuteEnumType.QUERY.getValue()==sqlMapConf.getExecuteType())
        {
            //带分页和返回类型的参数
            Class<?> cls = SoberUtil.getResultClass(sqlMapConf.getResultType(),exeMethod.getReturnType()) ;
            Object currentPageObj = ClassUtil.getParameterValue(exeMethod, sqlMapConf.getCurrentPage(), arg);
            Object countObj = ClassUtil.getParameterValue(exeMethod, sqlMapConf.getCount(), arg);
            Integer currentPage = currentPageObj != null ? ObjectUtil.toInt(currentPageObj) : null;
            Integer count = countObj != null ? ObjectUtil.toInt(countObj) : null;
            if (!ObjectUtil.isEmpty(arg)) {
                boolean haveMap = false;
                for (Object o : arg) {
                    if ((o instanceof Map)) {
                        valueMap.putAll((Map) o);
                        haveMap = true;
                    } else if (currentPage == null && (o instanceof Number)) {
                        log.debug("分页参数,页数请是用标准名称:" + sqlMapConf.getCurrentPage());
                        currentPage = ((Number) o).intValue();
                    } else if (count == null && (o instanceof Number)) {
                        log.debug("分页参数,返回行数请是用标准名称:" + sqlMapConf.getCount());
                        count = ((Number) o).intValue();
                    }
                    if (cls == null && (o instanceof Class)) {
                        cls = (Class<?>) o;
                    }
                }
                //构造参数对象
                if (!haveMap) {
                    //执行到这里说明一直都没用标准的参数进入，在这里构造参数列表
                    Map<String, Type> parameterMap = ClassUtil.getParameterNames(exeMethod);
                    if (parameterMap != null) {
                        for (String key : parameterMap.keySet()) {
                            valueMap.put(key, ClassUtil.getParameterValue(exeMethod, key, arg));
                        }
                    }
                }
            }
            if (cls == null) {
                cls = exeMethod.getReturnType();
                if (ClassUtil.isCollection(cls)) {
                    cls = null;
                }
            }

            //开始载入拦截器---------------
            if (currentPage!=null)
            {
                valueMap.put(KEY_CURRENT_PAGE, currentPage);
            }
            if (count!=null)
            {
                valueMap.put(KEY_COUNT, count);
            }
            valueMap.put(KEY_RETURN_CLASS, cls);
        }
        else
        {
            //非查询方式
            boolean haveMap = false;
            if (!ObjectUtil.isEmpty(arg)) {
                for (Object o : arg) {
                    if ((o instanceof Map)) {
                        valueMap.putAll((Map) o);
                        haveMap = true;
                    }
                }
            }
            if (!haveMap)
            {
                Map<String, Type> parameterMap = ClassUtil.getParameterNames(exeMethod);
                if (parameterMap != null) {
                    for (String key : parameterMap.keySet()) {
                        valueMap.put(key, ClassUtil.getParameterValue(exeMethod, key, arg));
                    }
                }
            }
        }
    }

    /**
     * 这里采用异常终止的方式
     * @return 返回查询对象
     * @throws Exception 异常
     */

    public  Object invoke() throws Exception {
        for (Interceptor interceptor:interceptors)
        {
            interceptor.init();
            interceptor.before(soberSupport,valueMap);
        }
        Object result = invokeSqlMap();
        for (Interceptor interceptor:interceptors)
        {
            result = interceptor.after(soberSupport,valueMap,result);
            interceptor.destroy();
        }
        return result;
    }
    /**
     *
     * @return 查询接口
     * @throws Exception 异常
     */
    public Object invokeSqlMap() throws Exception {
        Class<?> cls = (Class<?>) valueMap.get(InterceptorProxy.KEY_RETURN_CLASS);
        if (cls==null && !StringUtil.isNull(sqlMapConf.getResultType()))
        {
            cls = SoberUtil.getResultClass (sqlMapConf.getResultType(), DataMap.class);
        }
        if (ExecuteEnumType.UPDATE.getValue()==sqlMapConf.getExecuteType())
        {
            return soberSupport.getBaseSqlMap().update(sqlMapConf, valueMap);
        }
        if (ExecuteEnumType.EXECUTE.getValue()==sqlMapConf.getExecuteType())
        {
            return soberSupport.getBaseSqlMap().execute(sqlMapConf, valueMap);
        }
        if (QueryModelEnumType.SINGLE.getValue()==sqlMapConf.getQueryModel())
        {
            if (cls != null && ClassUtil.isStandardType(cls)) {
                Object result = soberSupport.getBaseSqlMap().getUniqueResult(sqlMapConf, valueMap);
                return BeanUtil.getTypeValue(result, cls);
            }
            List<?> list = soberSupport.getBaseSqlMap().query(sqlMapConf, valueMap, 1, 1,  false, cls);
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.get(0);
        }

        if (QueryModelEnumType.COUNT.getValue()== sqlMapConf.getQueryModel()) {
            Object result = soberSupport.getBaseSqlMap().queryCount(sqlMapConf, valueMap);
            if (cls != null && ClassUtil.isStandardType(cls)) {
                return BeanUtil.getTypeValue(result, cls);
            }
            return result;
        }

        //valueMap 是根据 sqlMapConf 来的
        int currentPage = ObjectUtil.toInt(valueMap.get(InterceptorProxy.KEY_CURRENT_PAGE)==null?1:valueMap.get(InterceptorProxy.KEY_CURRENT_PAGE));
        int count = ObjectUtil.toInt(valueMap.get(InterceptorProxy.KEY_COUNT)==null?soberSupport.getMaxRows():valueMap.get(InterceptorProxy.KEY_COUNT));
        boolean rollRows =  ObjectUtil.toBoolean(valueMap.get(InterceptorProxy.KEY_ROLL_ROWS));
        return soberSupport.getBaseSqlMap().query(sqlMapConf, valueMap, currentPage, count, rollRows, cls);
    }

    /**
     * 清理变量
     */
    public void clean()
    {
        valueMap.clear();
    }




}

/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.io.jar.ClassScannerUtils;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.config.ConfigureContext;
import com.github.jspxnet.sioc.tag.BeanElement;
import com.github.jspxnet.sioc.util.AnnotationUtil;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.vo.OperateVo;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.txweb.view.OperateComparator;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-5
 * Time: 15:50:50
 */
@Slf4j
public class TXWebConfigManager implements WebConfigManager {

    private static WebConfigManager instance;
    //Map<String, ActionConfigBean> 这里使用Map主要是方便后边查询名称
    static private Map<String, Map<String, ActionConfigBean>> configTable;
    static private Map<String, String> extendMap;
    static private Map<String, List<DefaultInterceptorBean>> defaultInterceptorMap;
    //static private Map<String, List<DefaultUrlInterceptorBean>> defaultUrlInterceptorMap;
    static private Map<String, List<ResultConfigBean>> defaultResultMap;

    static final private ActionConfigBean DEFAULT_ACTION_CONFIG = new ActionConfigBean();

    static {
        DEFAULT_ACTION_CONFIG.setActionName(StringUtil.ASTERISK);
        DEFAULT_ACTION_CONFIG.setClassName(com.github.jspxnet.txweb.support.DefaultTemplateAction.class.getName());
        DEFAULT_ACTION_CONFIG.setCaption("默认模板");
        DEFAULT_ACTION_CONFIG.setMobile(true);
        DEFAULT_ACTION_CONFIG.setIocBean(com.github.jspxnet.txweb.support.DefaultTemplateAction.class.getName());
    }

    //这里保存已经扫描过的包，避免重复扫描
    static private List<String> scanPackageList;
    //static private Configuration configuration;

    //读取锁 begin


    //读取锁 end

    public static WebConfigManager getInstance() {
        if (instance == null) {
            synchronized (TXWebConfigManager.class) {
                instance = new TXWebConfigManager();
            }
        }
        return instance;
    }

    private TXWebConfigManager() {
        configTable = new HashMap<>();
        extendMap = new HashMap<>();
        defaultInterceptorMap = new HashMap<>();

        defaultResultMap = new HashMap<>();
        scanPackageList = new ArrayList<>();
    }

    /**
     * 从新载入配置文件
     */
    @Override
    synchronized public void clear() {
        if (configTable.isEmpty()) {
            return;
        }
        configTable.clear();
        defaultInterceptorMap.clear();
        defaultResultMap.clear();
        scanPackageList.clear();

        JSCacheManager.removeAll(OperateVo.class);
        //restFullPath.clear();
        extendMap.clear();
        //权限资源也从新载入
        if (log.isDebugEnabled()) {
            log.debug("TXWeb config clear");
        }
    }

    /**
     * 总表,触发扫描动作
     *
     * @return 得到配置列表
     */
    @Override
    public Map<String, Map<String, ActionConfigBean>> getConfigTable() {
        return configTable;
    }

    /**
     * 检查师傅已经载入
     */
    @Override
    synchronized public void checkLoad() {
        if (!configTable.isEmpty()) {
            return;
        }
        //1.初始化所有配置动作

        scanPackageList.clear();
        Configuration configuration = DefaultConfiguration.getInstance();
        try {
            configTable.putAll(configuration.loadConfigMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        extendMap.clear();
        extendMap.putAll(configuration.getExtendMap());

        defaultInterceptorMap.clear();
        defaultInterceptorMap.putAll(configuration.getDefaultInterceptorMap());

        defaultResultMap.clear();
        defaultResultMap.putAll(configuration.getDefaultResultMap());


        //2.生成restFull路径
        for (String namespace : configTable.keySet()) {
            Map<String, ActionConfigBean> map = configTable.get(namespace);
            if (map != null) {
                for (ActionConfigBean actionConfigBean : map.values()) {
                    if (actionConfigBean == null) {
                        log.error("出现空：ActionConfigBean for " + namespace);
                    } else if (!StringUtil.hasLength(actionConfigBean.getActionName())) {
                        log.error("发现配置中存在错误,不能找到name:" + actionConfigBean.toString());
                    } else {
                        initActionConfigBean(actionConfigBean, namespace);
                    }
                }
            }
        }
        //扫描注释中的HttpMethod,补充载入进来

        List<ScanConfig> scanConfigList = configuration.getScanPackageList();
        for (ScanConfig scanConfig : scanConfigList) {
            if (scanPackageList.contains(scanConfig.getPackageName())) {
                continue;
            }
            scanPackageList.add(scanConfig.getPackageName());
            log.debug("start san action package " + scanConfig.getPackageName());
            sanAction(scanConfig.getPackageName());
        }

        //补充ioc中扫描到的begin
        IocContext iocContext = ConfigureContext.getInstance();
        List<BeanElement> beanElements = iocContext.getElementList();
        for (BeanElement beanElement : beanElements) {
            Class<?> cls = null;
            try {
                cls = ClassUtil.loadClass(beanElement.getClassName());
                registerAction(cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log.error("配置错误,不能载入:{}", beanElement.getClassName());
            }

        }
        //补充ioc中扫描到的end
    }

    /**
     * 初始化actionBean XML配置外的其他信息
     *
     * @param actionConfigBean TXWeb配置对象
     * @param namespace        命名空间
     */
    private static void initActionConfigBean(ActionConfigBean actionConfigBean, String namespace) {
        String className = null;
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        IocContext iocContext = beanFactory.getIocContext();
        try {
            BeanElement beanElement = iocContext.getBeanElement(actionConfigBean.getIocBean(), namespace);
            if (beanElement == null) {
                log.error("namespace:" + namespace + " not found bean element:" + actionConfigBean.getIocBean());
                throw new ClassNotFoundException("namespace:" + namespace + " not found bean element:" + actionConfigBean.getIocBean() + " roc is \r\n" + actionConfigBean.toString());
            }
            className = beanElement.getClassName();
            Class<?> cla = Class.forName(StringUtil.trim(className));

            actionConfigBean.setClassName(cla.getName());
            //这里要检查放入 HttpMethod 的caption ,这载入的都是手工配置的，如果是动态载入的，在后边配置

            //兼容性配置begin
            HttpMethod httpMethod = cla.getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                if (StringUtil.isNull(actionConfigBean.getIocBean())) {
                    actionConfigBean.setIocBean(AnnotationUtil.getBeanId(cla));
                }
                if (StringUtil.isNull(actionConfigBean.getCaption())) {
                    actionConfigBean.setCaption(httpMethod.caption());
                }
                if ((StringUtil.isNull(beanElement.getNamespace()) || Sioc.global.equals(beanElement.getNamespace())) && !StringUtil.isNull(httpMethod.namespace())) {
                    beanElement.setNamespace(httpMethod.namespace());
                }
                if (StringUtil.isNull(actionConfigBean.getCaption())) {
                    log.warn("className " + className + " httpMethod  not write caption");
                }
            }
            //兼容性配置end
        } catch (Exception e) {
            log.error("className not found " + className + " action config is " + actionConfigBean.toString(), e);
        }
    }

    /**
     * 扫描要加载的类对象
     *
     * @param className 类路径
     */
    @Override
    public void sanAction(String className) {
        Set<Class<?>> list = ClassScannerUtils.searchClasses(className,EnvFactory.getBaseConfiguration().getDefaultPath());
        for (Class<?> cls : list) {
            if (cls == null) {
                continue;
            }
            try {
                registerAction(cls);
            } catch (Exception e) {
                log.error("txweb config load class dir error" + cls, e);
            }
        }
    }

    /**
     * 动态注册
     *
     * @param cla 类对象
     */
    @Override
    public void registerAction(Class<?> cla) {

        if (cla.isInterface() || cla.isPrimitive() || cla.isAnonymousClass() || cla.isEnum()) {
            return;
        }
        HttpMethod httpMethod = cla.getAnnotation(HttpMethod.class);
        if (httpMethod == null) {
            return;
        }

        if (StringUtil.isEmpty(httpMethod.actionName())) {
            return;
        }

        ActionConfigBean actionConfigBean = new ActionConfigBean();
        actionConfigBean.setActionName(httpMethod.actionName());
        actionConfigBean.setCaption(httpMethod.caption());
        //actionConfigBean.setIocBean(cla.getName());
        actionConfigBean.setIocBean(AnnotationUtil.getBeanId(cla));
        actionConfigBean.setClassName(cla.getName());
        actionConfigBean.setMobile(httpMethod.mobile());
        actionConfigBean.setSecret(httpMethod.secret());
        actionConfigBean.setRegister(true);
        log.debug("register action package:\r\n{}",actionConfigBean.toString());
        Map<String, ActionConfigBean> actionConfigBeanMap = configTable.computeIfAbsent(httpMethod.namespace(), k -> new HashMap<>());
        actionConfigBeanMap.put(actionConfigBean.getActionName(), actionConfigBean);

        //检查放入继承关系
        String namespace = httpMethod.namespace();
        while (namespace.contains(StringUtil.BACKSLASH) && !extendMap.containsKey(namespace)) {
            String exNamespace = StringUtil.substringBeforeLast(namespace, StringUtil.BACKSLASH);
            extendMap.put(namespace, exNamespace);
            namespace = exNamespace;
        }
        if (!namespace.contains(StringUtil.BACKSLASH) && !extendMap.containsKey(namespace) && !TXWeb.global.equalsIgnoreCase(namespace)) {
            extendMap.put(namespace, TXWeb.global);
        }
    }

    /**
     * @param namespace 命名空间
     * @return 得到默认拦截器配置, 默认拦截器不使用继承方式
     */
    @Override
    public List<String> getDefaultInterceptors(String namespace)  {
        checkLoad();
        String nameK = StringUtil.fixedNamespace(namespace);
        if (StringUtil.isNull(nameK)) {
            nameK = TXWeb.global;
        }
        final List<String> result = new LinkedList<>();
        List<DefaultInterceptorBean> list = defaultInterceptorMap.get(nameK);
        if (list != null) {
            for (DefaultInterceptorBean defaultInterceptorBean : list) {
                result.add(defaultInterceptorBean.getName());
            }
        }

        int x = nameK.lastIndexOf(StringUtil.BACKSLASH);
        if (x > 1) {
            //子扩展
            while (!StringUtil.isNull(nameK) && nameK.contains(StringUtil.BACKSLASH) && !Sioc.global.equalsIgnoreCase(nameK)) {
                x = nameK.lastIndexOf(StringUtil.BACKSLASH);
                nameK = nameK.substring(0, x);
                if (StringUtil.isEmpty(nameK)) {
                    nameK = Sioc.global;
                }
                list = defaultInterceptorMap.get(nameK);
                if (list != null && !list.isEmpty()) {
                    for (DefaultInterceptorBean defaultInterceptorBean : list) {
                        if (defaultInterceptorBean.isExtend()) {
                            result.add(defaultInterceptorBean.getName());
                        }
                    }
                }
                if (!nameK.contains(StringUtil.BACKSLASH)) {
                    break;
                }
            }
        }
        return result;
    }


    /**
     * @return 得到命名空间列表
     */
    @Override
    public List<String> getNamespaceList() {
        checkLoad();
        List<String> result = new ArrayList<>();
        for (String key : extendMap.keySet()) {
            String v = extendMap.get(key);
            if (!result.contains(key)) {
                result.add(key);
            }
            if (!result.contains(v)) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     * 命名空间第一层表示软件名称
     *
     * @return 得到部署了那些软件
     */
    @Override
    public List<String> getSoftList() {
        List<String> result = new ArrayList<>();
        for (String key : extendMap.keySet()) {
            if (key != null && !key.contains(StringUtil.BACKSLASH) && !result.contains(key) && !TXWeb.global.equalsIgnoreCase(key)) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * @return 得到命名空间继承关系列表
     */
    @Override
    public Map<String, String> getExtendList()  {
        return extendMap;
    }

    /**
     * @param namespace 命名空间
     * @return 得到默认返回配置
     */
    @Override
    public List<ResultConfigBean> getDefaultResults(String namespace) {
        String nameKey = StringUtil.isNull(namespace) ? TXWeb.global : StringUtil.fixedNamespace(namespace);
        List<ResultConfigBean> result = defaultResultMap.get(nameKey);
        if (!StringUtil.hasLength(nameKey) || (result != null && !result.isEmpty())) {
            return result;
        }
        int x = nameKey.lastIndexOf(StringUtil.BACKSLASH);
        if (x > 1) {
            //子扩展
            nameKey = nameKey.substring(0, x);
            result = defaultResultMap.get(nameKey);
        }
        return result;
    }

    /**
     * @param namespace 命名空间列表
     * @return 得到本命名空间下的命名空间
     */
    @Override
    public List<String> getActionList(String namespace) {
        String nameKey = StringUtil.isNull(namespace) ? TXWeb.global : StringUtil.fixedNamespace(namespace);
        List<String> result = new ArrayList<>();
        for (String key : configTable.keySet()) {
            if (key.startsWith(nameKey)) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * 得到配置信息
     *
     * @param namePart  action name
     * @param namespace 命名控件
     * @param reload  是否重新载入
     * @return 配置
     */
    @Override
    public ActionConfig getActionConfig(String namePart, String namespace, boolean reload)  {

        if (namePart == null) {
            namePart = StringUtil.ASTERISK;
        }
        String nameKey = StringUtil.isNull(namespace) ? TXWeb.global : StringUtil.fixedNamespace(namespace);
        ActionConfigBean result = null;
        Map<String, ActionConfigBean> elementMap;
        while (!StringUtil.isNull(nameKey) && !Sioc.global.equalsIgnoreCase(nameKey)) {
            elementMap = configTable.get(nameKey);
            if (elementMap != null) {
                for (String name : elementMap.keySet()) {
                    //通配符* 放在最后判断，否则会替代前边的配置
                    if (!StringUtil.ASTERISK.equals(name) && StringUtil.getPatternFind(namePart, name)) {
                        result = elementMap.get(name);
                        result.setNamespace(nameKey);
                        return result;
                    }
                }
                result = elementMap.get(StringUtil.ASTERISK);
                if (result != null) {
                    result.setNamespace(nameKey);
                    return result;
                }
            }
            if (extendMap.containsKey(nameKey)) {
                nameKey = extendMap.get(nameKey);
            } else {
                if (nameKey.contains(StringUtil.BACKSLASH)) {
                    nameKey = StringUtil.substringBeforeLast(nameKey, StringUtil.BACKSLASH);
                } else {
                    nameKey = Sioc.global;
                }
            }
        }
        elementMap = configTable.get(Sioc.global);
        if (elementMap != null) {
            for (String name : elementMap.keySet()) {
                if (!StringUtil.ASTERISK.equals(name) && StringUtil.getPatternFind(namePart, name)) {
                    result = elementMap.get(name);
                    result.setNamespace(nameKey);
                    return result;
                }
            }
            result = elementMap.get(StringUtil.ASTERISK);
        }
        // 通过RestFull 内置方式得到
        if (result == null) {
            result = getActionConfigBean(namePart, namespace);
        }
        //如果都没有就使用默认的
        if (result == null && elementMap != null) {
            result = elementMap.get(StringUtil.ASTERISK);
        }
        if (reload && result == null && log.isDebugEnabled()) {
            log.debug("TXWeb config not find ActionConfigBean name:" + namePart + "  namespace: " + namespace);
        }
        if (result == null) {
            return DEFAULT_ACTION_CONFIG;
        }
        return result;
    }

    /**
     * @param namePart  文件名称部分
     * @param namespace 命名空间，也是RESTFull 的路径
     * @return 通过RESTFull 方式得到配置
     */
    private ActionConfigBean getActionConfigBean(String namePart, String namespace) {
        String nameKey = StringUtil.isNull(namespace) ? TXWeb.global : StringUtil.fixedNamespace(namespace);
        Map<String, ActionConfigBean> map = configTable.get(nameKey);
        if (map == null) {
            return null;
        }
        for (String key : map.keySet()) {
            ActionConfigBean actionConfigBean = map.get(key);
            if (namePart.matches(actionConfigBean.getActionName())) {
                return actionConfigBean;
            }
        }
        return null;
    }


    /**
     * 得到某命名空间里边的动作列表,让你能够在开发的过程中得到动作列表来判断权限
     *
     * @param namespace 命名空间
     * @return 动作配置列表
     */
    @Override
    public Map<String, ActionConfigBean> getActionMap(String namespace)  {

        String nameKey = StringUtil.isNull(namespace) ? TXWeb.global : StringUtil.fixedNamespace(namespace);
        Map<String, Map<String, ActionConfigBean>> fullConfigTable = getConfigTable();
        Map<String, ActionConfigBean> elementMap = fullConfigTable.get(nameKey);
        //多接一级命名空间方便 动态域名空间方式  begin
        int x = nameKey.lastIndexOf(StringUtil.BACKSLASH);
        if (elementMap == null && x > 1) {
            //子扩展
            nameKey = nameKey.substring(0, x);
            elementMap = fullConfigTable.get(nameKey);
        }
        return elementMap;
    }


    /**
     * @param namespace 命名空间
     * @return 操作列表
     * @throws Exception 异常
     */
    @Override
    public List<OperateVo> getOperateForNamespace(String namespace) throws Exception {
        //得到下架所有的命名空间begin
        List<String> namespaceList = new ArrayList<>();
        namespaceList.add(namespace);
        Map<String, String> extend = getExtendList();
        for (String key : extend.keySet()) {
            //这里不能够使用模糊判断
            if (key.startsWith(namespace)) {
                namespaceList.add(key);
            }
        }
        //得到下架所有的命名空间end
        //保存已经处理过的类

        List<OperateVo> result = new ArrayList<>();
        //保存格式action Name , 配置
        for (String childNamespace : namespaceList) {
            Map<String, ActionConfigBean> list = getActionMap(childNamespace);
            if (ObjectUtil.isEmpty(list)) {
                continue;
            }
            for (String key : list.keySet()) {
                ActionConfigBean actionBean = list.get(key);
                String className = actionBean.getClassName();
                if (StringUtil.isNull(className)) {
                    className = actionBean.getIocBean();
                    actionBean.setClassName(className);
                }

                Class<?> cls = ClassUtil.loadClass(className);
                Map<Operate, Method> operateMap = TXWebUtil.getClassOperateList(cls);
                //手动配置部分 begin
                if (!actionBean.isRegister()) {

                    if (StringUtil.isEmpty(actionBean.getMethod()) || actionBean.getMethod().contains("@")) {
                        String actionName = actionBean.getActionName();
                        boolean addExecute = true;
                        for (Operate operate : operateMap.keySet()) {
                            String tempAction = null;
                            if ("*".equals(actionName) && !StringUtil.isEmpty(operate.method()) && !operate.method().contains("${") && !operate.method().contains("}") && !"@".equals(operate.method())) {
                                tempAction = operate.method();
                            } else {
                                tempAction = actionName;
                            }
                            Method method = operateMap.get(operate);
                            OperateVo operateVO = new OperateVo();
                            operateVO.setCaption(actionBean.getCaption());
                            operateVO.setMethodCaption(StringUtil.isEmpty(operate.caption()) ? method.getName() : operate.caption());

                            operateVO.setActionName(tempAction);
                            operateVO.setClassName(cls.getName());
                            operateVO.setNamespace(childNamespace);
                            operateVO.setClassMethod(method.getName());
                            if (TXWebUtil.defaultExecute.equals(method.getName())) {
                                addExecute = false;
                            }
                            result.add(operateVO);
                        }

                        if (addExecute) {
                            //一个什么都没有的类，保留浏览控制 begin
                            OperateVo operateVO = new OperateVo();
                            operateVO.setCaption(actionBean.getCaption());
                            operateVO.setMethodCaption("浏览");
                            operateVO.setClassName(actionBean.getClassName());
                            operateVO.setActionName(actionBean.getActionName());
                            operateVO.setNamespace(childNamespace);
                            if (StringUtil.isNull(operateVO.getClassName())) {
                                operateVO.setClassName(actionBean.getIocBean());
                            }
                            operateVO.setClassMethod(TXWebUtil.defaultExecute);
                            result.add(operateVO);
                            //一个什么都没有的类，保留浏览控制 end
                        }
                    } else {
                        //单个的方法
                        OperateVo operateVO = new OperateVo();
                        operateVO.setCaption(actionBean.getCaption());
                        operateVO.setMethodCaption(actionBean.getCaption());
                        operateVO.setActionName(actionBean.getActionName());
                        operateVO.setClassName(actionBean.getClassName());
                        operateVO.setNamespace(childNamespace);
                        operateVO.setClassMethod(actionBean.getMethod());
                        if (StringUtil.isNull(operateVO.getClassName())) {
                            operateVO.setClassName(actionBean.getIocBean());
                        }
                        result.add(operateVO);

                    }


                    continue;
                }
                //手动配置部分 end


                String actionName = "*";
                HttpMethod httpMethod = cls.getAnnotation(HttpMethod.class);
                if (httpMethod != null) {
                    actionName = httpMethod.actionName();
                }
                for (Operate operate : operateMap.keySet()) {
                    String tempAction = null;
                    if ("*".equals(actionName) && !StringUtil.isEmpty(operate.method()) && !operate.method().contains("${") && !operate.method().contains("}") && !"@".equals(operate.method())) {
                        tempAction = operate.method();
                    } else {
                        tempAction = actionName;
                    }
                    Method method = operateMap.get(operate);
                    OperateVo operateVO = new OperateVo();
                    operateVO.setCaption(actionBean.getCaption());
                    operateVO.setMethodCaption(StringUtil.isEmpty(operate.caption()) ? method.getName() : operate.caption());
                    operateVO.setActionName(tempAction);
                    operateVO.setClassName(cls.getName());
                    operateVO.setNamespace(childNamespace);
                    operateVO.setClassMethod(method.getName());
                    result.add(operateVO);
                }
            }
        }
        return result;
    }

    /**
     * @param namespace 支持多个是用 ; 分割
     * @return 操作列表, 包含继承的动作 /user/xxxx  条件为user
     * @throws Exception 异常
     */
    @Override
    public List<OperateVo> getOperateList(String namespace) throws Exception {

        //为了区分分布式多处部署
        String cacheKey = namespace + "_" + IpUtil.getIpEnd();
        List<OperateVo> list = (List<OperateVo>) JSCacheManager.get(OperateVo.class, cacheKey);
        if (!ObjectUtil.isEmpty(list)) {
            return list;
        }
        Map<String, OperateVo> checkMap = new HashMap<>();
        String[] spaceList = StringUtil.split(namespace, StringUtil.SEMICOLON);
        for (String space : spaceList) {
            List<OperateVo> voList = getOperateForNamespace(space);
            if (!ObjectUtil.isEmpty(voList)) {
                for (OperateVo vo : voList) {
                    String id = vo.getId();
                    if (checkMap.containsKey(id)) {
                        continue;
                    }
                    checkMap.put(id, vo);
                }
            }
        }

        List<OperateVo> resultList = new ArrayList<>(checkMap.size());
        resultList.addAll(checkMap.values());
        Collections.sort(resultList, new OperateComparator());
        JSCacheManager.put(OperateVo.class, cacheKey, resultList);
        return resultList;
    }

    /**
     * 通过id得到 操作方法
     *
     * @param namespace 命名空间
     * @param id        id
     * @return 操作方法
     * @throws Exception 异常
     */
    @Override
    public OperateVo getOperate(String namespace, String id) throws Exception {
        List<OperateVo> list = getOperateList(namespace);
        for (OperateVo operateVO : list) {
            if (operateVO.getId().equals(id)) {
                return operateVO;
            }
        }
        return null;
    }

}
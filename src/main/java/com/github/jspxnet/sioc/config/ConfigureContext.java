/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.config;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.io.jar.ClassScannerUtils;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.RpcClient;
import com.github.jspxnet.sioc.tag.*;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.AnnotationUtil;
import com.github.jspxnet.sioc.util.Empty;
import com.github.jspxnet.utils.*;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.core.TagNode;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 12:27:19
 * 1.0
 *
 * @author chenYuan
 */
@Slf4j
public class ConfigureContext implements IocContext {
    private static final IocContext INSTANCE = new ConfigureContext();
    //永远不清除，并且保留,这里是动态注册的bean对象数据
    private final Map<String, Map<String, BeanElement>> registerBeanMap = new HashMap<>();

    private final transient List<BeanElement> injectionBeanElements = new ArrayList<>();
    //永远不清除这里表示动态扫描加载的目录，如果已经扫描了的，就不在扫描加载
    private final List<String> scanPackageList = new ArrayList<>();

    /**
     * 设计到定时任务的类注册
     * 放入格式，<bean名称，命名空间>
     */
    private final transient Map<String, String> schedulerMap = new HashMap<>();

    private final Map<String, Map<String, BeanElement>> beanElementMap = new HashMap<>();
    //总表
    private final List<BeanElement> elementList = new ArrayList<>();
    //继承关系表
    // key ext subName
    private final Map<String, String> extendMap = new HashMap<>();
    private final Map<String, String> applicationMap = new HashMap<>();

    private final EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
    private String[] configFile;
    private static final List<String> FILE_ID_LIST = new ArrayList<>();

    public static IocContext getInstance() {
        return INSTANCE;
    }

    private ConfigureContext() {

    }


    /**
     * {@code
     * Map<String,Map<String,BeanElement>>  Map<命名空间,Map<bean name,BeanElement>>
     * }
     * 得到bean 命名空间
     *
     * @throws Exception 异常
     */

    @Override
    public void reload() throws Exception {
        log.debug("ioc reload " + ObjectUtil.toString(configFile));
        synchronized (ConfigureContext.class) {
            for (String namespace : beanElementMap.keySet()) {
                Map<String, BeanElement> beanMap = beanElementMap.get(namespace);
                beanMap.clear();
            }
            beanElementMap.clear();
            extendMap.clear();
            elementList.clear();
            applicationMap.clear();
            injectionBeanElements.clear();

            List<TagNode> iocElements = getIocElements();
            for (TagNode element : iocElements) {
                SiocElement siocElement = (SiocElement) element;
                String namespace = StringUtil.fixedNamespace(siocElement.getNamespace());
                String extend = siocElement.getExtends();
                String appName = siocElement.getApplication();
                if (!StringUtil.isNull(appName)) {
                    applicationMap.put(namespace, appName);
                }
                try {

                    extendMap.put(namespace, extend);
                    Map<String, BeanElement> beanElements = beanElementMap.computeIfAbsent(namespace, k -> new HashMap<>());

                    List<TagNode> beanLists = siocElement.getBeanElements();
                    for (TagNode aElement : beanLists) {
                        BeanElement beanElement = (BeanElement) aElement;
                        beanElements.put(beanElement.getId(), beanElement);
                    }
                } catch (Exception e)
                {
                    log.error("载入配置错误,appName:{},namespace:{},extend:{}",appName,namespace,extend);
                }

            }
            iocElements.clear();
            //gc的时候修复
            if (!registerBeanMap.isEmpty()) {
                for (String namespace : registerBeanMap.keySet()) {
                    Map<String, BeanElement> regBeanMap = beanElementMap.get(namespace);
                    if (regBeanMap != null && !regBeanMap.isEmpty()) {
                        beanElementMap.put(namespace, registerBeanMap.get(namespace));
                    }
                }
            }

            //扫描目录加载注释的IocBean
            for (String classPath : scanPackageList) {
                sanIocBean(StringUtil.trim(classPath));
            }

            //生成总列表,同时把需要注入的bean过滤出来
            for (String namespace : beanElementMap.keySet()) {
                Map<String, BeanElement> map = beanElementMap.get(namespace);
                if (map != null) {
                    for (BeanElement beanElement : map.values()) {
                        beanElement.setNamespace(namespace);
                        elementList.add(beanElement);
                        if (!StringUtil.isNull(beanElement.getInjection())) {
                            injectionBeanElements.add(beanElement);
                        }
                    }
                }
            }
        }
    }

    /**
     * 扫描载入
     *
     * @param className 类名称
     */
    @Override
    public void sanIocBean(String className) {
        Set<Class<?>> list = ClassScannerUtils.searchClasses(className,EnvFactory.getBaseConfiguration().getDefaultPath());
        for (Class<?> cls : list) {
            if (cls == null) {
                continue;
            }
            try {
                registryIocBean(cls);
            } catch (Exception e) {
                log.error("ioc scan load class dir error" + cls, e);
            }
        }
    }

    /**
     * 注册class
     *
     * @param cla 类对象
     */
    @Override
    public void registryIocBean(Class<?> cla) {
        //注册bean标签
        try {
            Bean iocBean = cla.getAnnotation(Bean.class);
            RpcClient rpcClient = cla.getAnnotation(RpcClient.class);
            if (iocBean == null&&rpcClient==null) {
                return;
            }
            String id;
            String namespace;
            BeanModel beanModel = new BeanModel();
            if (rpcClient!=null)
            {
                id = rpcClient.bind().getName();
                namespace = rpcClient.namespace();

            } else
            {
                id = iocBean.id();
                if (StringUtil.isNull(id) && !iocBean.bind().equals(Empty.class)) {
                    id = iocBean.bind().getName();
                }
                if (StringUtil.isNull(id)) {
                    id = AnnotationUtil.getBeanId(cla);
                }
                namespace = iocBean.namespace();
                beanModel.setCreate(iocBean.create());
            }
            beanModel.setId(id);
            if (rpcClient!= null) {
                beanModel.setSingleton(true);
            } else
            {
                beanModel.setSingleton(iocBean.singleton());
            }
            beanModel.setNamespace(namespace);
            beanModel.setClassName(cla.getName());
            //log.info("registry Ioc Bean class=" + cla + " id=" + id + " namespace=" + namespace);
            registerBean(beanModel);
        } catch (Exception e) {
            log.error("ioc load error" + cla, e);
        }
    }

    @Override
    public void setConfigFile(String file) {
        String[] configFile = new String[]{file};
        setConfigFile(configFile);
    }

    @Override
    public void setConfigFile(String[] configFile) {
        if (ArrayUtil.equals(this.configFile, configFile)) {
            return;
        }
        this.configFile = configFile;
        try {
            reload();
        } catch (Exception e) {
            log.error("ioc load " + ObjectUtil.toString(configFile), e);
        }
    }

    /**
     * 注册在registerBeanMap 中的bean对象不会清除，将一直保留在系统中
     *
     * @param beanElement bean
     */
    @Override
    public void registerBean(BeanElement beanElement) {
        try {
            if (containsBean(beanElement.getId(), beanElement.getNamespace())) {
                //优先保留手动配置
                log.info("ioc bean is already register id:" + beanElement.getId() + "  namespace:" + beanElement.getNamespace() + " ClassName:" + beanElement.getClassName());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, BeanElement> temp = registerBeanMap.computeIfAbsent(beanElement.getNamespace(), k -> new HashMap<>());
        Map<String, BeanElement> fileElementConfig = beanElementMap.get(beanElement.getNamespace());
        if (fileElementConfig == null) {
            beanElementMap.put(beanElement.getNamespace(), temp);
        } else {
            fileElementConfig.put(beanElement.getId(), beanElement);
        }

        if (temp.containsKey(beanElement.getId())) {
            //单例模式就不重复注册了
            BeanElement element = temp.get(beanElement.getId());
            if (element != null && element.isSingleton()) {
                return;
            }
        }
        temp.put(beanElement.getId(), beanElement);

        //注册扫描到的定时任务
        try {
            Class<?> cls = ClassUtil.loadClass(beanElement.getClassName());
            if (AnnotationUtil.hasScheduled(cls)) {
                schedulerMap.put(beanElement.getId(), beanElement.getNamespace());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String readFileText(String fileName) throws Exception {
        String fileNamePath = fileName;
        if (!FileUtil.isFileExist(fileName)) {

            File file = EnvFactory.getFile(fileName);
            if (file != null) {
                fileNamePath = file.getPath();
            }
            if (file==null)
            {
                log.error("jspx sioc no find file path: " + fileNamePath + " \r\n 不能找到配置文件:" + fileNamePath);
                return null;
            }
        }

        return IoUtil.autoReadText(fileNamePath,envTemplate.getString(Environment.encode, Environment.defaultEncode));
    }


    /**
     * 读取文件,支持url 方式
     *
     * @param file 读取文件
     * @return 文件内容
     * @throws Exception 异常 运行错误
     */
    private String readContext(File file) throws Exception {
        String  configString = readFileText(file.getPath());
        if (configString==null)
        {
            return null;
        }

        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(LoadElement.TAG_NAME, LoadElement.class.getName());
        List<TagNode> results = xmlEngine.getTagNodes(configString);
        for (TagNode tNode : results) {
            LoadElement se = (LoadElement) tNode;
            String loadFile = se.getFile();

            Map<String, Object> valueMap = envTemplate.getVariableMap();
            for (String attName : se.getAttributeName()) {
                String value = XMLUtil.deleteQuote(se.getStringAttribute(attName));
                valueMap.put(attName, value);
            }
            String encode = se.getEncode();
            if (StringUtil.isNull(encode)) {
                encode = Environment.defaultEncode;
            }

            File findFile = EnvFactory.getFile(loadFile);
            if (findFile==null)
            {
                log.debug("ioc not found file:" + loadFile);
                throw new Exception("ioc not found file:" + loadFile);
            }
            String readCont = IoUtil.autoReadText(findFile.getPath(),encode);
            int headPost = StringUtil.indexIgnoreCaseOf(readCont, "<?xml");
            if (headPost != -1) {
                readCont = StringUtil.substringAfter(readCont, ">");
            }
            readCont = StringUtil.trim(StringUtil.substringBeforeLast(StringUtil.substringAfter(readCont, ">"), "<"));
            String readTxt = EnvFactory.getPlaceholder().processTemplate(valueMap, readCont);
            String loadTagSource =  se.getSource();
            configString = StringUtil.replace(configString, loadTagSource, readTxt);
        }
        return EnvFactory.getPlaceholder().processTemplate(envTemplate.getVariableMap(), configString);
    }

    /**
     * @param file 文件
     * @return 读取所有配置文件
     * @throws Exception 异常
     */
    private List<TagNode> getIocElementsForFile(File file) throws Exception {
        if (file==null)
        {
            return new ArrayList<>(0);
        }
        String fileId = file.getName() + StringUtil.UNDERLINE + file.length();
        if (FILE_ID_LIST.contains(fileId))
        {
            return new ArrayList<>(0);
        }
        FILE_ID_LIST.add(fileId);

        String defaultPath = envTemplate.getString(Environment.defaultPath);
        if (FileUtil.isPatternPath(file.getName())) {
            List<TagNode> results = new ArrayList<>();
            List<File> findFiles = FileUtil.getPatternFiles(defaultPath, file.getName());
            if (defaultPath!=null&&!defaultPath.contains(".jar"))
            {
                findFiles.addAll(FileUtil.getPatternFiles(null, file.getName()));
            }

            if (!ObjectUtil.isEmpty(findFiles))
            {
                for (File f : findFiles) {
                    results.addAll(getIocElementsForFile(f));
                }
            }
            return results;
        }

        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(SiocElement.TAG_NAME, SiocElement.class.getName());
        String txt = readContext(file);
        if (txt==null)
        {
            return new ArrayList<>();
        }
        log.debug("jspx sioc load file:" + file);
        List<TagNode> results = xmlEngine.getTagNodes(txt);
        String[] includeFiles = null;

        for (TagNode tNode : results) {
            SiocElement se = (SiocElement) tNode;
            //得到包含标签
            for (TagNode iNode : se.getIncludeElements()) {
                IncludeElement node = (IncludeElement) iNode;
                includeFiles = ArrayUtil.add(includeFiles, node.getFile());
            }
            //得到要自动扫描的目录
            for (TagNode iNode : se.getScanElements()) {
                ScanElement node = (ScanElement) iNode;
                if (!scanPackageList.contains(node.getPackage())) {
                    scanPackageList.add(StringUtil.trim(node.getPackage()));
                }
            }
        }
        if (includeFiles != null) {
            for (String fileName : includeFiles) {
                if (StringUtil.isEmpty(fileName))
                {
                    continue;
                }
                results.addAll(getIocElementsForFile(new File(fileName)));
            }
        }
        return results;
    }

    /**
     * @return 得到基本的配置<sioc></sioc>
     * @throws Exception 异常
     */
    private List<TagNode> getIocElements() throws Exception {
        List<TagNode> results = new ArrayList<>();
        for (String fileName : configFile) {
            if (StringUtil.isEmpty(fileName))
            {
                continue;
            }
            results.addAll(getIocElementsForFile(new File(fileName)));
        }
        return results;
    }

    @Override
    public List<BeanElement> getElementList() {
        return elementList;
    }

    /**
     * @param beanName  得到 bean名称
     * @param namespace 命名空间
     * @return 返回配置
     */
    @Override
    public BeanElement getBeanElement(String beanName, String namespace) {
        if (StringUtil.isEmpty(beanName)) {
            return null;
        }
        String nameKey = StringUtil.fixedNamespace(namespace);
        if (StringUtil.isNull(nameKey)) {
            nameKey = Sioc.global;
        }
        Map<String, BeanElement> elementMap;
        BeanElement result = null;
        while (!StringUtil.isNull(nameKey) && !Sioc.global.equalsIgnoreCase(nameKey)) {
            elementMap = beanElementMap.get(nameKey);
            if (elementMap != null) {
                result = elementMap.get(beanName);
                if (result != null) {
                    return result;
                }
            }
            if (extendMap.containsKey(nameKey)) {
                nameKey = extendMap.get(nameKey);
            } else {
                if (nameKey.contains("/")) {
                    nameKey = StringUtil.substringBeforeLast(nameKey, "/");
                } else {
                    nameKey = Sioc.global;
                }
            }
        }
        elementMap = beanElementMap.get(Sioc.global);
        if (elementMap != null) {
            result = elementMap.get(beanName);
        }
        return result;
    }

    /**
     * @param beanName  name
     * @param namespace 命名空间
     * @return 直接返回本命名空间配置数据
     */
    @Override
    public BeanElement getBeanElementForNamespace(String beanName, String namespace) {
        String nameKey = namespace;
        if (StringUtil.isNull(nameKey)) {
            nameKey = Sioc.global;
        }
        final Map<String, BeanElement> elementMap = beanElementMap.get(nameKey);
        if (elementMap == null || elementMap.isEmpty()) {
            return null;
        }
        return elementMap.get(beanName);
    }


    /**
     * @param beanName  名称
     * @param namespace 命名空间
     * @return 判断是否存在
     */
    @Override
    public boolean containsBean(String beanName, String namespace) {
        return getBeanElement(beanName, namespace) != null;
    }

    /**
     * @return 继承关心
     */
    @Override
    public Map<String, String> getExtendMap() {
        return extendMap;
    }

    /**
     * 得到应用名称列表
     *
     * @return map 方式
     */
    @Override
    public Map<String, String> getApplicationMap() {
        return applicationMap;
    }


    /**
     *
     * @return 得到定时器map
     */
    @Override
    public Map<String, String> getSchedulerMap() {
        return schedulerMap;
    }


    @Override
    public List<BeanElement> getInjectionBeanElements() {
        return injectionBeanElements;
    }

    @Override
    public void shutdown()
    {
        registerBeanMap.clear();
        injectionBeanElements.clear();
        scanPackageList.clear();
        schedulerMap.clear();
        beanElementMap.clear();
        elementList.clear();
        extendMap.clear();
        applicationMap.clear();
        FILE_ID_LIST.clear();
        configFile = null;
    }

}
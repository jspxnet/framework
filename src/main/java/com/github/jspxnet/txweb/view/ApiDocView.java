package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.tag.BeanElement;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.NullClass;
import com.github.jspxnet.sober.util.AnnotationUtil;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.annotation.*;
import com.github.jspxnet.txweb.apidoc.*;
import com.github.jspxnet.txweb.bundle.action.EditConfigAction;
import com.github.jspxnet.txweb.bundle.action.EditLanguageAction;
import com.github.jspxnet.txweb.config.ActionConfigBean;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@HttpMethod(caption = "API文档")
public class ApiDocView extends ActionSupport {
    final private static String[] noViewClass = new String[]{
            com.github.jspxnet.txweb.support.DefaultTemplateAction.class.getName(),
            com.github.jspxnet.txweb.view.HelperView.class.getName(),
            com.github.jspxnet.txweb.view.DownloadFileView.class.getName(),
            com.github.jspxnet.txweb.ueditor.adaptor.UEditorAdaptor.class.getName(),
            ApiDocView.class.getName(),
            EditConfigAction.class.getName(),
            EditLanguageAction.class.getName(),
            TemplateView.class.getName(),
    };


    final private WebConfigManager webConfigManager = TXWebConfigManager.getInstance();
    final private BeanFactory beanFactory = EnvFactory.getBeanFactory();
    final private String API_INDEX_CACHE = "api:index:cache:%s";
    final private String API_FIELD_CACHE = "api:field:cache:%s";

    @Operate(caption = "应用名称", post = false, method = "appname")
    public String getAppName() {
        String appName = beanFactory.getApplicationMap().get(getRootNamespace());
        if (!StringUtil.isNull(appName)) {
            return appName;
        }
        appName = getRootNamespace();
        return appName;
    }

    @Operate(caption = "文档索引", method = "indexing", post = false)
    public Collection<ApiAction> index() throws Exception {
        Map<String, ApiAction> indexCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()));
        if (indexCache != null && !indexCache.isEmpty()) {
            return indexCache.values();
        }
        indexCache = new HashMap<>();
        String softName = getRootNamespace();
        Map<String, ApiAction> resultMap = new HashMap<>();
        IocContext iocContext = beanFactory.getIocContext();
        Map<String, String> extendList = webConfigManager.getExtendList();
        for (String name : extendList.keySet()) {
            if (!name.contains(softName)) {
                continue;
            }
            Map<String, ActionConfigBean> map = webConfigManager.getActionMap(name);
            if (map!=null)
            {
                for (String namespace : map.keySet()) {
                    ActionConfigBean configBean = map.get(namespace);
                    BeanElement beanElement = iocContext.getBeanElement(configBean.getIocBean(), name);
                    if (beanElement==null||ArrayUtil.inArray(noViewClass, beanElement.getClassName(), true)) {
                        continue;
                    }

                    ApiAction vo = new ApiAction();
                    vo.setUrl("/" + name + "/" + configBean.getActionName());
                    vo.setTitle(configBean.getCaption());
                    vo.setConfMethod(configBean.getMethod());
                    vo.setClassName(beanElement.getClassName());
                    vo.setNamespace(namespace);
                    vo.setId(EncryptUtil.getMd5(beanElement.getClassName()));
                    resultMap.put(vo.getId(), vo);
                }
            }
        }
        indexCache.putAll(resultMap);
        JSCacheManager.put(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()), indexCache);
        return indexCache.values();
    }

    @Operate(caption = "字段索引", method = "fielding", post = false)
    public Collection<ApiAction> fielding() throws Exception {
        Map<String, ApiAction> fieldCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_FIELD_CACHE, getRootNamespace()));
        if (fieldCache != null && !fieldCache.isEmpty()) {
            return fieldCache.values();
        }
        fieldCache = new HashMap<>();
        String softName = getRootNamespace();
        List<String> list = new ArrayList<>();
        IocContext iocContext = beanFactory.getIocContext();
        Map<String, String> extendList = webConfigManager.getExtendList();
        for (String name : extendList.keySet()) {
            if (!name.contains(softName)) {
                continue;
            }
            Map<String, ActionConfigBean> map = webConfigManager.getActionMap(name);
            if (map!=null)
            {
                for (String namespace : map.keySet()) {
                    ActionConfigBean configBean = map.get(namespace);
                    BeanElement beanElement = iocContext.getBeanElement(configBean.getIocBean(), name);
                    if (beanElement==null)
                    {
                        continue;
                    }
                    String className = StringUtil.substringBeforeLast(beanElement.getClassName(), ".");
                    if (className.endsWith("action") || className.endsWith("view") || className.endsWith("controller")) {
                        while (StringUtil.countMatches(className, ".") > 1) {
                            className = StringUtil.substringBeforeLast(className, ".");
                            if (StringUtil.countMatches(className, ".") <= 0) {
                                break;
                            }
                        }
                    }
                    if (!StringUtil.isNull(className) && !list.contains(className)) {
                        list.add(className);
                    }
                }
            }

        }

        //过滤重复的类对象begin
        List<Class<?>> classList = new ArrayList<>();
        for (String className : list) {
            if (className.toLowerCase().endsWith("view") || className.toLowerCase().endsWith("action") || className.contains("com.github")) {
                continue;
            }
            if (!className.contains(softName)) {
                continue;
            }
            List<Class<?>> findClassList = AnnotationUtil.getTableAnnotationClassList(className);
            if (findClassList.isEmpty()) {
                continue;
            }
            for (Class<?> cls : findClassList) {
                if (cls == null) {
                    continue;
                }
                if (!classList.contains(cls)) {
                    classList.add(cls);
                }
            }
        }
        //过滤重复的类对象end
        Map<String, ApiAction> resultMap = new HashMap();
        for (Class<?> cls : classList) {
            ApiAction vo = new ApiAction();
            vo.setUrl("/" + softName + "/" + cls.getName());
            vo.setTitle(AnnotationUtil.getTableCaption(cls));
            vo.setConfMethod("table");
            vo.setClassName(cls.getName());
            vo.setNamespace(softName);
            vo.setId(EncryptUtil.getMd5(cls.getName()));
            resultMap.put(vo.getId(), vo);
        }
        fieldCache.putAll(resultMap);
        JSCacheManager.put(DefaultCache.class, String.format(API_FIELD_CACHE, getRootNamespace()), fieldCache);
        return fieldCache.values();
    }

    /**
     * @param lass 类对象
     * @return 得到参数对象的文档
     */
    private static ApiParam getApiParam(Class lass) {
        ApiParam apiParam = new ApiParam();
        Field[] fields = ClassUtil.getDeclaredFields(lass);
        for (Field field : fields) {
            if (field.getModifiers() > 25) {
                continue;
            }
            String key = field.getName();
            JsonField jsonField = field.getAnnotation(JsonField.class);
            if (jsonField != null && !StringUtil.isNull(jsonField.name())) {
                key = jsonField.name();
            }
            JsonIgnore notExpose = field.getAnnotation(JsonIgnore.class);
            if (notExpose != null && !notExpose.isNull()) {
                continue;
            }
            apiParam.setFiled(key);
            if (jsonField!=null)
            {
                apiParam.setCaption(jsonField.caption());
                apiParam.setFormat(jsonField.format());
            } else
            {
                apiParam.setCaption(field.getName());
                apiParam.setFormat(StringUtil.empty);
            }
            apiParam.setFiledType(field.getType().getSimpleName());
        }
        return apiParam;
    }


    /**
     * 这里只得到方法的参数说明
     *
     * @param exeMethod 方法
     * @param url       路径
     * @return 文档操作对象
     */
    public static ApiOperate getMethodApiOperate(Method exeMethod, final String url) {
        Operate operate = exeMethod.getAnnotation(Operate.class);
        ApiOperate apiOperate = new ApiOperate();
        apiOperate.setCaption(operate.caption());
        apiOperate.setAction(operate.post() ? "POST" : "GET;POST");

        ApiMethod apiMethod = new ApiMethod();

        String callMethod = operate.method();
        if (TXWebUtil.AT.equals(callMethod)) {
            apiMethod.setName(exeMethod.getName());
        }
        if (!StringUtil.isNull(operate.method())) {
            if (url.endsWith("*")) {
                String tmpUrl = url.substring(0, url.length() - 1);
                if (tmpUrl.endsWith("/") && exeMethod.getName().startsWith("/")) {
                    apiOperate.setUrl(tmpUrl.substring(0, tmpUrl.length() - 1) + exeMethod.getName());
                } else {
                    apiOperate.setUrl(url.substring(0, url.length() - 1) + exeMethod.getName());
                }
            } else {
                apiOperate.setUrl(url + exeMethod.getName());
            }
        }
        if (StringUtil.isNull(apiMethod.getName()) && !StringUtil.isNull(operate.method())) {
            apiMethod.setName(operate.method());
        }

        apiOperate.setUrl(url);
        apiOperate.setMethod(apiMethod);

        Describe describe = exeMethod.getAnnotation(Describe.class);
        if (describe!=null&&!ArrayUtil.isEmpty(describe.value()))
        {
            String cont = ArrayUtil.toString(describe.value(),"<br />");
            apiOperate.setDescribe(ScriptMarkUtil.getMarkdownHtml(cont));
        }
        //方法参数-------------------------------------------------------------------------------------------
        TreeMap<String, ApiParam> methodParamList = new TreeMap<>();
        apiMethod.setParams(methodParamList);
        //i 表示第几个参数，下边完成参数组装
        Annotation[][] parameterAnnotations = exeMethod.getParameterAnnotations();
        Parameter[] parameters = exeMethod.getParameters();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            ApiParam methodParam = new ApiParam();
            Annotation[] annotations = parameterAnnotations[i];
            methodParam.setName(parameters[i].getName());
            methodParam.setFiledType(parameters[i].getType().getSimpleName());
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    methodParam.setCaption(param.caption());
                    methodParam.setFiled(parameters[i].getName());
                    methodParamList.put(methodParam.getName(), methodParam);
                    if (ClassUtil.isStandardProperty(parameters[i].getType())&&param.request()) {
                        if (!param.enumType().equals(NullClass.class))
                        {
                            methodParam.setFiledType("enum");
                            Map<Object,Object> enumMap = ClassUtil.getEnumMap(param.enumType(),"value","name");
                            if (enumMap!=null)
                            {
                                methodParam.setFormat(enumMap.toString());
                            }
                        } else if (ClassUtil.isNumberProperty(parameters[i].getType())) {

                            long max = Long.MAX_VALUE;
                            if (param.max() == Long.MAX_VALUE &&parameters[i].getType().equals(Integer.class))
                            {
                                max = Integer.MAX_VALUE;
                            }
                            methodParam.setSafety(param.min() + "-" + Long.min(max,param.max()));
                            methodParam.setFiledType(parameters[i].getType().getSimpleName());

                        } else if (parameters[i].getType().equals(String.class)) {
                            methodParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-"+param.max() + ",安全[" + param.level() + "]");
                            methodParam.setFiledType(parameters[i].getType().getSimpleName());
                        }

                    } else if (!param.type().equals(NullClass.class)) {
                        methodParam.setFiledType(param.type().getSimpleName());
                        addMethodParam(methodParam.getChildren() ,param.type());
                    } else if (param.type().equals(NullClass.class)&&!ClassUtil.isStandardProperty(parameters[i].getType())) {
                        methodParam.setFiledType(parameters[i].getType().getSimpleName());
                        methodParam.setClassParam(true);
                        addMethodParam(methodParam.getChildren() ,parameters[i].getType());
                    }
                    else  if (!ClassUtil.isIocInterfaces(parameters[i].getType())) {
                        methodParam.setFiledType(parameters[i].getType().getSimpleName());
                        addMethodParam(methodParam.getChildren(),parameters[i].getType());
                    }
                }
                if (annotation instanceof Validate) {
                    //验证说明
                    methodParam.setRequired(true);
                }
                if (annotation instanceof PathVar) {
                    PathVar pathVar = (PathVar) annotation;
                    methodParam.setFiledType("PathVar");
                    methodParam.setFiled(StringUtil.isNull(pathVar.name()) ? parameters[i].getName() : pathVar.name());
                    methodParam.setRequired(true);
                }
            }

        }
        //修复路径方式表述
        if (!StringUtil.isNull(apiMethod.getName()) && apiOperate.getUrl().endsWith("*")) {
            String tmpUrl = apiOperate.getUrl().substring(0, apiOperate.getUrl().length() - 1);
            if (tmpUrl.endsWith("/") && apiMethod.getName().startsWith("/")) {
                apiOperate.setUrl(tmpUrl.substring(0, tmpUrl.length() - 1) + apiMethod.getName());
            } else {
                apiOperate.setUrl(tmpUrl + apiMethod.getName());
            }
        }
        return apiOperate;
    }

    /**
     * 添加方法参数
     * @param children 包含参数列表
     * @param type cls类型
     */
    static public void addMethodParam(List<ApiParam> children,Class type)
    {
          try {
            Field[] fields = ClassUtil.getDeclaredFields(type);
            for (Field field : fields) {
                Param param = field.getAnnotation(Param.class);
                ApiParam objParam = new ApiParam();
                objParam.setFiled(field.getName());
                objParam.setFiledType(field.getType().getSimpleName());

                if (param!=null)
                {
                    objParam.setSafety("安全级别[" + param.level() + "]");
                    objParam.setRequired(param.required());
                    objParam.setName(field.getName());
                    objParam.setCaption(param.caption());
                    if (!param.enumType().equals(NullClass.class))
                    {
                        Map<Object,Object> enumMap = ClassUtil.getEnumMap(param.enumType(),"value","name");
                        if (enumMap!=null)
                        {
                            objParam.setFormat(enumMap.toString());
                        }
                    } else
                    if (ClassUtil.isNumberProperty(field.getType())) {
                        long max = Long.MAX_VALUE;
                        if (param.max() == Long.MAX_VALUE &&field.getType().equals(Integer.class))
                        {
                            max = Integer.MAX_VALUE;
                        }
                        objParam.setSafety(param.min() + "-" + Long.min(max,param.max()));
                    } else if (field.getType().equals(String.class)) {
                        objParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-"+param.max() + ",安全[" + param.level() + "]");
                    }
                }
                children.add(objParam);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        这里得到Class中所有set方法的参数
     */
    private static Map<String, ApiParam> getSetMethodApiOperate(Class cla) {
        Method[] methods = ClassUtil.getDeclaredSetMethods(cla);
        Map<String, ApiParam> classParamList = new LinkedHashMap<>();
        for (Method method : methods) {
            if (method.getParameterCount() != 1) {
                continue;
            }
            Param param = method.getAnnotation(Param.class);
            if (param == null || !param.request()) {
                continue;
            }
            Operate operate = method.getAnnotation(Operate.class);
            if (operate != null) {
                continue;
            }

            for (Parameter parameter : method.getParameters()) {

                ApiParam methodParam = new ApiParam();
                methodParam.setFiled(ClassUtil.getMethodFiledName(method.getName()));
                methodParam.setName(parameter.getName());
                methodParam.setRequired(true);
                String typeName = parameter.getType().getTypeName();
                if (typeName.contains(".")) {
                    methodParam.setFiledType(StringUtil.substringAfterLast(typeName, "."));
                } else {
                    methodParam.setFiledType(typeName);
                }

                methodParam.setRequired(false);
                methodParam.setCaption(param.caption());

                if (ClassUtil.isNumberType(parameter.getType())) {
                    methodParam.setSafety(param.min() + "-" + param.max());
                } else if (parameter.getType().equals(String.class)) {
                    methodParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-"+param.max() + ",安全[" + param.level() + "]");
                }
                classParamList.put(methodParam.getName(), methodParam);
            }
        }
        return classParamList;
    }


    @Operate(caption = "文档", method = "/document/${id}", post = false)
    public ApiDocument getDocument(@PathVar String id) throws Exception {
        Map<String, ApiAction> indexCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()));
        if (indexCache == null || indexCache.isEmpty()) {
            index();
            indexCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()));
        }
        AssertException.isNull(indexCache,"不存在的文档索引");

        ApiAction apiAction = indexCache.get(id);
        AssertException.isNull(indexCache,"不存在的文档id");

        ApiDocument apiDocument = BeanUtil.copy(apiAction, ApiDocument.class);
        Class<?> cla = ClassUtil.loadClass(apiDocument.getClassName());


        Map<String, ApiParam> params = getSetMethodApiOperate(cla);
        apiDocument.setParams(params);
        //得到操作列表
        List<ApiOperate> operateList = new LinkedList<>();
        Map<Operate, Method> operateMap = TXWebUtil.getClassOperateList(cla);
        for (Method exeMethod : operateMap.values()) {
            ApiOperate apiOperate = getMethodApiOperate(exeMethod, apiDocument.getUrl());

            Map<String, ApiParam> methodParamList = apiOperate.getMethod().getParams();
            for (ApiParam param : methodParamList.values()) {
                if ("object".equals(param.getFiledType()) && !StringUtil.isEmpty(param.getFiled())) {
                    Map<String, ApiParam> theParams = new LinkedHashMap<>();
                    try {
                        ApiParam apiParam = getApiParam(ClassUtil.loadClass(param.getFiled()));
                        theParams.put(apiParam.getName(), apiParam);
                        apiDocument.setParams(theParams);
                    } catch (ClassNotFoundException e) {
                        //...
                    }
                }
            }
            try {
                Class<?> aClass = exeMethod.getReturnType();
                apiOperate.setResultType(aClass.getSimpleName());
                Object obj = null;
                if (ClassUtil.isCollection(aClass) || aClass.isAssignableFrom(List.class))
                {
                    Type type = ((ParameterizedType) aClass.getGenericSuperclass()).getActualTypeArguments()[0];
                    if (ClassUtil.isStandardType(type)) {
                        apiOperate.setResult(new ArrayList<>());
                    } else {
                        obj = ClassUtil.newInstance(type.getTypeName());
                        List<ApiField> fieldList = getApiFieldList(obj);
                        if (fieldList != null && !fieldList.isEmpty()) {
                            apiOperate.setResult(fieldList);
                        }
                    }
                } else if (aClass.isAssignableFrom(Map.class)) {
                    apiOperate.setResult(new ArrayList<>());
                } else {
                    obj = ClassUtil.newInstance(aClass.getName());
                }

                if (obj != null) {
                    List<ApiField> fieldList = getApiFieldList(obj);
                    if (fieldList != null && !fieldList.isEmpty()) {
                        apiOperate.setResult(fieldList);
                    }
                }
                apiOperate.setParams(params);
            } catch (Exception e) {
                //...
            }
            operateList.add(apiOperate);
        }
        apiDocument.setOperateList(operateList);
        return apiDocument;
    }

    @Operate(caption = "字段文档", method = "/table/${id}", post = false)
    public TableModels getTable(@PathVar String id) throws Exception {
        Map<String, ApiAction> fieldCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_FIELD_CACHE, getRootNamespace()));
        if (fieldCache == null || fieldCache.isEmpty()) {
            fielding();
            fieldCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_FIELD_CACHE, getRootNamespace()));
        }
        if (fieldCache == null) {
            addFieldInfo(Environment.ERROR, "不存在的表结构");
            return null;//RocRespo
        }
        ApiAction apiAction = fieldCache.get(id);
        if (apiAction == null) {
            addFieldInfo(Environment.ERROR, "不存在的表结构");
            return null;//RocResponse.error(-1,"不存在的表结构");
        }

        Class builderClass = ClassUtil.loadClass(apiAction.getClassName());
        if (builderClass == null) {
            addFieldInfo(Environment.ERROR, "不存在的表结构");
            return null;//
        }

        return AnnotationUtil.getSoberTable(builderClass);
    }

    /**
     * 类对象转json描述
     *
     * @param object bean对象
     * @return json描述
     */
    public static List<ApiField> getApiFieldList(Object object) {
        Field[] fields = ClassUtil.getDeclaredFields(object.getClass());
        if (fields == null) {
            return null;
        }
        List<ApiField> fieldList = new ArrayList<>();
        for (Field field : fields) {
            if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                continue;
            }
            if ("hash".equalsIgnoreCase(field.getName())) {
                continue;
            }
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
            if (jsonIgnore != null) {
                continue;
            }
            if (ClassUtil.isStandardProperty(field.getType()) || field.getType().equals(JSONObject.class)) {

                ApiField apiField = new ApiField();
                apiField.setName(field.getName());
                apiField.setType(field.getType().getSimpleName());
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    apiField.setCaption(column.caption());
                    if (!column.enumType().equals(NullClass.class))
                    {
                        Map<Object,Object> enumMap = ClassUtil.getEnumMap(column.enumType(),"value","name");
                        if (enumMap!=null)
                        {
                            apiField.setCaption(column.caption() + ":" + enumMap.toString());
                        }
                    }
                }
                fieldList.add(apiField);
            } else {
                ApiField apiField = new ApiField();
                apiField.setName(field.getName());
                apiField.setType(field.getType().getSimpleName());
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    apiField.setCaption(column.caption());
                    if (!column.enumType().equals(NullClass.class))
                    {
                        Map<Object,Object> enumMap = ClassUtil.getEnumMap(column.enumType(),"value","name");
                        if (enumMap!=null)
                        {
                            apiField.setCaption(column.caption() + ":" + enumMap.toString());
                        }
                    }
                }
                Object childObj = null;
                try {
                    childObj = ClassUtil.newInstance(field.getType().getName());
                } catch (Exception e) {
                    //...
                }
                if (childObj != null) {
                    List<ApiField> childFieldList = getApiFieldList(childObj);
                    if (childFieldList != null && !childFieldList.isEmpty()) {
                        apiField.setChild(childFieldList);
                        fieldList.add(apiField);
                    }
                }
            }
        }
        return fieldList;
    }

    public static void main(String[] arg) throws Exception {

        JspxNetApplication.autoRun();
        ApiDocView apiDocView = new ApiDocView();
        //System.out.println(new JSONObject(apiDocView.indexing(),true).toString(4));

        String id = "09b47479a03dc515c2d7bd0705708d27";
        ApiDocument response = apiDocView.getDocument(id);

        System.out.println(new JSONObject(response, true).toString(4));
    }
}

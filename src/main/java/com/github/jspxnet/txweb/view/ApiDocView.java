package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.IocContext;
import com.github.jspxnet.sioc.tag.BeanElement;
import com.github.jspxnet.sober.TableModels;
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
import com.github.jspxnet.txweb.util.ApiDocUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import java.lang.reflect.*;
import java.util.*;

@HttpMethod(caption = "API文档")
public class ApiDocView extends ActionSupport {
    final private static String[] NO_VIEW_CLASS = new String[]{
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
    public Collection<ApiAction> index()  {
        Map<String, ApiAction> indexCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()));
        if (indexCache != null && !indexCache.isEmpty()) {
            return indexCache.values();
        }
        indexCache = new HashMap<>();
        String softName = getRootNamespace();
        Map<String, ApiAction> resultMap = new TreeMap<>();
        IocContext iocContext = beanFactory.getIocContext();
        Map<String, String> extendList = webConfigManager.getExtendList();
        for (String name : extendList.keySet()) {
            if (!name.contains(softName)) {
                continue;
            }
            Map<String, ActionConfigBean> map = webConfigManager.getActionMap(name);
            if (map != null) {
                for (String namespace : map.keySet()) {
                    ActionConfigBean configBean = map.get(namespace);
                    BeanElement beanElement = iocContext.getBeanElement(configBean.getIocBean(), name);
                    if (beanElement == null || ArrayUtil.inArray(NO_VIEW_CLASS, beanElement.getClassName(), true)) {
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
            if (map != null) {
                for (String namespace : map.keySet()) {
                    ActionConfigBean configBean = map.get(namespace);
                    BeanElement beanElement = iocContext.getBeanElement(configBean.getIocBean(), name);
                    if (beanElement == null) {
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





    @Operate(caption = "文档", method = "document/${id}", post = false)
    public ApiDocument getDocument(@PathVar String id) throws Exception {
        Map<String, ApiAction> indexCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()));
        if (indexCache == null || indexCache.isEmpty()) {
            index();
            indexCache = (Map<String, ApiAction>) JSCacheManager.get(DefaultCache.class, String.format(API_INDEX_CACHE, getRootNamespace()));
        }
        AssertException.isNull(indexCache, "不存在的文档索引");

        ApiAction apiAction = indexCache.get(id);
        AssertException.isNull(indexCache, "不存在的文档id");

        ApiDocument apiDocument = BeanUtil.copy(apiAction, ApiDocument.class);
        Class<?> cla = ClassUtil.loadClass(apiDocument.getClassName());

        Map<String, ApiParam> params = ApiDocUtil.getSetMethodApiOperate(cla);
        apiDocument.setParams(params);
        //得到操作列表
        List<ApiOperate> operateList = new LinkedList<>();
        Map<Operate, Method> operateMap = TXWebUtil.getClassOperateList(cla);
        for (Method exeMethod : operateMap.values()) {
            ApiOperate apiOperate = ApiDocUtil.getMethodApiOperate(cla,exeMethod, apiDocument.getUrl());
            Map<String, ApiParam> methodParamList = apiOperate.getMethod().getParams();
            for (ApiParam param : methodParamList.values()) {
                if ("object".equals(param.getFiledType()) && !StringUtil.isEmpty(param.getFiled())) {
                    Map<String, ApiParam> theParams = new LinkedHashMap<>();
                    try {
                        ApiParam apiParam = ApiDocUtil.getApiParam(ClassUtil.loadClass(param.getFiled()));
                        theParams.put(apiParam.getName(), apiParam);
                        apiDocument.setParams(theParams);
                    } catch (ClassNotFoundException e) {
                        //...
                    }
                }
            }

            apiOperate.setParams(params);
            operateList.add(apiOperate);
            ApiDocUtil.putReturnApiField(exeMethod,apiOperate);
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
        AssertException.isNull(fieldCache,"不存在的表结构");
        ApiAction apiAction = fieldCache.get(id);
        AssertException.isNull(apiAction,"不存在的表结构");

        Class<?> builderClass = ClassUtil.loadClass(apiAction.getClassName());
        AssertException.isNull(builderClass,"不存在的表结构");
        return AnnotationUtil.getSoberTable(builderClass);
    }

    public static void main(String[] arg) throws Exception {

        JspxNetApplication.autoRun();
        ApiDocView apiDocView = new ApiDocView();
        //System.out.println(new JSONObject(apiDocView.indexing(),true).toString(4));

        String id = "6d63185d5018a4ec48c6000db1834e1a";
        ApiDocument response = apiDocView.getDocument(id);

        System.out.println(new JSONObject(response, true).toString(4));
    }
}

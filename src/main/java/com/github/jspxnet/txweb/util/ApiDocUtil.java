package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.Empty;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.NullClass;
import com.github.jspxnet.txweb.annotation.*;
import com.github.jspxnet.txweb.apidoc.*;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/11 11:44
 * description: API 文档工具
 **/
@Slf4j
public class ApiDocUtil {
    private ApiDocUtil()
    {

    }


    /**
     * @param lass 类对象
     * @return 得到参数对象的文档
     */
    public static List<ApiParam> getApiParamList(Class<?> lass) {
        List<ApiParam> result = new ArrayList<>();
        Field[] fields = ClassUtil.getDeclaredFields(lass);
        if (fields==null)
        {
            return result;
        }
        for (Field field : fields) {
            if (field.getModifiers() > 25) {
                continue;
            }

            ApiParam objParam = new ApiParam();
            objParam.setFiled(field.getName());
            objParam.setFiledType(field.getType().getSimpleName());

            Param param = field.getAnnotation(Param.class);
            if (param != null) {
                objParam.setSafety("安全级别[" + param.level() + "]");
                objParam.setRequired(param.required());
                objParam.setName(field.getName());
                objParam.setCaption(param.caption());
                if (!param.enumType().equals(NullClass.class)) {
                    Map<Object, Object> enumMap = ClassUtil.getEnumMap(param.enumType(), "value", "name");
                    if (enumMap != null) {
                        objParam.setFormat(enumMap.toString());
                    }
                } else if (ClassUtil.isNumberProperty(field.getType())) {
                    long max = Long.MAX_VALUE;
                    if (param.max() == Long.MAX_VALUE && field.getType().equals(Integer.class)) {
                        max = Integer.MAX_VALUE;
                    }
                    objParam.setSafety(param.min() + "-" + (Long.MAX_VALUE == Long.min(max, param.max()) ? "..." : Long.min(max, param.max())));
                } else if (field.getType().equals(String.class)) {
                    objParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-" + (Long.MAX_VALUE == param.max() ? "..." : param.max()) + ",安全[" + param.level() + "]");
                }
            } else
            {
                objParam.setCaption(field.getName());
            }

            result.add(objParam);
        }
        return result;
    }

    public static void putChildApiParamForReturnTypeModel(ApiParam apiParam,List<Class<Object>> classList,String returnTypeModel) {
        if (returnTypeModel.startsWith("<")&&returnTypeModel.endsWith(">"))
        {
            returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
        }
        //得到第一层
        String className =  StringUtil.trim(StringUtil.substringBefore(returnTypeModel,"<"));
        if ("list".equalsIgnoreCase(className)||List.class.getName().equalsIgnoreCase(className)||Set.class.getName().equalsIgnoreCase(className)||Collections.class.getName().equalsIgnoreCase(className))
        {
            apiParam.setFiledType("list");
            returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
            if (!StringUtil.isEmpty(returnTypeModel))
            {
                putChildApiParamForReturnTypeModel(apiParam, classList, returnTypeModel);
            }
        } else
        if ("map".equalsIgnoreCase(className)||Map.class.getName().equalsIgnoreCase(className))
        {
            apiParam.setFiledType("map");
            returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
            apiParam.setCaption(returnTypeModel);

            if (!StringUtil.isEmpty(returnTypeModel) && returnTypeModel.contains(","))
            {
                String returnTypeModelKey = StringUtil.substringBefore(returnTypeModel,",");
                String returnTypeModelValue = StringUtil.substringAfter(returnTypeModel,",");

                ApiField apiFieldKey = new ApiField();
                apiFieldKey.setName("key");
                apiFieldKey.setType(returnTypeModelKey);
                apiFieldKey.setCaption("关键字");

                ApiField apiFieldValue = new ApiField();
                apiFieldValue.setName("value");
                apiFieldValue.setType(returnTypeModelValue);
                apiFieldValue.setCaption("值");

                if (!StringUtil.isEmpty(returnTypeModelKey))
                {
                    putChildApiFieldForReturnTypeModel(apiFieldKey, classList, returnTypeModelKey);
                }
                if (!StringUtil.isEmpty(returnTypeModelValue))
                {
                    putChildApiFieldForReturnTypeModel(apiFieldValue, classList, returnTypeModelValue);
                }
                JSONObject json = new JSONObject();
                json.put("key",apiFieldKey);
                json.put("value",apiFieldValue);
                apiParam.setChildJson(json);
            }
        }
        else
        {
            Class<?> theClass = findDtoClass(className,classList);
            if (theClass!=null)
            {
                apiParam.setChildren(getApiParamList(theClass));

            }
        }

    }
    /**
     * 添加方法参数
     *
     * @param children 包含参数列表
     * @param type     cls类型
     */
    static public void addMethodParam(List<ApiParam> children, Class<?> type) {
        if (JSONObject.class.equals(type))
        {
            return;
        }
        try {
            Field[] fields = ClassUtil.getDeclaredFields(type);
            if (fields==null)
            {
                return;
            }
            for (Field field : fields) {
                Param param = field.getAnnotation(Param.class);
                ApiParam objParam = new ApiParam();
                objParam.setFiled(field.getName());
                objParam.setFiledType(field.getType().getSimpleName());

                if (param != null) {
                    objParam.setSafety("安全级别[" + param.level() + "]");
                    objParam.setRequired(param.required());
                    objParam.setName(field.getName());
                    objParam.setCaption(param.caption());
                    if (!param.enumType().equals(NullClass.class)) {
                        Map<Object, Object> enumMap = ClassUtil.getEnumMap(param.enumType(), "value", "name");
                        if (enumMap != null) {
                            objParam.setFormat(enumMap.toString());
                        }
                    } else if (ClassUtil.isNumberProperty(field.getType())) {
                        long max = Long.MAX_VALUE;
                        if (param.max() == Long.MAX_VALUE && field.getType().equals(Integer.class)) {
                            max = Integer.MAX_VALUE;
                        }
                        objParam.setSafety(param.min() + "-" + (Long.MAX_VALUE == Long.min(max, param.max()) ? "..." : Long.min(max, param.max())));
                    } else if (field.getType().equals(String.class)) {
                        objParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-" + (Long.MAX_VALUE == param.max() ? "..." : param.max()) + ",安全[" + param.level() + "]");
                    }
                }

                String returnTypeModel = field.getGenericType().getTypeName();
                if (returnTypeModel.contains("<") && returnTypeModel.contains(">"))
                {
                    Class<?>[] returnTypeClass  = ClassUtil.getClassForTypeModel(field.getGenericType().getTypeName()).toArray(new Class<?>[0]);
                    List<Class<Object>> classList =  toList(returnTypeClass);
                    putChildApiParamForReturnTypeModel(objParam, classList,returnTypeModel);
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
    public static Map<String, ApiParam> getSetMethodApiOperate(Class<?> cla) {
        Method[] methods = ClassUtil.getDeclaredSetMethods(cla);
        Map<String, ApiParam> classParamList = new LinkedHashMap<>();
        if (methods==null)
        {
            return classParamList;
        }
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
                if (typeName.contains(StringUtil.DOT)) {
                    methodParam.setFiledType(StringUtil.substringAfterLast(typeName, StringUtil.DOT));
                } else {
                    methodParam.setFiledType(typeName);
                }

                methodParam.setRequired(false);
                methodParam.setCaption(param.caption());

                if (ClassUtil.isNumberType(parameter.getType())) {
                    methodParam.setSafety(param.min() + "-" + (Long.MAX_VALUE == param.max() ? "..." : param.max()));
                } else if (parameter.getType().equals(String.class)) {
                    methodParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-" + (Long.MAX_VALUE == param.max() ? "..." : param.max()) + ",安全[" + param.level() + "]");
                }
                classParamList.put(methodParam.getName(), methodParam);
            }
        }
        return classParamList;
    }

    /**
     * 类对象转json描述
     *
     * @param cla bean对象
     * @param jsonIgnoreShow 是否显示 非空才显示的相
     * @param checkList 递归判断,防止死循环
     * @return json描述
     */
    public static List<ApiField> getApiFieldList(Class<?> cla,boolean jsonIgnoreShow,List<Class<?>> checkList) {
        if (ClassUtil.isStandardType(cla)||Class.class.equals(cla) || Object.class.equals(cla) ||
                 cla.equals(List.class) || cla.equals(Map.class)|| cla.equals(Set.class)
        )
        {
            return null;
        }
        if (cla.equals(JSONObject.class) || cla.equals(JSONArray.class)  || cla.isInterface())
        {
            List<ApiField> fieldList = new ArrayList<>();
            ApiField apiField = new ApiField();
            apiField.setName(cla.getName());
            apiField.setType(cla.getSimpleName());
            apiField.setCaption("复杂格式,不推荐是用");
            fieldList.add(apiField);
            return fieldList;
        }
        Field[] fields = ClassUtil.getDeclaredFields(cla);
        if (fields == null) {
            return null;
        }

        checkList.add(cla);
        List<ApiField> fieldList = new ArrayList<>();
        for (Field field : fields) {
            if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                continue;
            }
            if ("hash".equalsIgnoreCase(field.getName())) {
                continue;
            }
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
            if (jsonIgnore!=null&&!jsonIgnoreShow)
            {
                continue;
            }
            if (jsonIgnore != null&&!jsonIgnore.isNull()) {
                continue;
            }
            if (checkList.contains(field.getType()))
            {
                ApiField apiField = new ApiField();
                apiField.setName(field.getName());
                apiField.setType(field.getType().getSimpleName());
                apiField.setCaption("设计存在循环嵌套,需要修正返回对象");
                fieldList.add(apiField);
                break;
            }
            if (Serializable.class.isAssignableFrom(field.getType())&&!ClassUtil.isStandardType(field.getType()))
            {
                checkList.add(field.getType());
            }
            if (ClassUtil.isStandardProperty(field.getType()) || field.getType().equals(JSONObject.class)) {

                ApiField apiField = new ApiField();
                apiField.setName(field.getName());
                apiField.setType(field.getType().getSimpleName());
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    apiField.setCaption(column.caption());
                    if (!column.enumType().equals(NullClass.class)) {
                        Map<Object, Object> enumMap = ClassUtil.getEnumMap(column.enumType(), "value", "name");
                        if (enumMap != null) {
                            apiField.setCaption(column.caption() + ":" + enumMap.toString());
                        }
                    }
                }
                if (jsonIgnore!=null)
                {
                    apiField.setCaption(apiField.getCaption()+ ",非空显示");
                }
                fieldList.add(apiField);
            } else {
                ApiField apiField = new ApiField();
                apiField.setName(field.getName());
                apiField.setType(field.getType().getSimpleName());
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    apiField.setCaption(column.caption());
                    if (!column.enumType().equals(NullClass.class)) {
                        Map<Object, Object> enumMap = ClassUtil.getEnumMap(column.enumType(), "value", "name");
                        if (enumMap != null) {
                            apiField.setCaption(column.caption() + ":" + enumMap.toString());
                        }
                    }
                }
                if (jsonIgnore!=null)
                {
                    apiField.setCaption(apiField.getCaption()+ ",非空显示");
                }
                fieldList.add(apiField);
                Class<?> childObj = null;
                try {
                    childObj =  field.getType();
                    if (cla.equals(childObj)||Class.class.equals(childObj)||ClassUtil.isStandardType(childObj))
                    {
                        break;
                    }

                } catch (Exception e) {
                    //...
                    log.error("childObj:{},error:{}",childObj,e);
                }

                if (childObj != null) {

                    List<ApiField> childFieldList = getApiFieldList(childObj,jsonIgnoreShow,checkList);
                    if (childFieldList != null && !childFieldList.isEmpty()) {
                        apiField.setChildren(childFieldList);
                    }
                }
            }
        }

        Method[] methods = ClassUtil.getDeclaredMethods(cla);
        for (Method method:methods)
        {
            JsonField jsonField = method.getAnnotation(JsonField.class);
            if (jsonField != null) {
                ApiField apiField = new ApiField();
                apiField.setName(jsonField.name());
                apiField.setType(method.getReturnType().getSimpleName());
                apiField.setCaption(jsonField.caption());
                fieldList.add(apiField);
            }
        }
        return fieldList;
    }



    /**
     * 这里只得到方法的参数说明
     * @param cla 类型
     * @param exeMethod 方法
     * @param url       路径
     * @param namespace 备用命名空间
     * @return 文档操作对象
     */
    public static ApiOperate getMethodApiOperate(Class<?> cla,Method exeMethod, final String url,String namespace) {
        Operate operate = exeMethod.getAnnotation(Operate.class);
        ApiOperate apiOperate = new ApiOperate();
        apiOperate.setCaption(operate.caption());
        apiOperate.setAction(operate.post() ? "POST" : "GET;POST");


        Deprecated deprecated = exeMethod.getAnnotation(Deprecated.class);
        apiOperate.setDeprecated(deprecated!=null);

        ApiMethod apiMethod = new ApiMethod();
        String callMethod = operate.method();
        if (TXWebUtil.AT.equals(callMethod)) {
            apiMethod.setName(exeMethod.getName());
        }
        if (!StringUtil.isNull(operate.method())) {
            if (url.endsWith(StringUtil.ASTERISK)) {
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
        if (describe != null) {
            String cont = getDescribeValue(cla.getName() + StringUtil.DOT + exeMethod.getName(),describe,namespace);
            apiOperate.setDescribe(cont);
        }

        //方法参数-------------------------------------------------------------------------------------------
        Map<String, ApiParam> methodParamList = new LinkedHashMap<>();

        //i 表示第几个参数，下边完成参数组装
        Annotation[][] parameterAnnotations = exeMethod.getParameterAnnotations();
        Parameter[] parameters = exeMethod.getParameters();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            ApiParam methodParam = new ApiParam();
            Annotation[] annotations = parameterAnnotations[i];
            methodParam.setName(parameters[i].getName());
            methodParam.setFiledType(parameters[i].getType().getSimpleName());
            methodParam.setFiledClass(parameters[i].getType());
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    methodParam.setCaption(param.caption());
                    methodParam.setFiled(parameters[i].getName());
                    methodParamList.put(methodParam.getName(), methodParam);
                    if (ClassUtil.isStandardProperty(parameters[i].getType()) && param.request()) {
                        if (!param.enumType().equals(NullClass.class)) {
                            methodParam.setFiledType("enum");
                            Map<Object, Object> enumMap = ClassUtil.getEnumMap(param.enumType(), "value", "name");
                            if (enumMap != null) {
                                methodParam.setFormat(enumMap.toString());
                            }
                        } else if (ClassUtil.isNumberProperty(parameters[i].getType())) {

                            long max = Long.MAX_VALUE;
                            if (param.max() == Long.MAX_VALUE && parameters[i].getType().equals(Integer.class)) {
                                max = Integer.MAX_VALUE;
                            }
                            methodParam.setSafety(param.min() + "-" + (Long.MAX_VALUE == Long.min(max, param.max()) ? "..." : Long.min(max, param.max())));
                            methodParam.setFiledType(parameters[i].getType().getSimpleName());

                        } else if (parameters[i].getType().equals(String.class)) {
                            methodParam.setSafety("限长" + (Math.max(param.min(), 0)) + "-" + (Long.MAX_VALUE == param.max() ? "..." : param.max()) + ",安全[" + param.level() + "]");
                            methodParam.setFiledType(parameters[i].getType().getSimpleName());
                        }

                    } else if (!param.type().equals(NullClass.class)) {
                        methodParam.setFiledType(param.type().getSimpleName());
                        addMethodParam(methodParam.getChildren(), param.type());
                    } else if (param.type().equals(NullClass.class) && !ClassUtil.isStandardProperty(parameters[i].getType())) {
                        methodParam.setFiledType(parameters[i].getType().getSimpleName());
                        methodParam.setClassParam(true);
                        addMethodParam(methodParam.getChildren(), parameters[i].getType());
                    } else if (!ClassUtil.isIocInterfaces(parameters[i].getType())) {
                        methodParam.setFiledType(parameters[i].getType().getSimpleName());
                        addMethodParam(methodParam.getChildren(), parameters[i].getType());
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
        if (!StringUtil.isNull(apiMethod.getName()) && apiOperate.getUrl().endsWith(StringUtil.ASTERISK)) {
            String tmpUrl = apiOperate.getUrl().substring(0, apiOperate.getUrl().length() - 1);
            if (tmpUrl.endsWith("/") && apiMethod.getName().startsWith("/")) {
                apiOperate.setUrl(tmpUrl.substring(0, tmpUrl.length() - 1) + apiMethod.getName());
            } else {
                apiOperate.setUrl(tmpUrl + apiMethod.getName());
            }
        }

      /*
       排序,目前不是用,是用默认的字段顺序
        List<Map.Entry<String, ApiParam>> list = new ArrayList<>(methodParamList.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String, ApiParam>>() {
            //升序排序
            @Override
            public int compare(Map.Entry<String, ApiParam> o1,
                               Map.Entry<String, ApiParam> o2) {
                if (o1==null || o2==null || o1.getValue()==null || o2.getValue()==null || o1.getValue().getCaption()==null)
                {
                    return 0;
                }
                return o1.getValue().getCaption().compareTo(o2.getValue().getCaption());
            }
        });

        Map<String, ApiParam> sortMap = new LinkedHashMap<>();
        for(Map.Entry<String, ApiParam> mapping:list){
            sortMap.put(mapping.getKey(),mapping.getValue());
        }*/

        apiMethod.setParams(methodParamList);
        return apiOperate;
    }


    public static void putReturnApiField(Method exeMethod,ApiOperate apiOperate)
    {
        if (exeMethod.getReturnType().equals(void.class))
        {
            //无返回,无容器
            List<Class<?>> checkList = new ArrayList<>();
            apiOperate.setResult(getApiFieldList(RocResponse.class,false,checkList));
            return;
        }
        String returnTypeModel = exeMethod.getGenericReturnType().getTypeName();
        Class<?>[] returnTypeClass  = ClassUtil.getClassForTypeModel(exeMethod.getGenericReturnType().getTypeName()).toArray(new Class<?>[0]);
        apiOperate.setResultType(returnTypeModel);
        List<Class<Object>> classList =  toList(returnTypeClass);
        apiOperate.setResult(getApiFieldForReturnTypeModel(classList,returnTypeModel));
    }


    public static List<Class<Object>> toList(Class<?>[] array) {
        List<Class<Object>> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (Class<?> aClass : array) {
            list.add((Class<Object>) aClass);
        }
        return list;
    }
    /**
     *  根据配置的返回模型解构生成API
     * @param classList 配置的类对象列表
     * @param returnTypeModel 结构模型
     * @return 根据配置的返回模型解构生成API
     */
    public static List<ApiField> getApiFieldForReturnTypeModel(List<Class<Object>> classList,String returnTypeModel) {

        //得到第一层
        String firstName =  StringUtil.trim(StringUtil.substringBefore(returnTypeModel,"<"));
        Class<Object> firstClass = findDtoClass(firstName,classList);
        if (firstClass==null)
        {
            return null;
        }
        if (!ObjectUtil.isEmpty(classList)&&!classList.isEmpty())
        {
            classList.remove(firstClass);
        }
        List<Class<?>> checkList = new ArrayList<>();
        List<ApiField> apiFieldList = getApiFieldList(firstClass,classList.contains(List.class),checkList);

        if (!ObjectUtil.isEmpty(apiFieldList))
        {
            if (firstClass.equals(RocResponse.class))
            {
                ApiField apiField = findApiFieldForName("data",apiFieldList);
                if (apiField==null)
                {
                    apiField = new ApiField();
                    apiFieldList.add(apiField);
                    apiField.setCaption("数据");
                    apiField.setName("data");
                }
                returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
                if (!StringUtil.isEmpty(returnTypeModel))
                {
                    putChildApiFieldForReturnTypeModel(apiField,classList, returnTypeModel);
                }
            }
        }
        return apiFieldList;
    }

    /**
     * 查询返回
     * @param name 字段名称
     * @param apiFieldList 字段列表
     * @return 返回字段信息
     */
    public static ApiField findApiFieldForName(String name,List<ApiField> apiFieldList)
    {
        if (name==null||ObjectUtil.isEmpty(apiFieldList))
        {
            return null;
        }
        for (ApiField apiField:apiFieldList)
        {
            if (name.equalsIgnoreCase(apiField.getName()))
            {
                return apiField;
            }
        }
        return null;

    }

    /**
     * 更具泛型模型和类生成返回数据结构
     * @param apiField api字段容器
     * @param classList 类列表
     * @param returnTypeModel  泛型模型
     */
    public static void putChildApiFieldForReturnTypeModel(ApiField apiField,List<Class<Object>> classList,String returnTypeModel) {
        if (returnTypeModel.startsWith("<")&&returnTypeModel.endsWith(">"))
        {
            returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
        }
        //得到第一层
        String className =  StringUtil.trim(StringUtil.substringBefore(returnTypeModel,"<"));
        if ("list".equalsIgnoreCase(className)||List.class.getName().equalsIgnoreCase(className)||Set.class.getName().equalsIgnoreCase(className)||Collections.class.getName().equalsIgnoreCase(className))
        {
            apiField.setType("list");
            returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
            if (!StringUtil.isEmpty(returnTypeModel))
            {
                putChildApiFieldForReturnTypeModel(apiField, classList, returnTypeModel);
            }
        } else
        if ("map".equalsIgnoreCase(className)||Map.class.getName().equalsIgnoreCase(className))
        {
            apiField.setType("map");
            returnTypeModel = StringUtil.substringOutBetween(returnTypeModel,"<",">");
            apiField.setCaption(returnTypeModel);

            if (!StringUtil.isEmpty(returnTypeModel) && returnTypeModel.contains(","))
            {
                String returnTypeModelKey = StringUtil.substringBefore(returnTypeModel,",");
                String returnTypeModelValue = StringUtil.substringAfter(returnTypeModel,",");

                ApiField apiFieldKey = new ApiField();
                apiFieldKey.setName("key");
                apiFieldKey.setType(returnTypeModelKey);
                apiFieldKey.setCaption("关键字");

                ApiField apiFieldValue = new ApiField();
                apiFieldValue.setName("value");
                apiFieldValue.setType(returnTypeModelValue);
                apiFieldValue.setCaption("值");

                if (!StringUtil.isEmpty(returnTypeModelKey))
                {
                    putChildApiFieldForReturnTypeModel(apiFieldKey, classList, returnTypeModelKey);
                }
                if (!StringUtil.isEmpty(returnTypeModelValue))
                {
                    putChildApiFieldForReturnTypeModel(apiFieldValue, classList, returnTypeModelValue);
                }
                JSONObject json = new JSONObject();
                json.put("key",apiFieldKey);
                json.put("value",apiFieldValue);
                apiField.setChildJson(json);
            }
        }
        else
        {
            Class<?> theClass = findDtoClass(className,classList);
            if (theClass!=null)
            {
                List<Class<?>> checkList = new ArrayList<>();
                apiField.setChildren(getApiFieldList(theClass,false,checkList));

            }
        }

    }


    /**
     * 根据模型名称查询类
     * @param name 模型名称,不区分大小写
     * @param clsArray  类列表
     * @return 根据模型名称查询类
     */
    public static Class<Object> findDtoClass(String name,Class<Object>[] clsArray)
    {
        if (ObjectUtil.isEmpty(clsArray))
        {
            return null;
        }

        List<Class<Object>> clsList = Arrays.asList(clsArray);
        return findDtoClass(name,clsList);
    }

    public static Class<Object> findDtoClass(String name,List<Class<Object>> clsArray)
    {
        if (ObjectUtil.isEmpty(clsArray))
        {
            return null;
        }

        for (Class<Object> a:clsArray)
        {
            if (a==null|| Empty.class.equals(a))
            {
                continue;
            }
            if (a.getName().equalsIgnoreCase(name)||a.getSimpleName().equalsIgnoreCase(name))
            {
                return a;
            }
        }
        return null;
    }

    /**
     * 查找注释文档
     * @param name 方法名称
     * @param flag 方法名称重复加标识识别
     * @param namespace 命名空间
     * @return 内容
     */
    public static String findDescribe(String name, String flag,String namespace)
    {
        if (StringUtil.isEmpty(name))
        {
            return null;
        }
        File file = new File(EnvFactory.getBaseConfiguration().getDefaultPath(),namespace + ".describe.xml");
        if (!file.isFile())
        {
            file = EnvFactory.getFile(namespace + ".describe.xml");
        }
        if (file==null)
        {
            return null;
        }
        String xml = null;
        try {
            xml = IoUtil.autoReadText(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtil.isEmpty(xml))
        {
            return null;
        }

        Document document;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            log.error("文档XML格式错误 file:{}",file);
            e.printStackTrace();
            return null;
        }

        Element element = document.getRootElement();
        if (element==null)
        {
            return null;
        }

        Iterator<?> elementList = element.elementIterator("describe");
        if (ObjectUtil.isEmpty(elementList))
        {
            return null;
        }
        while (elementList.hasNext())
        {
            Element el = (Element)elementList.next();
            String id = StringUtil.trim(el.attributeValue("id"));
            if (StringUtil.isEmpty(flag)&&name.equalsIgnoreCase(id)||!StringUtil.isEmpty(flag)&&name.equalsIgnoreCase(id)&&flag.equalsIgnoreCase(StringUtil.trim(el.attributeValue("flag"))))
            {
                return el.getStringValue();
            }
        }
        return null;
    }

    /**
     *
     * @param id 文档id
     * @param describe 注释
     * @param namespace 备用命名空间
     * @return 得到描述
     */
    public static String getDescribeValue(String id,Describe describe,String namespace)
    {
        if (describe == null)
        {
            return null;
        }
        String cont;
        if (!ObjectUtil.isEmpty(describe.value()) && !"[\"\"]".equalsIgnoreCase(ObjectUtil.toString(describe.value()))) {
            cont = ArrayUtil.toString(describe.value(), "<br />");
        } else {
            String name = describe.namespace();
            if (StringUtil.isNull(name)|| Sioc.global.equals(name))
            {
                name = namespace;
            }
            cont = findDescribe(id,describe.flag(),name);
            cont = StringUtil.replace(cont,"\n        ```","\n```");
            cont = StringUtil.replace(cont,"\n\t```","\n```");
        }
        return ScriptMarkUtil.getMarkdownHtml(StringUtil.trim(cont));
    }


}

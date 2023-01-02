package com.github.jspxnet.txweb.context;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.ParamUtil;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ObjectUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;


public class ActionContext implements Serializable {
    final private Map<String, Object> environment = new HashMap<>();
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Object result;
    //如果执行了完整的一次变成 true
    private boolean executed = false;

    //执行过程中 resultCode 会不断的变动,最后为返回需要的
  //  private String resultCode;
    //action执行返回
    private String actionResult;
    //执行方法,替代代理类里边的变量,这样更加安全
    private Method method = null;
    private String exeType;

    //用来保存action自己的子变量, key 为类名_hashCode_变量名
    final private Map<String, Object> componentEnvironment = new HashMap<>(1);

    public boolean containsKey(String key)
    {
        if (ObjectUtil.isEmpty(environment))
        {
            return false;
        }
        return environment.containsKey(key);
    }


    public void put(String key, Object value)
    {
        if (ObjectUtil.isEmpty(environment))
        {
            return;
        }
        environment.put(key,value);
    }

    public Object get(String key)
    {
        return environment.get(key);
    }

    public String getString(String key)
    {
        return (String)environment.get(key);
    }

    public Object getOrDefault(String key,Object def)
    {
        return environment.getOrDefault(key,def);
    }


    /**
     * 只能放入一次
     * @param result 返回对象
     */
    public void setResult(Object result) {
        if (this.result!=null)
        {
            return;
        }
        if (hasFieldInfo())
        {
            return;
        }
        this.result = result;
    }

    /**
     * @return 得到错误信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getFieldInfo() {
        Map<String, String> fieldErrors = (Map<String, String>) environment.get(ActionEnv.Key_FieldInfo);
        if (fieldErrors == null) {
            fieldErrors = new HashMap<>(0);
            environment.put(ActionEnv.Key_FieldInfo, fieldErrors);
        }
        return fieldErrors;
    }
    /**
     * 添加提示信息或者错误错误类型
     *
     * @param keys 错误类型
     * @param msg  信息
     */
    @SuppressWarnings("unchecked")
    public void addFieldInfo(String keys, String msg) {
        Map<String, String> fieldError = (Map<String, String>) environment.get(ActionEnv.Key_FieldInfo);
        if (fieldError == null) {
            fieldError = new HashMap<>();
            environment.put(ActionEnv.Key_FieldInfo, fieldError);
        }
        fieldError.put(keys, msg);
    }


    /**
     * @param errors 放入错误信息
     */
    @SuppressWarnings("unchecked")
    public void addFieldInfo(Map<String, String> errors) {
        Map<String, String> fieldError = (Map<String, String>) environment.get(ActionEnv.Key_FieldInfo);
        if (fieldError == null) {
            fieldError = new HashMap<>(0);
            environment.put(ActionEnv.Key_FieldInfo, errors);
        }
        fieldError.putAll(errors);
    }

    /**
     * @return 判断是否有错误信息
     */
    @SuppressWarnings("unchecked")
    public boolean hasFieldInfo() {
        Map<String, String> fieldError = (Map<String, String>) environment.get(ActionEnv.Key_FieldInfo);
        return !ObjectUtil.isEmpty(fieldError);
    }

    /**
     * 满足ROC方式,得到第一条错误信息
     *
     * @return 失败消息
     */
    @SuppressWarnings("unchecked")
    public String getFailureMessage() {
        Map<String, String> fieldError = (Map<String, String>) environment.get(ActionEnv.Key_FieldInfo);
        if (fieldError != null && !fieldError.isEmpty()) {
            Iterator<String> iterator = fieldError.values().iterator();
            return iterator.next();
        }
        return null;
    }


    /**
     * @param msg 添加说明信息
     */
    @SuppressWarnings("unchecked")
    public void addActionMessage(String msg) {
        List<String> actionMsg =  (List<String>)  environment.get(ActionEnv.Key_ActionMessages);
        if (actionMsg == null) {
            actionMsg = new ArrayList<>();
        }
        actionMsg.add(msg);
        environment.put(ActionEnv.Key_ActionMessages, actionMsg);
    }

    /**
     * @return 说明信息列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getActionMessage() {
        List<String> list =  (List<String>)  environment.get(ActionEnv.Key_ActionMessages);
        if (list == null) {
            list = new ArrayList<>();
            environment.put(ActionEnv.Key_ActionMessages, list);
        }
        return list;
    }
    @SuppressWarnings("unchecked")
    public String getSuccessMessage() {
        List<String> list =  (List<String>)  environment.get(ActionEnv.Key_ActionMessages);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * @return 是否有说明信息
     */
    @SuppressWarnings("unchecked")
    public boolean hasActionMessage() {
        List<String> list =  (List<String>)  environment.get(ActionEnv.Key_ActionMessages);
        return !ObjectUtil.isEmpty(list);
    }


    /**
     *
     * @return 得到请求参数
     */
    public JSONObject getJsonParams() {
        JSONObject json = (JSONObject) environment.get(ActionEnv.Key_CallRocJsonData);
        if (json==null)
        {
            return null;
        }

        boolean isRoc = ParamUtil.isRocRequest(json);
        if (!isRoc)
        {
            return json;
        }
        JSONObject methodJson = json.getJSONObject(Environment.rocMethod);
        if (methodJson!=null&&methodJson.containsKey(Environment.rocParams))
        {
            return methodJson.getJSONObject(Environment.rocParams);
        }
        return json.getJSONObject(Environment.rocParams);
    }


    /**
     * @return 得到ip
     */
    public String getRemoteAddr() {
        return RequestUtil.getRemoteAddr(request);
    }

    /**
     *
     * @return 得到调用的json
     */
    public JSONObject getCallJson()
    {
        return (JSONObject) environment.get(ActionEnv.Key_CallRocJsonData);
    }


    /**
     *
     * @return 得到action 名
     */
    public String getActionName()
    {
        return getString(ActionEnv.Key_ActionName);
    }

    /**
     *
     * @return 得到命名空间
     */
    public String getNamespace()
    {
        return getString(ActionEnv.Key_Namespace);
    }

    public void setMethod(Method method) {
        this.method = method;
        //如果是void 方法，自动设置 NONE begin
        if (void.class.equals(method.getGenericReturnType())) {
            actionResult = ActionSupport.NONE;
        }
        //如果是void 方法，自动设置 NONE end
    }

    public Map<String, Object> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, Object> environment) {
        this.environment.clear();
        this.environment.putAll(environment);
    }

    @SuppressWarnings("unchecked")
    public void setComponentEnvironment(Class<?> clas,int hashCode,Map<String, Object> env)
    {
        Map<String, Object> map = (Map<String, Object>)componentEnvironment.get(TXWebUtil.getComponentEnvKey(clas,hashCode));
        if (ObjectUtil.isEmpty(map))
        {
            map = new HashMap<>();
            componentEnvironment.put(TXWebUtil.getComponentEnvKey(clas,hashCode),map);
        }
        map.clear();
        map.putAll(env);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getComponentEnvironment(Class<?> clas,int hashCode)
    {
        Map<String, Object> map = (Map<String, Object>)componentEnvironment.get(TXWebUtil.getComponentEnvKey(clas,hashCode));
        if (ObjectUtil.isEmpty(map))
        {
            map = new HashMap<>();
            componentEnvironment.put(TXWebUtil.getComponentEnvKey(clas,hashCode),map);
        }
        return map;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Object getResult() {
        return result;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public Method getMethod() {
        return method;
    }

    public String getExeType() {
        return exeType;
    }

    public void setExeType(String exeType) {
        this.exeType = exeType;
    }

    /**
     * 释放内存
     */
    public void clean()
    {
        ObjectUtil.free(environment);
        ObjectUtil.free(componentEnvironment);
    }



}

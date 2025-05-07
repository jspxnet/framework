/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb;

import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.txweb.table.UserSession;
import org.apache.poi.ss.formula.functions.T;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 16:54:35
 */

public interface Action extends Serializable {

    static public final String SUCCESS = "success"; //无问题

    //返回---begin 功能和 KEY_RETURN 一样,以后就不要去了
    static public final String NONE = "none"; //什么也不处理 并且不会调用 Redirect

    static public final String ERROR = "error"; //发生错误

    static public final String INPUT = "input"; //录入不完整

    static public final String FAIL = "fail"; //操作失败

    static public final String LOGIN = "login"; //跳转到登录
    //static public final String PASSWORD = "password"; //需要密码
    static public final String UNTITLED = "untitled";  //无权利的
    static public final String MESSAGE = "message"; //返回信息
    static public final String TEMPLATE = "template"; //返回信息  ,最基本的模板返回
    static public final String HtmlImg = "htmlImg"; //html转换为png 图片
    static public final String HtmlPdf = "htmlPdf"; //html转换为pdf文档
    static public final String Markdown = "markdown"; //html转换为pdf文档
    static public final String FileSave = "fileSave"; //将页面生成的文件保存为文件，主要提供生成静态文件使用
    static public final String PdfPageImg = "pdfPageImg"; //pdf 一页转换为图片输出
    static public final String ZipFile = "zipFile"; //pdf 一页转换为图片输出
    static public final String LICENSE = "license"; //返回信息

    static public final String CHAIN = "chain"; //返回信息
    static public final String XSTREAM = "xstream"; //XML Bean
    static public final String QRCode = "qrcode"; //XML Bean

    static public final String ROC = "roc"; //json xml 格式的的数据对象返回
    static public final String EXCEL = "excel"; //excel格式
    static public final String JXLS = "jxls"; //excel jxls模版 格式的消息
    static public final String CHARTS = "charts"; //FusionCharts 图形
    static public final String REDIRECT = "redirect"; //页面跳转
    static public final String PRINT = "print"; //页面跳转
    static public final String FORWARD = "forward"; //页面跳转
    //返回---end

    //浏览器控制 begin
   /* static public final String BrowserCache = "BrowserCache"; //是否使用浏览器缓存 默认为 true
    static public final String ContentType = "ContentType"; //请求类型
    static public final String Content_Disposition = "Content-Disposition"; //下载类型
    static public final String KEY_RedirectUrl = "RedirectUrl"; //注释方式的配置返回
    //浏览器控制 end

    //是否载入手机模板,如果是手机，那么载入 name.mobile.ftl 否则 name.ftl  目录不变
    static public final String KEY_MobileTemplate = "mobileTemplate";

    //static public final String BOM = "bom"; //UTF-8 带BOM输出,图形报表使用

    //保存调用方法，用来判断是否发生了操作，例如发生操作后是否清空刷新缓存
    static public final String Key_CallMethodName = "callMethodName";
    static public final String Key_CallRocJsonData = "callRocDataJson";

    static public final String Key_Session = "session";
    static public final String Key_Request = "request";
    static public final String Key_Response = "response";
    static public final String Key_RealPath = "RealPath";
    static public final String Key_FormHash = "formHash";

    static public final String Key_ActionName = "ActionName";
    static public final String Key_This = "action";
    static public final String Key_Namespace = "Namespace";

    static public final String Key_FieldInfo = "FieldInfo";

    //返回的数据对象


    static public final String Key_ActionMessages = "ActionMessages";

    static public final String Key_Config = "Config";  //配置
    static public final String Key_Language = "Language"; //语言
    static public final String Key_Option = "Option"; //字典

    //static public final String Key_UserSession = "IUserSession"; //TXWeb 不做处理 留给用户自己处理
    static public final String Key_ResultMethods = "resultMethods"; //json 返回数据的方法列表
    static public final String Key_PageEnable = "pageEnable";   //是否打开分页
    public static final String KEY_organizeId = "organizeId";

    static public final String mobileTemplateSuffix = "mobile"; //返回不运行下边的程序

    //日志记录对象begin
    static public final String Key_ActionLogContent = "ActionLogContent";
    static public final String Key_ActionLogTitle = "ActionLogTitle";
    //日志记录对象end

    //返回数据对象
    static public final String Key_ActionResultObject = "ActionResultObject";
*/
    //初始化,在调用 配置方法之前 或者 如果无配置方法在execute之前执行和标签运行前
    void initialize() throws Exception;

    //卸载数据,运行玩后释放空间
    void destroy();

    //执行
    String execute() throws Exception;

    boolean isComponent();

    @Deprecated
    Map<String, Object> getEnv();

    void initEnv(Map<String, Object> paramMap, String exeType);

    void put(String key, Object obj);

    //信息存储
    List<String> getActionMessage();

    void addActionMessage(String msg);

    boolean hasActionMessage();

    boolean isGuest();

    //字段错误信息
    Map<String, String> getFieldInfo();

    void addFieldInfo(String keys, String msg);

    void addFieldInfo(Map<String, String> errors);

    boolean hasFieldInfo();

    //file 容器对象
    //void setRequest(HttpServletRequest request);

    //void setResponse(HttpServletResponse response);

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    HttpSession getSession();

    String getEncode();

    String getLocaleName();

    //返回对象
    Object getResult();

    void setResult(Object value);

    void setTemplatePath(String templatePath);

    String getTemplatePath();

    String getTemplateFile();

    <T> T getBean(Class<T> cla);

    String getString(String name);

    String getString(String name, boolean checkSql);

    String getString(String name, String def, boolean checkSql);

    int getInt(String name);

    int getInt(String name, int def);

    String[] getArray(String name, boolean checkSql);

    int[] getIntArray(String name);

    int[] getIntArray(String name, int[] defArray);

    Integer[] getIntegerArray(String name);

    Integer[] getIntegerArray(String name, Integer[] defArray);

    Long[] getLongArray(String name);

    Long[] getLongArray(String name, Long[] defArray);

    double[] getDoubleArray(String name);

    Double[] getDoubleObjectArray(String name);

    float[] getFloatArray(String name);

    Float[] getFloatObjectArray(String name);

    double getDouble(String name, double def);

    BigDecimal[] getBigDecimalArray(String name);

    long getLong(String name);

    long getLong(String name, long def);

    float getFloat(String name, float def);

    boolean getBoolean(String name);

    Date getDate(String name, String format);

    Date getDate(String name);

    String getPathLevel(int level);

    long getUrlNumber();

    String[] getParameterNames();

    String[] getAttributeNames();

    String getRemoteAddr();

    void setEnv(Map<String, Object> environment);

    boolean containsKey(String key);

    String getEnv(String keys);

    Long[] getLongJoinArray(String name1, String name2);

    String toQueryString(String name) throws Exception;

    String toQueryString(Map<String, String> param) throws Exception;

    <T> T getEnv(String keys, Class<T> t);

    //用户seesion
    UserSession getUserSession();

    //i18n
    Bundle getLanguage();

    Option getOption();

    Bundle getConfig();

    //日志对象
    void setActionLogTitle(String value);

    String getActionLogTitle();

    void setActionLogContent(Serializable value);

    void setOrganizeId(Serializable value);

    Object getActionLogContent();

    ActionLog getActionLog();

    String getCookie(String name);

    String getActionResult();

    String getRootNamespace();

    void printError(Object out, int status);

    void print(Object html);

    void setActionResult(String actionResult);

    String getSuccessMessage();

    boolean isMethodInvoked();

    boolean isMobileBrowser();

    boolean containsUserAgent(String str);

    String getFailureMessage();

}
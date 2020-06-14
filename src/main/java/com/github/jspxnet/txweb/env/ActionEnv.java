package com.github.jspxnet.txweb.env;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/8/14 22:13
 * description: jspbox
 **/
public class ActionEnv {
    private ActionEnv()
    {

    }

    public static final String[] NO_CLEAN = new String[]{"ActionName", "ContentType", "mobile", "mobileTemplate", "Namespace", "RealPath"};
    static public final String BrowserCache = "BrowserCache"; //是否使用浏览器缓存 默认为 true
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
    static public final String KEY_ACTION_RESULT_OBJECT = "ActionResultObject";

    static public final String KEY_ACTION_RESULT = "keyActionResult";
}

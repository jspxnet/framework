package com.github.jspxnet.boot.sign;

public class HttpStatusType {
    private HttpStatusType() {

    }

    static public int HTTP_status_OK = 200;
    //-----------------------------------
    //(错误请求) 服务器不理解请求的语法，一般表示参数错误
    static public int HTTP_status_400 = 400;

    static public String lan_http_status_400 = "http_status_400";


    //（未授权) 请求要求进行身份验
    static public int HTTP_status_401 = 401;

    static public String lan_http_status_401 = "http_status_401";

    //(已禁止) 服务器拒绝请求
    static public int HTTP_status_403 = 403;

    static public String lan_http_status_403 = "http_status_403";

    //无法找到文件
    static public int HTTP_status_404 = 404;

    static public String lan_http_status_404 = "http_status_404";

    //资源被禁止   用来访问本页面的 HTTP 谓词不被允许
    static public int HTTP_status_405 = 405;


    static public String lan_http_status_405 = "http_status_405";

    //无法使用请求的内容特性来响应请求的网页
    static public int HTTP_status_406 = 406;

    static public String lan_http_status_406 = "http_status_406";

    //URI 太长
    static public int HTTP_status_414 = 414;

    static public String lan_http_status_414 = "http_status_414";
    //内部服务器错误
    static public int HTTP_status_500 = 500;
    static public String lan_http_status_500 = "http_status_500";
    //服务器太忙
    static public int HTTP_status_513 = 513;

    static public String lan_http_status_513 = "http_status_513";
}

<%@ page import="java.util.*" %>
<%@ page import="com.github.jspxnet.component.alipay.Payment" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=GBK">
    <title>支付宝支付</title>
</head>

<%
    Date Now_Date = new Date();
    String paygateway = "https://www.alipay.com/cooperate/gateway.do?";    //'支付接口
    String service = "create_direct_pay_by_user";//快速付款交易服务
    String sign_type = "MD5";
    String out_trade_no = Now_Date.toString();    //商户网站订单
    String input_charset = "GBK";
    String partner = ""; //支付宝合作伙伴id (账户内提取)
    String key = ""; //支付宝安全校验码(账户内提取)
    String body = "阿"; //商品阿描述，推荐格式：商品名称（订单编号：订单编号）
    String total_fee = "0.01";                 //订单总价
    String payment_type = "1";//支付宝类型.1代表商品购买
    String seller_email = "";         //卖家支付宝帐户
    String subject = "AAA:" + out_trade_no;             //商品名称
    String show_url = "www.sina.com.cn";
    String notify_url = "http://10.2.17.136:8081/jsp_direct_gbk/alipay_notify.jsp";                    //通知接收URL
    String return_url = "http://localhost:8081/jsp_direct_gbk/alipay_return.jsp";    //支付完成后跳转返回的网址URL

    String ItemUrl = Payment.CreateUrl(paygateway, service, sign_type, out_trade_no, input_charset, partner, key, show_url, body, total_fee, payment_type, seller_email, subject, notify_url, return_url);

%>
<a href="<%=ItemUrl%>">
    <img src="images/alipay_bwrx.gif" border="0"></a>
<body>

</body>
</html>
<%@ page import="java.util.*" %>
<%@ page import="com.github.jspxnet.component.alipay.Payment" %>
<%@ page import="com.github.jspxnet.component.alipay.SignatureHelper" %>
<%
    String partner = ""; //partner合作伙伴id（必须填写）
    String privateKey = ""; //partner 的对应交易安全校验码（必须填写）
//**********************************************************************************
//如果您服务器不支持https交互，可以使用http的验证查询地址
    //String alipayNotifyURL = "https://www.alipay.com/cooperate/gateway.do?service=notify_verify"
    String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?"
            + "partner="
            + partner
            + "&notify_id="
            + request.getParameter("notify_id");

    //获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
    String responseTxt = Payment.check(alipayNotifyURL);

    Map params = new HashMap();
    //获得POST 过来参数设置到新的params中
    Map requestParams = request.getParameterMap();
    for (Iterator iter = requestParams.keySet().iterator(); iter
            .hasNext(); ) {
        String name = (String) iter.next();
        String[] values = (String[]) requestParams.get(name);
        String valueStr = "";
        for (int i = 0; i < values.length; i++) {
            valueStr = (i == values.length - 1) ? valueStr + values[i]
                    : valueStr + values[i] + ",";
        }
        params.put(name, valueStr);
    }

    String mysign = SignatureHelper.sign(params, privateKey);

    if (mysign.equals(request.getParameter("sign")) && "true".equals(responseTxt)) {

        if ("TRADE_FINISHED".equals(request.getParameter("trade_status"))) {
            out.println("success");
        }
    } else {
        out.println("fail");
    }
%>

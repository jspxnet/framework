/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.alipay;

import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * <pre>
 * 目前暂时给出15个，成功集成了的朋友，集成过程中遇到的问题可以和大家分享，这样可以让大家少走很多弯路。
 * 更新咯2006.8.31
 * 1、ILLEGAL_PARTNER，HASH_NO_PRIVILEGE，ILLEGAL_SIGN这三种常见的错误代码，代表什么意思，一般是什么情况下出现的？
 * 答：ILLEGAL_PARTNER为无效的合作伙伴id。Partner id是在自己的支付宝账户内提取的一串数字，常见的为填写成了支付宝email或者是复制到程序的时候多了空格。
 * HASH_NO_PRIVILEGE，没有权限，如果是虚拟或者实物交易，请账户内自己申请。
 * flash演示。点击查看
 * ILLEGAL_SIGN，签名错误。说明程序计算出来的sign和支付宝计算结果不匹配。出现这种错误的情况很多，比如签名的排序错误，安全校验码填写错误，net。Java程序中没有制定编码。
 * 1. MD5校验顺序问题
 * MD5是根据参数汇总表的参数，按照字母升序顺序排列。您需要传递哪些参数，就要将那些参数加入到MD5的原始字符串中。原始字符串最后记得串上自己的安全校验码。例如：调用某接口需要以下参数
 * service= create_direct_pay_by_user
 * partner=2088006300000000
 * key(支付宝安全校验码)=1234567890123
 * email=testaio@msn.com
 * 那么待签名数据就是：{@code email=testaio@msn.com&partner=2088006300000000&service= create_direct_pay_by_user1234567890123 }
 * 2.通知返回的种类
 * 现支付宝的通知有两类。通知地址不需要像以前一样去账户内设置，而是由客户在支付的时候通过参数传递给我地址。例如notify_url=”http://www.xxx.com/notify_alipay.asp”
 * A服务器通知，对应的参数为notify_url，支付宝通知使用POST方式
 * B页面跳转通知，对应的参数为return_url，支付宝通知使用GET方式
 * 3.通知返回是返回到哪里？
 * 不需要像以前一样去账户内设置，而是由客户在支付的时候通过参数传递给我地址。例如notify_url=http://www.xxx.com/ntofiy_alipay.asp
 * 我们将根据传递过来的地址，返回给你消息，请注意格式，地址要是全的http://的绝对地址。
 * 4.为什么我都设置对了没有收到消息通知呢?
 * A您设置的接受消息通知的URL没有写全,您可能写成www.alipay.com了,请写成http://www.alipay.com或者https://www.alipay.com这样完全的URL
 * 5.虚拟物品的流程问题
 * 由于支付宝的ATN（active trade notify）是服务器通知，有完备的通知策略，可以说能接近达到0掉单。但是可能会由于网络的原因通知不能实时到达！
 * 6.订单号参数设置问题
 * 客户的订单号参数为out_trade_no。每个客户的购物网站上，都有自己的订单流水号，可以把相关的订单号传递给支付宝，以作对账之用。在支付宝程序中，out_trade_no,一定是变量，不可以是一个定值。
 * 7.中文编码问题
 * 我的参数排序肯定没有错，为何我无论怎么设置，总提示错误？ILLEGAL_SIGN
 * 无论使用何种语言写的支付程序，请注意url请求（request）过来时候的编码，需要使用gb2312或者gbk。
 * 8.我是淘宝会员也是开发者，请问在淘宝上使用支付宝和在外部使用有什么区别么？
 * 在任何使用支付宝的网站上所进行的“支付宝交易”和在淘宝上使用没有区别，支付宝会给您做交易中介的。
 * 交易时您最好看清楚交易的内容，价格，商品介绍等信息。
 * 作为开发者一定要熟悉“支付宝交易”的流程。
 * 9.为何我一直接收不到支付宝的交易通知？
 * 会有支付宝到您服务器的网络不通的情况：可能由于DNS解析，网通或电信线路维修，DNS问题可以在URL里面直接设置IP来解决。
 * 能接收到通知的先决条件：必须有不重复的交易订单号，支付时传递给了支付宝了通知返回URL（notify_url或者return_url）。
 * 支付宝通知的请求是一串url，如果您是apache服务器，可以查看apache的access.log看是否有访问记录。iis也有相应的访问log（具体位置不记得了。知道的朋友麻烦站内信息发我一下）
 * 附：
 * dboyzhang：建立一个最简单的程序看看有没有返回信息，比如一个很简单的写入文件程序，把GET方式传来的信息写少许进文件，然后在商家工具进行故障申请查看返回结果。注意是GET方式，ASP中是request("notify_id")而不是request.form("notify_id")
 * 10.购物车打包使用支付交易时如何设置请求
 * 请分别传递您商品的实际总价格price和邮费，以及邮费实际承担方，这样就不会出现让客户感觉很奇怪的一些问题了。11.通知返回接口，在收到支付宝通知以后，是否再返回给支付宝消息？
 * 支付宝的通知形式有两种：
 * A服务器通知，对应的参数为notify_url，支付宝通知使用POST方式
 * B页面跳转通知，对应的参数为return_url，支付宝通知使用GET方式
 * 对于return的页面通知，接受到支付宝的消息以后，不需要给支付宝系统任何的回应。
 * 而对于notify的服务器通知，在收到支付宝通知后，请按照自己需要的业务逻辑处理，并返回一个不包含任何HTML标签的页面，里面仅包含“Success”或者“Fail”的文本串，注意请不要包含任何其他的字符，如空格等
 * 通知环节的设置
 * 12.Notify响应给支付宝的消息是什么
 * 在收到支付宝通知后，请按照自己需要的业务逻辑处理，并返回一个不包含任何HTML标签的页面，里面仅包含“success”或者“fail”的文本串，注意请不要包含任何其他的字符，如空格等。对应的“success”或者“fail”的含义请看下表：
 * 返回结果
 * 结果说明
 * success
 * 处理成功，结束发送
 * fail
 * 处理失败，重新发送
 * 特别注意
 * 在接收到支付宝服务器的通知以后，请使用HTTP通知验证接口 校验该通知的合法性，以确保你的系统的后续操作的正确性。
 * 13.通知环节有哪些，为什么我支付以后不给我通知
 * https://www.alipay.com/cooperate/apply_digi_goods_security_trade_service.htm
 * 请先确保账户内选择了通知环节。现在有一下通知环节选取：
 * 请选择您需要得到通知的交易状态：
 * 交易创建
 * 买家付款成功
 * 卖家发货成功
 * 交易成功
 * 交易关闭
 * 退款成功
 * 退款关闭
 * 修改交易价格
 * 否则会出现支付以后，无法返回收到通知消息的
 * 14.我以前积压的信息，申请故障恢复是否可以重发吗？
 * 使用notify url来接收服务器通知，如果返回异常或者fail。致使notify的通知停止了，可以在支付宝的账户内，商家工具，左下角“申请故障恢复”。申请成功后，会立刻返回消息。
 * 15.通知接口的工作原理
 * 支付宝的两个通知接口都使用同样的工作原理，两个工作步骤：
 * 1对支付宝的通知过来的消息做验证，得到true或者false的结果。
 * {@code
 * //String alipayNotifyURL =https://www.alipay.com/cooperate/gateway.do?service=notify_verify
 * String alipayNotifyURL =http://notify.alipay.com/trade/notify_query.do?
 * +"&partner="
 * + partner
 * +"notify_id="
 * +request.getParameter("notify_id");
 * String sign=request.getParameter("sign");
 * 如上java程序，使用https或者http的查询地址。传递partner和notify id去验证消息的结果。(详细请见文档中的。“http通知验证接口”)
 * 2通过验证接口，得到正确的true结果，然后对传递过来的参数进行签名比对。（签名机制同支付程序）。
 * 比对签名通过，并得到正确的验证结果true。接着判断交易状态（红字其他交易状态在支付宝账户内可以选择，见“通知环节有哪些，为什么我支付以后不给我通知”），再在页面上打印出成功消息。或者失败消息（详情见“Notify响应给支付宝的消息是什么”）例如
 * If mysign=request.Form("sign") And ResponseTxt="true" Then
 * If request.Form("trade_status")="TRADE_FINISHED" Then
 * （客户的发货程序，更新订单）
 * response.write "success"
 * End If
 * Else
 * response.write "fail"
 * End If
 * }</pre>
 * 3注意，在返回success之前作客户网站的业务参数，比如发货，更新订单。
 */
public class Payment {

    /* * 对字符串进行MD5加密
     * @param urlvalue url地址
     * @return 获取url内容
     */
    public static String check(String urlvalue) {
        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //系统打印出抓取得验证结果
        return StringUtil.empty;
    }

    public static String CreateUrl(String paygateway, String service, String sign_type,
                                   String show_url, String quantity, String partner,
                                   String key, String body, String notify_url, String out_trade_no,
                                   String price, String return_url, String seller_email,
                                   String subject, String input_charset) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("service", service);
        params.put("partner", partner);
        params.put("subject", subject);
        params.put("body", body);
        params.put("out_trade_no", out_trade_no);
        params.put("price", price);
        params.put("show_url", show_url);
        params.put("quantity", quantity);
        params.put("seller_email", seller_email);
        params.put("return_url", return_url);
        params.put("notify_url", notify_url);
        params.put("_input_charset", input_charset);


        StringBuilder parameter = new StringBuilder(paygateway);
        List<String> keys = new ArrayList<>(params.keySet());
        for (String key1 : keys) {
            try {
                parameter.append(key1).append("=").append(URLEncoder.encode(params.get(key1), input_charset)).append("&");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        }

        String sign = EncryptUtil.getMd5(getContent(params, key));
        return parameter + "sign=" + sign + "&sign_type=" + sign_type;

    }

    private static String getContent(Map<String, String> params, String privateKey) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder prestr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {
                prestr.append(key).append("=").append(value);
            } else {
                prestr.append(key).append("=").append(value).append("&");
            }
        }
        return prestr + privateKey;
    }
}
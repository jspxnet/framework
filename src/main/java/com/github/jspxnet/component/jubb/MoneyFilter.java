/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.jubb;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.PaymentType;
import com.github.jspxnet.txweb.model.param.SignParam;
import com.github.jspxnet.txweb.util.ParamUtil;

import com.github.jspxnet.utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 12-12-4
 * Time: 下午10:11
 * 付款才能查看
 * IntegralFilter
 */
public class MoneyFilter extends HTMLFilter {

    private String moneyTip = "【此部分为隐藏内容,支付${money}RMB后可看,<button class=\"paymentButton\" data=\"${data}\" money=\"${money}\" sign=\"${sign}\" signType=\"${signType}\">支付</button>】";

    private Map<Integer, Double> payMap = new HashMap();
    private Map<String, Object> valueMap = new HashMap<String, Object>();


    public MoneyFilter(String s) {
        super(s);
    }

    public MoneyFilter() {

    }

    public String getMoneyTip() {
        return moneyTip;
    }

    public void setMoneyTip(String moneyTip) {
        this.moneyTip = moneyTip;
    }

    public Map<Integer, Double> getPayMap() {
        return payMap;
    }

    public void setPayMap(Map<Integer, Double> payMap) {

        this.payMap = payMap;
    }

    public void setValueMap(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public String convertString() {
        return hideConverter();
    }


    public String hideConverter() {
        boolean isGuest = ObjectUtil.toBoolean(valueMap.get("isGuest"));

        final Map<Integer, Double> moneyMap = new HashMap();
        Pattern pattern = compile("(\\[money=([0-9]*)\\])(.+?)(\\[\\/money\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();

        //不能删除,后边还要用的
        //签名验证算法
        String hashAlgorithmKey = (String) valueMap.get(Environment.hashAlgorithmKey);
        String hashAlgorithm = (String) valueMap.get(Environment.hashAlgorithm);

        int i = 0;
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {
            i++;
            String lan = matcher.group(2);
            double inMoney = StringUtil.toDouble(lan);
            moneyMap.put(i, inMoney);
            valueMap.remove("sign");
            valueMap.remove("data");
            valueMap.put("sort", i);
            valueMap.put(PaymentType.KEY_AMOUNT, inMoney);

            if (!isGuest && ValidUtil.isNumber(lan) && payMap.containsKey(i) && payMap.get(i) >= moneyMap.get(i)) {
                matcher.appendReplacement(stringbuffer, matcher.group(3));
            } else {
                //不满足条件
                try {
                    if (isGuest) {
                        valueMap.put("data", "");
                        valueMap.put("sign", "");
                        matcher.appendReplacement(stringbuffer, moneyTip);
                    } else {
                        //只是加密验证的MD5
                        SignParam signParam = ParamUtil.createSignParam(valueMap, hashAlgorithmKey, hashAlgorithm);
                        valueMap.put("data", signParam.getData());
                        valueMap.put("sign", signParam.getSign());
                        valueMap.put("signType", signParam.getSignType());
                        String txt = EnvFactory.getPlaceholder().processTemplate(valueMap, moneyTip);
                        matcher.appendReplacement(stringbuffer, txt);
                    }
                } catch (Exception e) {
                    matcher.appendReplacement(stringbuffer, "moneyTip 提示配置错误");
                    e.printStackTrace();
                }
            }
        }
        matcher.appendTail(stringbuffer);
        return stringbuffer.toString();
    }
}
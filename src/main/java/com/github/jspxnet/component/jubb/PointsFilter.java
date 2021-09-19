/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.component.jubb;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.PaymentType;
import com.github.jspxnet.security.asymmetric.AsyEncrypt;
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
 * date: 13-8-25
 * Time: 下午10:07
 */
public class PointsFilter extends HTMLFilter {

    private int points = 0;
    private String pointsTip = "【此部分为隐藏内容,支付${points}积分后可看,<button class=\"paymentButton\" data=\"${data}\" points=\"${points}\" sign=\"${sign}\" signType=\"${signType}\">支付</button>】";

    private Map<Integer, Integer> payMap = new HashMap();
    private Map<String, Object> valueMap = new HashMap();

    public PointsFilter(String s) {
        super(s);
    }

    public PointsFilter() {

    }

    public Map<Integer, Integer> getPayMap() {
        return payMap;
    }

    public void setPayMap(Map<Integer, Integer> payMap) {
        this.payMap = payMap;
    }

    public void setValueMap(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPointsTip() {
        return pointsTip;
    }

    public void setPointsTip(String pointsTip) {
        this.pointsTip = pointsTip;
    }

    @Override
    public String convertString() {
        return hideConverter();
    }

    public String hideConverter() {

        boolean isGuest = ObjectUtil.toBoolean(valueMap.get("isGuest"));
        AsyEncrypt encrypt = EnvFactory.getAsymmetricEncrypt();
        final Map<Integer, Integer> pointsMap = new HashMap<Integer, Integer>();
        Pattern pattern = compile("(\\[points=([0-9]*)\\])(.+?)(\\[\\/points\\])", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        StringBuffer stringbuffer = new StringBuffer();
        String hashAlgorithmKey = (String) valueMap.get(Environment.hashAlgorithmKey);
        String hashAlgorithm = (String) valueMap.get(Environment.hashAlgorithm);

        int i = 0;
        for (boolean flag = matcher.find(); flag; flag = matcher.find()) {

            valueMap.remove("sign");
            valueMap.remove("payData");
            String lan = matcher.group(2);
            int payValue = StringUtil.toInt(lan);
            i++;
            pointsMap.put(i, payValue);
            valueMap.put(PaymentType.KEY_POINTS, payValue);
            valueMap.put("sort", i);
            if (!isGuest && ValidUtil.isNumber(lan) && payMap.containsKey(i) && payMap.get(i) >= pointsMap.get(i)) {
                matcher.appendReplacement(stringbuffer, matcher.group(3));
            } else {
                try {

                    if (isGuest) {
                        valueMap.put("data", "");
                        valueMap.put("sign", "");
                        matcher.appendReplacement(stringbuffer, pointsTip);
                    } else {
                        SignParam signParam = ParamUtil.createSignParam(valueMap, hashAlgorithmKey, hashAlgorithm);
                        valueMap.put("data", signParam.getData());
                        valueMap.put("sign", signParam.getSign());
                        valueMap.put("signType", signParam.getSignType());
                        String txt = EnvFactory.getPlaceholder().processTemplate(valueMap, pointsTip);
                        matcher.appendReplacement(stringbuffer, txt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    matcher.appendReplacement(stringbuffer, "pointsTip 提示配置错误");
                }
            }
        }
        matcher.appendTail(stringbuffer);

        return stringbuffer.toString();
    }
}
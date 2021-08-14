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

import java.util.*;

public class SignatureHelper_return {
    public static String sign(Map params, String privateKey) {
        Map<String, String> properties = new HashMap<String, String>();
        for (Object o : params.keySet()) {
            String name = (String) o;
            Object value = params.get(name);
            if (name == null || "sign".equalsIgnoreCase(name)
                    || "sign_type".equalsIgnoreCase(name)) {
                continue;
            }

            properties.put(name, value.toString());

        }
        return sign(getSignatureContent(properties), privateKey);
    }

    public static String getSignatureContent(Map<String, String> properties) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<String>(properties.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = properties.get(key);
            content.append(i == 0 ? "" : StringUtil.AND).append(key).append(StringUtil.EQUAL).append(value);
        }
        return content.toString();
    }

    public static String sign(String content, String privateKey) {
        if (privateKey == null || "".equals(privateKey)) {
            return StringUtil.empty;
        }
        return EncryptUtil.getMd5(content + privateKey);

    }

}
package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

public abstract class BasePermissionInterceptor extends InterceptorSupport {

    protected final static EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();

    public final static String GUEST_STOP_URL_TXT = "guest_stop_url_txt";
    public final static String ADMIN_RULE_URL_TXT = "admin_rule_url_txt";

    @Setter
    @Getter
    protected String guestUrlFile = "guesturl.properties";
    protected static String[] guestStopUrl = null;
    protected static String[] ruleOutUrl = null;

    protected String adminUrlFile = "adminurl.properties";
    protected static String[] adminRuleUrl = null;
    protected static String[] adminRuleOutUrl = null;

    @Setter
    protected boolean permission = true;

    @Setter
    protected boolean autoOrganizeId = true;

    @Setter
    protected boolean useGuestUrl = true;

    public static void decodeGuestUrl(String txt) {

        String[] array = StringUtil.split(StringUtil.replace(txt, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
        for (String str : array) {
            if (str == null) {
                continue;
            }
            String substringAfter = StringUtil.substringAfter(str, "!");
            if (str.startsWith("!") && !ArrayUtil.contains(guestStopUrl, substringAfter)) {
                guestStopUrl = ArrayUtil.add(guestStopUrl, substringAfter);
                continue;
            }
            if (!ArrayUtil.contains(ruleOutUrl, str)) {
                ruleOutUrl = ArrayUtil.add(ruleOutUrl, str);
            }
        }
    }

    public static void decodeAdminUrl(String txt) {

        String[] array = StringUtil.split(StringUtil.replace(txt, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
        for (String str : array) {
            if (str == null) {
                continue;
            }
            String substringAfter = StringUtil.substringAfter(str, "!");
            if (str.startsWith("!") && !ArrayUtil.contains(adminRuleOutUrl, substringAfter)) {
                adminRuleOutUrl = ArrayUtil.add(adminRuleOutUrl,substringAfter);
                continue;
            }
            if (!ArrayUtil.contains(ruleOutUrl, str)) {
                adminRuleUrl = ArrayUtil.add(adminRuleUrl, str);
            }
        }
    }

    public static boolean isRuleOutUrl(String url) {
        if (url == null) {
            return true;
        }
        if (ObjectUtil.isEmpty(ruleOutUrl)) {
            return false;
        }
        for (String ruleUrl : ruleOutUrl) {
            if (ruleUrl.equals(url) || StringUtil.getPatternFind(url, ruleUrl)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdminRuleUrl(String url) {
        if (url == null) {
            return true;
        }
        if (ObjectUtil.isEmpty(adminRuleUrl)) {
            return false;
        }
        for (String ruleUrl : adminRuleUrl) {
            if (ruleUrl.equals(url) || StringUtil.getPatternFind(url, ruleUrl)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void destroy() {

    }
}

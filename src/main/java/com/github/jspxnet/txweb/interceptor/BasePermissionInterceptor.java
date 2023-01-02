package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

public abstract class BasePermissionInterceptor extends InterceptorSupport {

    protected final static EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();

    protected final static String GUEST_STOP_URL_TXT = "guest_stop_url_txt";
    protected final static String ADMIN_RULE_URL_TXT = "admin_rule_url_txt";



    protected String guestUrlFile = "guesturl.properties";
    protected static String[] guestStopUrl = null;
    protected static String[] ruleOutUrl = null;

    protected String adminUrlFile = "adminurl.properties";
    protected static String[] adminRuleUrl = null;
    protected static String[] adminRuleOutUrl = null;

    protected boolean permission = true;

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    protected boolean autoOrganizeId = true;

    public void setAutoOrganizeId(boolean autoOrganizeId) {
        this.autoOrganizeId = autoOrganizeId;
    }

    protected boolean useGuestUrl = true;

    public void setUseGuestUrl(boolean useGuestUrl) {
        this.useGuestUrl = useGuestUrl;
    }

    public String getGuestUrlFile() {
        return guestUrlFile;
    }

    public void setGuestUrlFile(String guestUrlFile) {
        this.guestUrlFile = guestUrlFile;
    }

    public static void decodeGuestUrl(String txt)
    {

        String[] array = StringUtil.split(StringUtil.replace(txt, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
        for (String str : array) {
            if (str == null) {
                continue;
            }
            if (str.startsWith("!")) {
                guestStopUrl = ArrayUtil.add(guestStopUrl, StringUtil.substringAfter(str, "!"));
            } else {
                ruleOutUrl = ArrayUtil.add(ruleOutUrl, str);
            }
        }
    }

    public static void decodeAdminUrl(String txt)
    {

        String[] array = StringUtil.split(StringUtil.replace(txt, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
        for (String str : array) {
            if (str == null) {
                continue;
            }
            if (str.startsWith("!")) {
                adminRuleOutUrl = ArrayUtil.add(adminRuleOutUrl, StringUtil.substringAfter(str, "!"));
            } else {
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

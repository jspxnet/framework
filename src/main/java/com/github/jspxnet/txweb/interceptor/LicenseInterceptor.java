package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.mac.NetworkInfo;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ILicense;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuan on 14-5-25.
 * <p>
 * 这里提供一个注册许可功能接口，方便开发人员开发商业软件，发放注册号
 * 默认注册版本
 * versionType:0:free;1:Professional;2:Enterprise  free:免费版;Professional:专业版;Enterprise:企业版
 * versionBind: 0:域名;1:mac  许可号绑定方式
 * license:许可号
 * 具体的现在，需要自己在应该中限制，这里只是提供一个设计框架
 */
public class LicenseInterceptor extends InterceptorSupport {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    //两种方式  GUID
    private String verifyType = "MAC";

    private ILicense license;

    public void setLicense(ILicense license) {
        this.license = license;
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        //这里是不需要验证的action
        Action action = actionInvocation.getActionProxy().getAction();
        String licenseVersion = Environment.versionFree;

        //许可值 0,没有注册,1：专业版:2:企业版
        String mac = StringUtil.empty;
        if ("MAC".equalsIgnoreCase(verifyType))
        {
           mac = NetworkInfo.getMacAddress();
        } else {
           mac = SystemUtil.SYSTEM_GUID;
        }
        action.put(Environment.mac, mac);
        //许可计算 begin
        String licString = StringUtil.trim(config.getString(Environment.license));
        if (!StringUtil.isNull(licString)) {
            String softLicense = StringUtil.empty;
            //0:Professional(专业版);1:Enterprise(企业版)
            int versionType = config.getInt(Environment.versionType);
            //0:域名;1:mac
            int versionBind = config.getInt(Environment.versionBind);
            if (versionBind == 0) {
                HttpServletRequest request = action.getRequest();
                softLicense = StringUtil.trim(license.getLicense(request.getServerName(), versionType));
            } else {
                softLicense = StringUtil.trim(license.getLicense(mac, versionType));
            }
            //许可计算 end
            if (softLicense != null && softLicense.equals(licString)) {
                licenseVersion = license.getLicenseVersion(licString);
            }
        }
        action.put(Environment.versionType, licenseVersion);
        //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }
}
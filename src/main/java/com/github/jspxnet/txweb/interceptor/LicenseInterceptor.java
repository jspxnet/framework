package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.VersionBindEnumType;
import com.github.jspxnet.enums.VersionEnumType;
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
 * versionType: VersionEnumType
 * versionBind: VersionBindEnumType 许可号绑定方式
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
    private ILicense license;

    public void setLicense(ILicense license) {
        this.license = license;
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        //这里是不需要验证的action
        Action action = actionInvocation.getActionProxy().getAction();

        int verifyType =  config.getInt(Environment.versionBind);

        //许可值 0,没有注册,1：专业版:2:企业版
        String verifyValue = StringUtil.empty;
        if (verifyType==VersionBindEnumType.MIXED_KEY.getValue())
        {
            verifyValue = SystemUtil.SYSTEM_GUID;
        } else if (verifyType==VersionBindEnumType.MAC.getValue())
        {
            verifyValue = NetworkInfo.getMacAddress();
        }  else if (verifyType==VersionBindEnumType.DOMAIN.getValue())
        {
            HttpServletRequest request = action.getRequest();
            if (request!=null)
            {
                verifyValue = request.getServerName();
            }
        }
        action.put(Environment.LICENSE_VERIFY_VALUE, verifyValue);
        //许可计算 begin
        //添加多台服务器许可支持
        String licString = StringUtil.trim(config.getString(Environment.license));
        VersionEnumType versionEnumType  = license.getLicenseVersion(licString,verifyValue);
        action.put(Environment.versionType, versionEnumType);
        //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }
}
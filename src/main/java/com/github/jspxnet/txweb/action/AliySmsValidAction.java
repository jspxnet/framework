package com.github.jspxnet.txweb.action;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.ValidateCodeCache;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

@HttpMethod(caption = "阿里云短信")
@Slf4j
public class AliySmsValidAction extends ActionSupport {
    @Ref
    protected MemberDAO memberDAO;

    private String accessKeyId = StringUtil.empty;
    private String secret = StringUtil.empty;

    private String signName = StringUtil.empty;

    // 验证码字符个数
    private int codeCount = 0;
    private String regionId = StringUtil.empty;
    private String templateCode = StringUtil.empty;

    @Ref
    private ValidateCodeCache validateCodeCache;
    /**
     * @param accessKeyId 阿里云 accessKeyId
     */

    @Param(caption = "阿里云accessKeyId",request = false)
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    @Param(caption = "验证码字符个数",min = 3, max = 10, request = false)
    public void setCodeCount(int codeCount) {
        this.codeCount = codeCount;
    }

    /**
     * @param secret 阿里云 secret
     */
    @Param(request = false)
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * @param signName 阿里云 短信模板签名
     */
    @Param(request = false)
    public void setSignName(String signName) {
        this.signName = signName;
    }


    @Param(request = false)
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Param(request = false)
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }


    private void loadConfig() {
        //为了兼容手动配置 begin
        if (StringUtil.isNull(accessKeyId)) {
            accessKeyId = config.getString("aliySmsAccessKeyId");
        }

        if (StringUtil.isNull(secret)) {
            secret = StringUtil.trim(config.getString("aliySmsSecret"));
        }

        if (StringUtil.isNull(signName)) {
            signName = StringUtil.trim(config.getString("aliySmsSignName"));
        }

        if (StringUtil.isNull(regionId)) {
            regionId = StringUtil.trim(config.getString("aliySmsRegionId"));
        }

        if (codeCount <= 2) {
            codeCount = config.getInt("aliySmsCodeCount", 5);
        }
        //为了兼容手动配置 end
    }

    //发送到那个手机号
    private String phoneNumbers = StringUtil.empty;

    @Param(caption = "手机号", min = 5, max = 15)
    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Operate(caption = "登录验证")
    public void sendLoginValid() {
        loadConfig();
        //登录确认验证码, 不动
        if (StringUtil.isNull(templateCode)) {
            templateCode = config.getString("aliySmsLoginValidTemplateCode");
        }
        send();
    }

    @Operate(caption = "注册验证")
    public void sendRegValid() {
        loadConfig();
        //登录确认验证码, 不动
        if (StringUtil.isNull(templateCode)) {
            templateCode = config.getString("aliySmsRegValidTemplateCode");
        }
        send();
    }

    @Operate(caption = "身份验证")
    public void sendIdentityValid() {

        loadConfig();
        //登录确认验证码, 不动
        if (StringUtil.isNull(templateCode)) {
            templateCode = config.getString("aliySmsIdentityTemplateCode");
        }
        send();
    }

    private String makeCode() {
        StringBuilder randomCode = new StringBuilder();
        for (int i = 0; i < this.codeCount; i++) {
            randomCode.append(RandomUtil.getRandomInt(0, 9));
        }
        return randomCode.toString();
    }
    private void send() {
        if (!config.getBoolean(Environment.useSms)) {
            addFieldInfo(Environment.warningInfo, "短信功能没有开启");
            return;
        }
        try {
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
            //这里阿里云sdk有个bug，在web方式会载入不到配置文件

            IAcsClient client = new DefaultAcsClient(profile);

            String code = makeCode();
            validateCodeCache.addSmsCode(phoneNumbers,code);

            CommonRequest request = new CommonRequest();
            //request.setProtocol(ProtocolType.HTTPS);
            request.setSysProduct("Dysmsapi");
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.setSysRegionId(regionId);
            request.putQueryParameter("PhoneNumbers", phoneNumbers);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

            CommonResponse commonResponse = client.getCommonResponse(request);
            JSONObject json = new JSONObject(commonResponse.getData());
            json.put("validateSecond", config.getInt("validateSecond", 80));
            if ("OK".equalsIgnoreCase(json.getString("Code"))) {
                addActionMessage("发送成功");
            } else {
                log.error(json.toString(4));
                addFieldInfo(Environment.warningInfo, "发送失败");
            }
/*
            JSONObject json = new JSONObject();
            json.put("Code",1);
            json.put("BizId",1);
            json.put("RequestId",1);
*/
            String bizId = json.getString("BizId");
            if (bizId==null)
            {
                bizId = DateUtil.toString(new Date(),"HHmmss");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

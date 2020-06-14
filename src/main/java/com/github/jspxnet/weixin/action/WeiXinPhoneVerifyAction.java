package com.github.jspxnet.weixin.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.OAuthOpenIdType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.txweb.table.OAuthOpenId;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.weixin.env.WeiXinEnv;

@HttpMethod(caption = "手机号验证")
public class WeiXinPhoneVerifyAction extends ActionSupport {


    public String phone;//手机号
    public String verifyCode;//验证码

    public String getPhone() {
        return phone;
    }

    @Param(caption = "电话号码", max = 50)
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    @Param(caption = "验证码", max = 50)
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Ref
    private MemberDAO memberDAO;

    @Operate(caption = "核对验证码")
    public void verify() throws Exception {
        setActionResult(ROC);
        int validateSecond = config.getInt("validateSecond", 80);//验证码有效期
        long uid = ObjectUtil.toLong(session.getAttribute("uid"));//用户ID
        String code = (String) session.getAttribute(TXWeb.jspxSmsValidate);//验证码
        long validateTime = ObjectUtil.toLong(session.getAttribute(TXWeb.jspxSmsValidateTime));//发送时间
        if (System.currentTimeMillis() - validateTime < DateUtil.SECOND * validateSecond && verifyCode.equals(code)) {
            //将手机号写入member数据库表
            String openId = (String) session.getAttribute(WeiXinEnv.weiXinOpenId);
            OAuthOpenId oAuthOpenId = memberDAO.getOAuthOpenId(OAuthOpenIdType.weiXin, openId);
            Member phoneMember = memberDAO.getForPhone(phone);
            if (!StringUtil.isNull(openId) && oAuthOpenId != null && oAuthOpenId.getId() > 0 && phoneMember != null && phoneMember.getId() > 0) {
                memberDAO.delete(Member.class, uid);
                oAuthOpenId.setUid(phoneMember.getId());
                if (oAuthOpenId.getId() > 0) {
                    memberDAO.update(oAuthOpenId, new String[]{"uid"});
                }
                addActionMessage("已绑定已存在用户");
            } else {
                Member member = memberDAO.getForId(uid);
                if (member != null) {
                    member.setPhone(phone);
                    if (memberDAO.update(member, new String[]{"phone"}) >= 0) {
                        addActionMessage("绑定成功");
                    }
                }
            }
            addActionMessage("成功");
        } else if (!verifyCode.equals(code)) {
            addFieldInfo(Environment.warningInfo, "验证码错误，请输入核对!");
        } else {
            addFieldInfo(Environment.warningInfo, "验证码过期，请重新获取!");
        }

    }
}

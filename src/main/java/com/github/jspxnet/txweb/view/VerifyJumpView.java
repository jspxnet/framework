package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenyuan on 2016/1/4
 * 解决关注的问题，验证后跳转到对应的页面,禁止分享，转发等功能
 * 本页单独作为一个页面，在跳转到主体也没
 * com.github.jspxnet.txweb.view.VerifyJumpView
 */
@HttpMethod(caption = "微信跳转验证")
public class VerifyJumpView extends ActionSupport {
    public static final String KEY_VERIFY_CODE = "verifyCode";
    @Getter
    private final String verifyCode = RandomUtil.getRandomAlphanumeric(4) + DateUtil.toString("yyMMddHHmmssS");

    public VerifyJumpView() {

    }

    private String link = StringUtil.empty;

    @Param(request = false)
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String execute() throws Exception {
        HttpServletRequest request = getRequest();
        request.getSession().setAttribute(KEY_VERIFY_CODE, verifyCode);
        if (!StringUtil.isNull(link)) {
            getResponse().sendRedirect(link + "?" + KEY_VERIFY_CODE + StringUtil.EQUAL + verifyCode);
        }
        return NONE;
    }
}

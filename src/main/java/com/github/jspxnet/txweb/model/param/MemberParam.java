package com.github.jspxnet.txweb.model.param;


import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chenyuan
 */
@Data
public class MemberParam implements Serializable {

    //昵称，中文名称方式登录
    @Param(caption = "昵称", max = 32, required = true, message = "不能为空")
    private String name = StringUtil.empty;

    //达人称号
    @Param(caption = "绰号", max = 50, required = true, message = "不能为空")
    private String nickname = StringUtil.empty;

    //允许邮箱登录
    @Param(caption = "邮箱", max = 50)
    private String mail = StringUtil.empty;

    //手机方式登录
    @Param(caption = "手机", max = 50)
    private String phone = StringUtil.empty;
    // end

    //内部办公系统时用
    @Param(caption = "工作电话", max = 20)
    private String workPhone = StringUtil.empty;

    //头像
    @Param(caption = "头像URL", max = 500)
    private String faceImage = StringUtil.empty;

    @Param(caption = "性别")
    private String sex = "保密";

    //通过生日计数年龄
    @Param(caption = "生日")
    private Date birthday = DateUtil.empty;

    @Param(caption = "上级ID")
    private long pid = 0;

    @Param(caption = "qq")
    private String qq = StringUtil.empty;

    @JsonIgnore
    @Param(caption = "密码",  max = 20, required = true, message = "密码没有设置")
    private String password = StringUtil.empty;

    //注册时候的来源，一般默认，来源的域名
    @Param(caption = "来源标识", max = 60)
    private String origin = StringUtil.empty;

    @Param(caption = "机构ID", max = 65)
    private String organizeId = StringUtil.empty;

    @Param(caption = "分享id")
    private long linkId = 0;

    @Param(caption = "验证码")
    private String validate = StringUtil.empty;

}

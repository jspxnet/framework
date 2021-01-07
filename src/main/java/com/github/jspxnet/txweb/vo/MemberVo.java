package com.github.jspxnet.txweb.vo;


import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.*;

/**
 * 这里只是提供外部显示账户信息
 */
@Data
@Table(caption = "账号信息", create = false)
public class MemberVo implements Serializable {
    @Column(caption = "ID")
    private long id;

    //昵称，中文名称方式登录
    @Column(caption = "昵称")
    private String name = StringUtil.empty;

    //达人称号
    @Column(caption = "绰号")
    private String nickname = StringUtil.empty;

    //允许邮箱登录
    @Column(caption = "邮箱")
    private String mail = StringUtil.empty;

    //手机方式登录
    @Column(caption = "手机")
    private String phone = StringUtil.empty;
    // end

    //内部办公系统时用
    @Column(caption = "工作电话")
    private String workPhone = StringUtil.empty;

    //手机方式登录
    @Column(caption = "卡号")
    private String kid = StringUtil.empty;
    //支持这的登录方式 end

    //头像
    @Column(caption = "勋章")
    private String medals = StringUtil.empty;

    @Column(caption = "头像URL")
    private String faceImage = StringUtil.empty;

    @Column(caption = "性别")
    private String sex = "保密";

    //通过生日计数年龄
    @Column(caption = "生日")
    @JsonField(format = DateUtil.DAY_FORMAT)
    private Date birthday = DateUtil.empty;

    //积分属性 begin
    @Column(caption = "威望")
    private int prestige = 0;

    //提现活跃度
    @Column(caption = "活跃度")
    private int fascination = 0;

    @Column(caption = "信誉值")
    private int credit = 0;

    @Column(caption = "扩展1")
    private int extcredits1 = 0;
    //积分属性 end

    //用做shop 返点,是虚拟的积分,就用这个作为积分
    @Column(caption = "积分")
    private int points = 0;

    @Column(caption = "国家")
    private String country = "中国大陆";

    @Column(caption = "省份")
    private String province = StringUtil.empty;

    //基本属性begin
    @Column(caption = "签名")
    private String signature = StringUtil.empty;

    @Column(caption = "隐藏信息")
    private int hideInfo = 0;

    @Column(caption = "地址")
    private String address = StringUtil.empty;

    @Column(caption = "登陆次数")
    private int loginTimes = 0;

    @Column(caption = "上级ID")
    private long pid = 0;
    //城市
    @Column(caption = "城市")
    private String city = StringUtil.empty;

    @Column(caption = "来源标识")
    private String origin = StringUtil.empty;

    @Column(caption = "机构ID")
    private String organizeId = StringUtil.empty;

    @Column(caption = "最后登陆时间")
    private Date loginDate = new Date();

    //不保存到数据库  ,这种方式SQL查询太频繁
    @Column(caption = "在线")
    @JsonField(name = "isOnline")
    private boolean online = false;

    //是否已经关注,不保存到数据库,动态计算
    @Column(caption = "关注")
    @JsonField(name = "isFollow")
    private boolean follow = false;

    //是否已经是联系人,不保存到数据库,动态计算
    @Column(caption = "联系人")
    @JsonField(name = "isContacts")
    private boolean contacts = false;

    //冻结后不允许登录
    @Column(caption = "冻结",enumType = CongealEnumType.class)
    private int congealType = CongealEnumType.NO_CONGEAL.getValue();

    @Column(caption = "被冻结时间", notNull = true)
    private Date congealDate = new Date();

    @Column(caption = "自动冻结日期", notNull = true)
    private Date autoCongealDate = DateUtil.addYear(50);

    //小于当前日期为未禁言
    @Column(caption = "禁言", enumType = YesNoEnumType.class)
    private int confineType = YesNoEnumType.NO.getValue();

    @Column(caption = "禁言结束日期", notNull = true)
    private Date confineDate = DateUtil.addYear(-1);
    //基本属性end

    //第三方登陆标识
    @Column(caption = "openId")
    private String openId;

    @Column(caption = "unionid")
    private String unionid;

    @Column(caption = "部门列表")
    private List<MemberDeptVo> deptList = new ArrayList(0);

    @JsonIgnore
    private List<Role> roleList = new ArrayList<>();

    public MemberDeptVo getDepartment()
    {
        for (MemberDeptVo memberDeptVo:deptList)
        {
            if (YesNoEnumType.YES.getValue()==memberDeptVo.getDefaultType())
            {
                return memberDeptVo;
            }
        }
        MemberDeptVo memberDeptVo = new MemberDeptVo();
        memberDeptVo.setDepartmentId(null);
        return  memberDeptVo;
    }

    @JsonField(name = "roleCaption", caption = "角色列表")
    public String getRoleCaption()
    {
        StringBuilder sb = new StringBuilder();
    //    sb.append(":").append("无").append(StringUtil.SEMICOLON);
        if (roleList != null) {
            for (Role role : roleList)
            {
                if (role != null) {
                    sb.append(role.getNamespace()).append(":").append(role.getName()).append(StringUtil.SEMICOLON);
                }
            }
        }
        if (sb.toString().endsWith(StringUtil.SEMICOLON)) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}

/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.txweb.IMember;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-27
 * Time: 14:50:30
 * 只是基本的用户信息,这些信息为内存中需要经常调用使用的信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member", caption = "默认用户信息")
public class Member extends OperateTable implements IMember {
    //支持这的登录方式 begin
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    //昵称，中文名称方式登录
    @Column(caption = "昵称", length = 50, dataType = "isLengthBetween(2,32)", notNull = true)
    private String name = StringUtil.empty;

    //达人称号
    @Column(caption = "绰号", length = 50, dataType = "isLengthBetween(2,26)")
    private String nickname = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "拼音", length = 100, hidden = true, notNull = true)
    private String spelling = StringUtil.empty;

    @Column(caption = "邮箱已验证")
    private int mailValidated = YesNoEnumType.NO.getValue();

    //允许邮箱登录
    @Column(caption = "邮箱", length = 50, dataType = "isEmail")
    private String mail = StringUtil.empty;


    @Column(caption = "手机已验证")
    private int phoneValidated = YesNoEnumType.NO.getValue();

    //手机方式登录
    @Column(caption = "手机", dataType = "isMobile", length = 50)
    private String phone = StringUtil.empty;
    // end

    //内部办公系统时用
    @Column(caption = "工作电话", length = 20, dataType = "isLengthBetween(0,20)")
    private String workPhone = StringUtil.empty;

    //手机方式登录
    @Column(caption = "卡号", length = 50)
    private String kid = StringUtil.empty;
    //支持这的登录方式 end

    /**
     * 密码保存格式  [md5]904328109384
     * [Sha]904328109384
     */
    @JsonIgnore
    @Column(caption = "密码", length = 200, hidden = true, notNull = true)
    private String password = StringUtil.empty;

    //头像
    @Column(caption = "头像URL", length = 500)
    private String faceImage = StringUtil.empty;

    @Column(caption = "性别", option = "男;女;保密", length = 4, notNull = true)
    private String sex = "保密";

    //通过生日计数年龄
    @Column(caption = "生日")
    private Date birthday = DateUtil.empty;

    //积分属性 begin
    @Column(caption = "威望", notNull = true)
    private int prestige = 0;

    //提现活跃度
    @Column(caption = "活跃度", notNull = true)
    private int fascination = 0;

    @Column(caption = "信誉值", notNull = true)
    private int credit = 0;
    //积分属性 end

    @Column(caption = "扩展变量",length = 1000)
    private String valueMap = StringUtil.empty;

    //奖励
    //数据保存格式  xx奖励=xxx.jpg 一行一个
    @Column(caption = "勋章", length = 250)
    private String medals = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "扩展1")
    private int extcredits1 = 0;

    @JsonIgnore
    @Column(caption = "扩展2")
    private int extcredits2 = 0;

    //当发生动作的时候，更具配置增加
    @JsonIgnore
    @Column(caption = "经验值")
    private int activity = 0;

    //真正的钱,所有应用中不允许直接修改,否则数据会失效
    //电子商务 平台级别,如果是店铺级别的另外建表,是用支付中心来处理
    @Column(caption = "积分", notNull = true)
    private int points = 0;

    @JsonIgnore
    @Column(caption = "人民币数量")
    private double storeMoney = 0;

    @Column(caption = "折扣组", length = 50)
    private String discountGroup = StringUtil.empty;
    //电子商务end

    //基本属性begin
    @Column(caption = "隐藏信息", option = "0:否1:是;", notNull = true)
    private int hideInfo = 0;

    @Column(caption = "上级ID")
    private long pid = 0;

    @Column(caption = "登陆次数")
    private int loginTimes = 0;

    @Column(caption = "最后登陆时间")
    private Date loginDate = new Date();

    //冻结后不允许登录
    @Column(caption = "冻结", option = "0:激活;1:冻结")
    private int congealType = CongealEnumType.NO_CONGEAL.getValue();

    @Column(caption = "被冻结时间", notNull = true)
    private Date congealDate = new Date();

    @Column(caption = "自动冻结日期", notNull = true)
    private Date autoCongealDate = DateUtil.addYear(50);

    //小于当前日期为未禁言
    @Column(caption = "禁言", option = "0:否;1:是")
    private int confineType = YesNoEnumType.NO.getValue();

    @Column(caption = "禁言结束日期", notNull = true)
    private Date confineDate = DateUtil.addYear(-1);
    //基本属性end

    @Column(caption = "用户类型", option = "0:个人;1:企业;2:管理人员")
    private int userType = 0;

    @Column(caption = "皮肤", length = 50)
    private String skin = "default";

    @Column(caption = "qq", length = 20)
    private String qq = StringUtil.empty;

    //国家地区
    @Column(caption = "国家", length = 100, defaultValue = "中国")
    private String country = "中国大陆";

    @Column(caption = "省份", length = 50)
    private String province = StringUtil.empty;

    //城市
    @Column(caption = "城市", length = 50)
    private String city = StringUtil.empty;

    @Column(caption = "区", length = 50)
    private String area = StringUtil.empty;

    @Column(caption = "地址", length = 250)
    private String address = StringUtil.empty;

    @Column(caption = "签名", length = 250)
    private String signature = StringUtil.empty;

    @Column(caption = "备注", length = 250)
    private String remark = StringUtil.empty;

    //作为FTP的基本用户目录，
    @JsonIgnore
    @Column(caption = "个人目录", length = 250)
    private String uploadFolder = StringUtil.empty;

    /*
     *添加到联系方式
     */
    @Column(caption = "联系人判断", option = "0:所有人;1:禁止所有;2:我关注的会员;3:设置问题", notNull = true)
    private int contactsType = 0;

    @JsonIgnore
    @Column(caption = "问题", length = 200)
    private String contactsQuestion = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "答案", length = 200)
    private String contactsAnswer = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "扩展", length = 1000)
    private String other = StringUtil.empty;

    //电子钥匙登录方式 begin
    @Column(caption = "电子钥匙登录", notNull = true)
    private int useUsb = 0;

    @JsonIgnore
    @Column(caption = "电子钥匙key", length = 250)
    private String usbKey = StringUtil.empty;
    //电子钥匙登录方式 end

    //用于支付验证等,保存的时候要加密
    @JsonIgnore
    @Column(caption = "认证密码", length = 200)
    private String payPassword = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "使用快速支付积分")
    private int useFastPayPoints = 0;

    @JsonIgnore
    @Column(caption = "快速支付积分限额")
    private int fastPayPoints = 100;

    @JsonIgnore
    @Column(caption = "使用快速支付金额")
    private int useFastPayAmount = 0;

    @JsonIgnore
    @Column(caption = "快速支付金额限额")
    private int fastPayAmount = 100;

    //防止手动修改金额，等重要信息
    @JsonIgnore
    @Column(caption = "校验码", length = 32)
    private String token = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "版本")
    private int version = 0;

    @Column(caption = "来源标识", length = 60)
    private String origin = StringUtil.empty;

    @Column(caption = "机构ID", length = 65)
    private String organizeId = StringUtil.empty;

    @Column(caption = "来源分享id")
    private long linkId = 0;

    @Nexus(mapping = MappingType.OneToMany, field = "id", targetField = "uid", targetEntity = MemberRole.class, chain = true, delete = false)
    private List<MemberRole> memberRoles = new ArrayList<>();


    public Map<String, String> getMedalMap()
    {
        Map<String, String> result = new HashMap<>();
        String[] medalArray = StringUtil.split(StringUtil.convertCR(medals), StringUtil.CR);
        for (String value : medalArray) {
            if (StringUtil.isNull(value) || !value.contains(StringUtil.EQUAL)) {
                continue;
            }
            result.put(StringUtil.substringBefore(value, StringUtil.EQUAL), StringUtil.substringAfter(value, StringUtil.EQUAL));
        }
        return result;
    }

    /**
     * 判断是否满足快速支付
     * @param amount 金额
     * @param points 积分
     * @return   判断是否满足快速支付
     */
    @Override
    public boolean isFastPay(double amount,int points)
    {
        if (YesNoEnumType.YES.getValue()==useFastPayAmount&&points==0&&amount<fastPayAmount&&amount>0)
        {
            return true;
        }
        if (YesNoEnumType.YES.getValue()==useFastPayPoints&&amount==0&&points<fastPayPoints&&points>0)
        {
            return true;
        }
        return YesNoEnumType.YES.getValue() == useFastPayAmount && YesNoEnumType.YES.getValue() == useFastPayPoints
                && amount < fastPayAmount && points < fastPayPoints;
    }


    @Override
    public Map<String, IRole> getRoles() {
        Map<String, IRole> result = new HashMap<String, IRole>(memberRoles.size());
        for (MemberRole memberRole : memberRoles) {
            Role role = memberRole.getRole();
            if (role == null) {
                continue;
            }
            result.put(role.getNamespace(), role);
        }
        return result;
    }

    @Override
    public Role getRole(String softName) {
        if (softName == null) {
            return new Role();
        }
        for (MemberRole memberRole : memberRoles) {
            if (softName.equalsIgnoreCase(memberRole.getNamespace())) {
                return memberRole.getRole();
            }
        }
        return new Role();
    }

    @Override
    public int getMaxUserType() {
        if (memberRoles.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (MemberRole memberRole : memberRoles) {
            Role role = memberRole.getRole();
            if (role == null) {
                continue;
            }
            if (result < role.getUserType()) {
                result = role.getUserType();
            }
        }
        return result;
    }


    @Override
    public void setMemberRoles(List<MemberRole> memberRoles) {
        this.memberRoles.clear();
        this.memberRoles.addAll(memberRoles);
    }

    @Override
    public void setRole(Role role) {
        for (MemberRole memberRole : memberRoles) {
            if (memberRole.getNamespace().equalsIgnoreCase(role.getNamespace()) && memberRole.getOrganizeId().equals(role.getOrganizeId())) {
                memberRole.setRole(role);
                memberRole.setUid(id);
                memberRole.setRoleId(role.getId());
                return;
            }
        }
        MemberRole newMemberRole = new MemberRole();
        newMemberRole.setNamespace(role.getNamespace());
        newMemberRole.setUid(id);
        newMemberRole.setRoleId(role.getId());
        newMemberRole.setRole(role);
        memberRoles.add(newMemberRole);
    }

    @JsonField(name = "old", caption = "年龄")
    public int getOld() {
        return DateUtil.getYear() - DateUtil.getYear(birthday);
    }

    public void setProperty(String key, String value)
    {
        StringMap<String,String> hashMap = new StringMap<>();
        hashMap.setKeySplit(StringUtil.EQUAL);
        hashMap.setLineSplit(StringUtil.CRLF);
        hashMap.setString(other);
        hashMap.put(key, value);
        this.other = hashMap.toString();
    }

    @Override
    public String getProperty(String key)
    {
        StringMap<String, String> hashMap = new StringMap();
        hashMap.setKeySplit(StringUtil.EQUAL);
        hashMap.setLineSplit(StringUtil.CRLF);
        hashMap.setString(other);
        return hashMap.get(key);
    }

    public String getPayPassword() {
        //如果没有设置，那么就使用登录密码
        if (StringUtil.isNull(payPassword)) {
            return password;
        }
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    /**
     * @return 提供外部简单的信息显示
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("nickname", nickname);
        json.put("mail", mail);
        json.put("phone", phone);
        json.put("kid", kid);
        json.put("faceImage", faceImage);
        json.put("sex", sex);
        json.put("birthday", birthday);
        json.put("points", points);
        return json;
    }
    public int registrationDays()
    {
        return DateUtil.getCountMonthDay(createDate);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject(this);
        if (memberRoles.isEmpty()) {
            json.remove("memberRoles");
        }
        return json.toString(4);
    }
}


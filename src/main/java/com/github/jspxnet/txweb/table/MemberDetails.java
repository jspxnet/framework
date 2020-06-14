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

import com.github.jspxnet.enums.YesNoEnumType;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Created by yuan on 14-3-11.
 * 用户详细信息,作为求职，档案记录等软件中使用
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_member_details", caption = "默认用户信息")
public class MemberDetails extends OperateTable {
    @Id
    @Column(caption = "用户ID", notNull = true)
    private long id = 0;

    @Column(caption = "姓名", dataType = "isLengthBetween(2,100)", length = 100)
    private String name = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "拼音", length = 100, hidden = true, notNull = true)
    private String spelling = StringUtil.empty;

    @Column(caption = "性别", option = "男;女;保密", length = 4, notNull = true)
    private String sex = "保密";

    @Column(caption = "民族", dataType = "isLengthBetween(0,20)", length = 20)
    private String nation = "汉族";

    @Column(caption = "毕业时间")
    private Date graduationDate = DateUtil.empty;

    @Column(caption = "专业", dataType = "isLengthBetween(0,50)", length = 50)
    private String professional = StringUtil.empty;

    @Column(caption = "学校", dataType = "isLengthBetween(0,80)", length = 80)
    private String school = StringUtil.empty;

    @Column(caption = "学历", dataType = "isLengthBetween(2,50)", length = 50, notNull = false)
    private String education = StringUtil.empty;

    @Column(caption = "婚姻状况", dataType = "isLengthBetween(2,50)", length = 50, notNull = false)
    private String maritalStatus = StringUtil.empty;

    @Column(caption = "政治面貌", dataType = "isLengthBetween(2,100)", length = 100, notNull = false)
    private String political = StringUtil.empty;

    @Column(caption = "兴趣爱好", dataType = "isLengthBetween(2,250)", length = 250, notNull = false)
    private String interest = StringUtil.empty;

    @Column(caption = "特长", dataType = "isLengthBetween(2,250)", length = 250, notNull = false)
    private String forte = StringUtil.empty;

    //希望
    @Column(caption = "薪资水平")
    private int salary = 0;

    @Column(caption = "入职日期")
    private Date positionDate = new Date();

    @Column(caption = "相片", dataType = "isLengthBetween(2,200)", length = 200)
    private String images = StringUtil.empty;

    @Column(caption = "个人简历", length = 2000, dataType = "isLengthBetween(0,2000)")
    private String content = StringUtil.empty;

    @Column(caption = "证件类型", dataType = "isLengthBetween(0,20)", length = 20, notNull = true)
    private String cardType = "居民身份证";

    @Column(caption = "证件号", dataType = "isLengthBetween(2,30)", length = 30, notNull = false)
    private String cardNumber = StringUtil.empty;

    @Column(caption = "证件图片", dataType = "isLengthBetween(0,200)", length = 200, notNull = true)
    private String cardImage = StringUtil.empty;

    @Column(caption = "证件已验证")
    private int cardValidated = YesNoEnumType.NO.getValue();

    @Column(caption = "家庭住址", dataType = "isLengthBetween(1,250)", length = 200, notNull = false)
    private String address = StringUtil.empty;

    @Column(caption = "户籍类别", length = 200, dataType = "isLengthBetween(0,200)", notNull = false)
    private String householdType = StringUtil.empty;

    @Column(caption = "付口所在地", length = 200, dataType = "isLengthBetween(0,200)", notNull = false)
    private String placeHousehold = StringUtil.empty;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "附件", length = 1200, dataType = "isLengthBetween(0,1200)", notNull = false)
    private String attachments = StringUtil.empty;

    @Column(caption = "备注", length = 250, notNull = true)
    private String remark = StringUtil.empty;

}
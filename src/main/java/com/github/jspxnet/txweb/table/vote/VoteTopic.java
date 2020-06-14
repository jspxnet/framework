/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table.vote;


import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.enums.MappingType;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-2-28
 * Time: 17:02:40
 */

@EqualsAndHashCode(callSuper = true)
@Table(name = "jspx_vote_topic", caption = "投票主题")
@Data
public class VoteTopic extends OperateTable {
    public VoteTopic() {

    }

    @Id(auto = true, length = 24, type = IDType.uuid)
    @Column(caption = "ID", length = 32, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "组ID", length = 50, notNull = false)
    private String groupId = StringUtil.empty;

    @Column(caption = "标题", length = 250, notNull = true)
    private String topicText = StringUtil.empty;

    @Column(caption = "选项个数", option = "0:单选;1:多选", notNull = true)
    private int voteType = 0;

    @Column(caption = "显示方式", notNull = true)
    private int viewType = 0;

    //默认为空表示任意,更具后台设置判断多个使用 ;分割
    @Column(caption = "允许投票的角色", length = 250, notNull = true, defaultValue = "")
    private String roleIds = StringUtil.empty;

    @Column(caption = "表示图", option = "0:条型;1:柱状;2:饼型;4:线型", notNull = true)
    private int shape = 0;

    @Column(caption = "重复投票", option = "0:否;1:是", notNull = true)
    private int repeat = 0;

    //多选多时候一次选择几个
    @Column(caption = "一次投票个", length = 4, notNull = true)
    private int optionsNumber = 1;

    @Column(caption = "投票开始时间", notNull = true)
    private Date startDate = new Date();

    @Column(caption = "投票结束时间", notNull = true)
    private Date endDate = DateUtil.addMonth(1, new Date());

    @Column(caption = "投票选项排序", length = 200, notNull = true, defaultValue = "sortType:A;sortDate:D")
    private String sort = "sortType:A;sortDate:D";

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @Column(caption = "投票才能看结果", option = "0:否;1:是", length = 2, notNull = true)
    private int pollLook = 0;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "最后操作时间", notNull = true)
    private Date lastDate = new Date();

    @Nexus(mapping = MappingType.OneToMany, field = "id", targetField = "topicId", orderBy = "${sort}", length = "1000", targetEntity = VoteItem.class, save = true, delete = true, update = true)
    private List<VoteItem> voteItemList = new LinkedList<VoteItem>();

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    public boolean isInRoleIds(String roleId) {
        if (StringUtil.isNull(roleIds) || (StringUtil.ASTERISK.equals(roleIds))) {
            return true;
        }
        if (roleIds.contains("[") && roleIds.contains("]")) {
            return roleIds.contains("[" + roleId + StringUtil.COLON);
        }
        return StringUtil.isNull(roleIds) || ArrayUtil.inArray(StringUtil.split(roleIds, StringUtil.SEMICOLON), roleId, true);
    }

    public boolean isVoteItemId(String id) {
        for (VoteItem voteItem : voteItemList) {
            if (voteItem.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public int getSumPoint() {
        int result = 0;
        for (VoteItem vv : getVoteItemList()) {
            result = result + vv.getVotePoint();
        }
        return result;
    }

    public String getSort() {
        return sort;
    }
}
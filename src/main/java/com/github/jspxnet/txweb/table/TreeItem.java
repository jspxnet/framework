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

import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-10
 * Time: 9:12:29
 * 数结构
 * com.github.jspxnet.txweb.table.TreeItem
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_tree_item", caption = "栏目树")
public class TreeItem extends OperateTable {
    public TreeItem() {

    }

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "节点ID", length = 50, notNull = true)
    private String nodeId = StringUtil.empty;

    @Column(caption = "节点值", length = 200, notNull = true)
    private String itemValue = StringUtil.empty;

    @Column(caption = "父ID", length = 100, notNull = true)
    private String parentNodeId = StringUtil.empty;

    @Column(caption = "名称", length = 100, notNull = true)
    private String caption = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "拼音", length = 100, hidden = true, notNull = true)
    private String spelling = StringUtil.empty;

    @Column(caption = "节点动作", length = 100, notNull = true)
    private String nodeAction = StringUtil.empty;

    @Column(caption = "图标", length = 100, notNull = true)
    private String icon = StringUtil.empty;

    @Column(caption = "打开的图标", length = 100, notNull = true)
    private String openIcon = StringUtil.empty;


    @Column(caption = "隐藏", option = "0:不隐藏;1:隐藏", notNull = true)
    private int hide = 0;

    @Column(caption = "录入框", length = 50)
    private String inputType = StringUtil.empty;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "页面排序", notNull = true)
    private int showSortType = 0;

    @Column(caption = "打开的方式", length = 40, notNull = true)
    private String target = "_self";

    //发布的格式 CMS  option = "0:html;1:markdown;2:link;3:img;4:layout;5:quote;6:电子报;10:公文;12:邮票"
    @Column(caption = "格式", notNull = true)
    private int formatType = 0;

    @Column(caption = "描述", length = 250, notNull = true)
    private String description = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "密码", length = 200, notNull = true)
    private String password = StringUtil.empty;

    //保存 {76:管理员};{75:会员} (多个用;分开) xin新颁布改为{ } 表示角色，[] 表示用户
    @Column(caption = "查看角色", length = 240, notNull = true)
    private String roleIds = StringUtil.empty;

    @Column(caption = "允许发布", length = 2, option = "0:否;1:是", notNull = true)
    private int allowPost = 1;

    @Column(caption = "是否允许回复", length = 2, option = "0:否;1:是", notNull = true, defaultValue = "1")
    private int allowReply = 1;

    @Column(caption = "是否允许下载附件", length = 2, option = "0:否;1:是", notNull = true, defaultValue = "1")
    private int allowDownAttach = 1;

    @Column(caption = "是否允许上传附件", length = 2, option = "0:否;1:是", notNull = true)
    private int allowPostAttach = 1;

    @Column(caption = "回复数量", notNull = true)
    private int replies = 0;

    @Column(caption = "总发布数", notNull = true)
    private int posts = 1;

    @Column(caption = "精华数量", notNull = true)
    private int distillates = 1;

    //1发方法方法方法1190620768admin
    //论坛等常用begin
    @JsonIgnore
    @Column(caption = "最后发布Id", length = 32)
    private String lastOperateId = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "最后发布标题", length = 200)
    private String lastSubject = StringUtil.empty;
    @JsonIgnore
    @Column(caption = "最后发布用户", length = 50)
    private String lastOperateUser = StringUtil.empty;
    @JsonIgnore
    @Column(caption = "最后发布用户")
    private long lastOperateUid = 0;
    //论坛等常用end
    @JsonIgnore
    @Column(caption = "样式", length = 50)
    private String styleName = StringUtil.empty;

    @Column(caption = "节点类型", option = "0:不显示;1:首页显示;2:专题", notNull = true)
    private int nodeType = 1;

    @Column(caption = "图片URL", length = 200, notNull = true)
    private String showImage = StringUtil.empty;

    @Column(caption = "正文连接地址", length = 200, notNull = true)
    private String linkPage = StringUtil.empty;

    @Column(caption = "显示模版", length = 200, notNull = true)
    private String templatePage = StringUtil.empty;
    @JsonIgnore
    @Column(caption = "栏目连接地址", length = 200, notNull = true)
    private String nodeLinkPage = StringUtil.empty;
    @JsonIgnore
    @Column(caption = "列表模版", length = 200, notNull = true)
    private String templateListPage = StringUtil.empty;
    @JsonIgnore
    //CMS里边判断是否静态化，各个软件作用不同
    @Column(caption = "模版类型", notNull = true)
    private int templateType = 0;

    //做点菜时候的物品单位
    @Column(caption = "单位", length = 250, notNull = true)
    private String units = StringUtil.empty;

    @Column(caption = "栏目广告", length = 250, notNull = true)
    private String drumbeating = StringUtil.empty;

    //格式[id:name]多个使用;分开
    @Column(caption = "管理员", length = 250, notNull = false)
    private String manager = StringUtil.empty;

    @Column(caption = "文书", length = 250, notNull = false)
    private String paperwork = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    //论坛做最后发布时间
    @JsonIgnore
    @Column(caption = "操作时间", notNull = true)
    private Date lastDate = new Date();

    @JsonIgnore
    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    /**
     * 现在已经改为 支持角色，和用户两种形式 [1:xxx][2:用户];{2:角色};
     * 和浏览权限不同,这里如果不设置，表示不能管理
     *
     * @param roleId 角色
     * @param uid    用户
     * @return 是否位本角色
     */
    public boolean isInManager(String roleId, long uid) {
        if (StringUtil.isNull(manager)) {
            return false;
        }
        if ((manager.contains("[") && manager.contains("]")) || (manager.contains("{") && manager.contains("}"))) {
            return roleIds.contains("[" + uid + StringUtil.COLON) || manager.contains("{" + roleId + StringUtil.COLON);
        }
        return StringUtil.isNull(manager);
    }

    public boolean isInRoleIds(String roleId, long uid) {
        if (StringUtil.isNull(roleIds) || (StringUtil.ASTERISK.equals(roleIds))) {
            return true;
        }
        if ((roleIds.contains("[") && roleIds.contains("]")) || (roleIds.contains("{") && roleIds.contains("}"))) {
            return roleIds.contains("[" + uid + StringUtil.COLON) || roleIds.contains("{" + roleId + StringUtil.COLON);
        }
        return StringUtil.isNull(roleIds);
    }


    //合并
    public void joinRoleIds(String roleIds) {
        String[] lines = StringUtil.split(roleIds, StringUtil.SEMICOLON);
        for (String line : lines) {
            if (!this.roleIds.contains(line)) {
                if (StringUtil.isNull(this.roleIds)) {
                    this.roleIds = line;
                } else {
                    this.roleIds = this.roleIds + StringUtil.SEMICOLON + line;
                }
            }
        }
    }

    @JsonField(name = "rolesCaption")
    public String getRolesCaption() {
        StringBuilder sb = new StringBuilder();
        String[] lines = StringUtil.split(roleIds, StringUtil.SEMICOLON);
        for (String line : lines) {
            sb.append(StringUtil.substringBetween(line, ":", "}")).append(StringUtil.SEMICOLON);
        }
        if (sb.toString().endsWith(StringUtil.SEMICOLON)) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public boolean isInPaperwork(int userId) {
        if (StringUtil.isNull(paperwork)) {
            return false;
        }
        if (paperwork.contains("[") && paperwork.contains("]")) {
            return paperwork.contains("[" + userId + ":");
        }
        return StringUtil.isNull(paperwork) || ArrayUtil.inArray(StringUtil.split(paperwork, StringUtil.SEMICOLON), userId + "", true);
    }

    public boolean lookThrough(String roleId) {
        return ArrayUtil.inArray(StringUtil.split(roleId, StringUtil.SEMICOLON), roleId, false);
    }

    public String getXmlString(boolean selected) {
        StringBuilder result = new StringBuilder();
        result.append("<node id=\"").append(nodeId).append("\" text=\"").append(StringUtil.replace(caption, "'", "\\'")).append("\" ");
        result.append("openIcon=\"").append(openIcon).append("\" ");
        result.append("hide=\"").append(hide).append("\" ");
        result.append("inputType=\"").append(inputType).append("\" ");
        result.append("pid=\"").append(parentNodeId).append("\" ");
        result.append("value=\"").append(itemValue).append("\" ");
        result.append("state=\"").append(selected).append("\" ");
        result.append("sort=\"").append(sortType).append("\" />");
        return result.toString();
    }

}
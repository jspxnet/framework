/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.MemberTreeDAO;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.dao.TreeItemDAO;
import com.github.jspxnet.txweb.dao.impl.TreeItemDAOImpl;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.MemberTree;
import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.HtmlUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-21
 * Time: 14:52:21
 */
@Slf4j
@HttpMethod(caption = "树结构")
public class TreeView extends ActionSupport {
    private String inputName = "nodeId";
    private String input = null; //输入框 text  jspx tree 不使用   radio
    private boolean useLimb = false; //是否使用叶子部分
    private boolean assigner = false; //默认方式 true:按照分配的权限读取
    protected long uid = -1;//会员ID 不为空,显示用户树

    public TreeView() {

    }

    protected TreeItemDAO treeItemDAO;
    @Param(request = false)
    public void setTreeItemDAO(TreeItemDAO treeItemDAO) {
        this.treeItemDAO = treeItemDAO;
    }

    protected MemberTreeDAO memberTreeDAO;

    @Param(request = false)
    public void setMemberTreeDAO(MemberTreeDAO memberTreeDAO) {
        this.memberTreeDAO = memberTreeDAO;
    }

    @Param(caption = "机构ID", request = false)
    public void setOrganizeId(String organizeId) {
        treeItemDAO.setOrganizeId(organizeId);
        memberTreeDAO.setOrganizeId(organizeId);
    }

    public String getNamespace() {
        return treeItemDAO.getNamespace();
    }

    public boolean isAssigner() {
        return assigner;
    }

    @Param(caption = "分配的权限读取")
    public void setAssigner(boolean assigner) {
        this.assigner = assigner;
    }

    public String getInput() {
        return input;
    }

    @Param(caption = "输入框(text,radio)")
    public void setInput(String input) {
        this.input = input;
    }

    public boolean isUseLimb() {
        return useLimb;
    }

    @Param(caption = "使用中间节点")
    public void setUseLimb(boolean useLimb) {
        this.useLimb = useLimb;
    }

    public String getInputName() {
        return inputName;
    }

    @Param(caption = "输入框名称")
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    @Param(caption = "显示隐藏节点")
    public boolean isShow() {
        return treeItemDAO.isShow();
    }

    public void setShow(boolean show) {
        treeItemDAO.setShow(show);
    }

    protected String nodeId;

    @Param(caption = "节点ID", max = 50, level = SafetyEnumType.HEIGHT)
    public void setNodeId(String nodeId) {

        this.nodeId = nodeId;
    }

    public long getUid() {
        return uid;
    }


    public void setUid(long uid) {
        this.uid = uid;
    }

    /**
     * 不会识别多个方法 ,同名请求参数重复设置不可控
     *
     * @param nodeIds 节点id
     */
    @Param(caption = "节点ID", max = 120, level = SafetyEnumType.HEIGHT)
    public void setNodeIds(String[] nodeIds) {
        this.nodeId = ArrayUtil.toString(nodeIds, StringUtil.SEMICOLON);
    }

    /**
     * @return 得到选项
     */
    @Operate(caption = "得到选项列表")
    public String getOptionList() {
        if (assigner) {
            IUserSession userSession = getUserSession();

            IRole role = userSession.getRole(treeItemDAO.getNamespace(),treeItemDAO.getOrganizeId());
            if (!isGuest() && role!=null&&role.getUserType() < UserEnumType.ChenYuan.getValue()) {
                treeItemDAO.setChecked(memberTreeDAO.getMemberTreeSplitString(userSession.getUid()));
            } else {
                treeItemDAO.setChecked(ArrayUtil.emptyString);
            }
        } else {
            treeItemDAO.setChecked(ArrayUtil.emptyString);
        }
        Map<String, String> map = treeItemDAO.getSelectTreeItemMap("....|-");
        StringBuilder sb = new StringBuilder();
        for (String k : map.keySet()) {
            sb.append(k).append(":").append(HtmlUtil.escapeEncoderHTML(map.get(k))).append(StringUtil.SEMICOLON);
        }
        map.clear();
        return sb.toString();
    }

    /**
     * @return 树XML
     */
    @Operate(caption = "树XML")
    public String getTreeItemXml() {
        if (StringUtil.isNull(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        return treeItemDAO.toXMLString();
    }

    /**
     * 得到平铺方式的json数据
     *
     * @return 树代码
     */
    @Operate(caption = "树json")
    public String getTreeSrc() {
        if (uid > 0) {
            nodeId = memberTreeDAO.getMemberTreeSplitString(uid);
        } else if (!StringUtil.hasLength(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        treeItemDAO.setChecked(ArrayUtil.emptyString);
        return treeItemDAO.getTreeSrc(TreeItemDAOImpl.TYPE_JSON);
    }

    /**
     * @return 得到递进方式的json数据
     */
    @Operate(caption = "递进方式的json1")
    public String getJsonTreeSrc() {
        if (uid > 0) {
            nodeId = memberTreeDAO.getMemberTreeSplitString(uid);
        } else if (!StringUtil.hasLength(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        return treeItemDAO.getTreeSrc(TreeItemDAOImpl.Type_jsonTree);
    }

    /**
     * @return 得到递进方式的json数据
     */
    @Operate(caption = "递进方式的角色json")
    public String getRoleTreeSrc() {
        if (uid > 0) {
            nodeId = memberTreeDAO.getMemberTreeSplitString(uid);
        } else if (!StringUtil.hasLength(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        return treeItemDAO.getTreeSrc(TreeItemDAOImpl.TYPE_ROLE_JSON);
    }

    /**
     * @return 得到递进方式的json数据
     */
    @Operate(caption = "递进方式的角色json")
    public String getRoleJsonTreeSrc() {
        if (uid > 0) {
            nodeId = memberTreeDAO.getMemberTreeSplitString(uid);
        } else if (!StringUtil.hasLength(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        return treeItemDAO.getTreeSrc(TreeItemDAOImpl.Type_roleJsonTree);
    }

    /**
     * 得到包含角色信息的树结构（数据为平铺方式）,资源占用比较大
     *
     * @return 树代码
     * @throws Exception 异常
     */
    public String getRoleTree() throws Exception {
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        PermissionDAO permissionDAO = beanFactory.getBean(PermissionDAO.class, treeItemDAO.getNamespace());
        List<TreeItem> treeItemList = treeItemDAO.createRepairTreeRole(permissionDAO.getRoleList());
        return TreeItemDAOImpl.getJson(treeItemList, "role", null, null, treeItemDAO.getNamespace());
    }

    /**
     * 得到包含角色信息的树结构（数据为递进方式）,资源占用比较大
     *
     * @return 树代码
     * @throws Exception 异常
     */
    public String getRoleJsonTree() throws Exception {
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        PermissionDAO permissionDAO = beanFactory.getBean(PermissionDAO.class, treeItemDAO.getNamespace());
        List<TreeItem> treeItemList = treeItemDAO.createRepairTreeRole(permissionDAO.getRoleList());
        return TreeItemDAOImpl.getJsonTree(treeItemList, "role", null, null, treeItemDAO.getNamespace()).toString(4);
    }

    /**
     * @return 用户树代码, 提供后台控制使用
     */
    public String getUserTreeSrc() {
        IUserSession userSession = getUserSession();
        IRole role = userSession.getRole(treeItemDAO.getNamespace(),treeItemDAO.getOrganizeId());
        if (!isGuest() && role!=null&&role.getUserType() < UserEnumType.ChenYuan.getValue()) {
            treeItemDAO.setChecked(memberTreeDAO.getMemberTreeSplitString(userSession.getUid()));
        } else {
            treeItemDAO.setChecked(ArrayUtil.emptyString);
        }
        if (!StringUtil.hasLength(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }

        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        return treeItemDAO.getTreeSrc(TreeItemDAOImpl.TYPE_JSON);
    }


    /**
     * @return 更具角色得到角色树
     */
    public String getRoleNodeTreeSrc() {
        IRole role = getRole();
        if (StringUtil.isNull(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        treeItemDAO.setChecked(treeItemDAO.getRoleNodeIds(role.getId()));
        return treeItemDAO.getTreeSrc(TreeItemDAOImpl.TYPE_JSON);
    }


    /**
     * @return 得到树代码
     */
    public String getChildSrc() {
        if (StringUtil.isNull(nodeId)) {
            nodeId = ArrayUtil.toString(getArray("nodeId", true), StringUtil.SEMICOLON);
        }
        treeItemDAO.setSelected(nodeId);
        treeItemDAO.setInput(input);
        treeItemDAO.setUseLimb(useLimb);
        return treeItemDAO.getJsonChildSrc(nodeId);
    }


    /**
     * @return 得到第一个节点
     */
    public String getFirstNodeId() {
        if (!StringUtil.hasLength(nodeId)) {
            return "root";
        }
        if (!nodeId.contains(StringUtil.SEMICOLON)) {
            return nodeId;
        }
        return StringUtil.substringBefore(nodeId, StringUtil.SEMICOLON);
    }

    /**
     * @return 导出树
     */
    @Operate(caption = "导出树关系")
    public String getTreeExport() {
        Map<String, String> map = treeItemDAO.getSelectTreeItemMap("....|-");
        StringBuilder sb = new StringBuilder();
        for (String k : map.keySet()) {
            sb.append(k).append(":").append(map.get(k)).append(StringUtil.CRLF);
        }
        map.clear();
        return sb.toString();
    }

    /**
     * 得到节点路径
     *
     * @return 节点列表
     */
    @Operate(caption = "得到节点路径")
    public List<TreeItem> getTreeItemPath() {
        return treeItemDAO.getTreeItemPath(getFirstNodeId());
    }

    /**
     * @return String 得到节点
     */
    @Operate(caption = "得到节点ID")
    public String getNodeId() {
        if (!StringUtil.isNull(nodeId)) {
            return nodeId;
        }
        return "root";
    }

    /**
     * @return 得到当前节点
     */
    @Operate(caption = "得到当前节点")
    public TreeItem getTreeItem() {
        if (treeItemDAO == null) {
            log.error("treeItemDAO not config ,没有配置treeItemDAO");
        }
        TreeItem result = treeItemDAO.getTreeItem(getFirstNodeId());
        if (result == null) {
            return treeItemDAO.getRootTreeItem();
        }
        return result;
    }

    /**
     * @return 得到上级目录
     */
    @Operate(caption = "得到上级目录")
    public List<TreeItem> getParentChildTreeItem() {
        TreeItem treeItem = getTreeItem();
        if (TreeItemDAOImpl.rootId.equalsIgnoreCase(treeItem.getParentNodeId())) {
            return treeItemDAO.getChildTreeItem(treeItem.getNodeId());
        }
        return treeItemDAO.getChildTreeItem(getTreeItem().getParentNodeId());
    }

    @Operate(caption = "得到上级目录")
    public TreeItem getParentTreeItem() {
        TreeItem treeItem = getTreeItem();
        if (treeItem == null) {
            return treeItemDAO.getRootTreeItem();
        }
        if (TreeItemDAOImpl.rootId.equalsIgnoreCase(treeItem.getNodeId())) {
            return treeItem;
        }
        return treeItemDAO.getTreeItem(treeItem.getParentNodeId());
    }


    /**
     * @param nodeId 节点id
     * @return 得到root节点后的第一个节点，就是主菜单节点
     * @throws Exception 异常
     */
    @Operate(caption = "节点后的第一个节点")
    public TreeItem getFirstParentTreeItem(String nodeId) throws Exception {
        return treeItemDAO.getFirstParentTreeItem(nodeId);
    }

    /**
     * @return 得到子节点列表
     */
    @Operate(caption = "得到子节点列表")
    public List<TreeItem> getChildTreeItem() {
        String nid;
        if (nodeId != null && !nodeId.contains(StringUtil.SEMICOLON)) {
            nid = nodeId;
        } else {
            nid = StringUtil.substringBefore(nodeId, StringUtil.SEMICOLON);
        }
        return treeItemDAO.getChildTreeItem(nid);
    }

    /**
     * @param nodeId 节点id
     * @return 得到子
     */
    @Operate(caption = "得到子节点列表")
    public List<TreeItem> getChildTreeItem(String nodeId) {
        return treeItemDAO.getChildTreeItem(nodeId);
    }

    /**
     * @return 得到用户数
     */
    @Operate(caption = "得到用户数")
    public String[] getMemberTreeArray() {
        return memberTreeDAO.getMemberTreeArray(uid);
    }

    /**
     * @return 得到所有树节点
     */
    @Operate(caption = "得到所有树节点")
    public List<TreeItem> getTreeList() {
        return treeItemDAO.getList();
    }

    public TreeItem getTreeItem(String nodeId) {
        return treeItemDAO.getTreeItem(nodeId);
    }


    public boolean isLimb() {
        return isLimb(nodeId);
    }

    public boolean isLimb(String nodeId) {
        return treeItemDAO.isLimb(nodeId);
    }

    public boolean isManager(String nodeId, String roleId, long uid) {
        return treeItemDAO.isManager(nodeId, roleId, uid);
    }

    /**
     * @return 得到用户树
     */
    @Operate(caption = "得到用户树")
    public List<MemberTree> getManTree() {
        return memberTreeDAO.getMemberTree(uid);
    }


    /**
     * 常用的单元方法，判断一个列表里边是否存在自定的id
     *
     * @param list   节点列表
     * @param nodeId 节点id
     * @return 常用的单元方法
     */
    @Operate(caption = "判断一个列表里边是否存在的id")
    static public TreeItem getFindTreeItem(List<TreeItem> list, String nodeId) {
        if (StringUtil.isNull(nodeId) || list.isEmpty()) {
            return null;
        }
        for (TreeItem treeItem : list) {
            if (treeItem.getNodeId().equalsIgnoreCase(nodeId)) {
                return treeItem;
            }
        }
        return null;
    }

    @Override
    public String execute() throws Exception {
        TreeItem treeItem = getTreeItem();
        if (treeItem == null) {
            treeItem = new TreeItem();
        }
        put("nodeId", getFirstNodeId());
        put("treeItem", treeItem);
        return super.execute();
    }
}
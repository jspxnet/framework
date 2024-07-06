/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.parse.html.NodeTag;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.*;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.txweb.model.param.TreeItemParam;
import com.github.jspxnet.txweb.model.param.TreeParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.txweb.table.TreeRole;
import com.github.jspxnet.txweb.view.TreeView;
import com.github.jspxnet.utils.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-24
 * Time: 15:20:38
 */

@HttpMethod(caption = "栏目树")
public class TreeManageAction extends TreeView {


    public TreeManageAction() {

    }



    @Operate(caption = "清空缓存")
    public void clear() {
        setActionResult(ROC);
        treeItemDAO.evict(TreeItem.class);
        addActionMessage(language.getLang(LanguageRes.operationSuccess));
    }

    @Operate(caption = "添加节点")
    public RocResponse<Integer> addTreeItem(@Param TreeItemParam param) throws Exception {
        TreeItem treeItem =BeanUtil.copy(param,TreeItem.class);
        IUserSession userSession = getUserSession();
        treeItem.setPutUid(userSession.getUid());
        treeItem.setPutName(userSession.getName());

        //得到简拼begin
        treeItem.setSpelling(ChineseUtil.getFullSpell(treeItem.getCaption(), ""));
        //得到简拼end

        if (treeItemDAO.addTreeItem(getFirstNodeId(), treeItem)) {
            return RocResponse.success(1,language.getLang(LanguageRes.saveSuccess));
        }
        return RocResponse.error(1,language.getLang(LanguageRes.saveFailure));
    }

    @Operate(caption = "添加子节点")
    public RocResponse<Integer> addChildTreeItem(@Param(caption = "参数") TreeItemParam param) throws Exception {
        if (StringUtil.isEmpty(param.getCaption())) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(),language.getLang(LanguageRes.nodeNameNeedInput));
        }
        TreeItem treeItem =BeanUtil.copy(param,TreeItem.class);
        IUserSession userSession = getUserSession();
        treeItem.setPutName(userSession.getName());
        treeItem.setPutUid(userSession.getUid());
        //得到简拼begin
        treeItem.setSpelling(ChineseUtil.getFullSpell(treeItem.getCaption(), ""));
        //得到简拼end
        if (treeItemDAO.addChildTreeItem(getFirstNodeId(), treeItem)) {
            return RocResponse.success(1,language.getLang(LanguageRes.addChildNodeSuccess));

        }
        return RocResponse.error(1,language.getLang(LanguageRes.saveFailure));
    }


    @Operate(caption = "删除节点")
    public RocResponse<Integer> deleteTreeItem(@Param(caption = "nodeId",required = true,min = 1,max = 64,message = "节点ID不能为空") String nodeId)  {
        if (treeItemDAO.deleteTreeItem(nodeId)) {
            return RocResponse.success(1,language.getLang(LanguageRes.deleteSuccess));

        }
        return RocResponse.error(1,language.getLang(LanguageRes.deleteFailure));
    }

    @Operate(caption = "上移节点",method = "top")
    public RocResponse<Integer> top(@Param(caption = "nodeId",required = true,min = 1,max = 64,message = "节点ID不能为空") String nodeId) throws Exception {
        if (treeItemDAO.nodeTop(nodeId)) {
            return RocResponse.success(1,language.getLang(LanguageRes.success));
        }
        return RocResponse.error(1,language.getLang(LanguageRes.operationFailure));
    }

    @Operate(caption = "下移节点",method = "down")
    public RocResponse<Integer> down(@Param(caption = "nodeId",required = true,min = 1,max = 64,message = "节点ID不能为空") String nodeId) throws Exception {
        if (treeItemDAO.nodeDown(nodeId)) {
            return RocResponse.success(1,language.getLang(LanguageRes.success));
        }
        return RocResponse.error(1,language.getLang(LanguageRes.operationFailure));
    }

    @Operate(caption = "编辑节点")
    public void editTreeItem(@Param TreeItemParam param) throws Exception
    {
        if (isGuest()) {
            return;
        }

        AssertException.isNull(nodeId,"编辑的节点nodeId,不允许为空");
        TreeItem treeItemOld = null;

        if (param.getId() > 0) {
            treeItemOld = treeItemDAO.get(TreeItem.class, param.getId());
        } else if (!StringUtil.isNull(param.getNodeId())) {
            treeItemOld = treeItemDAO.getTreeItem(param.getNodeId());
        }
        if (treeItemOld == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameter));
            return;
        }

        IUserSession userSession = getUserSession();
        TreeItem treeItem =BeanUtil.copy(param,TreeItem.class);
        treeItem.setPutName(userSession.getName());
        treeItem.setPutUid(userSession.getUid());
        treeItem.setId(treeItemOld.getId());
        treeItem.setNamespace(treeItemOld.getNamespace());
        treeItem.setCreateDate(treeItemOld.getCreateDate());
        treeItem.setParentNodeId(treeItemOld.getParentNodeId());
        //得到简拼begin
        treeItem.setSpelling(ChineseUtil.getFullSpell(treeItem.getCaption(), ""));
        //得到简拼end
        if (treeItemDAO.editTreeItem(treeItem)) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess));
            setActionResult(SUCCESS);
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }

    @Describe("兼容老版本")
    @Deprecated
    @Operate(caption = "删除节点")
    public void deleteTreeItem() {
        if (StringUtil.isEmpty(getFirstNodeId())) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.nodeNameNeedInput));
            return;
        }
        if (treeItemDAO.deleteTreeItem(getFirstNodeId())) {
            addActionMessage(language.getLang(LanguageRes.deleteSuccess));
            setActionResult(SUCCESS);
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.deleteFailure));
        }
    }



    private long id = 0;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Describe("兼容老版本")
    @Deprecated
    @Operate(caption = "编辑节点")
    public void editTreeItem() throws Exception
    {
        if (isGuest()) {
            return;
        }
        String nodeId = getNodeId();
        TreeItem treeItemOld = null;

        if (id > 0) {
            treeItemOld = treeItemDAO.get(TreeItem.class, id);
        } else if (!StringUtil.isNull(nodeId)) {
            treeItemOld = treeItemDAO.getTreeItem(nodeId);
        }
        if (treeItemOld == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameter));
            return;
        }

        IUserSession userSession = getUserSession();
        TreeItem treeItem = getBean(TreeItem.class);
        treeItem.setNodeId(getFirstNodeId());
        treeItem.setPutName(userSession.getName());
        treeItem.setPutUid(userSession.getUid());
        treeItem.setId(treeItemOld.getId());
        treeItem.setNamespace(treeItemOld.getNamespace());
        treeItem.setCreateDate(treeItemOld.getCreateDate());
        treeItem.setParentNodeId(treeItemOld.getParentNodeId());
        //得到简拼begin
        treeItem.setSpelling(ChineseUtil.getFullSpell(treeItem.getCaption(), ""));
        //得到简拼end
        if (treeItemDAO.editTreeItem(treeItem)) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess));
            setActionResult(SUCCESS);
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }


    @Operate(caption = "保存XML列表")
    public void saveXml(@Param(value = "treeXML",min = 0,max = 30000,level = SafetyEnumType.NONE,required = true,message ="XML不允许为空") String treeValue) throws Exception {
        IUserSession userSession = getUserSession();
        if (userSession == null || userSession.isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            setActionResult(LOGIN);
            return;
        }

        setActionLogContent(treeValue);

        List<TreeItem> saveList = new LinkedList<TreeItem>();
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("node", NodeTag.class.getName());
        List<TagNode> tagList = xmlEngine.getTagNodes(treeValue);
        for (TagNode node : tagList) {
            NodeTag nodeTag = (NodeTag) node;
            TreeItem treeItem = new TreeItem();
            treeItem.setNodeId(XMLUtil.deleteQuote(nodeTag.getStringAttribute("id")));
            TreeItem oldTreeItem = treeItemDAO.getTreeItem(treeItem.getNodeId());
            if (oldTreeItem != null) {
                BeanUtil.copyFiledValue(oldTreeItem, treeItem);
            }
            treeItem.setCaption(HtmlUtil.escapeDecodeHtml(XMLUtil.deleteQuote(nodeTag.getStringAttribute("text"))));
            //得到简拼begin
            treeItem.setSpelling(ChineseUtil.getFullSpell(treeItem.getCaption(), ""));
            //得到简拼end
            treeItem.setParentNodeId(XMLUtil.deleteQuote(nodeTag.getStringAttribute("pid")));
            treeItem.setIcon(XMLUtil.deleteQuote(nodeTag.getStringAttribute("icon")));
            treeItem.setOpenIcon(XMLUtil.deleteQuote(nodeTag.getStringAttribute("openIcon")));
            treeItem.setSortType(StringUtil.toInt(XMLUtil.deleteQuote(nodeTag.getStringAttribute("sort"))));
            treeItem.setNamespace(treeItemDAO.getNamespace());
            treeItem.setPutName(userSession.getName());
            treeItem.setPutUid(userSession.getUid());
            saveList.add(treeItem);
        }

        //ID有可能会重复,需要检查后在保存 begin
        String[] idArray = null;
        for (TreeItem treeItem : saveList) {
            if (StringUtil.isNull(treeItem.getNodeId())) {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedSaveNullNodeId));
                return;
            }
            if (!ArrayUtil.inArray(idArray, treeItem.getNodeId(), true)) {
                idArray = ArrayUtil.add(idArray, treeItem.getNodeId());
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedSaveRepeatNodeId) + ":" + treeItem.getNodeId() + " " + treeItem.getCaption());
                return;
            }
        }
        //ID有可能会重复,需要检查后在保存 end
        if (saveList.isEmpty()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notDataFind));
            return;
        }
        try {
            if (treeItemDAO.deleteTree()) {
                int i = treeItemDAO.save(saveList);
                if (i > 0) {
                    addActionMessage(language.getLang(LanguageRes.saveSuccess) + ":" + i);
                }
            }
            setActionResult(SUCCESS);
        } catch (Exception e) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }


    /**
     *
     * @param treeParam json 格式参数对象
     */
    @Transaction
    public void save(@Param(required = true,message = "TreeParam不允许为空") TreeParam treeParam) {

        List<TreeItemParam> treeItemParamList =  BeanUtil.copyList(treeParam.getList(),TreeItemParam.class);
        String[] idArray = null;
        List<TreeItem> saveList = new ArrayList<>();
        for (TreeItemParam treeItemParam:treeItemParamList) {
            if (StringUtil.isNull(treeItemParam.getNodeId())) {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedSaveNullNodeId));
                return;
            }
            if (!ArrayUtil.inArray(idArray, treeItemParam.getNodeId(), true)) {
                idArray = ArrayUtil.add(idArray, treeItemParam.getNodeId());
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedSaveRepeatNodeId) + ":" + treeItemParam.getNodeId() + " " + treeItemParam.getCaption());
                return;
            }
            if (treeItemParam.getDelete()!=1) {
                TreeItem treeItem = BeanUtil.copy(treeItemParam,TreeItem.class);
                treeItem.setNamespace(getNamespace());
                treeItem.setOrganizeId(treeItemDAO.getOrganizeId());
                saveList.add(treeItem);
            }
        }
        if (saveList.isEmpty()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notDataFind));
            return;
        }
        try {
            if (treeItemDAO.deleteTree()) {
                int i = treeItemDAO.save(saveList);
                if (i > 0) {
                    addActionMessage(language.getLang(LanguageRes.saveSuccess) + ":" + i);
                }
            }
            setActionResult(SUCCESS);
        } catch (Exception e) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }

    /**
     * 更新用户角色浏览权限，每次编辑树结构后都需要重新生成
     * 当角色id变动后，权限要重新设置
     * @throws Exception            异常
     */
    @Operate(caption = "更新角色浏览权限")
    private void updateTreeRoles() throws Exception
    {
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        PermissionDAO permissionDAO = beanFactory.getBean(PermissionDAO.class, getRootNamespace());
        List<Role> roleList = permissionDAO.getRoleList();
        List<TreeItem> treeItemList = treeItemDAO.createRepairTreeRole(roleList);
        Collection<TreeRole> treeRoleList = treeItemDAO.createTreeRole(treeItemList, roleList);
        treeItemDAO.deleteTreeRole();
        treeItemDAO.save(treeRoleList);
        treeItemDAO.evict(TreeRole.class);
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            treeItemDAO.evictTree();
            updateTreeRoles();
        }
        return super.execute();
    }
}
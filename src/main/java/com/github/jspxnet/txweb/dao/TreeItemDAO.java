/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.exception.ValidException;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.txweb.table.TreeRole;

import java.util.Collection;
import java.util.Map;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-10
 * Time: 9:42:53
 */
public interface TreeItemDAO extends SoberSupport {
    boolean isShow();

    void setShow(boolean show);

    String[] getSelected();

    void setSelected(String selected);

    void setSelected(String[] selected);

    void setNamespace(String namespace);

    String getNamespace();

    String getBehavior();

    void setChecked(String checkeds);

    String[] getChecked();

    void setChecked(String[] checked);

    TreeItem getRootTreeItem();

    List<TreeItem> getList();

    String[] getRoleNodeIds(String roleId);

    //得到树结构
    String getTreeSrc(String type);

    String getXmlChildSrc(String nodeId);

    String getJsonChildSrc(String nodeId);

    List<TreeItem> getLeafList();

    int getSize();

    boolean addTreeItem(String nodeId, TreeItem node) throws Exception;

    boolean addChildTreeItem(String nodeId, TreeItem node) throws Exception;

    boolean deleteTreeItem(String nodeId);

    boolean editTreeItem(TreeItem node) throws Exception;

    String[] getChildTreeItemIdArray(String nodeId, boolean all);

    List<TreeItem> getTreeItemPath(String nodeId);

    Map<String, String> getSelectTreeItemMap(String fen);

    List getTreeItemPath(TreeItem treeItem);

    boolean deleteTree() throws Exception;

    List<TreeItem> getListForType(int nodeType);

    List<TreeItem> getTreeItemForFind(final String find, final int ipage, final int icount);

    long getCountForFind(final String find);

    boolean editSortDate(String[] checkbox) throws ValidException;

    boolean editShowSortType(String[] checkbox, int sortType) throws ValidException;

    List<TreeItem> getChildTreeItem(final String nodeId);

    void setBehavior(String behavior);

    TreeItem getTreeItem(String nodeId);

    boolean isLimb(String nodeId);

    String[] deleteLimb(String[] nodeId);

    String[] addLimb(String[] nodeId);

    Map<String, String> getTreeItemMap();

    String[] getTreeItemIdArray();

    Map<String, String> getDrumbeatingTreeItemMap();

    boolean deleteTreeRole();

    String toXMLString();

    void setInputName(String inputName);

    String getInputName();

    String getInput();

    void setInput(String input);

    boolean isUseLimb();

    void setUseLimb(boolean useLimb);

    boolean isManager(String nodeId, String roleId, long uid);

    boolean isRoleNodeId(String nodeId, String roleId);

    TreeItem getFirstParentTreeItem(String nodeId) throws Exception;

    List<TreeItem> createRepairTreeRole(List<Role> roleList);

    Object getExtendsNodeValue(String nodeId, String field) throws NoSuchMethodException;

    Collection<TreeRole> createTreeRole(List<TreeItem> treeList, List<Role> roleList);

    void setOrganizeId(String organizeId);

    String getOrganizeId();

    void evictTree();

}
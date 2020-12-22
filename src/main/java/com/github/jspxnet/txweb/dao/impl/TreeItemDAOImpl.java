/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.TreeItemDAO;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.txweb.table.TreeRole;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.Criteria;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-10
 * Time: 9:43:25
 * 通过节点生成js 树,支持Xtree和 XloadTree
 * 1.3
 */
@Slf4j
public abstract class TreeItemDAOImpl extends JdbcOperations implements TreeItemDAO {

    final static public String BEHAVIOR_EXPLORER = "explorer";
    final static public String BEHAVIOR_CLASSIC = "classic";
    final static public String TYPE_CHECKBOX = "checkbox";
    final static public String TYPE_RADIO = "radio";
    final static public String TYPE_TEXT = "text";

    public static final String Type_json = "json";
    public static final String Type_roleJson = "roleJson";
    public static final String Type_jsonTree = "jsonTree";
    public static final String Type_roleJsonTree = "roleJsonTree";


    protected String organizeId = StringUtil.empty;
    protected String namespace; //命名空间
    protected boolean show = true; //是否显示隐藏
    protected String[] selected = ArrayUtil.emptyString; //当前 选中状态
    protected String input = StringUtil.empty; //输入框 text
    protected String inputName = "nodeId"; //输入框 text
    protected boolean useLimb = false; //是否使用叶子部分
    final static public String rootId = "root";
    //党群是否展开,默认展开
    protected String behavior = BEHAVIOR_CLASSIC;

    public TreeItemDAOImpl() {

    }

    protected String[] checked; //权限判断

    @Override
    public void setChecked(String checked) {
        this.checked = StringUtil.split(StringUtil.replace(checked, ",", StringUtil.SEMICOLON), StringUtil.SEMICOLON);
    }

    @Override
    public void setChecked(String[] checked) {
        this.checked = checked;
    }

    @Override
    public String[] getChecked() {
        return checked;
    }

    @Override
    public String getInputName() {
        return inputName;
    }

    @Override
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public boolean isUseLimb() {
        return useLimb;
    }

    @Override
    public void setUseLimb(boolean useLimb) {
        this.useLimb = useLimb;
    }

    @Override
    public boolean isShow() {
        return show;
    }

    @Override
    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String[] getSelected() {
        return selected;
    }

    @Override
    public void setSelected(String selected) {
        this.selected = StringUtil.split(selected, StringUtil.SEMICOLON);
    }

    @Override
    public void setSelected(String[] selected) {
        this.selected = selected;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    @Override
    public String getOrganizeId() {
        return organizeId;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    @Override
    public String getBehavior() {
        return behavior;
    }

    public static TreeItem createRootTreeItem(String namespace) {
        TreeItem root = new TreeItem();
        root.setNodeId(rootId);
        root.setCaption(rootId);
        root.setDescription(rootId);
        root.setIp("127.0.0.1");
        root.setPutName("admin");
        root.setPutUid(0);
        root.setParentNodeId(StringUtil.empty);
        root.setNamespace(namespace);
        root.setHide(0);
        return root;
    }

    static public boolean isRootTreeItem(TreeItem treeItem) {
        if (treeItem == null) {
            return false;
        }
        return rootId.equalsIgnoreCase(treeItem.getNodeId()) && StringUtil.isNull(treeItem.getParentNodeId());
    }

    /**
     * @param boxattr 修复属性
     * @return 属性
     */
    static public TreeItem fixAttribute(TreeItem boxattr) {
        if (boxattr == null) {
            boxattr = new TreeItem();
            boxattr.setPassword(StringUtil.empty);
            boxattr.setRoleIds(StringUtil.empty);
            boxattr.setShowImage(StringUtil.empty);
            boxattr.setDescription(StringUtil.empty);
            boxattr.setLinkPage(StringUtil.empty);
            boxattr.setPutName("admin");
            boxattr.setPutUid(0);
            boxattr.setIp("127.0.0.1");
            return boxattr;
        }
        return boxattr;
    }

    /**
     * @return 得到首页的排序
     */

    @Override
    public List<TreeItem> getListForType(int nodeType) {
        List<TreeItem> result = new ArrayList<>();
        for (TreeItem treeItem : getList()) {
            if (nodeType == treeItem.getNodeType()) {
                result.add(treeItem);
            }
        }
        Collections.sort(result, new SortTypeComparator());
        return result;
    }


    /**
     * 得到页面列表
     *
     * @param find  查询
     * @param page  页数
     * @param count 一页行数
     * @return 得到列表
     */
    @Override
    public List<TreeItem> getTreeItemForFind(final String find, final int page, final int count) {
        Criteria criteria = createCriteria(TreeItem.class);
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.or(Expression.like("description", "%" + StringUtil.checkSql(find) + "%"), Expression.like("caption", "%" + StringUtil.checkSql(find) + "%")));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }

        return criteria.addOrder(Order.desc("sortType")).addOrder(Order.desc("sortDate")).addOrder(Order.desc("createDate"))
                .setCurrentPage(page).setTotalCount(count).list(false);
    }

    /**
     * @param find 查询
     * @return int 得到连接数量
     */
    @Override
    public long getCountForFind(String find) {
        Criteria criteria = createCriteria(TreeItem.class);
        if (StringUtil.hasLength(find)) {
            criteria = criteria.add(Expression.or(Expression.like("description", "%" + StringUtil.checkSql(find) + "%"), Expression.like("caption", "%" + StringUtil.checkSql(find) + "%")));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }

    /**
     * 排序
     *
     * @param checkbox 参数
     * @param sortType 排序
     * @return boolean 是否成功
     */
    @Override
    public boolean editShowSortType(String[] checkbox, int sortType) {
        if (ArrayUtil.isEmpty(checkbox)) {
            return true;
        }
        try {
            for (String aCheckbox : checkbox) {
                if (StringUtil.isNull(aCheckbox)) {
                    continue;
                }
                TreeItem treeItem = get(TreeItem.class, aCheckbox);
                if (treeItem != null) {
                    treeItem.setSortType(sortType);
                    super.update(treeItem);
                }
            }
        } catch (Exception e) {
            log.error(MessageFormatter.format("editShowSortType checkbox {} sortType {}",
                    ArrayUtil.toString(checkbox, StringUtil.COMMAS), sortType).getMessage(), e);

            return false;
        }
        return true;
    }

    /**
     * 提前
     *
     * @param checkbox 参数
     * @return boolean
     */
    @Override
    public boolean editSortDate(String[] checkbox) {
        if (ArrayUtil.isEmpty(checkbox)) {
            return true;
        }
        try {
            for (String aCheckbox : checkbox) {
                if (!StringUtil.hasLength(aCheckbox)) {
                    continue;
                }
                TreeItem treeItem = get(TreeItem.class, aCheckbox);
                if (treeItem != null) {
                    treeItem.setSortDate(new Date());
                    super.update(treeItem);
                }
            }
        } catch (Exception e) {
            log.error(MessageFormatter.format("editSortDate checkbox {}",
                    ArrayUtil.toString(checkbox, StringUtil.COMMAS)).getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 删除树枝节点，保留叶子部分
     *
     * @param nodeId id
     * @return String[]
     */
    @Override
    public String[] deleteLimb(String[] nodeId) {
        if (ArrayUtil.isEmpty(nodeId)) {
            return nodeId;
        }
        String[] result = null;
        for (String id : nodeId) {
            if (!isLimb(id)) {
                result = ArrayUtil.add(result, id);
            }
        }
        return result;
    }


    /**
     * 为了方便ManTree 保持权限的时候加入中间节点
     *
     * @param nodeId id
     * @return String[]
     */
    @Override
    public String[] addLimb(String[] nodeId) {
        if (ArrayUtil.isEmpty(nodeId)) {
            return nodeId;
        }
        String[] result = null;
        for (String id : nodeId) {
            List<TreeItem> treeItemList = getTreeItemPath(id);
            if (!treeItemList.isEmpty()) {
                for (TreeItem treeItem : treeItemList) {
                    if (!ArrayUtil.inArray(result, treeItem.getNodeId(), true)) {
                        result = ArrayUtil.add(result, treeItem.getNodeId());
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param nodeId 节点id
     * @return boolean 判断是否为树枝节点
     */
    @Override
    public boolean isLimb(String nodeId) {
        if (nodeId == null) {
            return false;
        }
        List<TreeItem> list = getList();
        for (TreeItem treeItem : list) {
            if (nodeId.equals(treeItem.getParentNodeId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param nodeId 节点id
     * @return TreeItem 得到  TreeItem
     */
    @Override
    public TreeItem getTreeItem(String nodeId) {
        if (nodeId == null) {
            return null;
        }
        List<TreeItem> list = getList();
        for (TreeItem treeItem : list) {
            if (treeItem.getNodeId().equalsIgnoreCase(nodeId)) {
                return treeItem;
            }
        }
        return null;
    }

    @Override
    public Object getExtendsNodeValue(String nodeId, String field) {
        if (rootId.equalsIgnoreCase(nodeId) || !StringUtil.hasLength(nodeId)) {
            return null;
        }
        TreeItem treeItem = getTreeItem(nodeId);
        if (treeItem == null) {
            return null;
        }
        Object value = BeanUtil.getProperty(treeItem, field);
        while (StringUtil.isNull((String) value)) {
            treeItem = getTreeItem(treeItem.getParentNodeId());
            if (treeItem != null) {
                value = BeanUtil.getProperty(treeItem, field);
            }
            if (treeItem == null || treeItem.getId() <= 0 || StringUtil.isNull(treeItem.getNodeId()) || "root".equalsIgnoreCase(treeItem.getNodeId())) {
                break;
            }
        }
        return value;
    }


    /**
     * 判断角色是否有管理权限,继承方式
     *
     * @param nodeId 栏目ID
     * @param roleId 角色ID
     * @param uid    用户ID
     * @return 判断角色是否有管理权限, 继承方式
     */
    @Override
    public boolean isManager(String nodeId, String roleId, long uid) {
        TreeItem treeItem = getTreeItem(nodeId);
        if (treeItem == null) {
            return false;
        }
        while (treeItem.getId() <= 0 && !isRootTreeItem(treeItem)) {
            if (treeItem.isInManager(roleId, uid)) {
                return true;
            }
            treeItem = getTreeItem(treeItem.getParentNodeId());
            if (treeItem == null) {
                return false;
            }
        }
        return false;
    }

    /**
     * @param nodeId 得到节点
     * @return 得到子节点列表
     */
    @Override
    public List<TreeItem> getChildTreeItem(String nodeId) {
        return getChildTreeItem(getList(), nodeId);
    }


    /**
     * @param list   所有节点
     * @param nodeId 得到节点
     * @return 得到子节点列表
     */
    public static List<TreeItem> getChildTreeItem(List<TreeItem> list, String nodeId) {
        List<TreeItem> result = new ArrayList<TreeItem>();
        for (TreeItem treeItem : list) {
            if (StringUtil.hasLength(treeItem.getParentNodeId()) && treeItem.getParentNodeId().equals(nodeId)) {
                result.add(treeItem);
            }
        }
        Collections.sort(result, new SortTypeComparator());
        return result;
    }

    /**
     * @param nodeId 节点
     * @param all    所有子节点
     * @return 得到子节点数组
     */
    @Override
    public String[] getChildTreeItemIdArray(String nodeId, boolean all) {
        if (nodeId == null || rootId.equalsIgnoreCase(nodeId)) {
            return new String[0];
        }
        String[] resultArray = new String[0];
        if (!all) {
            List<TreeItem> treeItemList = getChildTreeItem(nodeId);
            for (TreeItem treeIt : treeItemList) {
                resultArray = ArrayUtil.add(resultArray, treeIt.getNodeId());
            }
            return resultArray;
        } else {
            //循环得到所有子列表
            return getChildFullTreeItem(getList(), nodeId);
        }
    }


    public static TreeItem getRootTreeItem(List<TreeItem> list) {
        for (TreeItem treeItem : list) {
            if (treeItem.getNodeId().equals(rootId) && StringUtil.isNull(treeItem.getParentNodeId())) {
                return treeItem;
            }
        }
        TreeItem treeItem = new TreeItem();
        treeItem.setNodeId(rootId);
        treeItem.setCaption(rootId);
        return treeItem;
    }

    /**
     * @return 得到根节点
     */
    @Override

    public TreeItem getRootTreeItem() {
        return getRootTreeItem(getList());
    }

    /**
     * @return 得到树的所有节点
     */
    @Override
    public List<TreeItem> getList() {
        Criteria criteria = createCriteria(TreeItem.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId))
        {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.addOrder(Order.asc("sortType")).list(false);
    }

    /**
     * @return 得到数的所有节点ID
     */
    @Override
    public String[] getTreeItemIdArray() {
        String[] result = null;
        List<TreeItem> list = getList();
        for (TreeItem treeItem : list) {
            result = ArrayUtil.add(result, treeItem.getNodeId());
        }
        return result;
    }

    /**
     * @return 得到节点的数目
     */
    @Override
    public int getSize() {
        return getList().size();
    }


    /**
     * @param node 节点
     * @param fen  分隔号
     * @return Map     数据够转换为选择框形式
     */
    private Map<String, String> getTreeItemMap(TreeItem node, String fen) {
        if (!show && node.getHide() == 1) {
            return new LinkedHashMap<String, String>();
        }
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (StringUtil.isNull(node.getParentNodeId())) {
            //result.put("", "ALL");
            for (TreeItem nNode : getChildTreeItem(node.getNodeId())) {
                Map<String, String> childResult = getTreeItemMap(nNode, fen);
                for (String key : childResult.keySet()) {
                    result.put(key, childResult.get(key));
                }
            }
            return result;
        } else if (ArrayUtil.isEmpty(checked)) {
            result.put(node.getNodeId(), node.getCaption());
        } else if (ArrayUtil.inArray(checked, node.getNodeId(), true)) {
            result.put(node.getNodeId(), node.getCaption());
        }
        for (TreeItem nNode : getChildTreeItem(node.getNodeId())) {
            Map<String, String> childResult = getTreeItemMap(nNode, fen);
            for (String key : childResult.keySet()) {
                if (rootId.equalsIgnoreCase(nNode.getParentNodeId())) {
                    result.put(key, childResult.get(key));
                } else {
                    result.put(key, fen + childResult.get(key));
                }
            }
        }
        return result;
    }

    /**
     * @param fen 分隔号
     * @return Map 选择框
     */
    @Operate(caption = "选择框")
    @Override
    public Map<String, String> getSelectTreeItemMap(String fen) {
        TreeItem rootNode = getRootTreeItem();
        return getTreeItemMap(rootNode, fen);
    }

    /**
     * @return 创建节点ID, 不能是数字开头和有特殊字符
     */

    private String createTreeItemId() {
        int size = getSize() + 1;
        String id = namespace.toLowerCase() + size;
        TreeItem checkTreeItem = getTreeItem(id);
        while (checkTreeItem != null) {
            size++;
            id = namespace.toLowerCase() + size;
            checkTreeItem = getTreeItem(id);
        }
        return id;
    }

    /**
     * @param nodeId 添加的节点id
     * @param node   要添加的节点
     * @return boolean 是否添加成功
     */
    @Operate(caption = "添加的节点")
    @Override
    public boolean addTreeItem(String nodeId, TreeItem node) throws Exception {
        if (node == null) {
            return false;
        }
        TreeItem treeItem = getTreeItem(nodeId);
        if (treeItem == null || rootId.equalsIgnoreCase(nodeId)) {
            treeItem = getRootTreeItem();
            if (treeItem == null) {
                treeItem = createRootTreeItem(namespace);
            }
            save(treeItem, false);
            return addChildTreeItem(treeItem.getNodeId(), node);
        }
        if (StringUtil.isNull(treeItem.getParentNodeId())) {
            return false;
        }
        node.setNodeId(createTreeItemId());
        if (node.getNodeId().equalsIgnoreCase(node.getParentNodeId())) {
            return false;
        }
        node.setNamespace(namespace);
        node.setOrganizeId(organizeId);
        node.setCaption(node.getCaption());
        node.setParentNodeId(treeItem.getParentNodeId());
        save(node, false);
        return true;
    }

    /**
     * @param nodeId 相对节点
     * @param node   节点
     * @return boolean  增加到子节点 是否成功
     */
    @Operate(caption = "添加相对节点")
    @Override
    public boolean addChildTreeItem(String nodeId, TreeItem node) throws Exception {
        if (node == null) {
            return false;
        }
        TreeItem rootTreeItem = getRootTreeItem();
        if (rootTreeItem == null) {
            rootTreeItem = createRootTreeItem(namespace);
            save(rootTreeItem, false);
        }
        node.setCaption(node.getCaption());
        if (StringUtil.isNull(nodeId)) {
            nodeId = rootTreeItem.getNodeId();
        }
        TreeItem treeItem = getTreeItem(nodeId);
        if (treeItem == null) {
            return false;
        }
        node.setNodeId(createTreeItemId());
        node.setNamespace(namespace);
        node.setOrganizeId(organizeId);
        node.setParentNodeId(treeItem.getNodeId());
        if (node.getNodeId().equalsIgnoreCase(node.getParentNodeId())) {
            return false;
        }
        save(node, false);
        return true;
    }

    /**
     * @param nodeId 删除的节点id
     * @return boolean  删除节点 是否成功
     */
    @Operate(caption = "删除的节点")
    @Override
    public boolean deleteTreeItem(@Param(caption = "删除的节点id") String nodeId) {
        Criteria criteria = createCriteria(TreeItem.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.add(Expression.or(Expression.eq("parentNodeId", nodeId), Expression.eq("nodeId", nodeId))).delete(false) > 0;
    }

    /**
     * @return boolean 删除树
     */
    @Operate(caption = "删除树")
    @Override
    public boolean deleteTree() {
        Criteria criteria = createCriteria(TreeItem.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.delete(false) > 0;
    }

    /**
     * @param node 编辑节点
     * @return boolean  编辑节点 是否成功
     */
    @Operate(caption = "编辑节点")
    @Override
    public boolean editTreeItem(@Param(caption = "编辑的节点") TreeItem node) throws Exception {
        node.setNamespace(namespace);
        node.setOrganizeId(organizeId);
        return !StringUtil.isEmpty(node.getNodeId()) && super.update(node) > 0;
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * @return 删除所有树角色树节点
     */
    @Operate(caption = "删除所有树角色树节点")
    @Override
    public boolean deleteTreeRole() {
        Criteria criteria = createCriteria(TreeRole.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.delete(false) > 0;
    }

    /**
     * NodeId 得到路径ID
     *
     * @param nodeId 树节点
     * @return 树列表
     */
    @Operate(caption = "得到路径ID")
    @Override
    public List<TreeItem> getTreeItemPath(String nodeId) {
        TreeItem treeItem = getTreeItem(nodeId);
        return getTreeItemPath(treeItem);
    }

    /**
     * @param treeItem 节点
     * @return 路径
     */
    @Operate(caption = "路径节点列表")
    @Override
    public List<TreeItem> getTreeItemPath(TreeItem treeItem) {
        List<TreeItem> list = new LinkedList<TreeItem>();
        TreeItem parentNode = treeItem;
        while (parentNode != null && !rootId.equalsIgnoreCase(parentNode.getNodeId())) {
            list.add(0, parentNode);
            parentNode = getTreeItem(parentNode.getParentNodeId());
        }
        return list;
    }

    /**
     * @return Map  返回结点Map方便 得到对应的分类
     */
    @Override
    public Map<String, String> getTreeItemMap() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        List<TreeItem> list = getList();
        for (TreeItem treeItem : list) {
            result.put(treeItem.getNodeId(), treeItem.getCaption());
        }
        return result;
    }

    /**
     * @return Map 得到广告列表
     */
    @Override
    public Map<String, String> getDrumbeatingTreeItemMap() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        List<TreeItem> list = getList();
        for (TreeItem treeItem : list) {
            result.put(treeItem.getNodeId(), treeItem.getDrumbeating());
        }
        return result;
    }


    /**
     * 排序数
     */
    public static class SortTypeComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null) {
                return 0;
            }
            if (o2 == null) {
                return 0;
            }
            if (((TreeItem) o1).getSortType() > ((TreeItem) o2).getSortType()) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * @return 得到xml格式数据
     */
    @Override
    public String toXMLString() {
        List<TreeItem> list = getList();
        StringBuilder sb = new StringBuilder();
        sb.append("<tree>\r\n");
        for (TreeItem treeItem : list) {
            sb.append(treeItem.toString());
        }
        sb.append("</tree>\r\n");
        return sb.toString();
    }

    @Override
    public TreeItem getFirstParentTreeItem(String nodeId) {
        if (nodeId == null) {
            return null;
        }
        TreeItem treeItem = getTreeItem(nodeId);
        if (treeItem == null || "root".equalsIgnoreCase(treeItem.getParentNodeId())) {
            return treeItem;
        }
        List<TreeItem> list = getList();
        while (!rootId.equalsIgnoreCase(treeItem.getNodeId())) {
            treeItem = getParentTreeItem(list, treeItem);
            if (treeItem == null) {
                treeItem = getRootTreeItem();
            }
            if (rootId.equalsIgnoreCase(treeItem.getNodeId()) || "root".equalsIgnoreCase(treeItem.getParentNodeId())) {
                return treeItem;
            }

        }
        return getRootTreeItem();
    }

    /**
     * 得到节点父ID，提高两种方式，此方法内部使用，方便一次查询提高速度
     *
     * @param list     列表
     * @param treeItem 节点
     * @return 得到节点父ID
     */
    private static TreeItem getParentTreeItem(List<TreeItem> list, TreeItem treeItem) {
        for (TreeItem item : list) {
            if (treeItem.getParentNodeId().equalsIgnoreCase(item.getNodeId())) {
                return item;
            }
        }
        return null;
    }

    private static String[] getChildFullTreeItem(List<TreeItem> list, String nodeId) {
        String[] resultArray = new String[]{nodeId};
        for (TreeItem treeItem : list) {
            if (StringUtil.hasLength(treeItem.getParentNodeId()) && treeItem.getParentNodeId().equals(nodeId)) {
                resultArray = ArrayUtil.join(resultArray, getChildFullTreeItem(list, treeItem.getNodeId()));
            }
        }
        return resultArray;
    }

    /**
     * 判断是否为叶子节点
     *
     * @param treeItem 节点
     * @return 是否为叶子节点
     */
    private static boolean isLeaf(List<TreeItem> treeList, TreeItem treeItem) {
        if (isRootTreeItem(treeItem)) {
            return false;
        }
        for (TreeItem item : treeList) {
            if (item.getParentNodeId().equalsIgnoreCase(treeItem.getNodeId())) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return 得到叶子节点列表
     */
    @Override
    public List<TreeItem> getLeafList() {
        List<TreeItem> treeList = getList();
        List<TreeItem> leafList = new ArrayList<>();
        for (TreeItem treeItem : treeList) {
            if (isLeaf(treeList, treeItem)) {
                leafList.add(treeItem);
            }
        }
        return leafList;
    }

    /**
     * @param list   所有节点
     * @param nodeId 节点id
     * @return 得到TreeItem
     */
    public static TreeItem getTreeItem(List<TreeItem> list, String nodeId) {
        if (nodeId == null) {
            return null;
        }
        for (TreeItem treeItem : list) {
            if (treeItem.getNodeId().equalsIgnoreCase(nodeId)) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 向上的路径角色都是空
     *
     * @param list     节点列表
     * @param treeItem 节点
     * @return 向上的路径角色都是空
     */
    public boolean isEmptyParentPathRole(final List<TreeItem> list, TreeItem treeItem) {
        TreeItem topTreeItem = getParentTreeItem(list, treeItem);
        while (topTreeItem != null && !isRootTreeItem(topTreeItem)) {
            if (!StringUtil.isNull(topTreeItem.getRoleIds())) {
                return false;
            }
            topTreeItem = getParentTreeItem(list, topTreeItem);
        }
        return true;
    }

    /**
     * 父节点和子节点角色不同，父节点权限将合并子节点（子节点不能合并父节点）
     * 合并父路径上的角色,只合并非空的节点,空节点有可能是全部权限
     *
     * @param list 节点列表
     * @param nodeId 节点ID
     */
    private static void joinParentPathRole(final List<TreeItem> list, String nodeId) {
        TreeItem treeItem = getTreeItem(list, nodeId);
        TreeItem topTreeItem = getParentTreeItem(list, treeItem);
        while (topTreeItem != null && !isRootTreeItem(topTreeItem)) {
            if (!StringUtil.isNull(topTreeItem.getRoleIds())) {
                treeItem.joinRoleIds(topTreeItem.getRoleIds());
            }
            topTreeItem = getParentTreeItem(list, topTreeItem);
        }
    }

    /**
     * 查询子节点角色，把子节点角色设置给自己，这样树路径才能联通
     *
     * @param list 节点列表
     * @param nodeId 节点ID
     */
    private static void joinChildPathRole(final List<TreeItem> list, String nodeId) {
        TreeItem treeItem = getTreeItem(list, nodeId);
        String[] childNodeIds = getChildFullTreeItem(list, nodeId);
        childNodeIds = ArrayUtil.remove(childNodeIds, nodeId);
        for (String cNodeId : childNodeIds) {
            TreeItem item = getTreeItem(list, cNodeId);
            treeItem.joinRoleIds(item.getRoleIds());
        }
    }

    /**
     * 修复逻辑权限角色列表
     *
     * @param list       树结构
     * @param leafList   叶子节点,为了提高性能采用参数传递方式
     * @param allRoleIds 所有的角色列表
     */
    private void repairTreeRole(final List<TreeItem> list, List<TreeItem> leafList, String allRoleIds) {
        for (TreeItem item : leafList) {
            //填补父节点角色,填补方式为并集
            TreeItem topTreeItem = item;
            while (topTreeItem != null && !isRootTreeItem(topTreeItem)) {
                if (StringUtil.isNull(topTreeItem.getRoleIds()) && isEmptyParentPathRole(list, topTreeItem)) {
                    //填充
                    TreeItem tmpTopTreeItem = topTreeItem;
                    while (tmpTopTreeItem != null && !isRootTreeItem(tmpTopTreeItem)) {
                        tmpTopTreeItem.setRoleIds(allRoleIds);
                        tmpTopTreeItem = getParentTreeItem(list, tmpTopTreeItem);
                    }
                }
                if (StringUtil.isNull(topTreeItem.getRoleIds()) && !isEmptyParentPathRole(list, topTreeItem)) {
                    //合并
                    joinParentPathRole(list, topTreeItem.getNodeId());
                }
                if (!StringUtil.isNull(topTreeItem.getRoleIds())) {
                    //合并子节点
                    joinChildPathRole(list, topTreeItem.getNodeId());
                }
                topTreeItem = getParentTreeItem(list, topTreeItem);
            }
        }
        //--------------------------------------------
        //填补子节点
        for (TreeItem item : leafList) {
            if (StringUtil.isNull(item.getRoleIds())) {
                TreeItem topTreeItem = getParentTreeItem(list, item);
                while (topTreeItem != null && !isRootTreeItem(topTreeItem)) {
                    if (!StringUtil.isNull(topTreeItem.getRoleIds())) {
                        TreeItem treeItem = getTreeItem(list, item.getNodeId());
                        if (StringUtil.isNull(treeItem.getRoleIds())) {
                            treeItem.joinRoleIds(topTreeItem.getRoleIds());
                        }
                    }
                    topTreeItem = getParentTreeItem(list, topTreeItem);
                }
            }
        }

        //--------------------------------------------填补空节点
        for (TreeItem item : list) {
            if (StringUtil.isNull(item.getRoleIds())) {
                item.setRoleIds(allRoleIds);
            }
        }
    }


    /**
     * 自动修复填写逻辑角色
     *
     * @param roleList 角色列表
     * @return 返回一颗所有节点都填写了角色信息的树
     */
    @Override
    public List<TreeItem> createRepairTreeRole(List<Role> roleList) {
        StringBuilder allRoles = new StringBuilder();
        for (Role role : roleList) {
            allRoles.append("{").append(role.getId()).append(":").append(role.getName()).append("}").append(StringUtil.SEMICOLON);
        }
        if (allRoles.toString().endsWith(StringUtil.SEMICOLON)) {
            allRoles.setLength(allRoles.length() - 1);
        }
        //叶子节点
        List<TreeItem> leafList = getLeafList();
        //所有节点,克隆一份,避免影响其他节点
        List<TreeItem> treeList = new ArrayList<>(getList());
        repairTreeRole(treeList, leafList, allRoles.toString());
        return treeList;
    }


    /**
     * @param treeList createRepairTreeRole 创建的树结构
     * @param roleList 角色列表
     * @return 创建角色权限结构 TreeRole
     */
    @Override
    public Collection<TreeRole> createTreeRole(List<TreeItem> treeList, List<Role> roleList) {
        //生成对应关系
        Map<String, TreeRole> treeRoleMap = new HashMap<>();
        for (Role role : roleList) {
            if (StringUtil.isEmpty(role.getId())) {
                continue;
            }
            TreeRole treeRole = new TreeRole();
            treeRole.setNamespace(namespace);
            treeRole.setOrganizeId(organizeId);
            treeRole.setRoleId(role.getId());
            treeRoleMap.put(role.getId(), treeRole);
        }
        for (TreeRole tRole : treeRoleMap.values()) {
            for (TreeItem item : treeList) {
                if (item.isInRoleIds(tRole.getRoleId(), 0)) {
                    tRole.joinNodeIds(item.getNodeId());
                }
            }
        }
        return treeRoleMap.values();
    }

    /**
     * @param roleId 栏目id
     * @return 得到角色对应能够浏览的节点列表
     */
    @Override
    public String[] getRoleNodeIds(String roleId) {
        if (StringUtil.isNull(roleId))
        {
            return ArrayUtil.emptyString;
        }
        try {
            TreeRole treeRole = super.load(TreeRole.class, "roleId", roleId, false);
            return StringUtil.split(treeRole.getNodeIds(), StringUtil.SEMICOLON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ArrayUtil.emptyString;
    }


    /**
     * @param nodeId 栏目id
     * @param roleId 角色id
     * @return 判断是否有权限浏览
     */
    @Override
    public boolean isRoleNodeId(String nodeId, String roleId) {
        TreeRole treeRole = super.load(TreeRole.class, "roleId", roleId, false);
        if (!StringUtil.isEmpty(organizeId) && treeRole.getOrganizeId().equals(organizeId)) {
            return treeRole.isInNodeId(nodeId);
        } else {
            return treeRole.isInNodeId(nodeId);
        }
    }

    //-------------------------------------------------------------------------------------------
    public static String getJson(List<TreeItem> treeItemList, String treeType, String[] selectArray, String[] checked, String namespace) {
        TreeItem rootNode = getRootTreeItem(treeItemList);

        JSONArray jsona = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", rootNode.getNodeId());
        jsonObject.put("icon", rootNode.getIcon());
        jsonObject.put("openIcon", rootNode.getOpenIcon());
        jsonObject.put("inputType", rootNode.getInputType());
        jsonObject.put("text", rootNode.getCaption());
        jsonObject.put("pid", rootNode.getParentNodeId());
        jsonObject.put("value", rootNode.getItemValue());
        if (treeType.contains("role")) {
            jsonObject.put("rolesCaption", rootNode.getRolesCaption());
            jsonObject.put("roleIds", rootNode.getRoleIds());
            jsonObject.put("manager", rootNode.getManager());
        }
        jsonObject.put("action", "");
        jsonObject.put("checked", false);
        jsona.put(jsonObject);


        // String treeName = getNamespace();
        for (TreeItem nNode : treeItemList) {
            jsonObject = new JSONObject();
            jsonObject.put("id", nNode.getNodeId());
            jsonObject.put("icon", nNode.getIcon());
            jsonObject.put("openIcon", nNode.getOpenIcon());
            jsonObject.put("inputType", nNode.getInputType());
            jsonObject.put("text", nNode.getCaption());
            jsonObject.put("pid", nNode.getParentNodeId());
            jsonObject.put("value", nNode.getItemValue());
            if (treeType.contains("role")) {
                jsonObject.put("rolesCaption", nNode.getRolesCaption());
                jsonObject.put("roleIds", nNode.getRoleIds());
                jsonObject.put("manager", nNode.getManager());
            }
            jsonObject.put("action", namespace + "Click('" + nNode.getNodeId() + "')");
            jsonObject.put("checked", ArrayUtil.inArray(selectArray, nNode.getNodeId(), true));
            if (ArrayUtil.isEmpty(checked)) {
                jsona.put(jsonObject);
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                jsona.put(jsonObject);
            }
        }
        return jsona.toString();
    }

    /**
     * 递归方式，得到现在流行的json格式
     * <pre>{@code
     *
     * {
     * id:"root",
     * title:"第一级1"，
     * children：[
     * {
     * id:"3",
     * title:"第二级1",
     * children：[
     * {
     * id:"6",
     * title:"第三级2",
     * children：[]
     * }
     * ]
     * },
     * {
     * id:"4",
     * title:"第二级2",
     * children：[
     * {
     * id:"5",
     * title:"第三级1",
     * children：[]
     * }
     * ]
     * }
     * ]}
     *
     * }</pre>
     * <p>
     * 递归方式，得到现在流行的json格式
     *
     * @param treeItemList 节点列表
     * @param treeType     数类型
     * @param selected     选择的
     * @param checked      勾选的
     * @param treeName     树名称
     * @return 递归方式，得到现在流行的json格式
     */
    public static JSONArray getJsonTree(List<TreeItem> treeItemList, String treeType, String[] selected, String[] checked, String treeName) {
        TreeItem rootNode = getRootTreeItem(treeItemList);
        return getChildrenJsonTree(treeItemList, rootNode.getNodeId(), treeType, selected, checked, treeName);
    }

    private static JSONArray getChildrenJsonTree(List<TreeItem> treeItemList, String nodeId, String treeType, String[] selectArray, String[] checked, String treeName) {
        JSONArray jsonResult = new JSONArray();
        List<TreeItem> itemList = getChildTreeItem(treeItemList, nodeId);
        for (TreeItem treeItem : itemList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", treeItem.getNodeId());
            jsonObject.put("icon", treeItem.getIcon());
            jsonObject.put("openIcon", treeItem.getOpenIcon());
            jsonObject.put("inputType", treeItem.getInputType());
            jsonObject.put("text", treeItem.getCaption());
            jsonObject.put("pid", treeItem.getParentNodeId());
            jsonObject.put("value", treeItem.getItemValue());
            if (treeType.contains("role")) {
                jsonObject.put("rolesCaption", treeItem.getRolesCaption());
                jsonObject.put("roleIds", treeItem.getRoleIds());
                jsonObject.put("manager", treeItem.getManager());
            }
            jsonObject.put("action", treeName + "Click('" + treeItem.getNodeId() + "')");
            jsonObject.put("checked", ArrayUtil.inArray(selectArray, treeItem.getNodeId(), true));
            if (ArrayUtil.isEmpty(checked)) {
                jsonResult.put(jsonObject);
            } else if (ArrayUtil.inArray(checked, treeItem.getNodeId(), true)) {
                jsonResult.put(jsonObject);
            } else {
                jsonObject.put("children", getChildrenJsonTree(treeItemList, treeItem.getNodeId(), treeType, selectArray, checked, treeName));
            }
        }
        return jsonResult;
    }

    @Override
    public void evictTree()
    {
        Criteria criteria = createCriteria(TreeItem.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId))
        {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        JSCacheManager.queryRemove(TreeItem.class, criteria.getDeleteListCacheKey());
    }
}
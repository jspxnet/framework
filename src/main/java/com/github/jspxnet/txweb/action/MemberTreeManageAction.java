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


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.txweb.table.MemberTree;
import com.github.jspxnet.txweb.view.TreeView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-24
 * Time: 15:23:06
 */
@Slf4j
@HttpMethod(caption = "用户树")
public class MemberTreeManageAction extends TreeView {

    public MemberTreeManageAction() {

    }

    /**
     *
     * @param uid 用户ID
     * @param nodeId 删除内容,删除条件,1:包含mid列表,并且是在自己管理的栏目中
     * @throws Exception 异常
     */
    @Operate(caption = "保存")
    public void save(@Param(caption = "uid") long uid,@Param(caption = "nodeId",max = 64) String[] nodeId,@Param(caption = "树ID") String treeId) throws Exception
    {
        if (uid < 1) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        String[] nodeIdArray = nodeId;
        nodeIdArray = treeItemDAO.addLimb(nodeIdArray);
        try {
            memberTreeDAO.deleteForUid(uid,treeId);
            if (!ArrayUtil.isEmpty(nodeIdArray))
            {
                for (String aCheckbox : nodeIdArray) {
                    if (StringUtil.isNull(aCheckbox)) {
                        continue;
                    }
                    MemberTree memberTree = new MemberTree();
                    memberTree.setNodeId(aCheckbox);
                    memberTree.setUid(uid);
                    memberTree.setTreeId(treeId);
                    memberTree.setNamespace(treeItemDAO.getNamespace());
                    IUserSession userSession = getUserSession();
                    if (userSession != null) {
                        memberTree.setPutName(userSession.getName());
                        memberTree.setPutUid(userSession.getUid());
                    }
                    if (memberTreeDAO.save(memberTree) > 0) {
                        setActionResult(SUCCESS);
                    }
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(nodeIdArray, StringUtil.COMMAS), e);
            addActionMessage(language.getLang(LanguageRes.saveFailure));
            setActionResult(ERROR);
        }
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            memberTreeDAO.evict(MemberTree.class);
            memberTreeDAO.flush();
        }
        put("member", memberTreeDAO.load(Member.class, uid));
        return super.execute();
    }
}
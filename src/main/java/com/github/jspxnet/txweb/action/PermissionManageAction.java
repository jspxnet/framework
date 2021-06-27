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
import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.model.param.RoleParam;
import com.github.jspxnet.txweb.support.DefaultTemplateAction;
import com.github.jspxnet.txweb.table.Member;
import com.github.jspxnet.txweb.table.MemberRole;
import com.github.jspxnet.txweb.model.vo.OperateVo;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.txweb.view.PermissionView;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-8
 * Time: 17:50:31
 *
 */
@Slf4j
@HttpMethod(caption = "角色权限")
public class PermissionManageAction extends PermissionView {
    public PermissionManageAction() {

    }

    @Operate(caption = "添加角色")
    public void addRole(@Param(caption="角色",required = true) RoleParam params) throws Exception {
        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IUserSession userSession = getUserSession();
        //这个是得到当前用户角色,容易混
        IRole userRole = getRole();
        if ( userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }

        Role role = BeanUtil.copy(params,Role.class);
        role.setPutUid(userSession.getUid());
        role.setPutName(userSession.getName());
        role.setNamespace(permissionDAO.getNamespace());
        //得到简拼begin
        role.setSpelling(ChineseUtil.getFullSpell(role.getName(), ""));
        //得到简拼end

        if (permissionDAO.save(role) > 0) {
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
            setActionLogContent(ObjectUtil.getJson(role));
            setActionResult(SUCCESS);
        }
    }

    @Operate(caption = "拷贝角色")
    public void copyRole(@Param(caption = "角色ID列表", required = true, message = "角色ID不能为空") String[] ids) throws Exception {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IUserSession userSession = getUserSession();
        //这个是得到当前用户角色,容易混
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }

        for (String id : ids) {
            Role role = permissionDAO.getRole(id);
            role.setName(role.getName() + "_copy");
            role.setNamespace(permissionDAO.getNamespace());
            role.setPutUid(userSession.getUid());
            role.setPutName(userSession.getName());
            role.setId(null);
            if (permissionDAO.save(role) > 0) {
                addActionMessage(language.getLang(LanguageRes.operationSuccess));
                setActionLogContent(role.getId() + ":" + role.getName());
            }
        }
    }

    @Operate(caption = "编辑角色")
    public void editRole(@Param(caption="角色",required = true,message = "参数不允许为空") RoleParam params) throws Exception {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }

        //这个是得到当前用户角色,容易混
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }
        IUserSession userSession = getUserSession();
        Role role = BeanUtil.copy(params,Role.class);
        role.setPutUid(userSession.getUid());
        role.setPutName(userSession.getName());
        role.setNamespace(permissionDAO.getNamespace());

        //得到简拼begin
        String py = ChineseUtil.getFullSpell(role.getName(), "");
        role.setSpelling(py);
        //得到简拼end

        if (permissionDAO.update(role) > 0) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess));
            setActionLogContent(ObjectUtil.getJson(role));
            setActionResult(SUCCESS);
        }
    }

    @Operate(caption = "删除角色")
    public void delRole(@Param(caption = "角色ID列表",  required = true, message = "角色ID不能为空") String[] ids)  {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            log.debug("delRole userRole:{}",ObjectUtil.toString(userRole));
            return;
        }
        if (permissionDAO.deleteRoles(ids)) {
            addActionMessage(language.getLang(LanguageRes.deleteSuccess));
            setActionLogContent(ArrayUtil.toString(ids, StringUtil.SEMICOLON));
        } else {
            addActionMessage(language.getLang(LanguageRes.deleteFailure));
        }
    }


    /**
     * 排序时间
     *
     * @param ids id列表
     */
    @Operate(caption = "提前角色")
    public void sortDate(@Param(caption = "角色ID列表",  required = true, message = "角色ID不能为空") String[] ids) {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }

        if (permissionDAO.updateSortDate(ids)) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
            setActionLogContent(ArrayUtil.toString(ids, StringUtil.SEMICOLON));
        } else {
            addActionMessage(language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "锁定")
    public void okCongealType(@Param(caption = "角色ID列表",  required = true, message = "角色ID不能为空") String[] ids) {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }

        if (permissionDAO.updateCongealType(ids, CongealEnumType.YES_CONGEAL.getValue())) {
            addActionMessage(language.getLang(LanguageRes.deleteSuccess));
            setActionLogContent(ArrayUtil.toString(ids, StringUtil.SEMICOLON));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.deleteFailure));
        }
    }

    @Operate(caption = "解锁")
    public void noCongealType(@Param(caption = "角色ID列表",  required = true, message = "角色ID不能为空") String[] ids) {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }
        if (permissionDAO.updateCongealType(ids, CongealEnumType.NO_CONGEAL.getValue())) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
            setActionLogContent(ArrayUtil.toString(ids, StringUtil.SEMICOLON));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }


    @Operate(caption = "设置权限")
    public void saveOperate() throws Exception {
        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IRole userRole = getRole();
        if (userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }
        Role role = permissionDAO.getRole(getString("id"));
        if (role == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }

        if (!role.getNamespace().equalsIgnoreCase(permissionDAO.getNamespace())) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameter));
            return;
        }
        role.setOperates(ArrayUtil.toString(getArray("oid", false), StringUtil.CRLF));
        if (permissionDAO.update(role, new String[]{"operates"}) > 0) {
            role = null;
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
            setActionLogContent(ObjectUtil.getJson(role));
        }
    }

    @Operate(caption = "设置权限",method="save/operate")
    public void saveOperate(@Param(caption = "角色ID",  required = true, message = "角色ID不能为空") String id,
                            @Param(caption = "操作列表", required = true, message = "操作ID列表") String[] oid) throws Exception {
        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }

        Role role = permissionDAO.getRole(id);
        if (role == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }

        if (!role.getNamespace().equalsIgnoreCase(permissionDAO.getNamespace())) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameter));
            return;
        }
        role.setOperates(ArrayUtil.toString(oid, StringUtil.CRLF));
        if (permissionDAO.update(role, new String[]{"operates"}) > 0) {
            role = null;
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
            setActionLogContent(ObjectUtil.getJson(role));
        }
    }

    @Operate(caption = "批量设置角色")
    public void memberRoles(@Param(caption = "用户id列表", required = true, message = "用户id列表不能为空") long[] uids,
                            @Param(caption = "角色ID", required = true, message = "角色ID不能为空") String roleId) throws Exception {

        if (isGuest()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            return;
        }
        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addActionMessage(language.getLang(LanguageRes.needManagePower));
            return;
        }

        Role role = permissionDAO.getRole(roleId);
        if (role == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameter));
            return;
        }

        IUserSession userSession = getUserSession();
        if (userSession.getUid() != 10000 && userRole.getUserType() < UserEnumType.ChenYuan.getValue() && role.getUserType() > userRole.getUserType()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needManagePower));
            return;
        }

        for (long uid : uids) {
            List<MemberRole> memberRoleList = permissionDAO.getMemberRoles(uid,false);
            if (memberRoleList.size() == 1) {
                //替换方式
                MemberRole memberRole = memberRoleList.get(0);
                memberRole.setRoleId(roleId);
                memberRole.setUid(uid);
                memberRole.setPutUid(userSession.getUid());
                memberRole.setPutName(userSession.getName());
                memberRole.setNamespace(permissionDAO.getNamespace());
                if (permissionDAO.update(memberRole) > 0) {
                    addActionMessage(language.getLang(LanguageRes.configSuccessForEdit) + "," + uid);
                }
            } else {
                permissionDAO.deleteAll(memberRoleList);
                MemberRole memberRole = new MemberRole();
                memberRole.setRoleId(roleId);
                memberRole.setUid(uid);
                memberRole.setPutUid(userSession.getUid());
                memberRole.setPutName(userSession.getName());
                memberRole.setNamespace(permissionDAO.getNamespace());
                if (permissionDAO.save(memberRole) > 0) {
                    addActionMessage(language.getLang(LanguageRes.configSuccessForAdd) + "," + uid);
                }
            }
            memberRoleList.clear();
        }
        setActionLogContent(ArrayUtil.toString(uids, StringUtil.SEMICOLON));
    }

    @Operate(caption = "批量设置多角色")
    public void manyMemberRoles(@Param(caption = "用户id列表", required = true, message = "用户id列表不能为空") long[] uids,
                                @Param(caption = "角色ID列表", required = true, message = "角色ID列表不能为空") String[] roleIds,
                                @Param(caption = "机构ID", required = true, message = "机构ID不能为空") String organizeId) throws Exception {

        List<Role> roles = permissionDAO.load(Role.class,roleIds);
        IUserSession userSession = getUserSession();
        Map<String,Role> roleMap = new HashMap<>();
        for (Role role:roles)
        {
            roleMap.put(role.getId(),role);
        }
        for (long uid : uids) {
            List<MemberRole> memberRoleList = permissionDAO.getMemberRoles(uid,false);
            permissionDAO.deleteAll(memberRoleList);
            List<MemberRole> memberRoleSaveList = new ArrayList<>();
            for (String roleId:roleIds)
            {
                if (StringUtil.isNull(roleId))
                {
                    continue;
                }
                Role role = roleMap.get(roleId);
                if (role == null||StringUtil.isNull(role.getId()))
                {
                    continue;
                }
                MemberRole memberRole = new MemberRole();
                memberRole.setRoleId(role.getId());
                memberRole.setUid(uid);
                memberRole.setPutUid(userSession.getUid());
                memberRole.setPutName(userSession.getName());
                memberRole.setNamespace(permissionDAO.getNamespace());
                memberRole.setOrganizeId(organizeId);
                memberRoleSaveList.add(memberRole);
            }
            if (permissionDAO.save(memberRoleSaveList) > 0) {
                addActionMessage(language.getLang(LanguageRes.configSuccessForAdd) + "," + uid);
            }
            memberRoleList.clear();
        }
        setActionLogContent(ArrayUtil.toString(uids, StringUtil.SEMICOLON) + "\r\n" + ObjectUtil.toString(roles));
    }




    //更具默认配置分配权限
    @Operate(caption = "重置权限")
    public void reset() throws Exception {

        IRole userRole = getRole();
        if (userRole == null || userRole.getUserType() < UserEnumType.MANAGER.getValue()) {
            addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.needManagePower));
            log.info("userRole:{}",ObjectUtil.toString(userRole));
            return;
        }

        String[] globalOperates = null;
        String[] noneOperates = null;
        String[] userOperates = null;
        String[] manageOperates = null;
        List<OperateVo> operates = getOperateList();
        for (OperateVo op : operates) {
            if (op == null) {
                continue;
            }
            if (!op.getNamespace().contains("/")) {
                userOperates = ArrayUtil.add(userOperates, op.getActionMethodId());
                if (TXWebUtil.defaultExecute.equals(op.getClassMethod()) || "login".equalsIgnoreCase(op.getClassMethod())
                        || "exit".equalsIgnoreCase(op.getClassMethod())
                        || DefaultTemplateAction.class.getName().equalsIgnoreCase(op.getClassName())
                        || op.getNamespace().endsWith("validator") ||
                        op.getNamespace().endsWith("juweb/picture") || op.getNamespace().endsWith("juweb/download") || op.getNamespace().endsWith("jcms/download")) {
                    noneOperates = ArrayUtil.add(noneOperates, op.getActionMethodId());
                }
            }
            if (!op.getNamespace().contains("jcms/htdoc")) {
                userOperates = ArrayUtil.add(userOperates, op.getActionMethodId());
            }
            if (!op.getNamespace().contains("/validator")) {
                userOperates = ArrayUtil.add(userOperates, op.getActionMethodId());
            }
            if (!op.getNamespace().contains("/help")) {
                userOperates = ArrayUtil.add(userOperates, op.getActionMethodId());
            }

            if (TXWeb.global.equalsIgnoreCase(op.getNamespace()) || StringUtil.ASTERISK.equals(op.getActionName())) {
                globalOperates = ArrayUtil.add(globalOperates, op.getActionMethodId());
                noneOperates = ArrayUtil.add(noneOperates, op.getActionMethodId());
            }
            manageOperates = ArrayUtil.add(manageOperates, op.getActionMethodId());
        }

        List<Role> roles = permissionDAO.getRoleList();
        if (!roles.isEmpty()) {
            for (Role role : roles) {
                if (role.getUserType() == UserEnumType.NONE.getValue()) {
                    ArrayUtil.sort(noneOperates, "/", true);
                    role.setOperates(ArrayUtil.toString(noneOperates, StringUtil.CRLF));
                } else if (role.getUserType() < UserEnumType.MANAGER.getValue()) {
                    ArrayUtil.sort(userOperates, "/", true);
                    role.setOperates(ArrayUtil.toString(userOperates, StringUtil.CRLF));
                } else {
                    ArrayUtil.sort(manageOperates, "/", true);
                    role.setOperates(ArrayUtil.toString(manageOperates, StringUtil.CRLF));
                }
                permissionDAO.update(role, new String[]{"operates"});
            }
            roles.clear();
            addActionMessage(language.getLang(LanguageRes.powerResetSuccess));
        } else {

            Role role = new Role();
            role.setUserType(UserEnumType.NONE.getValue());
            ArrayUtil.sort(noneOperates, "/", true);
            role.setOperates(ArrayUtil.toString(noneOperates, StringUtil.CRLF));
            role.setImages("/share/pimg/usertype/0001.gif");
            role.setName("游客");
            //得到简拼begin
            String py = ChineseUtil.getFullSpell(role.getName(), "");
            role.setSpelling(py);
            //得到简拼end
            role.setUseUpload(YesNoEnumType.NO.getValue());
            role.setNamespace(permissionDAO.getNamespace());
            permissionDAO.save(role);

            role = new Role();
            role.setUserType(UserEnumType.USER.getValue());
            role.setImages("/share/pimg/usertype/0002.gif");
            ArrayUtil.sort(userOperates, "/", true);
            role.setOperates(ArrayUtil.toString(userOperates, StringUtil.CRLF));
            role.setName("会员");
            //得到简拼begin

            role.setSpelling(ChineseUtil.getFullSpell(role.getName(), ""));
            //得到简拼end
            role.setUseUpload(YesNoEnumType.YES.getValue());
            role.setUploadFileTypes("pdf;doc;docx;ppt;pptx;xls;xlsx;rar;zip;7z;rtf;wps;mht;ett;jpg;png;gif;bmp;swf;avi;flv;txt;mp3;mp4;mkv;one;tiff;gd;hlp;chm");
            role.setNamespace(permissionDAO.getNamespace());
            permissionDAO.save(role);


            role = new Role();
            role.setUserType(UserEnumType.INTENDANT.getValue());
            ArrayUtil.sort(manageOperates, "/", true);
            role.setOperates(ArrayUtil.toString(manageOperates, StringUtil.CRLF));
            role.setImages("/share/pimg/usertype/0005.gif");
            role.setName("操作人员");
            //得到简拼begin

            role.setSpelling(ChineseUtil.getFullSpell(role.getName()," "));

            //得到简拼end
            role.setUseUpload(YesNoEnumType.YES.getValue());
            role.setUploadFileTypes(StringUtil.ASTERISK);
            role.setNamespace(permissionDAO.getNamespace());
            permissionDAO.save(role);
            addActionMessage(language.getLang(LanguageRes.operationSuccess));


            role = new Role();
            role.setUserType(UserEnumType.MANAGER.getValue());
            ArrayUtil.sort(manageOperates, "/", true);
            role.setOperates(ArrayUtil.toString(manageOperates, StringUtil.CRLF));
            role.setImages("/share/pimg/usertype/0006.gif");
            role.setName("管理员");
            //得到简拼begin
            role.setSpelling(ChineseUtil.getFullSpell(role.getName(), ""));
            role.setSpelling(py);

            //得到简拼end
            role.setUseUpload(YesNoEnumType.YES.getValue());
            role.setUploadFileTypes(StringUtil.ASTERISK);
            role.setNamespace(permissionDAO.getNamespace());
            permissionDAO.save(role);
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        }
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            permissionDAO.evict(MemberRole.class);
            permissionDAO.evict(Role.class);
            permissionDAO.evict(Member.class);
        }
        return super.execute();
    }
}
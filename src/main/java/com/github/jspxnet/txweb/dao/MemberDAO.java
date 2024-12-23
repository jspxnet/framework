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
import com.github.jspxnet.txweb.IMember;
import com.github.jspxnet.txweb.table.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-11-12
 * Time: 14:21:24
 */
public interface MemberDAO extends SoberSupport {

    List<Member> getMemberForExpression(String select, TreeItemDAO treeItemDAO, long uid);

    int updateToken(long uid) throws Exception;

    MemberDept getMemberDept(long uid);

    List<MemberDept> getMemberDeptList(long uid, String sort, int page, int count);

    List<MemberCourt> getMemberCourtList(long uid, String sort, int page, int count);

    MemberCourt getMemberCourt(long uid);

    int getMemberCourtCount(long uid);

    int getMemberDeptCount(long uid);

    int updateMemberDeptDefault(long uid, long id) throws Exception;

    int updateMemberCourtDefault(long uid, long id) throws Exception;

    int getForMailCount(String mail);

    int getForNameCount(String name);

    boolean getIpPrompt(long uid);

    int getMemberCount();

    int getToDayMember();

    String createName(String name);

    boolean checkUserName(String loginId, long uid);

    Member getMember(String loginType, String loginId);

    Member getMemberV2(String loginType, String loginId);

    Member getForPhone(String name);

    Member getForId(long uid);

    Member getLastMember();

    Member getForName(String name);

    Member getForMail(String mail);

    Member getForKid(String kid);


    Member getParentMember(long childManId);

    OAuthOpenId getOAuthOpenId(String enumType, String openId);

    OAuthOpenId getOAuthOpenId(String namespace, long uid);

    boolean congeal(long uid) throws Exception;

    boolean haveUser(long id, int congeal);

    List<Member> getChildList(String[] field, String[] find, long pid, String term, String sortString, int page, int count, boolean load);

    long getChildCount(String[] field, String[] find, long pid, String term) throws Exception;

    List<Member> getDepartmentMember(TreeItemDAO treeItemDAO, IMember member) throws SQLException;

    List<Member> getDepartmentMember(TreeItemDAO treeItemDAO, String departmentId) throws SQLException;

    //------------------------------------------------------------------------------------------------------------------
    UserSession getUserSession(long uid) throws Exception;

    UserSession getUserSession(String sessionId);

    int getOnlineCount(String term);

    List<UserSession> getOnlineList(String term, String sort, int page, int count, boolean load);

    boolean deleteSession(String sessionId, long uid);

    boolean deleteOvertimeSession(long overtime);

    // boolean deleteAllSession() throws Exception;
    boolean isOnline(long uid);

    boolean isOnline(String sessionId);

    Properties getFtpAccount() throws Exception;

    int getForPhoneCount(String phone);

    //------------------------------------------------------------------------------------------------------------------
    List<Member> getList(String[] field, String[] find, String[] departmentId, String term, String sortString, int page, int count, boolean load);

    long getCount(String[] field, String[] find, String[] departmentId, String term);

    List<Member> getMemberListForRole(String roleId, int page, int count) throws Exception;

    int getMemberListForRoleCount(String roleId) throws Exception;

    //------------------------------------------------------------------------------------------------------------------
    //用户机构设置 begin

    List<Member> getMemberChildList(long memberId, String organizeId, String namespace, int page, int count);

    int getMemberChildCount(long memberId, String organizeId, String namespace) throws Exception;

    List<MemberSpace> getMemberSpaceList(String namespace, int page, int count);

    MemberDetails getMemberDetails(IMember member) throws Exception;

    MemberSpace getMemberSpace(long childId, String organizeId, String namespace) throws Exception;

    boolean isInMemberSpace(long uid, String organizeId, String namespace);

    List<Long> getIsOnline(List<Long> uidList);
    //用户机构设置 end
    //------------------------------------------------------------------------------------------------------------------

    boolean deleteOrganizeForMemberSpace(String organizeId, String namespace);
}
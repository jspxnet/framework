package com.github.jspxnet.txweb.dao.impl;

/*
  Created by ChenYuan on 2016/10/24.
 */

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.LoginField;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.SqlMapClient;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.expression.InExpression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.IMember;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.dao.TreeItemDAO;
import com.github.jspxnet.txweb.table.*;
import com.github.jspxnet.txweb.util.MemberUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-6-1
 * Time: 17:46:45
 * <p>
 * 将userSession 合并为一个dao处理
 *
 */
@Slf4j
public class MemberDAOImpl extends JdbcOperations implements MemberDAO {

    public MemberDAOImpl() {

    }

    @Override
    public MemberDept getMemberDept(long uid) {
        return (MemberDept) createCriteria(MemberDept.class).add(Expression.eq("uid", uid)).add(Expression.eq("def", YesNoEnumType.YES.getValue())).objectUniqueResult(false);
    }

    /**
     * 得到部门列表
     *
     * @param uid   用户id
     * @param sort  排序
     * @param page  页数
     * @param count 一页行数
     * @return 用户列表
     */
    @Override
    public List<MemberDept> getMemberDeptList(long uid, String sort, int page, int count) {
        Criteria criteria = createCriteria(MemberDept.class).add(Expression.eq("uid", uid));
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(false);
    }

    /**
     * 更新token 确保关键数据没有被修改
     *
     * @param uid 用户id
     * @return 新token 确保关键数据没有被修改
     */
    @Override
    public int updateToken(long uid) {
        if (uid <= 0) {
            return -2;
        }
        Member member = super.get(Member.class, uid);
        if (member == null) {
            return -1;
        }
        TableModels soberTableMember = getSoberTable(Member.class);
        String token = MemberUtil.builderToken(member);
        return super.update("UPDATE " + soberTableMember.getName() + " SET token=?,version=version+1 WHERE id=? AND version=?", new Object[]{token, member.getId(), member.getVersion()});
    }

    /**
     * @param uid 用户id
     * @return 得到部门数量
     */
    @Override
    public int getMemberDeptCount(long uid) {
        Criteria criteria = createCriteria(MemberDept.class).add(Expression.eq("uid", uid));
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     * 更新用户默认所属部门
     *
     * @param uid 用户id
     * @param id  id
     * @return 是否成功
     */
    @Override
    public int updateMemberDeptDefault(long uid, long id) {

        Map<String, Object> valueMap = new HashMap<>();
        TableModels soberTable = getSoberTable(MemberDept.class);
        valueMap.put("memberDeptTable", soberTable.getName());
        valueMap.put("uid", uid);
        valueMap.put("id", id);
        String sql1 = EnvFactory.getPlaceholder().processTemplate(valueMap, "UPDATE ${memberDeptTable} SET def=0 WHERE uid=${uid}");
        super.update(sql1);
        String sql2 = EnvFactory.getPlaceholder().processTemplate(valueMap, "UPDATE ${memberDeptTable} SET def=1 WHERE id=${id} AND uid=${uid}");
        return super.update(sql2);
    }

    /**
     * @param uid   用户id
     * @param sort  排序方式
     * @param page  页数
     * @param count 数量AuditingEnumType
     * @return 得到列表
     */
    @Override
    public List<MemberCourt> getMemberCourtList(long uid, String sort, int page, int count) {
        Criteria criteria = createCriteria(MemberCourt.class).add(Expression.eq("uid", uid));
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(false);
    }

    /**
     * @param uid 用户id
     * @return 得到用户所在城市
     */
    @Override
    public int getMemberCourtCount(long uid) {
        Criteria criteria = createCriteria(MemberCourt.class).add(Expression.eq("uid", uid));
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     * @param uid 用户id
     * @return 得到用户的所在小区，注意得到的是设置为默认的那个
     */
    @Override
    public MemberCourt getMemberCourt(long uid) {
        return (MemberCourt) createCriteria(MemberCourt.class).add(Expression.eq("uid", uid)).add(Expression.eq("def", YesNoEnumType.YES.getValue())).objectUniqueResult(false);
    }

    /**
     * @param uid 用户id
     * @param id  id
     * @return 更新部门所在城市
     */
    @Override
    public int updateMemberCourtDefault(long uid, long id) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("memberCourtTable", getTableName(MemberCourt.class));
        valueMap.put("uid", uid);
        valueMap.put("id", id);
        String sql1 = EnvFactory.getPlaceholder().processTemplate(valueMap, "UPDATE ${memberCourtTable} SET def=0 WHERE uid=${uid}");
        super.update(sql1);
        String sql2 = EnvFactory.getPlaceholder().processTemplate(valueMap, "UPDATE ${memberCourtTable} SET def=1 WHERE id=${id} AND uid=${uid}");
        return super.update(sql2);
    }

    /**
     * @param treeItemDAO 部门树结构
     * @param member      当前用户
     * @return 得到部门主管, 采用两种方式  1:直接通过pid 查找;2:通过部门树结构
     */
    @Override

    public List<Member> getDepartmentMember(TreeItemDAO treeItemDAO, IMember member) {
        List<Member> result = new ArrayList<>();
        if (member == null) {
            return result;
        }
        List<MemberDept> list = createCriteria(MemberDept.class).add(Expression.eq("uid", member.getId())).list(true);
        for (MemberDept memberDept : list) {
            result.addAll(getDepartmentMember(treeItemDAO, memberDept.getDepartmentId()));
        }
        return result;
    }

    /**
     * @param treeItemDAO  部门树结构
     * @param departmentId 部门ID
     * @return 得到部门主管, 采用两种方式  1:直接通过pid 查找;2:通过部门树结构
     */
    @Override
    public List<Member> getDepartmentMember(TreeItemDAO treeItemDAO, String departmentId) {
        List<Member> result = new ArrayList<>();
        TreeItem treeItem = treeItemDAO.getTreeItem(departmentId);
        if (treeItem == null) {
            return result;
        }
        String manager = treeItem.getManager();
        if (StringUtil.isNull(manager)) {
            return result;
        }
        String[] uidArray = StringUtil.split(StringUtil.convertSemicolon(manager), StringUtil.SEMICOLON);
        for (String uidStr : uidArray) {
            if (!StringUtil.hasLength(uidStr)) {
                continue;
            }
            long uid;
            if (uidStr.contains(":")) {
                uid = StringUtil.toLong(StringUtil.getNumber(StringUtil.substringBetween(uidStr, "[", ":")));
            } else {
                uid = StringUtil.toLong(uidStr);
            }
            result.add(super.get(Member.class, uid, false));
        }
        return result;
    }

    /**
     * @param uid 用户id
     * @return 得到用户session
     */
    @Override
    public UserSession getUserSession(long uid) {
        return super.load(UserSession.class, "uid", uid, true);
    }

    /**
     * @return 得到最后注册的用户
     */
    @Override
    public Member getLastMember() {
        return (Member) createCriteria(Member.class).addOrder(Order.desc("createDate")).objectUniqueResult(false);
    }

    /**
     * @return 得到今日注册的用户数量
     */
    @Override
    public int getToDayMember() {
        return createCriteria(Member.class).add(Expression.gt("createDate", DateUtil.getStartDateTime(new Date()))).setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     * @return 得到总用户数量
     */
    @Override
    public int getMemberCount() {
        return createCriteria(Member.class).setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     * @param uid 用户ID
     * @return 通过用户id得到用户
     */
    @Override
    public Member getForId(long uid) {
        if (uid <= 0) {
            return null;
        }
        return super.get(Member.class, uid, true);
    }

    /**
     * @param name 用户名称
     * @return 通过用户名称得到用户
     */
    @Override
    public Member getForName(String name) {
        if (StringUtil.isNull(name)) {
            return null;
        }
        return super.get(Member.class, "name", name, true);
    }

    /**
     * @param phone 用户电话号码
     * @return 通过用户名称得到用户
     */
    @Override
    public Member getForPhone(String phone) {
        if (StringUtil.isNull(phone)) {
            return null;
        }
        return super.get(Member.class, "phone", phone, true);
    }

    /**
     * @param mail 邮箱
     * @return 通过邮箱得到用户
     */
    @Override
    public Member getForMail(String mail) {
        return createCriteria(Member.class).add(Expression.eq("mail", mail)).objectUniqueResult(true);
    }


    /**
     * @param kid 卡号
     * @return 通过邮箱得到用户
     */
    @Override
    public Member getForKid(String kid) {
        return createCriteria(Member.class).add(Expression.eq("kid", kid)).objectUniqueResult(true);
    }

    /**
     * @param name 用户名称
     * @return 创建一个不会重复的用户名
     */
    @Override
    public String createName(String name) {
        if (StringUtil.isNull(name)) {
            return null;
        }
        int num = createCriteria(Member.class).add(Expression.eq("name", name)).setProjection(Projections.rowCount()).intUniqueResult();
        if (num == 0) {
            return name;
        }
        return name + (num + 1);
    }

    @Override
    public boolean checkUserName(String loginId, long uid) {
        return createCriteria(Member.class).add(Expression.eq("name", loginId)).add(Expression.ne("id", uid)).setProjection(Projections.rowCount()).booleanUniqueResult();
    }

    public static String getLoginIdType(String loginId) {
        if (loginId == null) {
            return StringUtil.empty;
        }
        if (ValidUtil.isMobile(loginId)) {
            return LoginField.Phone;
        } else if (ValidUtil.isMail(loginId)) {
            return LoginField.Mail;
        } else if (ValidUtil.isGoodName(loginId)) {
            return LoginField.Name;
        } else if (ValidUtil.isNumber(loginId) && loginId.length() == 16) {
            return LoginField.Kid;
        } else if (ValidUtil.isNumber(loginId) && loginId.length() < 10) {
            return LoginField.UID;
        }
        return LoginField.UID;
    }

    @Override
    public Member getMember(String loginType, String loginId) {
        Member member;
        if ((LoginField.ID.equalsIgnoreCase(loginType) || LoginField.UID.equalsIgnoreCase(loginType)) && ValidUtil.isNumber(loginId)) {
            member = getForId(StringUtil.toLong(loginId));
        } else if (LoginField.Phone.equalsIgnoreCase(loginType)) {
            member = getForPhone(loginId);
        } else if (LoginField.Name.equalsIgnoreCase(loginType)) {
            member = getForName(loginId);
        } else if (LoginField.Mail.equalsIgnoreCase(loginType)) {
            member = getForMail(loginId);
        } else if (LoginField.Kid.equalsIgnoreCase(loginType)) {
            member = getForKid(loginId);
        } else {
            member = getForName(loginId);
        }
        return member;
    }

    /**
     * 第三方登陆信息查询
     *
     * @param namespace 命名空间，qq，weiXin这些
     * @param openId    微信openid
     * @return 得到保存的信息
     */
    @Override
    public OAuthOpenId getOAuthOpenId(String namespace, String openId) {
        return createCriteria(OAuthOpenId.class).add(Expression.eq("openId", openId)).add(Expression.eq("namespace", namespace)).objectUniqueResult(true);
    }

    /**
     * @param namespace 命名空间，qq，weiXin这些
     * @param uid       用户id
     * @return 满足得到 accessToken 等的调用要求
     */
    @Override
    public OAuthOpenId getOAuthOpenId(String namespace, long uid) {
        return createCriteria(OAuthOpenId.class).add(Expression.eq("uid", uid)).add(Expression.eq("namespace", namespace)).objectUniqueResult(true);

    }

    /**
     * @param name 用户名称
     * @return 得到同名称的用户数量
     */
    @Override
    public int getForNameCount(String name) {
        return createCriteria(Member.class).add(Expression.eq("name", name)).setProjection(Projections.rowCount()).intUniqueResult();
    }


    /**
     * @param uid 用户ID
     * @return ip范围限制
     */
    @Override

    public boolean getIpPrompt(long uid) {
        List<LoginLog> list = createCriteria(LoginLog.class).add(Expression.eq("putUid", uid)).addOrder(Order.desc("createDate")).setTotalCount(2).setCurrentPage(1).list(true);
        if (list.size() <= 1) {
            return false;
        }
        LoginLog loginLog0 = list.get(0);
        LoginLog loginLog1 = list.get(1);
        String a = StringUtil.substringBeforeLast(loginLog0.getIp(), ".");
        String b = StringUtil.substringBeforeLast(loginLog1.getIp(), ".");
        return !a.equalsIgnoreCase(b);
    }

    /**
     * @param mail 邮箱
     * @return 得到邮箱数量
     */
    @Override
    public int getForMailCount(String mail) {
        return createCriteria(Member.class).add(Expression.eq("mail", mail)).setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public int getForPhoneCount(String phone) {
        return createCriteria(Member.class).add(Expression.eq("phone", phone)).setProjection(Projections.rowCount()).intUniqueResult();
    }
    /**
     * @param find       关键字
     * @param term       条件
     * @param sortString 排序
     * @param page       页
     * @param count      行
     * @return 得到用户列表
     */
    @Override
    public List<Member> getList(String[] field, String[] find, String[] departmentId, String term, String sortString, int page, int count, boolean load) {
        String sort;
        if (StringUtil.isNull(sortString)) {
            sort = "id:A;createDate:D";
        } else {
            sort = sortString;
        }
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field) && ("roleId".equalsIgnoreCase(field[0]) && !StringUtil.isNull(find[0]))) {
            return getMemberListForRole(find[0], page, count);
        }
        Criteria criteria = createCriteria(Member.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }

        if (!ArrayUtil.isEmpty(departmentId) && !StringUtil.isNull(departmentId[0])) {
            criteria = criteria.add(Expression.in("departmentId", departmentId));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        //这里是为了优化SQL，提高速度
        return criteria.setCurrentPage(page).setTotalCount(count).list(load);
    }

    /**
     * @param uidList 用户id列表
     * @return 返回在线的用户id列表
     */
    @Override
    public List<Long> getIsOnline(List<Long> uidList) {
        if (ObjectUtil.isEmpty(uidList)) {
            return new ArrayList<>(0);
        }
        Map<String, Object> valueMap = new HashMap<>();
        TableModels userSessionTable = getSoberTable(UserSession.class);
        valueMap.put("userSessionTable", userSessionTable.getName());
        valueMap.put("inSql", new InExpression("uid", uidList.toArray()).toString());
        String sql = EnvFactory.getPlaceholder().processTemplate(valueMap, "SELECT uid FROM ${userSessionTable} WHERE ${inSql}");
        String cacheKey = EncryptUtil.getMd5(sql);
        //动态类不要放在redis里边,有隐患

        //取出cache

        List<Long> result = (List<Long>) JSCacheManager.get(UserSession.class, cacheKey);
        if (!ObjectUtil.isEmpty(result))
        {
            return result;
        }
        List<?> queryList =  prepareQuery(sql, null);
        if (!ObjectUtil.isEmpty(queryList))
        {
            result = BeanUtil.copyFieldList(queryList,"uid");
        }
        if (!ObjectUtil.isEmpty(result))
        {
            JSCacheManager.put(UserSession.class, cacheKey,result,10);
        }
        if (ObjectUtil.isEmpty(result))
        {
            return   new ArrayList<>(0);
        }
        return result;
    }

    /**
     * @param field 字段
     * @param find  关键字
     * @param term  条件
     * @return 得到用户数量
     */
    @Override
    public long getCount(String[] field, String[] find, String[] departmentId, String term)  {
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field) && ("roleId".equalsIgnoreCase(field[0]) && !StringUtil.isNull(find[0]))) {
            return getMemberListForRoleCount(find[0]);
        }
        Criteria criteria = createCriteria(Member.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!ArrayUtil.isEmpty(departmentId) && !StringUtil.isNull(departmentId[0])) {
            criteria = criteria.add(Expression.in("departmentId", departmentId));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }

    /**
     * @param find       查询
     * @param pid        父ID
     * @param term       条件
     * @param sortString 排序
     * @param page       页数
     * @param count      显示行数
     * @return 得到子用户列表
     */
    @Override

    public List<Member> getChildList(String[] field, String[] find, long pid, String term, String sortString, int page, int count, boolean load) {
        String sort;
        if (StringUtil.isNull(sortString)) {
            sort = "sortType:D;createDate:D";
        } else {
            sort = sortString;
        }
        Criteria criteria = createCriteria(Member.class).add(Expression.eq("pid", pid));
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(load);
    }

    /**
     * @param find 查询
     * @param pid  父id
     * @param term 条件
     * @return 得到子用户数量
     */
    @Override
    public long getChildCount(String[] field, String[] find, long pid, String term) {

        Criteria criteria = createCriteria(Member.class).add(Expression.eq("pid", pid));
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }


    /**
     * @param uid         用户id
     * @param congealType 冻结
     * @return 判断冻结列表中是否存在该用户
     */
    @Override
    public boolean haveUser(long uid, int congealType) {
        Criteria criteria = createCriteria(Member.class).add(Expression.eq("id", uid));
        if (congealType > -1) {
            criteria = criteria.add(Expression.eq("congealType", congealType));
        }
        return criteria.setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    /**
     * @param uid 用户id
     * @return 得到父用户
     */
    @Override
    public Member getParentMember(long uid) {
        Member member = get(Member.class, uid);
        if (member == null) {
            return null;
        }
        return get(Member.class, member.getPid());
    }

    /**
     * @param uid 用户id
     * @return boolean  激活冻结用户
     */
    @Override
    public boolean congeal(long uid) throws Exception {
        if (uid <= 0) {
            return false;
        }
        Member member = get(Member.class, uid);
        if (member == null) {
            return false;
        }
        if (CongealEnumType.NO_CONGEAL.getValue() == member.getCongealType()) {
            member.setCongealType(CongealEnumType.YES_CONGEAL.getValue());
        } else {
            member.setCongealType(CongealEnumType.NO_CONGEAL.getValue());
        }
        member.setCongealDate(new Date());
        return super.update(member, new String[]{"congealType", "congealDate"}) > 0;
    }
    //----------------------------------

    /**
     * @param sessionId ,不使用id是为了避免mysql在高并发下死锁
     * @return 得到 UserSession
     */
    @Override
    public UserSession getUserSession(String sessionId) {
        return super.load(UserSession.class, sessionId);
    }

    /**
     * 删除超时
     *
     * @return 是否成功
     */
    @Override
    public boolean deleteOvertimeSession(long overtime) {
        long delTime = System.currentTimeMillis() - overtime;
        if (tableExists(UserSession.class)) {
            return createCriteria(UserSession.class).add(Expression.lt("lastRequestTime", delTime)).delete(false) > 0;
        }
        return false;
    }

    /**
     * @param sessionId sessionId 用户sessionId
     * @param uid       用户ID
     * @return 删除指定, 一次删除更有效
     */
    @Override
    public boolean deleteSession(String sessionId, long uid) {

        if (uid == 0 && sessionId != null) {
            evictLoad(UserSession.class, "id", sessionId);
            return createCriteria(UserSession.class).add(Expression.eq("id", sessionId)).delete(false) > 0;
        } else if (uid > 0 && StringUtil.isNull(sessionId)) {
            evictLoad(UserSession.class, "uid", uid);
            return createCriteria(UserSession.class).add(Expression.eq("uid", uid)).delete(false) > 0;
        } else {
            evictLoad(UserSession.class, "id", sessionId);
            evictLoad(UserSession.class, "uid", uid);
            return createCriteria(UserSession.class).add(Expression.or(Expression.eq("id", sessionId), Expression.eq("uid", uid))).delete(false) > 0;
        }
    }

    /**
     * 判断用户是否在线
     *
     * @param uid 用户id
     * @return 是否在线
     */
    @Override
    public boolean isOnline(long uid) {
        return createCriteria(UserSession.class).add(Expression.eq("uid", uid)).setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    /**
     * @param sessionId sessionID号
     * @return 判断是否存在
     */
    @Override
    public boolean isOnline(String sessionId) {
        return createCriteria(UserSession.class).add(Expression.eq("id", sessionId)).setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    /**
     * 在线用户列表
     *
     * @param term  条件
     * @param sort  排序
     * @param page  页数
     * @param count 行数
     * @param load  载入映射
     * @return 列表
     */
    @Override

    public List<UserSession> getOnlineList(String term, String sort, int page, int count, boolean load) {
        if (StringUtil.isNull(sort)) {
            sort = "lastRequestTime:D";
        }
        Criteria criteria = SSqlExpression.getTermExpression(createCriteria(UserSession.class), term);
        return SSqlExpression.getSortOrder(criteria, sort).setCurrentPage(page).setTotalCount(count).list(load);
    }

    /**
     * 查询带系统的数量
     *
     * @param term 条件
     * @return 总行数
     */
    @Override
    public int getOnlineCount(String term) {
        return SSqlExpression.getTermExpression(createCriteria(UserSession.class), term).setProjection(Projections.rowCount()).intUniqueResult();
    }

    /**
     * @param roleId 角色
     * @param page   页数
     * @param count  一页总算
     * @return 角色用户
     */
    @Override
    public List<Member> getMemberListForRole(String roleId, int page, int count) {
        SqlMapClient sqlMapClient = buildSqlMap();
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("memberTable", getTableName(Member.class));
        valueMap.put("memberRoleTable", getTableName(MemberRole.class));
        valueMap.put("roleId", roleId);
        return sqlMapClient.query(Environment.Global, getClassMethodName(), valueMap, page, count, true, false);
    }

    /**
     * @param roleId 角色id
     * @return 得到会员数量
     */
    @Override
    public int getMemberListForRoleCount(String roleId) {
        SqlMapClient sqlMapClient = buildSqlMap();
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("memberTable", getTableName(Member.class));
        valueMap.put("memberRoleTable", getTableName(MemberRole.class));
        valueMap.put("roleId", roleId);
        return ObjectUtil.toInt(sqlMapClient.getUniqueResult(Environment.Global, getClassMethodName(), valueMap));
    }


    /**
     * @param member 用户
     * @return 得到用户的详细资料
     */
    @Override
    public MemberDetails getMemberDetails(IMember member) {
        if (member == null) {
            return new MemberDetails();
        }
        return super.load(MemberDetails.class, member.getId());
    }

    /**
     * @return 绑定角色，分配FTP个人空间
     */
    @Override
    public Properties getFtpAccount() {
        List<Role> roles = createCriteria(Role.class).add(Expression.eq("namespace", "user")).addOrder(Order.desc("sortDate")).addOrder(Order.desc("userType")).list(false);
        Properties p = new Properties();
        for (Role role : roles) {
            if (role.getUserType() < UserEnumType.USER.getValue()) {
                continue;
            }
            if (role.getUseUpload() != YesNoEnumType.YES.getValue()) {
                continue;
            }
            if (!FileUtil.isDirectory(role.getUploadFolder())) {
                continue;
            }
            List<Member> list = getMemberListForRole(role.getId(), 1, 2000);
            try {
                for (Member member : list) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(role.getPermission()).append(",");
                    String pass = StringUtil.replace(member.getPassword(), ",", "-");
                    sb.append(pass).append(",");
                    sb.append(role.getDiskSize()).append(",");
                    sb.append("10000").append(":").append("10000").append(",");
                    if ("admin".equals(member.getName()) && Environment.SYSTEM_ID == member.getId()) {
                        sb.append("d:/");
                    } else {
                        sb.append(role.getUploadFolder()).append("/").append(member.getId()).append("/");
                    }
                    String userName = member.getName();
                    userName = StringUtil.replace(userName, ".", "_");
                    userName = StringUtil.replace(userName, ",", "-");
                    p.setProperty("ftp.server.user." + userName, StringUtil.replace(StringUtil.convertCR(sb.toString()), StringUtil.CR, ""));
                }
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
        }
        return p;
    }


    /**
     * 得到用户有哪些机构空间
     *
     * @param namespace 命名空间
     * @param page      页
     * @param count     行
     * @return 异常
     */
    @Override
    public List<MemberSpace> getMemberSpaceList(String namespace, int page, int count) {
        Criteria criteria = createCriteria(MemberSpace.class);
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.addOrder(Order.asc("createDate")).setCurrentPage(page).setTotalCount(count).list(true);
    }

    /**
     * @param memberId   用户ID
     * @param organizeId 组织ID
     * @param namespace  命名空间
     * @param page       页数
     * @param count      一页显示行数
     * @return 得到子用户
     */
    @Override
    public List<Member> getMemberChildList(long memberId, String organizeId, String namespace, int page, int count) {
        if (memberId <= 0 || StringUtil.isEmpty(organizeId) || !StringUtil.hasLength(namespace)) {
            return new ArrayList<>();
        }
        Criteria criteria = createCriteria(MemberSpace.class).add(Expression.eq("memberId", memberId));
        criteria = criteria.add(Expression.eq("organizeId", organizeId));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        List<MemberSpace> list = criteria.addOrder(Order.desc("createDate")).setCurrentPage(page).setTotalCount(count).list(false);
        Long[] ids = null;
        for (MemberSpace space : list) {
            ids = ArrayUtil.add(ids, space.getChildId());
        }
        return createCriteria(Member.class).add(Expression.in("id", ids)).setCurrentPage(page).setTotalCount(count).list(false);
    }


    /**
     * @param memberId   用户ID
     * @param organizeId 组织ID
     * @param namespace  命名空间
     * @return 得到子用户数量
     */
    @Override
    public int getMemberChildCount(long memberId, String organizeId, String namespace) {
        if (memberId <= 0 || StringUtil.isEmpty(organizeId) || !StringUtil.hasLength(namespace)) {
            return 0;
        }
        Criteria criteria = createCriteria(MemberSpace.class).add(Expression.eq("memberId", memberId));
        criteria = criteria.add(Expression.eq("organizeId", organizeId));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }


    /**
     * @param childId    子用户ID
     * @param organizeId 组织ID
     * @param namespace  命名空间
     * @return 得到用户所属空间
     */
    @Override
    public MemberSpace getMemberSpace(long childId, String organizeId, String namespace) {
        if (childId <= 0 || StringUtil.isEmpty(organizeId) || !StringUtil.hasLength(namespace)) {
            return new MemberSpace();
        }
        Criteria criteria = createCriteria(MemberSpace.class).add(Expression.eq("childId", childId));
        criteria = criteria.add(Expression.eq("organizeId", organizeId));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.objectUniqueResult(false);
    }

    /**
     * 判断用户是否属于某空间
     *
     * @param uid        用户
     * @param organizeId 机构ID
     * @param namespace  软件命名空间
     * @return true:是,false:否
     */
    @Override
    public boolean isInMemberSpace(long uid, String organizeId, String namespace) {
        if (uid <= 0 || StringUtil.isEmpty(organizeId) || !StringUtil.hasLength(namespace)) {
            return false;
        }
        Criteria criteria = createCriteria(MemberSpace.class).add(Expression.eq("childId", uid));
        criteria = criteria.add(Expression.eq("organizeId", organizeId));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    @Override
    public boolean deleteOrganizeForMemberSpace(String organizeId, String namespace) {
        if (StringUtil.isEmpty(organizeId) || !StringUtil.hasLength(namespace)) {
            return false;
        }
        Criteria criteria = createCriteria(MemberSpace.class).add(Expression.eq("organizeId", organizeId));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.delete(false) >= 0;
    }

}

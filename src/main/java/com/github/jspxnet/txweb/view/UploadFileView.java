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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.TurnPage;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.txweb.model.dto.AttachmentDto;
import com.github.jspxnet.txweb.model.param.FilePageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-12-4
 * Time: 下午2:23
 */
@Slf4j
@HttpMethod(caption = "浏览附件")
public class UploadFileView extends ActionSupport {
    private int count = 0;
    private long uid = 0;
    private long pid = 0;
    private int currentPage = 0;
    private String term = StringUtil.empty;
    private String sort = "sortType:D;sortDate:D";
    private String[] field = ArrayUtil.EMPTY_STRING_ARRAY;
    private String[] find = ArrayUtil.EMPTY_STRING_ARRAY;
    private String turnPageFile = "sturnpage.ftl";
    private long id;

    public UploadFileView() {

    }

    @Ref
    protected UploadFileDAO uploadFileDAO;

    @TurnPage(file = "@turnPageFile", params = "find;field;sort;uid")
    private String turnPage = StringUtil.empty;

    public String getTurnPageFile() {
        return turnPageFile;
    }

    @Param(request = false)
    public void setTurnPageFile(String turnPageFile) {
        this.turnPageFile = turnPageFile;
    }

    public String getTurnPage() {
        return turnPage;
    }

    public String[] getField() {
        return field;
    }

    @Param(caption = "查询字段", max = 20)
    public void setField(String[] field) {
        this.field = field;
    }

    public String[] getFind() {
        return find;
    }

    @Param(caption = "查询数据", max = 20)
    public void setFind(String[] find) {
        this.find = find;
    }

    public String getTerm() {
        return term;
    }

    @Param(caption = "条件", max = 50)
    public void setTerm(String term) {
        this.term = term;
    }

    public String getSort() {
        return sort;
    }

    @Param(caption = "排序", max = 20)
    public void setSort(String sort) {
        this.sort = sort;
    }

    public long getUid() {
        return uid;
    }

    @Param(caption = "用户id", max = 20)
    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        if (count <= 0) {
            count = config.getInt(Environment.rowCount, 18);
        }
        return count;
    }

    @Param(caption = "行数")
    public void setCount(int count) {
        this.count = count;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
    @Deprecated
    public List<IUploadFile> getList() throws Exception {
        IRole role = getRole();
        if (!isGuest() && role!= null && role.getUserType() < UserEnumType.MANAGER.getValue()) {
            IUserSession userSession = getUserSession();
            return uploadFileDAO.getList(field, find,null, getTerm(), sort, userSession.getUid(), pid, getCurrentPage(), getCount());
        }
        return uploadFileDAO.getList(field, find,null, getTerm(), sort, getUid(), pid, getCurrentPage(), getCount());
    }

    @Deprecated
    public long getTotalCount() {
        IUserSession userSession = getUserSession();
        IRole role = userSession.getRole(uploadFileDAO.getNamespace(),uploadFileDAO.getOrganizeId());
        if (!userSession.isGuest() && role != null && role.getUserType() < UserEnumType.MANAGER.getValue()) {
            return uploadFileDAO.getCount(field, find, null,term, userSession.getUid(), pid);
        }
        return uploadFileDAO.getCount(field, find,null, term, getUid(), pid);
    }

    public Object getUploadFile() throws Exception {
        return uploadFileDAO.load(id);
    }

    /**
     *
     * @param param 翻页参数
     * @return 日志列表
     */
    @Operate(caption = "附件翻页列表", method = "list/page")
    public RocResponse<List<AttachmentDto>> getList(@Param("翻页参数") FilePageParam param)
    {
        IRole role = getRole();
        if (role.getUserType() < UserEnumType.MANAGER.getValue()) {
            return RocResponse.error(ErrorEnumType.POWER);
        }
        int totalCount = uploadFileDAO.getCount(param.getField(),param.getFind(),param.getFileTypes(),param.getTerm(),param.getUid(),param.getPid());
        if (totalCount<=0)
        {
            return RocResponse.success(new ArrayList<>(),"无数据");
        }
        List<Object> list = uploadFileDAO.getList(param.getField(), param.getFind(),param.getFileTypes(), param.getTerm(),param.getSort(), param.getUid(),param.getPid(), param.getCurrentPage(), param.getCount());
        RocResponse<List<AttachmentDto>> rocResponse = RocResponse.success(BeanUtil.copyList(list, AttachmentDto.class));
        rocResponse.setTotalCount(totalCount);
        return rocResponse.setCurrentPage(param.getCurrentPage()).setCount(param.getCount());
    }

    @Override
    public String execute() throws Exception {
        if (uploadFileDAO == null || uploadFileDAO.getClassType() == null) {
            log.error("配置类对象uploadFileDAO不存在,请检查是否有重复的配置，或者配置不存在");
        }
        String classname = StringUtil.uncapitalize(StringUtil.substringAfterLast(uploadFileDAO.getClassType().getName(), StringUtil.DOT));
        put(classname, getUploadFile());
        put("namespace", uploadFileDAO.getNamespace());
        put("role", getRole());
        return getActionResult();
    }
}
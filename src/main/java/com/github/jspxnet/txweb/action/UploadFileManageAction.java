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
import com.github.jspxnet.boot.res.ToolTipsRes;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.UploadFileParam;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.view.UploadFileView;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-12-4
 * Time: 下午2:34
 */
@HttpMethod(caption = "附件管理")
public class UploadFileManageAction extends UploadFileView {

    /**
     * 排序时间
     *
     * @param ids id列表
     * @throws Exception 异常
     */
    @Operate(caption = "排序")
    public void sortDate(@Param(caption = "ids",min = 1,required = true, message = ToolTipsRes.notSelectObject) Long[] ids) throws Exception {

        if (uploadFileDAO.updateSortDate(ids)) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }


    /**
     * 提前
     *
     * @param ids      id列表
     * @param sortType 排序
     */
    @Operate(caption = "提前")
    public void sortType(@Param(caption = "ids",min = 1, required = true, message = ToolTipsRes.notSelectObject) Long[] ids,
                         @Param(caption = "排序标识", required = true) int sortType)  {
        try {
            if (uploadFileDAO.updateSortType(ids, sortType)) {
                addActionMessage(language.getLang(LanguageRes.updateSuccess));
                setActionResult(SUCCESS);
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
            }
        } catch (Exception e) {
            e.printStackTrace();
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }

    /**
     * @param ids id列表
     */
    @Operate(caption = "删除")
    public void delete(@Param(caption = "ids", required = true,min = 1,message = ToolTipsRes.notSelectObject) Long[] ids) {
        IUserSession userSession = getUserSession();
        StringBuilder actionLog = new StringBuilder();
        try {
            for (long id : ids) {
                Object uploadFile = uploadFileDAO.get(id);
                if (uploadFile == null) {
                    continue;
                }

                IUploadFile iUploadFile = (IUploadFile) uploadFile;
                deleteUploadFile(iUploadFile);
                actionLog.append(iUploadFile.getTitle()).append(" ").append(iUploadFile.getFileName()).append("<br>");

                //删除子文件，手机图片，缩图
                List<Object> childFile =  uploadFileDAO.getChildFileList(iUploadFile.getId());
                for (Object child : childFile) {
                    if (child == null) {
                        continue;
                    }
                    IUploadFile childUpload = (IUploadFile) child;
                    if (userSession.getUid() == childUpload.getPutUid()) {
                        deleteUploadFile(childUpload);
                        actionLog.append(childUpload.getTitle()).append(" ").append(childUpload.getFileName()).append("<br>");
                    }
                }
            }
            setActionLogContent(actionLog.toString());
            addActionMessage(language.getLang(LanguageRes.deleteSuccess));
        } catch (Exception e) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.deleteFailure));
            e.printStackTrace();
        }
    }

    private void deleteUploadFile(IUploadFile uploadFile) throws Exception {
        File deleteFile = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (uploadFileDAO.delete(uploadFile) >= 0) {
            //确保没有其他人在使用文件
            if (!uploadFileDAO.haveHash(uploadFile.getHash()) && deleteFile != null && deleteFile.isFile()) {
                //如果有缩图在删除缩图
                FileUtil.delete(deleteFile);

                File thumbnailFile = new File(deleteFile.getParentFile(), FileUtil.getThumbnailFileName(deleteFile.getName()));
                FileUtil.delete(thumbnailFile);

                File phoneImgFile = new File(deleteFile.getParentFile(), FileUtil.getMobileFileName(deleteFile.getName()));
                FileUtil.delete(phoneImgFile);
                //todo另外还要删除子图片
            }
        }
    }


    @Operate(caption = "编辑")
    public void update(@Param(caption = "上传对象") UploadFileParam param) throws Exception {
        Object uploadFile = BeanUtil.copy(param,uploadFileDAO.getClassType());
        IUploadFile iUploadFile = (IUploadFile) uploadFile;
        IUserSession userSession = getUserSession();
        iUploadFile.setPutName(userSession.getName());
        iUploadFile.setPutUid(userSession.getUid());
        iUploadFile.setIp(getRemoteAddr());

        IUploadFile uploadFileOld = (IUploadFile) uploadFileDAO.get(iUploadFile.getId());
        if (uploadFileOld == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameter) + ":" + iUploadFile.getId());
            setActionResult(ERROR);
            return;
        }
        if (StringUtil.isNull(iUploadFile.getHash())) {
            iUploadFile.setHash(uploadFileOld.getHash());
        }
        iUploadFile.setCreateDate(uploadFileOld.getCreateDate());
        int x = uploadFileDAO.update(uploadFile);
        if (x > 0) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess) + ":" + x);
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure) + ":" + x);
        }
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            uploadFileDAO.evict(uploadFileDAO.getClassType());
        }
        return super.execute();
    }

}
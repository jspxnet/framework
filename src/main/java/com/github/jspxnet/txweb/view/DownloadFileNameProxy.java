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
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.action.UploadFileAction;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 13-11-20
 * Time: 上午11:30
 * 不提供下载的数据，只返回下载的URL路径
 * 只是一个下载转向
 */
@HttpMethod(caption = "下载代理")
public class DownloadFileNameProxy extends ActionSupport {
    @Getter
    private String[] stopArray = new String[]{"sourceforge.net", "xunlei", "flashget", "dirbuster", "nikto", "sqlmap", "whatweb", "openvas", "jbrofuzz", "libwhisker", "webshag", "baiduspider", "googlebot", "yahoo", "msnbot", "scooter", "docin", "douban", "eapoo", "doc88", "baidu", "renrendoc"};
    private final static boolean DEBUG = EnvFactory.getEnvironmentTemplate().getBoolean(Environment.DEBUG);

    public DownloadFileNameProxy() {

    }

    ///////////////载入IOC DAO 对象 begin
    @Ref
    private UploadFileDAO uploadFileDAO;
    ///////////////载入IOC DAO 对象 end

    @Param(request = false)
    public void setStopArray(String[] stopArray) {
        this.stopArray = stopArray;
    }


    @Override
    public String execute() throws Exception {
        setActionResult(NONE);
        if (RequestUtil.isPirated(getRequest())) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedExternalLinks));
            TXWebUtil.print(language.getLang(LanguageRes.notAllowedExternalLinks), WebOutEnumType.HTML.getValue(), getResponse());
            return NONE;
        }
        if (config.getInt(Environment.openSite) == 0) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.closeSite));
            TXWebUtil.print(language.getLang(LanguageRes.closeSite), WebOutEnumType.HTML.getValue(), getResponse());
            return NONE;
        }


        long id = StringUtil.toLong(getEnv(ActionEnv.Key_ActionName));
        Object uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(), id);
        if (uploadFileObject == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.invalidParameterNotFindFile));
            printError(language.getLang(LanguageRes.invalidParameterNotFindFile), HttpStatusType.HTTP_status_400);
            return NONE;
        }

        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File fileName = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (fileName == null || !fileName.exists() || !fileName.isFile() || !fileName.canRead()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileNotFind) + ":" + uploadFile.getFileName());
            if (DEBUG) {
                printError(language.getLang(LanguageRes.invalidParameterNotFindFile) + "," + fileName, HttpStatusType.HTTP_status_404);
            } else {
                printError(language.getLang(LanguageRes.invalidParameterNotFindFile), HttpStatusType.HTTP_status_404);
            }
            return NONE;
        }
        uploadFile.setDownTimes(uploadFile.getDownTimes() + 1);
        uploadFileDAO.update(uploadFileObject, new String[]{"downTimes"});
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        PrintWriter out = getResponse().getWriter();
        out.write("/" + uploadFileDAO.getNamespace() + "/" + FileUtil.mendFile(FileUtil.getDecrease(setupPath, fileName.getAbsolutePath())));
        out.flush();
        out.close();
        return NONE;
    }

}
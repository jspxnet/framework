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
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.IpLocationDAO;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.table.DownloadFileClient;
import com.github.jspxnet.txweb.table.IpLocation;

import lombok.extern.slf4j.Slf4j;

import com.github.jspxnet.boot.environment.Environment;

import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.action.UploadFileAction;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-11-8
 * Time: 下午7:44
 * java 控制下载的方式
 * 有两个参数可以带
 * s=1  表示输出会进行统计
 * nameType=id ,name ,hash  表示下载模，的下载名称方式，式会带后缀名称输出下载
 */
@Slf4j
@HttpMethod(caption = "下载文件")
public class DownloadFileView extends ActionSupport {
    private String[] stopArray = new String[]{"sourceforge.net", "xunlei", "flashget", "dirbuster", "nikto", "sqlmap", "whatweb", "openvas", "jbrofuzz", "libwhisker", "webshag", "baiduspider", "googlebot", "yahoo", "msnbot", "scooter", "docin", "douban", "eapoo", "doc88", "baidu", "renrendoc"};
    final private static int BUFFER_SIZE = 1024;
    final private static float version = StringUtil.toFloat(System.getProperty("java.vm.specification.version"));
    private static int downloadUser = 0;

    final private static String NAME_TYPE_ID = "id";

    final private static String NAME_TYPE_NAME = "name";

    final private static String NAME_TYPE_HASH = "hash";

    private String nameType = NAME_TYPE_HASH;

    public DownloadFileView() {

    }

    ///////////////载入IOC DAO 对象 begin
    private UploadFileDAO uploadFileDAO;
    @Ref
    public void setUploadFileDAO(UploadFileDAO uploadFileDAO) {
        this.uploadFileDAO = uploadFileDAO;
    }
    ///////////////载入IOC DAO 对象 end

    @Ref
    private IpLocationDAO ipLocationDAO;

    @Param(request = false, caption = "不允许下载的网站")
    public void setStopArray(String[] stopArray) {
        this.stopArray = stopArray;
    }


    @Param(caption = "下载名称类型")
    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    @Override
    public String execute() throws Exception {
        AssertException.isNull(uploadFileDAO,"uploadFileDAO没有配置");
        if (RequestUtil.isPirated(request)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedExternalLinks));
            return NONE;
        }

        if (config.getInt(Environment.openSite) == YesNoEnumType.NO.getValue()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.closeSite));
            return NONE;
        }

        String userAgent = request.getHeader(RequestUtil.requestUserAgent);
        if (StringUtil.isNull(userAgent)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.closeSite));
            return NONE;
        }

        userAgent = userAgent.toLowerCase();
        for (String stop : stopArray) {
            if (userAgent.contains(stop)) {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.closeSite));
                return NONE;
            }
        }

        int maxDownloader = config.getInt(Environment.maxDownloader, 100);
        if (downloadUser > maxDownloader) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedExceedWaitMaxDownloader) + "," + maxDownloader);
            return NONE;
        }
        Object uploadFileObject = null;
        String urlName = getEnv(ActionEnv.Key_ActionName);
        if (ValidUtil.isNumber(urlName) && urlName.length() < 20) {
            //说明传的上ID
            long id = StringUtil.toLong(urlName);
            uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(), id);
        } else {
            //传的上hash 编码

        }


        if (uploadFileObject == null) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileNotFind));
            return NONE;
        }

        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        //载入原图 begin
        if (getBoolean("p")) {
            if (uploadFile.getPid() > 0) {
                uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(), uploadFile.getPid());
            }
            if (uploadFileObject == null) {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileNotFind));
                return NONE;
            }
            uploadFile = (IUploadFile) uploadFileObject;
        }
        //载入原图 end

        File fileName = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (fileName == null || !fileName.exists() || !fileName.isFile() || !fileName.canRead()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileNotFind) + "," + uploadFile.getFileName());
            boolean debug = EnvFactory.getEnvironmentTemplate().getBoolean(Environment.logJspxDebug);
            if (debug) {
                TXWebUtil.print(language.getLang(LanguageRes.fileNotFind) + "," + fileName, WebOutEnumType.HTML.getValue(), response);
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileNotFind));
            }
            return NONE;
        }

        if (getBoolean("s")) {
            uploadFile.setDownTimes(uploadFile.getDownTimes() + 1);
            uploadFileDAO.update(uploadFileObject, new String[]{"downTimes"});

            DownloadFileClient downloadFileClient = new DownloadFileClient();
            downloadFileClient.setBrowser(RequestUtil.getBrowser(request));
            downloadFileClient.setFid(uploadFile.getId());
            downloadFileClient.setSystem(RequestUtil.getSystem(request));
            downloadFileClient.setNetType(RequestUtil.getNetType(request));
            downloadFileClient.setUrl(request.getRequestURL().toString());
            // 统计的时候在放入，在这里放入性能低下
            IpLocation ipLocation = ipLocationDAO.getIpLocation(getRemoteAddr());
            downloadFileClient.setLocation(ipLocation.getCity() + " " + ipLocation.getCountry());
            IUserSession userSession = getUserSession();
            if (userSession != null) {
                downloadFileClient.setPutUid(userSession.getUid());
                downloadFileClient.setPutName(userSession.getName());
            } else {
                downloadFileClient.setPutUid(Environment.GUEST_ID);
                downloadFileClient.setPutName(Environment.GUEST_NAME);
            }
            downloadFileClient.setIp(getRemoteAddr());
            downloadFileClient.setNamespace(uploadFileDAO.getNamespace());
            uploadFileDAO.save(downloadFileClient);
        }

        String fileType = FileUtil.getTypePart(fileName.getName());
        String contentType = FileSuffixUtil.getContentType(fileName);

        response.reset();

        response.setHeader("framework", Environment.frameworkName + " " + Environment.VERSION);
        response.setContentType(contentType);
        response.setBufferSize(BUFFER_SIZE);
        response.setCharacterEncoding(config.get("encode", Environment.defaultEncode));
        //下载时候需要的文件名方式
        //以id方式命名
        if (NAME_TYPE_ID.equalsIgnoreCase(nameType)) {
            response.setHeader(RequestUtil.requestContentDisposition, "attachment;filename=" + uploadFile.getId() + StringUtil.DOT + fileType);
        } else
            //以文件名方式命名
            if (NAME_TYPE_NAME.equalsIgnoreCase(nameType)) {
                response.setHeader(RequestUtil.requestContentDisposition, "attachment;filename=" + URLEncoder.encode(uploadFile.getTitle(), StandardCharsets.UTF_8.name()) + StringUtil.DOT + fileType);
            } else {
                //以hash方式
                response.setHeader(RequestUtil.requestContentDisposition, "attachment;filename=" + uploadFile.getHash() + StringUtil.DOT + fileType);
            }
        //-----------------------------------------------------
        downloadUser++;
        try {
            if (config.getInt(Environment.downloadType) == 1) {
                singleDownFile(fileName);
            } else {
                manyDownFile(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        } finally {
            downloadUser--;
        }
        return NONE;
    }

    private void singleDownFile(File file) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        FileInputStream fin = new FileInputStream(file);
        byte[] bytes = new byte[BUFFER_SIZE];
        long fileLength = file.length();
        response.setHeader(RequestUtil.requestContentLength, Long.toString(fileLength));
        int count;
        if (fileLength > 0) {
            try {
                while ((count = fin.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.close();
            } catch (Exception e) {
                log.error(file.getPath(), e);
                out.flush();
                out.close();
                fin.close();
            }
        }
    }

    private void manyDownFile(File file)  {
        long l = file.length();
        long p = 0;

        response.setHeader(RequestUtil.requestAcceptRanges, "bytes");
        if (request.getHeader("Range") != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            String sClient = request.getHeader("Range");

            if (sClient != null) {
                sClient = StringUtil.trim(StringUtil.substringAfter(sClient, "bytes"));
                if (sClient.contains("-")) {
                    sClient = StringUtil.substringBefore(sClient, "-");
                }
                p = Long.parseLong(StringUtil.getNumber(sClient));
            }
        }

        response.setHeader(RequestUtil.requestContentLength, Long.toString(l - p));
        if (p != 0) {

            response.setHeader(RequestUtil.requestContentRange, "bytes " + p + "-" + (l - 1) + "/" + l);
        }
        int count;
        byte[] bytes = new byte[BUFFER_SIZE];
        try (FileInputStream fin = new FileInputStream(file)) {
            ServletOutputStream out = response.getOutputStream();
            if (p > -1) {
                p = fin.skip(p);
            }
            while ((count = fin.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.flush();
        } catch (Exception e) {
            log.error(file.getPath(), e);
        }
    }
}
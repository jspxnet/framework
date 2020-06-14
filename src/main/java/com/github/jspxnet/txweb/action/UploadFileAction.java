/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 */
package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.ReadPdfTextFile;
import com.github.jspxnet.io.ReadWordTextFile;
import com.github.jspxnet.lucene.ChineseAnalyzer;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.CloudServiceFactory;
import com.github.jspxnet.network.oss.CloudFileClient;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.MulRequest;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.support.MultipartSupport;
import com.github.jspxnet.txweb.table.CloudFileConfig;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.upload.MultipartRequest;
import com.github.jspxnet.upload.UploadedFile;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-2-4
 * Time: 下午7:48
 * 上传机制说明, 如果采用flash方式上传，每次只是一个上传文件，
 * 如果使用html type=file 方式上传，为多个文件方式
 * 上传后的图片会自动压缩
 * <p>
 * 图片 如果大于最大宽度就缩小到最大宽度，这张图作为原图
 * 1.参数thumbnail  成立，生成_s缩图
 * 2.所有图都生成_m手机图片
 * 3.如果已经存在的图片，将已经存在的图片返回，如果已经在的图片，就将现有图片拷贝覆盖到原图片
 */
@Slf4j
@HttpMethod(caption = "上传文件")
public class UploadFileAction extends MultipartSupport {
    final static public String[] officeFileTypes = FileSuffixUtil.officeTypes;
    final static public String[] stopExs = new String[]{"php", "jsp", "ftl", "html", "htm", "exe", "com", "bat", "asp", "aspx", "sh", "jar", "js", "dll"};

    // 状态
    public static String hashType = "MD5";

    //是否开启云盘上传
    protected boolean useCloudFile = false;

    private boolean useOriginalDate = false;

    private String organizeId = StringUtil.empty;

    public UploadFileAction() {
        setActionResult(NONE);
    }

    //分片上传，返回显示分片状态，满足断点续传，优化速度
    private boolean chunkSate = false;

    @Param(caption = "分块")
    public void setChunkSate(boolean chunkSate) {
        this.chunkSate = chunkSate;
    }

    @Param(request = false)
    public void setUseOriginalDate(boolean useOriginalDate) {
        this.useOriginalDate = useOriginalDate;
    }

    @Param(request = false)
    public void setHashType(String hashType) {
        UploadFileAction.hashType = hashType;
    }

    @Param(request = false)
    public void setUseCloudFile(boolean useCloudFile) {
        this.useCloudFile = useCloudFile;
    }

    @Param(caption = "机构id")
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    protected String getOrganizeId() {
        if (!StringUtil.isEmpty(uploadFileDAO.getOrganizeId())) {
            return uploadFileDAO.getOrganizeId();
        }
        return organizeId;
    }

    /**
     * @return 得到配置允许上传的文件类型
     */
    @Override
    public String getFileTypes() {
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            IRole role = userSession.getRole(uploadFileDAO.getNamespace(), getOrganizeId());
            if (role != null) {
                fileTypes = role.getUploadFileTypes();
            }
        }
        if (StringUtil.isNull(fileTypes) && config != null) {
            fileTypes = config.getString(Environment.allowedTypes);
        }
        if (StringUtil.ASTERISK.equalsIgnoreCase(fileTypes)) {
            String[] uploadTypes = new String[0];
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.imageTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.zipTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.videoTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.officeTypes);
            fileTypes = ArrayUtil.toString(uploadTypes, StringUtil.COMMAS);
        }
        return StringUtil.replace(fileTypes, StringUtil.COMMAS, StringUtil.SEMICOLON);
    }

    /**
     * @return 最大上传限制
     */
    @Override
    public int getMaxPostSize() {
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            IRole role = userSession.getRole(uploadFileDAO.getNamespace(), getOrganizeId());
            if (role != null) {
                maxPostSize = role.getUploadSize() * 1024;
            }
        }
        if (maxPostSize < 0 && config != null) {
            maxPostSize = config.getInt(Environment.uploadMaxSize) * 1024;
        }
        return maxPostSize;
    }

    /**
     * @return 得到上传路径
     */
    @Override
    public String getSaveDirectory() {
        return getUploadDirectory(config);
    }

    /**
     * @return 得到安装路径
     * @throws Exception 异常
     */

    protected String getSetupPath() throws Exception {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        if (!FileUtil.isDirectory(setupPath)) {
            setupPath = FileUtil.mendPath(FileUtil.getParentPath(getTemplatePath()));
            config.save(Environment.setupPath, setupPath);
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.setupConfigPathError) + ":" + setupPath);
        }
        return setupPath;
    }

    /**
     * @param config 配置接口
     * @return 上传路径的计算方式配置方式
     */
    @Param(request = false)
    public static String getUploadDirectory(Bundle config) {
        String saveDirectory = FileUtil.mendPath(config.getString(Environment.uploadPath));
        if (!FileUtil.isDirectory(saveDirectory)) {
            saveDirectory = FileUtil.mendPath(config.getString(Environment.setupPath)) + FileUtil.mendPath(config.getString(Environment.uploadPath));
        }
        boolean uploadPathType = config.getBoolean(Environment.uploadPathType);
        if (uploadPathType) {
            saveDirectory = FileUtil.mendPath(saveDirectory) + DateUtil.toString("yyyyMM") + "/";
        } else {
            saveDirectory = FileUtil.mendPath(saveDirectory) + DateUtil.toString("yyyy") + "/";
        }
        return saveDirectory;
    }

    /**
     * @param config 配置
     * @param name   名称
     * @return 得到文件
     */
    @Param(request = false)
    public static File getUploadFile(Bundle config, String name) {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        String searchPathList = config.getString(Environment.searchPaths);
        String[] searchPaths = ArrayUtil.remove(StringUtil.split(StringUtil.convertCR(searchPathList), StringUtil.CR), "");
        searchPaths = ArrayUtil.add(searchPaths, setupPath);
        searchPaths = ArrayUtil.add(searchPaths, setupPath + config.getString(Environment.uploadPath));
        searchPaths = ArrayUtil.add(searchPaths, (new File(setupPath).getParent()));
        return FileUtil.getFile(searchPaths, name);
    }

    /**
     * @return 是否覆盖
     */
    @Override
    public boolean getCovering() {
        return config.getBoolean(Environment.uploadCovering);
    }

    /**
     * @param multipartRequest 请求接口
     */
    @Override
    @Param(request = false)
    @MulRequest(covering = "@covering", saveDirectory = "@saveDirectory", fileTypes = "@fileTypes", maxPostSize = "@maxPostSize")
    public void setMultipartRequest(MultipartRequest multipartRequest) {
        request = this.multipartRequest = multipartRequest;
    }

    /**
     * DAO 对象
     */
    @Ref
    protected UploadFileDAO uploadFileDAO;

    /**
     * 中文分词
     */
    @Ref
    private ChineseAnalyzer chineseAnalyzer;

    private String hash = StringUtil.empty;

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Operate(caption = "hash验证")
    public void hasHash() {
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        if (isGuest()) {
            json.put(Environment.message, language.getLang(LanguageRes.needLogin));
            TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
            return;
        }

        if (StringUtil.isNull(hash)) {
            json.put(Environment.message, language.getLang(LanguageRes.invalidParameter));
            TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
            return;
        }

        Object alreadyUploadFile = uploadFileDAO.getForHash(hash);
        IUploadFile checkUploadFile = (IUploadFile) alreadyUploadFile;
        if (checkUploadFile != null && hash.equalsIgnoreCase(checkUploadFile.getHash()))
        {
            json.put(Environment.message, language.getLang(LanguageRes.alreadyExist));
            json.put("success", true);
            json.put("hash", hash);
            json.put("hashType", hashType);
            json.put("OK", 1);
        }
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }

    @Operate(caption = "秒传")
    public void fastUpload() throws Exception {
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        if (isGuest()) {
            printNoLoginError();
            return;
        }
        if (StringUtil.isNull(hash)) {
            json.put(Environment.message, language.getLang(LanguageRes.invalidParameter));
            TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
            return;
        }
        IUserSession userSession = getUserSession();
        Object alreadyUploadFile = uploadFileDAO.getForHash(hash);
        IUploadFile checkUploadFile = (IUploadFile) alreadyUploadFile;
        if (checkUploadFile != null && !StringUtil.isNull(hash) && checkUploadFile.getHash().equals(hash)) {
            Object[] copyUploadFile = copyUploadToUser(alreadyUploadFile, userSession);
            if (ObjectUtil.isEmpty(copyUploadFile))
            {
                TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
                return;
            }
            printUploadFileInfo(copyUploadFile,chunkJson, getBoolean("thumbnail"),getBoolean("mobile"),  uploadFileDAO.getNamespace(),
                    language.getLang(LanguageRes.success),response);
            return;
        }
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }

    private String fileName = StringUtil.empty;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Operate(caption = "续传判断")
    public void lastChunk() {
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        json.put("uploadType", "chunk");

        if (isGuest()) {
            printNoLoginError();
            return;
        }
        IUserSession userSession = getUserSession();
        UploadedFile uf = new UploadedFile(fileName, getUploadDirectory(config), fileName, fileName, "application/octet-stream", FileUtil.getTypePart(fileName));
        int diskChunk = uf.getLastChunk(NumberUtil.toString(userSession.getUid()));
        json.put("lastChunk", diskChunk);
        json.put("chunks", uf.getChunks());
        //兼容 plupload  begin
        if (diskChunk > 1) {
            json.put("OK", 1);
            json.put("success", true);
            json.put("uploadType", "file");
            json.put(Environment.message, "chunk upload " + language.getLang(LanguageRes.success));
        }
        //兼容 plupload  end
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }


    /**
     * 兼容swfupload 和 kindeditor, 和UEditor 1.2.0 的返回结果，当然普通页面上传也没问题
     *
     * @return json 返回结果
     * @throws Exception 异常
     */
    @Override
    @Operate(caption = "上传")
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            return NONE;
        }
        //--------------------------------------------------------------------------------------------------------------
        if (uploadFileDAO == null) {
            printDaoError();
            return NONE;
        }
        if (multipartRequest == null && !RequestUtil.isMultipart(request)&&!response.isCommitted()) {
            printRequestError();
            return NONE;
        }
        IUserSession userSession = getUserSession();
        if (userSession == null || userSession.isGuest()) {
            printNoLoginError();
            for (UploadedFile uf : multipartRequest.getFiles()) {
                FileUtil.delete(uf.getFile());
            }
            return NONE;
        }

        Object[]  objects = localUploadFile(userSession);
        if (ObjectUtil.isEmpty(objects))
        {
            printErrorInfo("上传失败");
            return NONE;
        }
        boolean thumbnail = getBoolean("thumbnail");
        boolean mobile = getBoolean("mobile");

        printUploadFileInfo(objects,chunkJson, thumbnail,mobile,  uploadFileDAO.getNamespace(),  language.getLang(LanguageRes.success),response);
        uploadFileDAO.evict(uploadFileDAO.getClassType());
        return NONE;
    }
    protected JSONObject chunkJson;
    /**
     *
     * @param userSession 用户信息
     * @return  上传的数据列表
     * @throws Exception 异常
     */
    public Object[] localUploadFile(IUserSession userSession) throws Exception {
        String setupPath = getSetupPath();

        for (UploadedFile uf : multipartRequest.getFiles()) {
            if (ArrayUtil.inArray(stopExs, uf.getFileType(), true)) {
                FileUtil.delete(uf.getFile());
                printErrorInfo(language.getLang(LanguageRes.notAllowedFileType) + ":" + uf.getFileType());
                return null;
            }
            if (!uf.isUpload()) {
                FileUtil.delete(uf.getFile());
                printErrorInfo(language.getLang(LanguageRes.notAllowedFileTypeOrUploadError));
                return null;
            }
            //分片上传
            if (uf.isChunkUpload()) {
                chunkJson = getChunkUploadInfo(uf, userSession);
                //0:未知;1:continue;2:错误;3:完成
                if (1 == chunkJson.getInt("chunked") || 0 == chunkJson.getInt("chunked")  || 2 == chunkJson.getInt("chunked")) {
                    printError(chunkJson, HttpStatusType.HTTP_status_OK);
                    return null;
                }
            } else
            {
                chunkJson = null;
            }
            String hashCode = FileUtil.getHash(uf.getFile(), hashType);
            if (!uf.moveToTypeDir()) {
                //没有移动成功的文件
                printErrorInfo(language.getLang(LanguageRes.folderWriteError));
                return null;
            }

            Object saveUploadFile = uploadFileDAO.getClassType().newInstance();
            IUploadFile upFile = (IUploadFile) saveUploadFile;
            upFile.setTitle(FileUtil.getNamePart(uf.getOriginal()));
            if ("Filedata".equalsIgnoreCase(upFile.getTitle()) || "blob".equalsIgnoreCase(upFile.getTitle())) {
                upFile.setTitle(FileUtil.getNamePart(uf.getFileName()));
            }
            upFile.setFileName(FileUtil.mendPath(FileUtil.getDecrease(setupPath, uf.getDir())) + uf.getFileName());
            upFile.setFileSize(uf.getFile().length());
            upFile.setFileType(uf.getFileType());
            upFile.setContent(FileUtil.getNamePart(uf.getOriginal()));
            upFile.setTags(chineseAnalyzer.getTag(uf.getOriginal(), StringUtil.space, 3, true));
            upFile.setPutName(userSession.getName());
            upFile.setPutUid(userSession.getUid());
            upFile.setIp(getRemoteAddr());
            upFile.setOrganizeId(getOrganizeId());
            upFile.setHash(FileUtil.getHash(uf.getFile(), hashType));
            Object alreadyUploadFile = null;
            if (!StringUtil.isNull(upFile.getHash()) && hashCode.equals(upFile.getHash())) {
                alreadyUploadFile = uploadFileDAO.getForHash(hashCode);
            }
            IUploadFile checkUploadFile = (IUploadFile) alreadyUploadFile;
            if (checkUploadFile != null && checkUploadFile.getPid() == 0 && !StringUtil.isNull(upFile.getHash()) &&
                    checkUploadFile.getHash().equals(upFile.getHash()) && checkUploadFile.getPutUid() == userSession.getUid()
                    && (checkUploadFile.getOrganizeId() != null && checkUploadFile.getOrganizeId().equals(getOrganizeId())
                    && checkUploadFile.getFileSize()==upFile.getFileSize()
            )
            ) {
                //已经上传过的,拷贝一份
                return copyUploadToUser(alreadyUploadFile, userSession);
            } else {
                //boolean thumbnail = getBoolean("thumbnail");
                //还没有保存到数据库
                Object[] uploadObjArray = newUpload(uf, upFile, userSession);
                if (ObjectUtil.isEmpty(uploadObjArray))
                {
                    return null;
                }
                if (!useCloudFile) {
                    //上传到本地目录

                    uploadFileDAO.save(uploadObjArray[0]);
                    IUploadFile uploadFile = (IUploadFile) uploadObjArray[0];
                    long pid = uploadFile.getId();
                    for (int i=1;i<uploadObjArray.length;i++)
                    {
                        IUploadFile tmpUploadFile = (IUploadFile) uploadObjArray[i];
                        if (tmpUploadFile==null)
                        {
                            continue;
                        }
                        tmpUploadFile.setPid(pid);
                        uploadFileDAO.save(uploadObjArray[i]);
                    }
                    return uploadObjArray;
                }
                else
                {
                    //上传到云盘
                    CloudFileConfig cloudFileConfig = uploadFileDAO.getCloudFileConfig();
                    AssertException.isNull(cloudFileConfig, "云盘空间没有配置");
                    CloudFileClient cloudFileClient = CloudServiceFactory.createCloudClient(cloudFileConfig);
                    AssertException.isNull(cloudFileClient, "云盘空间没有配置正确");
                    List<File> files = new ArrayList<>();
                    long pid = 0;
                    for (int i=0;i<uploadObjArray.length;i++)
                    {
                        IUploadFile tmpUploadFile = (IUploadFile) uploadObjArray[i];
                        if (tmpUploadFile==null)
                        {
                            continue;
                        }
                        File localFile = new File(tmpUploadFile.getTempFilePath());
                        String cloudUrl = cloudFileClient.upload(cloudFileConfig.getNamespace(),localFile);
                        tmpUploadFile.setFileName(cloudUrl);
                        files.add(localFile);
                        if (i == 0)
                        {
                            uploadFileDAO.save(uploadObjArray[i]);
                            pid = tmpUploadFile.getPid();
                        } else
                        {
                            tmpUploadFile.setPid(pid);
                            uploadFileDAO.save(uploadObjArray[i]);
                        }
                    }
                    return uploadObjArray;
                }
            }
        }
        return null;
    }

    /**
     * @param uf 上传的文件
     * @param saveUploadFile 上传将保存的对象
     * @param userSession 用户信息
     * @return 图片正常是3张
     * @throws Exception 异常
     */
    protected Object[] newUpload(UploadedFile uf, Object saveUploadFile, IUserSession userSession) throws Exception {
    //        boolean useUploadConverter = config.getBoolean(Environment.useUploadConverterTxt);
        IUploadFile upFile = (IUploadFile) saveUploadFile;
        int maxImageWidth = config.getInt(Environment.maxImageWidth, 1280);
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        //没有上传过的
        //已经上传成功的,还没有的上传上去的就上传上去
        Object[] result = new Object[3];

        if (FileSuffixUtil.isImageSuffix(uf.getFileType())) {
            File file = uf.getFile();
            //如果是图片就得到宽高
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } catch (IOException e1) {
                json.put("state", ERROR);
                json.put("success", false);
                //兼容 plupload  begin
                json.put("OK", 0);
                json.put(Environment.message, "不能识别的图片格式");
                setResult(json);
                log.info("未知不能识别的图片格式:" + file.getAbsolutePath());
                TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
                return null;
            }

            int w = image.getWidth();
            int h = image.getHeight();

            if (config.getBoolean(Environment.EXIF_SATE)) {
                StringMap<String, String> map = ImageUtil.parsePhotoExif(file, ImageUtil.simpleExifTags);
                upFile.setAttributes(map.toString());
            } else {
                upFile.setAttributes("width=" + w + "\r\nheight=" + h);
            }

            if (image.getWidth() > maxImageWidth) {
                h = (maxImageWidth / w) * h;
                if (!file.canWrite()) {
                    Thread.sleep(200);
                }
                boolean repair = ImageUtil.thumbnail(image, new FileOutputStream(file), uf.getFileType(), maxImageWidth, h);
                json.put("repair", repair);
            }

            String thumbnailImg = FileUtil.getThumbnailFileName(uf.getFileName());
            int width = config.getInt("thumbnailWidth", 400);
            int height = config.getInt("thumbnailHeight", 400);
            //创建缩图
            File thumbnailFile = new File(file.getParent(), thumbnailImg);
            if (ImageUtil.thumbnail(image, new FileOutputStream(thumbnailFile), uf.getFileType(), width, height)) {
                String thumbnailPath = FileUtil.mendPath(FileUtil.getDecrease(setupPath, uf.getDir())) + thumbnailImg;
                IUploadFile thumbnailUploadFile = (IUploadFile) uploadFileDAO.getClassType().newInstance();
                thumbnailUploadFile.setHash(FileUtil.getHash(thumbnailFile, hashType));
                thumbnailUploadFile.setTitle(FileUtil.getNamePart(thumbnailImg));
                thumbnailUploadFile.setFileType(FileUtil.getTypePart(thumbnailFile));
                thumbnailUploadFile.setFileName(thumbnailPath);
                thumbnailUploadFile.setPutName(userSession.getName());
                thumbnailUploadFile.setPutUid(userSession.getUid());
                thumbnailUploadFile.setFileSize(thumbnailFile.length());
                thumbnailUploadFile.setAttributes(upFile.getAttributes());
                thumbnailUploadFile.setSortType(0);
                thumbnailUploadFile.setSortDate(new Date());
                thumbnailUploadFile.setCreateDate(new Date());
                thumbnailUploadFile.setTags("thumbnail");
                thumbnailUploadFile.setTempFilePath(thumbnailFile.getAbsolutePath());
                thumbnailUploadFile.setOrganizeId(getOrganizeId());
                result[1] = thumbnailUploadFile;
            }

            //创建手机图片
            int mobileWidth = config.getInt("mobileWidth", 480);
            int mobileHeight = config.getInt("mobileHeight", 480);
            String mobileImg = FileUtil.getMobileFileName(uf.getFileName());
            File mobileFile = new File(file.getParent(), mobileImg);
            if (ImageUtil.thumbnail(image, new FileOutputStream(mobileFile), uf.getFileType(), mobileWidth, mobileHeight)) {
                json.put("name", FileUtil.getNamePart(mobileImg));
                json.put("mobile", 1);
                String mobilePath = FileUtil.mendPath(FileUtil.getDecrease(setupPath, uf.getDir())) + mobileImg;
                if (mobilePath.startsWith('/' + uploadFileDAO.getNamespace() + '/')) {
                    json.put("mobileUrl", mobilePath);
                } else {
                    json.put("mobileUrl", '/' + uploadFileDAO.getNamespace() + '/' + mobilePath);
                }
                IUploadFile mobileUploadFile = (IUploadFile) uploadFileDAO.getClassType().newInstance();
                mobileUploadFile.setHash(FileUtil.getHash(mobileFile, hashType));
                mobileUploadFile.setTitle(FileUtil.getNamePart(mobileImg));
                mobileUploadFile.setFileType(FileUtil.getTypePart(mobileFile));
                mobileUploadFile.setFileName(mobilePath);
                mobileUploadFile.setPutName(userSession.getName());
                mobileUploadFile.setPutUid(userSession.getUid());
                mobileUploadFile.setFileSize(mobileFile.length());
                mobileUploadFile.setAttributes(upFile.getAttributes());
                mobileUploadFile.setSortType(0);
                mobileUploadFile.setSortDate(new Date());
                mobileUploadFile.setCreateDate(new Date());
                mobileUploadFile.setTags("mobile");
                mobileUploadFile.setOrganizeId(getOrganizeId());
                mobileUploadFile.setTempFilePath(mobileFile.getAbsolutePath());
                result[2] = mobileUploadFile;
            }
        }
        boolean useUploadConverter = config.getBoolean(Environment.useUploadConverterTxt);
        if (useUploadConverter && ArrayUtil.inArray(officeFileTypes, uf.getFileType(), true))
        {
            //文档转换,为了方便安卓下编译
            String content = "";
            try {

                if ("doc".equalsIgnoreCase(uf.getFileType()) ||  "docx".equalsIgnoreCase(uf.getFileType()))
                {
                    AbstractRead abstractRead = new ReadWordTextFile();
                    abstractRead.setFile(uf.getFile());
                    content = abstractRead.getContent();
                }
                if ("pdf".equalsIgnoreCase(uf.getFileType()))
                {
                    AbstractRead abstractRead = new ReadPdfTextFile();
                    abstractRead.setFile(uf.getFile());
                    content = abstractRead.getContent();
                }
                content = StringUtil.cut(content, 2000, StringUtil.empty);
                upFile.setContent(content);
                upFile.setTags(chineseAnalyzer.getTag(content, StringUtil.space, 6, true));
            } catch (Exception e) {
                upFile.setContent(uf.getOriginal());
                upFile.setTags(chineseAnalyzer.getTag(uf.getOriginal(), StringUtil.space, 3, true));
            }
        }

        //导入日期
        Date createDate = new Date();
        if (useOriginalDate && uf.getOriginal().startsWith("[") && uf.getOriginal().contains("]")) {
            String dataStr = StringUtil.substringBetween(uf.getOriginal(), "[", "]");
            if (!StringUtil.isNull(dataStr)) {
                try {
                    createDate = StringUtil.getDate(dataStr);
                } catch (Exception e) {
                    createDate = new Date();
                }
            }
        }
        upFile.setCreateDate(createDate);
        result[0] = saveUploadFile;
        return result;
    }

    /**
     * 处理已经上传过的返回
     *
     * @param alreadyUploadFile 已经上传的文件
     * @throws Exception 异常
     */
    private Object[] copyUploadToUser(Object alreadyUploadFile, IUserSession userSession) throws Exception {
        Object copyUploadFile = BeanUtil.copy(alreadyUploadFile, uploadFileDAO.getClassType());
        IUploadFile copySaveUploadFile = (IUploadFile) copyUploadFile;
        long oldId = copySaveUploadFile.getId();
        copySaveUploadFile.setOrganizeId(getOrganizeId());
        copySaveUploadFile.setPutName(userSession.getName());
        copySaveUploadFile.setPutUid(userSession.getUid());
        copySaveUploadFile.setSortDate(new Date());
        copySaveUploadFile.setCreateDate(new Date());
        copySaveUploadFile.setSortType(0);
        copySaveUploadFile.setId(0);
        uploadFileDAO.save(copyUploadFile);
        long newPid = copySaveUploadFile.getId();

        List<Object> childList = uploadFileDAO.getChildFileList(oldId);
        if (childList.isEmpty()) {
            Object[] objects = new Object[1];
            objects[0] = copyUploadFile;
            return objects;
        }
        List<Object> saveChildList = BeanUtil.copyList(childList, uploadFileDAO.getClassType());
        Object[] objects = new Object[saveChildList.size()+1];
        objects[0] = copySaveUploadFile;

        for (int i=0;i<saveChildList.size();i++)
        {
            IUploadFile childUploadFile = (IUploadFile) childList.get(i);
            childUploadFile.setOrganizeId(getOrganizeId());
            childUploadFile.setPutName(userSession.getName());
            childUploadFile.setPutUid(userSession.getUid());
            childUploadFile.setSortDate(new Date());
            childUploadFile.setCreateDate(new Date());
            childUploadFile.setSortType(0);
            childUploadFile.setPid(newPid);
            childUploadFile.setId(0);
            uploadFileDAO.save(childUploadFile);
            objects[i+1] = childList.get(i);
        }
        return objects;
    }


    //---------------------------------------------

    /**
     * 分片上传,打印返回协议信息,返回
     *
     * @param uf 上传的文件
     * @param userSession 用户信息
     * @return chunked 0:未知;1:continue;2:错误;3:完成
     */
    protected JSONObject getChunkUploadInfo(UploadedFile uf, IUserSession userSession) {
        //分片上传 begin
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("repair", false);
        json.put("success", false);
        json.put("error", 1);
        json.put("url","");
        json.put("state", "error");
        json.put("thumbnail", 0);
        json.put("uploadType", "chunk");
        json.put("chunked", 0);
        if (!uf.moveToChunkFolder(NumberUtil.toString(userSession.getUid()))) {
            json.put(Environment.message, language.getLang(LanguageRes.saveFailure));
            json.put("chunked", 2);
            return json;
        }
        //如果是分片上传,追加进去后直接返回
        json.put("fileName", uf.getFileName());
        json.put("name", FileUtil.getNamePart(uf.getOriginal()));
        json.put("original", uf.getOriginal());
        json.put("title", uf.getOriginal());
        json.put("size", uf.getLength());
        json.put("type", uf.getFileType());

        //添加入最大的分片，判断是否断点续传
        //判断是否已经上传完成
        if (uf.isFolderChunkFull(NumberUtil.toString(userSession.getUid()))) {
            //已经上传完毕
            if (uf.mergeChunks(NumberUtil.toString(userSession.getUid()))) {
                json.put("state", "SUCCESS");
                json.put("successed", true);
                //兼容 plupload  begin
                json.put("OK", 1);
                json.put(Environment.message, language.getLang(LanguageRes.success));
                //兼容 plupload  end
                json.put("chunked", 3);
                return json;
            } else {
                json.put(Environment.message, language.getLang(LanguageRes.saveFailure));
                json.put("chunked", 2);
                return json;
            }
        } else {
            if (chunkSate) {
                int diskChunk = uf.getLastChunk(NumberUtil.toString(userSession.getUid()));
                if (diskChunk > 0 && diskChunk > uf.getChunk()) {
                    json.put("lastChunk", diskChunk);
                    json.put("chunk", uf.getChunk());
                    json.put("chunkSize", uf.getLength());
                    json.put("offset", diskChunk * uf.getLength());
                }
            }
            //兼容 plupload  begin
            json.put("OK", 1);
            json.put(Environment.message, "chunk upload " + language.getLang(LanguageRes.success));
            json.put("success", true);
            //兼容 plupload  end
            json.put("chunked", 3);
            return json;
        }
        //分片上传 end

    }

    /**
     * @param info 打印错误信息
     */
    protected void printErrorInfo(String info) {
        if (response.isCommitted())
        {
            return;
        }
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("repair", false);
        json.put("success", false);
        json.put("error", 1);
        json.put("state", "error");
        json.put("thumbnail", 0);
        json.put("fileName", StringUtil.empty);
        json.put("url", StringUtil.empty);
        json.put("original", StringUtil.empty);
        json.put("namespace", uploadFileDAO.getNamespace());
        json.put(Environment.message, info);
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }

    /**
     * 需要登陆
     */
    protected void printNoLoginError() {
        if (response.isCommitted())
        {
            return;
        }
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        json.put("repair", false);
        json.put("error", 1);
        json.put("state", "error");
        json.put("thumbnail", 0);
        json.put(Environment.message, "need login");
        json.put("namespace", getRootNamespace());
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response, ErrorEnumType.NEED_LOGIN.getValue());
    }

    /**
     * 打印uploadFileDAO配置错误
     */
    protected void printDaoError() {
        if (response.isCommitted())
        {
            return;
        }
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        json.put("repair", false);
        json.put("error", 1);
        json.put("state", "error");
        json.put("thumbnail", 0);
        json.put(Environment.message, "not config uploadFileDAO");
        json.put("namespace", getRootNamespace());
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }

    /**
     * 打印请求错误
     */
    protected void printRequestError() {
        JSONObject json = new JSONObject();
        json.put("OK", 0);
        json.put("success", false);
        json.put("repair", false);
        json.put("error", 1);
        json.put("state", "error");
        json.put("fileName", "");
        json.put("url", "");
        json.put("original", "");
        json.put("name", "");
        json.put("thumbnail", 0);
        json.put("namespace", uploadFileDAO.getNamespace());
        json.put(Environment.message, language.getLang(LanguageRes.uploadRequestError));
        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }


    /**
     *
     * @param objects 上次的对象
     * @param chunkJson 分片上传信息
     * @param thumbnail 是否有缩图
     * @param mobile 是否有手机
     * @param namespace 命名空间
     * @param message 消息
     * @param response 应答
     */
    public static void printUploadFileInfo(Object[]  objects, JSONObject chunkJson, boolean thumbnail, boolean mobile, String namespace, String message, HttpServletResponse response)
    {

        IUploadFile uploadFile = (IUploadFile)objects[0];
        JSONObject json;
        if (chunkJson==null)
        {
            json = new JSONObject();
        } else
        {
            json = chunkJson;
        }

        json.put("OK", 0);
        json.put("success", false);
        json.put("fileName", uploadFile.getFileName());
        json.put("name", uploadFile.getTitle());
        json.put("uploadType", "file");
        if (URLUtil.isUrl(uploadFile.getFileName())) {
            json.put("url", uploadFile.getFileName());
        } else {
            if (uploadFile.getFileName().startsWith('/' + namespace + '/')) {
                json.put("url", uploadFile.getFileName());
            } else {
                json.put("url", '/' + namespace + '/' + uploadFile.getFileName());
            }
        }

        //缩图
        if (thumbnail && objects.length>1) {
            IUploadFile thumbnailUploadFile = (IUploadFile)objects[1];
            if (URLUtil.isUrl(thumbnailUploadFile.getFileName())) {
                json.put("thumbnailUrl", thumbnailUploadFile.getFileName());
            } else {
                if (thumbnailUploadFile.getFileName().startsWith('/' + namespace + '/')) {
                    json.put("thumbnailUrl", thumbnailUploadFile.getFileName());
                } else {
                    json.put("thumbnailUrl", '/' + namespace + '/' + thumbnailUploadFile.getFileName());
                }
            }
        }

        if (mobile && objects.length>2) {
            IUploadFile mobileUploadFile = (IUploadFile)objects[2];
            if (URLUtil.isUrl(mobileUploadFile.getFileName())) {
                json.put("mobileUrl", mobileUploadFile.getFileName());
            } else {
                if (mobileUploadFile.getFileName().startsWith('/' + namespace + '/')) {
                    json.put("mobileUrl", mobileUploadFile.getFileName());
                } else {
                    json.put("mobileUrl", '/' + namespace + '/' + mobileUploadFile.getFileName());
                }
            }
        }
        json.put("name", uploadFile.getTitle());
        json.put("original", uploadFile.getTitle());
        json.put("title", uploadFile.getTitle());
        json.put("size", uploadFile.getFileSize());
        json.put("type", uploadFile.getFileType());
        json.put("id", uploadFile.getId());
        json.put("error", 0);
        json.put("fileType", uploadFile.getFileType());
        json.put("namespace", namespace);
        json.put("state", "SUCCESS");
        json.put("success", true);
        //兼容 plupload  begin
        json.put("OK", 1);
        //兼容 plupload  end
        json.put(Environment.message, message);

        TXWebUtil.print(json.toString(), WebOutEnumType.JSON.getValue(), response);
    }

}

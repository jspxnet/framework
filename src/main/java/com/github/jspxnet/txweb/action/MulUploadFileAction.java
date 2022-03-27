package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.component.zhex.ChineseAnalyzer;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.ReadPdfTextFile;
import com.github.jspxnet.io.ReadWordTextFile;
import com.github.jspxnet.network.oss.CloudFileClient;
import com.github.jspxnet.network.oss.CloudServiceFactory;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.MulRequest;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.txweb.enums.FileCoveringPolicyEnumType;
import com.github.jspxnet.txweb.enums.ImageSysEnumType;
import com.github.jspxnet.txweb.enums.UploadVerifyEnumType;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.MultipartRequest;
import com.github.jspxnet.txweb.support.MultipartSupport;
import com.github.jspxnet.txweb.table.CloudFileConfig;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.upload.UploadedFile;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public class MulUploadFileAction extends MultipartSupport {
    final static public String[] OFFICE_FILE_TYPES = FileSuffixUtil.OFFICE_TYPES;
    final static public String[] STOP_EXS = new String[]{"php", "jsp", "ftl", "html", "htm", "exe", "com", "bat", "asp", "aspx", "sh", "jar", "js", "dll"};

    //分组变量名称
    final public static String GROUP_VAR_NAME = "groupName";
    final public static String TITLE_VAR_NAME = "title";
    final public static String CONTENT_VAR_NAME = "content";

    //宽高控制
    final public static String THUMBNAIL_WIDTH_VAR_NAME = "thumbnailWidth";
    final public static String THUMBNAIL_HEIGHT_VAR_NAME = "thumbnailHeight";
    final public static String MAX_IMAGE_WIDTH_HEIGHT = "maxImageWidthHeight";

    //数据的签名
    final public static String SIGNATURE_KEY = "signature";

    //时间戳 变量
    final public static String TIMESTAMP_KEY = "timestamp";

    final private static String WIDTH_NAME = "width";
    final private static String HEIGHT_NAME = "height";

    final public static String ORGANIZE_ID = "organizeId";

    final public static String SYS_TYPE = "sysType";

    final public static String THUMBNAIL_VAR_NAME = "thumbnail";

    // 状态
    public static String hashType = "MD5";

    //是否开启云盘上传
    protected boolean useCloudFile = false;

    private boolean useOriginalDate = false;

    private boolean useFastUpload = false;

    private boolean useSave = true;

    //使用扩展功能， 裁剪，多图 等
    private boolean useExpand = true;

    public MulUploadFileAction() {

    }

    @Param(request = false)
    public void setUseExpand(boolean useExpand) {
        this.useExpand = useExpand;
    }

    @Param(request = false)
    public void setUseSave(boolean useSave) {
        this.useSave = useSave;
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

    @Param(caption = "是否是用快传", request = false)
    public void setUseFastUpload(boolean useFastUpload) {
        this.useFastUpload = useFastUpload;
    }

    //验证方式 0:游客方式不允许上传,其他通过角色配置  默认  1:游客也放开； 2:通过验证ApiKey判断
    private int verifyType = UploadVerifyEnumType.DEFAULT.getValue();

    @Param(request = false)
    public void setVerifyType(int verifyType) {
        this.verifyType = verifyType;
    }

    private String apiKey = StringUtil.empty;

    @Param(request = false)
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected String getApiKey() {
        return apiKey;
    }

    public boolean isUseFastUpload() {
        return useFastUpload;
    }

    protected String getOrganizeId() {
        if (!ObjectUtil.isEmpty(getString(ORGANIZE_ID))) {
            return getString(ORGANIZE_ID);
        }
        return uploadFileDAO.getOrganizeId();
    }

    protected int getSysType(ImageSysEnumType imageSysEnumType) {
        return getInt(SYS_TYPE, imageSysEnumType.getValue());
    }

    protected int getMaxImageWidth() {
        return getInt(MAX_IMAGE_WIDTH_HEIGHT, config.getInt(Environment.maxImageWidth, 1280));
    }

    protected int getMaxImageHeight() {
        return getInt(MAX_IMAGE_WIDTH_HEIGHT, config.getInt(Environment.maxImageHeight, 1280));
    }

    //得到请求 签名
    protected String getRequestSignature() {
        return getString(SIGNATURE_KEY, true);
    }

    //得到请求的 时间戳
    protected String getRequestTimestamp() {
        return getString(TIMESTAMP_KEY, true);
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
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.IMAGE_TYPES);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.ZIP_TYPES);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.VIDEO_TYPES);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.OFFICE_TYPES);
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
        if (maxPostSize == 0 && config != null) {
            maxPostSize = config.getInt(Environment.uploadMaxSize, 100 * 1024) * 1024;
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
     */

    public String getSetupPath() {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        if (!FileUtil.isDirectory(setupPath)) {
            setupPath = FileUtil.mendPath(FileUtil.getParentPath(getTemplatePath()));
            try {
                config.save(Environment.setupPath, setupPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
     * @param multipartRequest 请求接口
     */
    @Override
    @Param(request = false)
    @MulRequest(component = "apache", covering = FileCoveringPolicyEnumType.JSPX, saveDirectory = "@saveDirectory", fileTypes = "@fileTypes", maxPostSize = "@maxPostSize")
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
    @Ref(test = true)
    private ChineseAnalyzer chineseAnalyzer;

    @Operate(caption = "hash验证")
    public RocResponse<?> hasHash(@Param(caption = "hash", required = true) String hash) {
        if (UploadVerifyEnumType.DEFAULT.getValue() == verifyType && isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        if (UploadVerifyEnumType.API_KEY_1.getValue() == verifyType && !getRequestSignature().equalsIgnoreCase(apiKey)) {
            return RocResponse.error(ErrorEnumType.KEY_VERIFY);
        }

        if (UploadVerifyEnumType.API_KEY_2.getValue() == verifyType) {
            String signature = EncryptUtil.getMd5(apiKey + getRequestTimestamp());
            if (!signature.equalsIgnoreCase(getRequestSignature())) {
                return RocResponse.error(ErrorEnumType.SIGNATURE_VERIFY);
            }
        }

        if (!useFastUpload) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(), "秒传已经关闭");
        }
        if (StringUtil.isNull(hash)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }

        Object alreadyUploadFile = uploadFileDAO.getForHash(hash);
        IUploadFile checkUploadFile = (IUploadFile) alreadyUploadFile;
        if (checkUploadFile != null && hash.equalsIgnoreCase(checkUploadFile.getHash())) {
            return RocResponse.success(hash).setMessage(language.getLang(LanguageRes.alreadyExist));
        }
        return RocResponse.success(0, "验证失败");
    }

    @Operate(caption = "秒传")
    public RocResponse<IUploadFile[]> fastUpload(@Param(caption = "hash", required = true) String hash) {
        if (UploadVerifyEnumType.DEFAULT.getValue() == verifyType && isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        if (UploadVerifyEnumType.API_KEY_1.getValue() == verifyType && !getRequestSignature().equalsIgnoreCase(apiKey)) {
            return RocResponse.error(ErrorEnumType.KEY_VERIFY);
        }

        if (UploadVerifyEnumType.API_KEY_2.getValue() == verifyType) {
            String signature = EncryptUtil.getMd5(apiKey + getRequestTimestamp());
            if (!signature.equalsIgnoreCase(getRequestSignature())) {
                return RocResponse.error(ErrorEnumType.SIGNATURE_VERIFY);
            }
        }
        if (!useFastUpload) {
            return RocResponse.error(ErrorEnumType.CONFIG.getValue(), "秒传已经关闭");
        }

        if (StringUtil.isNull(hash)) {
            return RocResponse.error(ErrorEnumType.CONFIG.getValue(), language.getLang(LanguageRes.invalidParameter));
        }

        IUserSession userSession = getUserSession();
        Object alreadyUploadFile = uploadFileDAO.getForHash(hash);
        IUploadFile checkUploadFile = (IUploadFile) alreadyUploadFile;
        if (useFastUpload && checkUploadFile != null && !StringUtil.isNull(hash) && fileEquals(checkUploadFile.getHash(), hash)) {
            IUploadFile[] copyUploadFile = copyUploadToUser(alreadyUploadFile, userSession);
            if (ObjectUtil.isEmpty(copyUploadFile)) {
                return RocResponse.error(ErrorEnumType.CONFIG.getValue(), "不支持秒传");
            }
            return RocResponse.success(copyUploadFile, language.getLang(LanguageRes.success));
        }
        return RocResponse.success(null, "需要重传");
    }

    private String fileName = StringUtil.empty;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Operate(caption = "续传判断")
    public RocResponse<?> lastChunk() {
        if (UploadVerifyEnumType.DEFAULT.getValue() == verifyType && isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        if (UploadVerifyEnumType.API_KEY_1.getValue() == verifyType && !getRequestSignature().equalsIgnoreCase(apiKey)) {
            return RocResponse.error(ErrorEnumType.KEY_VERIFY);
        }

        if (UploadVerifyEnumType.API_KEY_2.getValue() == verifyType) {
            String signature = EncryptUtil.getMd5(apiKey + getRequestTimestamp());
            if (!signature.equalsIgnoreCase(getRequestSignature())) {
                return RocResponse.error(ErrorEnumType.SIGNATURE_VERIFY);
            }
        }

        IUserSession userSession = getUserSession();
        UploadedFile uf = new UploadedFile(fileName, getUploadDirectory(config), fileName, fileName, "application/octet-stream", FileUtil.getTypePart(fileName));
        int diskChunk = uf.getLastChunk(NumberUtil.toString(userSession.getUid()));
        RocResponse<?> rocResponse = new RocResponse<>();

        rocResponse.setProperty("lastChunk", diskChunk);
        rocResponse.setProperty("chunks", uf.getChunks());

        //兼容 plupload  begin
        if (diskChunk > 1) {
            rocResponse.setSuccess(YesNoEnumType.YES.getValue());
            rocResponse.setMessage("chunk upload " + language.getLang(LanguageRes.success));
        }
        return rocResponse;
    }


    /**
     * 兼容swfupload 和 kindeditor, 和UEditor 1.2.0 的返回结果，当然普通页面上传也没问题
     *
     * @return json 返回结果
     * @throws Exception 异常
     */
    @Operate(caption = "上传", method = "upload")
    public RocResponse<IUploadFile[]> upload() throws Exception {

        if (useSave && uploadFileDAO == null) {
            return RocResponse.error(ErrorEnumType.CONFIG.getValue(), "DAO配置错误");
        }
        //验证环境
        if (multipartRequest == null && !RequestUtil.isMultipart(request) && !response.isCommitted()) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), language.getLang(LanguageRes.uploadRequestError));
        }

        if (UploadVerifyEnumType.DEFAULT.getValue() == verifyType && isGuest()) {
            log.info(language.getLang(LanguageRes.needLogin));
            for (UploadedFile uf : multipartRequest.getFiles()) {
                FileUtil.delete(uf.getFile());
            }
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        if (UploadVerifyEnumType.API_KEY_1.getValue() == verifyType && !getRequestSignature().equalsIgnoreCase(apiKey)) {
            for (UploadedFile uf : multipartRequest.getFiles()) {
                FileUtil.delete(uf.getFile());
            }
            return RocResponse.error(ErrorEnumType.KEY_VERIFY);
        }

        if (UploadVerifyEnumType.API_KEY_2.getValue() == verifyType) {
            String signature = EncryptUtil.getMd5(apiKey + getRequestTimestamp());
            if (!signature.equalsIgnoreCase(getRequestSignature())) {
                for (UploadedFile uf : multipartRequest.getFiles()) {
                    FileUtil.delete(uf.getFile());
                }
                return RocResponse.error(ErrorEnumType.SIGNATURE_VERIFY);
            }
        }

        IUserSession userSession = getUserSession();
        boolean thumbnail = getBoolean(THUMBNAIL_VAR_NAME);
        IUploadFile[] objects = localUploadFile(userSession, thumbnail);
        if (ObjectUtil.isEmpty(objects)) {
            return RocResponse.error(ErrorEnumType.UNKNOWN.getValue(), "删除异常");
        }
        return RocResponse.success(objects).setProperty("thumbnail", thumbnail).setProperty("namespace", uploadFileDAO.getNamespace()).setMessage(language.getLang(LanguageRes.success));
    }

    @Destroy
    @Override
    public String execute() throws Exception {
        uploadFileDAO.evict(uploadFileDAO.getClassType());
        if (multipartRequest != null) {
            multipartRequest.destroy();
        }
        multipartRequest = null;
        return super.execute();
    }

    /**
     *
     * @param userSession 用户信息
     * @param thumbnail 是否生成缩图
     * @return 上传的数据列表
     * @throws Exception 异常
     */
    public IUploadFile[] localUploadFile(IUserSession userSession, boolean thumbnail) throws Exception {
        String setupPath = getSetupPath();

        String[] titleArray = multipartRequest.getParameterValues(TITLE_VAR_NAME);
        String[] contentArray = multipartRequest.getParameterValues(CONTENT_VAR_NAME);
        IUploadFile[] uploadObjArray = null;
        int index = -1;
        for (UploadedFile uf : multipartRequest.getFiles()) {
            index++;
            if (ArrayUtil.inArray(STOP_EXS, uf.getFileType(), true)) {
                FileUtil.delete(uf.getFile());
                throw new Exception(language.getLang(LanguageRes.notAllowedFileType) + StringUtil.COLON + uf.getFileType());
            }
            if (!uf.isUpload()) {
                FileUtil.delete(uf.getFile());
                throw new Exception(language.getLang(LanguageRes.notAllowedFileTypeOrUploadError));
            }

            if (!uf.moveToTypeDir()) {
                //没有移动成功的文件
                throw new Exception(language.getLang(LanguageRes.folderWriteError));
            }

            String hashCode = FileUtil.getFileGuid(uf.getFile(), hashType);
            Object saveUploadFile = null;
            try {
                saveUploadFile = uploadFileDAO.getClassType().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("not find class {}", uploadFileDAO.getClassType(), e);
            }
            IUploadFile upFile = (IUploadFile) saveUploadFile;

            upFile.setTitle(ArrayUtil.get(titleArray, index, FileUtil.getNamePart(uf.getOriginal())));
            upFile.setContent(ArrayUtil.get(contentArray, index, FileUtil.getNamePart(uf.getOriginal())));
            //这里只是修复，不影响
            if ("Filedata".equalsIgnoreCase(upFile.getTitle()) || "blob".equalsIgnoreCase(upFile.getTitle())) {
                upFile.setTitle(FileUtil.getNamePart(uf.getFileName()));
            }
            upFile.setFileName(FileUtil.mendPath(FileUtil.getDecrease(setupPath, uf.getDir())) + uf.getFileName());
            upFile.setFileSize(uf.getFile().length());
            upFile.setFileType(StringUtil.toLowerCase(uf.getFileType()));

            if (chineseAnalyzer != null) {
                try {
                    upFile.setTags(chineseAnalyzer.getTag(uf.getOriginal(), StringUtil.space, 3, true));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("upFile.setTags", e);
                }
            }
            upFile.setGroupName(getString(GROUP_VAR_NAME, "未分类", true));
            upFile.setPutName(userSession.getName());
            upFile.setPutUid(userSession.getUid());
            upFile.setIp(getRemoteAddr());
            upFile.setOrganizeId(getOrganizeId());
            upFile.setHash(hashCode);
            upFile.setNamespace(uploadFileDAO.getNamespace());
            upFile.setSysType(getSysType(ImageSysEnumType.NONE));

            Object alreadyUploadFile = null;
            if (useFastUpload && fileEquals(hashCode, upFile.getHash())) {
                alreadyUploadFile = uploadFileDAO.getForHash(hashCode);
            }
            IUploadFile checkUploadFile = (IUploadFile) alreadyUploadFile;
            if (useFastUpload && checkUploadFile != null && checkUploadFile.getPid() == 0 && !StringUtil.isNull(upFile.getHash()) &&
                    fileEquals(checkUploadFile.getHash(), upFile.getHash()) && checkUploadFile.getPutUid() == userSession.getUid()
                    && (checkUploadFile.getOrganizeId() != null && checkUploadFile.getOrganizeId().equals(getOrganizeId())
                    && checkUploadFile.getFileSize() == upFile.getFileSize())) {
                //已经上传过的,拷贝一份
                return copyUploadToUser(alreadyUploadFile, userSession);
            } else {
                //还没有保存到数据库  开始保存一个上传数据 begin
                Object[] oneUploadObjArray = newUpload(uf, upFile, thumbnail, userSession);
                if (ObjectUtil.isEmpty(oneUploadObjArray)) {
                    continue;
                }
                if (!useCloudFile && oneUploadObjArray[0] != null) {
                    //上传到本地目录
                    if (useSave) {
                        try {
                            uploadFileDAO.save(oneUploadObjArray[0]);
                            uploadObjArray = ArrayUtil.add(uploadObjArray, oneUploadObjArray[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    IUploadFile uploadFile = (IUploadFile) oneUploadObjArray[0];
                    if (uploadFile != null) {
                        long pid = uploadFile.getId();
                        if (pid != 0) {
                            for (int i = 1; i < oneUploadObjArray.length; i++) {
                                if (oneUploadObjArray[i] == null) {
                                    continue;
                                }
                                IUploadFile tmpUploadFile = (IUploadFile) oneUploadObjArray[i];
                                tmpUploadFile.setPid(pid);
                                if (useSave) {
                                    try {
                                        uploadFileDAO.save(oneUploadObjArray[i]);
                                        uploadObjArray = (IUploadFile[]) ArrayUtil.add(uploadObjArray, oneUploadObjArray[i]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                    }
                } else {
                    //上传到云盘
                    CloudFileConfig cloudFileConfig = uploadFileDAO.getCloudFileConfig();
                    AssertException.isNull(cloudFileConfig, "云盘空间没有配置");
                    CloudFileClient cloudFileClient = CloudServiceFactory.createCloudClient(cloudFileConfig);
                    AssertException.isNull(cloudFileClient, "云盘空间没有配置正确");
                    long pid = 0;
                    for (int i = 0; i < oneUploadObjArray.length; i++) {
                        if (oneUploadObjArray[i] == null) {
                            continue;
                        }
                        IUploadFile tmpUploadFile = (IUploadFile) oneUploadObjArray[i];
                        File localFile = new File(tmpUploadFile.getTempFilePath());
                        String createUrlFileName = cloudFileConfig.getNamespace() + StringUtil.BACKSLASH + RandomUtil.getRandomGUID(28) + StringUtil.DOT + FileUtil.getTypePart(localFile);
                        String cloudUrl = null;
                        try {
                            cloudUrl = cloudFileClient.upload(createUrlFileName, localFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("cloudFileClient.upload", e);
                        }
                        tmpUploadFile.setFileName(cloudUrl);
                        StringMap<String, String> attributeMap = tmpUploadFile.getAttributeMap();
                        attributeMap.put("configId", cloudFileConfig.getId() + "");
                        attributeMap.put("bucket", cloudFileConfig.getBucket());
                        tmpUploadFile.setAttributes(attributeMap.toString());
                        if (i == 0) {
                            if (useSave) {
                                try {
                                    uploadFileDAO.save(oneUploadObjArray[i]);
                                    uploadObjArray = (IUploadFile[]) ArrayUtil.add(uploadObjArray, oneUploadObjArray[i]);
                                    pid = tmpUploadFile.getId();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.error("uploadFileDAO.save", e);
                                }
                            }
                        } else if (useSave) {
                            tmpUploadFile.setPid(pid);
                            try {
                                uploadFileDAO.save(oneUploadObjArray[i]);
                                uploadObjArray = (IUploadFile[]) ArrayUtil.add(uploadObjArray, oneUploadObjArray[i]);
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("uploadFileDAO.save", e);
                            }
                        }
                    }
                }
                //还没有保存到数据库  开始保存一个上传数据 end
            }
        }
        return uploadObjArray;
    }

    /**
     * @param uf             上传的文件
     * @param saveUploadFile 上传将保存的对象
     * @param userSession    用户信息
     * @return 图片正常是3张
     */
    protected IUploadFile[] newUpload(UploadedFile uf, Object saveUploadFile, boolean thumbnail, IUserSession userSession) {
        IUploadFile upFile = (IUploadFile) saveUploadFile;

        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        //没有上传过的
        //已经上传成功的,还没有的上传上去的就上传上去
        IUploadFile[] result = new IUploadFile[2];
        File file = uf.getFile();
        upFile.setTempFilePath(file.getPath());

        if (useExpand && FileSuffixUtil.isImageSuffix(uf.getFileType())) {
            //如果是图片就得到宽高
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } catch (IOException e1) {
                log.info("未知不能识别的图片格式:" + file.getPath());
            }

            if (config.getBoolean(Environment.EXIF_SATE)) {
                StringMap<String, String> map = ImageUtil.parsePhotoExif(file, ImageUtil.simpleExifTags);
                upFile.setAttributes(map.toString());
            }
            int maxImageWidth = getMaxImageWidth();
            int maxImageHeight = getMaxImageHeight();
            if (image != null && (image.getHeight() > maxImageHeight || image.getWidth() > maxImageWidth)) {
                boolean repair = false;
                try {
                    repair = ImageUtil.thumbnail(image, new FileOutputStream(file), uf.getFileType(), Math.min(maxImageWidth, image.getWidth()), Math.min(image.getHeight(), maxImageHeight));
                    image = ImageIO.read(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("ImageUtil.thumbnail", e);
                }
                if (image != null) {
                    StringMap<String, String> attributeMap = upFile.getAttributeMap();
                    attributeMap.put(WIDTH_NAME, NumberUtil.toString(image.getWidth()));
                    attributeMap.put(HEIGHT_NAME, NumberUtil.toString(image.getHeight()));
                    attributeMap.put("repair", repair + "");
                    upFile.setAttributes(attributeMap.toString());
                }
            } else if (image != null) {
                StringMap<String, String> attributeMap = upFile.getAttributeMap();
                attributeMap.put(WIDTH_NAME, NumberUtil.toString(image.getHeight()));
                attributeMap.put(HEIGHT_NAME, NumberUtil.toString(image.getHeight()));
                upFile.setAttributes(attributeMap.toString());
            }
            upFile.setFileSize(file.length());

            String thumbnailImg = FileUtil.getThumbnailFileName(uf.getFileName());
            if (thumbnail) {
                int width = getInt(THUMBNAIL_WIDTH_VAR_NAME, config.getInt(THUMBNAIL_WIDTH_VAR_NAME, 400));
                int height = getInt(THUMBNAIL_HEIGHT_VAR_NAME, config.getInt(THUMBNAIL_HEIGHT_VAR_NAME, 400));
                //创建缩图
                File thumbnailFile = new File(file.getParent(), thumbnailImg);
                try {
                    if (ImageUtil.thumbnail(image, new FileOutputStream(thumbnailFile), uf.getFileType(), width, height)) {
                        String thumbnailPath = FileUtil.mendPath(FileUtil.getDecrease(setupPath, uf.getDir())) + thumbnailImg;
                        IUploadFile thumbnailUploadFile = (IUploadFile) uploadFileDAO.getClassType().newInstance();
                        thumbnailUploadFile.setHash(FileUtil.getFileGuid(thumbnailFile, hashType));
                        thumbnailUploadFile.setTitle(upFile.getTitle());
                        thumbnailUploadFile.setContent(upFile.getContent());
                        thumbnailUploadFile.setFileType(StringUtil.toLowerCase(FileUtil.getTypePart(thumbnailFile)));
                        thumbnailUploadFile.setFileName(thumbnailPath);
                        thumbnailUploadFile.setPutName(userSession.getName());
                        thumbnailUploadFile.setPutUid(userSession.getUid());
                        thumbnailUploadFile.setFileSize(thumbnailFile.length());
                        StringMap<String, String> attributeMap = upFile.getAttributeMap();
                        attributeMap.put(WIDTH_NAME, NumberUtil.toString(image.getWidth()));
                        attributeMap.put(HEIGHT_NAME, NumberUtil.toString(image.getHeight()));
                        thumbnailUploadFile.setAttributes(attributeMap.toString());
                        thumbnailUploadFile.setSortType(0);
                        thumbnailUploadFile.setSortDate(new Date());
                        thumbnailUploadFile.setCreateDate(new Date());
                        thumbnailUploadFile.setSysType(getSysType(ImageSysEnumType.THUMBNAIL));
                        thumbnailUploadFile.setTempFilePath(thumbnailFile.getPath());
                        thumbnailUploadFile.setOrganizeId(getOrganizeId());
                        thumbnailUploadFile.setNamespace(uploadFileDAO.getNamespace());
                        result[1] = thumbnailUploadFile;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("ImageUtil.thumbnail :{}", thumbnailFile, e);
                }
            }
        }
        boolean useUploadConverter = config.getBoolean(Environment.useUploadConverterTxt);
        if (useExpand && useUploadConverter && ArrayUtil.inArray(OFFICE_FILE_TYPES, uf.getFileType(), true)) {
            //文档转换,为了方便安卓下编译
            String content = "";
            try {
                if ("doc".equalsIgnoreCase(uf.getFileType()) || "docx".equalsIgnoreCase(uf.getFileType())) {
                    AbstractRead abstractRead = new ReadWordTextFile();
                    abstractRead.setFile(uf.getFile());
                    content = abstractRead.getContent();
                }
                if ("pdf".equalsIgnoreCase(uf.getFileType())) {
                    AbstractRead abstractRead = new ReadPdfTextFile();
                    abstractRead.setFile(uf.getFile());
                    content = abstractRead.getContent();
                }
                content = StringUtil.cut(content, 2000, StringUtil.empty);
                upFile.setContent(content);
                if (chineseAnalyzer != null) {
                    upFile.setTags(chineseAnalyzer.getTag(content, StringUtil.space, 6, true));
                }
            } catch (Exception e) {
                upFile.setContent(uf.getOriginal());
                if (chineseAnalyzer != null) {
                    try {
                        upFile.setTags(chineseAnalyzer.getTag(uf.getOriginal(), StringUtil.space, 3, true));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        log.error("upFile.setTags", ex);
                    }
                }
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
        result[0] = upFile;
        return result;
    }

    /**
     * 处理已经上传过的返回
     *
     * @param alreadyUploadFile 已经上传的文件
     */
    private IUploadFile[] copyUploadToUser(Object alreadyUploadFile, IUserSession userSession) {
        Object copyUploadFile = BeanUtil.copy(alreadyUploadFile, uploadFileDAO.getClassType());
        IUploadFile copySaveUploadFile = (IUploadFile) copyUploadFile;
        long oldId = copySaveUploadFile.getId();
        copySaveUploadFile.setOrganizeId(getOrganizeId());
        copySaveUploadFile.setPutName(userSession.getName());
        copySaveUploadFile.setPutUid(userSession.getUid());
        copySaveUploadFile.setSortDate(new Date());
        copySaveUploadFile.setCreateDate(new Date());
        copySaveUploadFile.setGroupName(getString(GROUP_VAR_NAME, "未分类", true));
        copySaveUploadFile.setSortType(0);
        copySaveUploadFile.setId(0);
        copySaveUploadFile.setNamespace(uploadFileDAO.getNamespace());
        long newPid = 0;
        if (useSave) {
            try {
                uploadFileDAO.save(copyUploadFile);
                newPid = copySaveUploadFile.getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Object> childList = uploadFileDAO.getChildFileList(oldId);
        if (childList.isEmpty()) {
            IUploadFile[] objects = new IUploadFile[1];
            objects[0] = copySaveUploadFile;
            return objects;
        }
        List<?> saveChildList = BeanUtil.copyList(childList, uploadFileDAO.getClassType());
        IUploadFile[] objects = new IUploadFile[saveChildList.size() + 1];
        objects[0] = copySaveUploadFile;
        if (useSave) {
            for (int i = 0; i < saveChildList.size(); i++) {
                IUploadFile childUploadFile = (IUploadFile) childList.get(i);
                childUploadFile.setOrganizeId(getOrganizeId());
                childUploadFile.setPutName(userSession.getName());
                childUploadFile.setPutUid(userSession.getUid());
                childUploadFile.setSortDate(new Date());
                childUploadFile.setCreateDate(new Date());
                childUploadFile.setGroupName(getString(GROUP_VAR_NAME, "未分类", true));
                childUploadFile.setSortType(0);
                childUploadFile.setPid(newPid);
                childUploadFile.setId(0);
                childUploadFile.setNamespace(uploadFileDAO.getNamespace());
                try {
                    uploadFileDAO.save(childUploadFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                objects[i + 1] = childUploadFile;
            }
        }
        return objects;
    }
}

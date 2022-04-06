package com.github.jspxnet.txweb.ueditor.adaptor;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.action.UploadFileAction;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.dao.UploadFileDAO;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.FileCoveringPolicyEnumType;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.support.MultipartRequest;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.ueditor.ConfigManager;
import com.github.jspxnet.txweb.ueditor.StorageManager;
import com.github.jspxnet.txweb.ueditor.define.*;
import com.github.jspxnet.txweb.ueditor.hunter.FileManager;
import com.github.jspxnet.txweb.ueditor.hunter.ImageHunter;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.upload.multipart.RenamePolicy;
import com.github.jspxnet.utils.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by chenyuan on 2015-8-6.
 * 为了兼容UEditor 的适配器而设计
 * UEditor 1.43版本
 * <p>
 * 配置在
 * ,serverUrl: "${action.getString('namespace')}ueditorcontroller.js.${suffix}"
 *
 */
@HttpMethod(caption = "UEditor上传适配器")
public class UEditorAdaptor extends ActionSupport {

    @Ref
    private UploadFileDAO uploadFileDAO;

    @Ref
    private UploadFileAction uploadFileAction;


    /**
     * @return 得到配置允许上传的文件类型
     */
    @Operate(caption = "得到配置允许上传的文件类型",post = false)
    public String getFileTypes() {
        String fileTypes = StringUtil.ASTERISK;
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            IRole role = userSession.getRole(uploadFileDAO.getNamespace(),uploadFileDAO.getOrganizeId());
            if (role!=null)
            {
                fileTypes = role.getUploadFileTypes();
            }
        }
        if (StringUtil.isNull(fileTypes) && config != null) {
            fileTypes = config.getString(Environment.allowedTypes);
        }
        return fileTypes;
    }

    /**
     * @return 得到安装路径
     * @throws Exception 异常
     */
    private String getSetupPath() throws Exception {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        if (!FileUtil.isDirectory(setupPath)) {
            setupPath = FileUtil.mendPath(FileUtil.getParentPath(getTemplatePath()));
            config.save(Environment.setupPath, setupPath);
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.setupConfigPathError) + ":" + setupPath);
        }
        return setupPath;
    }

    /**
     * @return 最大上传限制
     */
    @Operate(caption = "最大上传限制",post = false)
    public int getMaxPostSize() {
        IUserSession userSession = getUserSession();
        int maxPostSize = 0;
        if (userSession != null) {
            IRole role = userSession.getRole(uploadFileDAO.getNamespace(),uploadFileDAO.getOrganizeId());
            if (role!=null)
            {
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
    @Operate(caption = "得到上传路径",post = false)
    public String getSaveDirectory() {
        return UploadFileAction.getUploadDirectory(config);
    }

    private ConfigManager configManager = null;


    /**
     * @param name 参数名称
     * @return callback参数验证
     */
    public boolean validCallbackName(String name) {
        return name.matches("^[a-zA-Z_]+[\\w0-9_]*$");
    }

    public int getStartIndex() {

        return getInt("start", 0);
    }

    private String invoke() throws Exception {

        String saveDirectory = getSaveDirectory();
        String setupPath = getSetupPath();
        long maxSize = getMaxPostSize();
        String[] fileTypes = StringUtil.split(getFileTypes(), StringUtil.SEMICOLON);
        String actionType = getString("action", true);
        if (actionType == null || !ActionMap.MAPPING.containsKey(actionType)) {
            return new BaseState(false, AppInfo.INVALID_ACTION).toJsonString();
        }
        if (this.configManager == null || !this.configManager.valid()) {
            return new BaseState(false, AppInfo.CONFIG_ERROR).toJsonString();
        }

        State state = null;
        int actionCode = ActionMap.getType(actionType);

        JSONObject jsonConfig = configManager.getAllConfig();
        if (jsonConfig.getInt("imageMaxSize") == 0) {
            jsonConfig.put("imageMaxSize", maxSize);
        }

        if (jsonConfig.getInt("scrawlMaxSize") == 0) {
            jsonConfig.put("scrawlMaxSize", maxSize);
        }

        jsonConfig.put("imagePathFormat", saveDirectory);
        jsonConfig.put("scrawlPathFormat", saveDirectory);
        jsonConfig.put("snapscreenPathFormat", saveDirectory);
        jsonConfig.put("catcherPathFormat", saveDirectory);
        jsonConfig.put("videoPathFormat", saveDirectory);
        jsonConfig.put("filePathFormat", saveDirectory);
        jsonConfig.put("imageManagerListPath", saveDirectory);
        jsonConfig.put("fileManagerListPath", saveDirectory);

        Map<String, Object> conf = null;

        switch (actionCode) {

            case ActionMap.CONFIG:
                return jsonConfig.toString();
            case ActionMap.UPLOAD_IMAGE:
            case ActionMap.UPLOAD_SCRAWL:
            case ActionMap.UPLOAD_VIDEO:
            case ActionMap.UPLOAD_FILE:
                conf = this.configManager.getConfig(actionCode);
                String filedName = (String) conf.get("fieldName");
                if ("true".equals(conf.get("isBase64"))) {
                    state = base64Save(this.request.getParameter(filedName), saveDirectory, setupPath, maxSize);
                } else {
                    //saveDirectory,setupPath,maxSize,fileTypes
                    state = binarySave();
                }
                break;
            case ActionMap.CATCH_IMAGE:
                conf = configManager.getConfig(actionCode);
                String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
                String[] filter = (String[]) conf.get("filter");
                state = new ImageHunter(saveDirectory, System.currentTimeMillis() + "", maxSize, fileTypes, filter).capture(list);
                break;

            case ActionMap.LIST_IMAGE:
            case ActionMap.LIST_FILE:
                conf = configManager.getConfig(actionCode);
                int start = this.getStartIndex();
                int count = ObjectUtil.toInt(conf.get("count"));
                String[] allowFiles = (String[]) conf.get("allowFiles");
                state = new FileManager(saveDirectory, Dispatcher.getRealPath(), allowFiles, count).listFile(start);
                break;
            default:{
                break;
            }
        }
        return state.toJsonString();

    }

    private State binarySave() {
        boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;
        if (isAjaxUpload) {
            try {
                request.setCharacterEncoding(Environment.defaultEncode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (!RequestUtil.isMultipart(request)) {
            return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
        }
        try {
            uploadFileAction.setEditorUpload(true);
            uploadFileAction.setUseFastUpload(false);
            uploadFileAction.setRequest(request);
            uploadFileAction.setResponse(response);
            uploadFileAction.setConfig(config);
            uploadFileAction.setLanguage(language);
            uploadFileAction.initialize();
            uploadFileAction.setMultipartRequest((MultipartRequest) uploadFileAction.getRequest());
            uploadFileAction.execute();
            Object obj = uploadFileAction.getResult();
            if (obj==null)
            {
                return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
            }

            if (obj instanceof JSONObject)
            {
                JSONObject json = (JSONObject)obj;
                if (json.getString(Environment.message)!=null&&(json.getString(Environment.message).contains("登录")||json.getString(Environment.message).contains("login"))) {
                    return new BaseState(false, AppInfo.USER_NEED_LOGIN);
                }
                BaseState state = new BaseState(json.getBoolean("success"),json.getString(Environment.message));
                state.setJson( new JSONObject(json.clone()));
                if (json.getBoolean("success") || json.getBoolean("OK")) {
                    return state;
                }
            }

            if (obj instanceof JSONArray)
            {
                JSONArray jsonArray = (JSONArray)obj;
                MultiState multiState = new MultiState(true);
                for (int i=0;i<jsonArray.size();i++)
                {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json==null)
                    {
                        continue;
                    }
                    BaseState state = new BaseState(json.getBoolean("success"),json.getString(Environment.message));
                    state.setJson(new JSONObject(json.clone()));
                    multiState.addState(state);
                }
                return multiState;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            uploadFileAction.destroy();
        }
        return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
    }

    private State base64Save(String content, String saveDirectory, String setupPath, long maxSize) throws Exception {

        byte[] data = com.github.jspxnet.security.utils.Base64.decode(content, com.github.jspxnet.security.utils.Base64.DEFAULT);
        if (!validSize(data, maxSize)) {
            return new BaseState(false, AppInfo.MAX_SIZE);
        }
        String suffix = FileType.getSuffix("JPG");
        RenamePolicy fileRenamePolicy = FileCoveringPolicyEnumType.JSPX.getRenamePolicy();
        File file = new File(saveDirectory, System.currentTimeMillis() + suffix);
        file = FileUtil.moveToTypeDir(file, fileRenamePolicy, false);
        if (file == null) {
            return new BaseState(false, AppInfo.IO_ERROR);
        }

        boolean thumbnail = getBoolean("thumbnail");
        int maxImageWidth = config.getInt(Environment.maxImageWidth, 1280);

        State state = StorageManager.saveBinaryFile(data, file.getPath());

        //------------------------------------------------------hash算法 判断重复
        String url = "/" + uploadFileDAO.getNamespace() + "/" + FileUtil.getDecrease(file.getPath(), setupPath);
        url = FileUtil.mendFile(url);

        if (state.isSuccess()) {
            state.putInfo("url", url);
            state.putInfo("type", suffix);
            state.putInfo("original", "");
        }

        Object uploadFile = uploadFileDAO.getClassType().newInstance();
        IUploadFile upFile = (IUploadFile) uploadFile;
        upFile.setTitle("none");
        upFile.setFileName(url);
        upFile.setFileSize(file.length());
        upFile.setFileType(suffix);
        upFile.setContent("none");

        IUserSession userSession = getUserSession();
        if (userSession != null) {
            upFile.setPutName(userSession.getName());
            upFile.setPutUid(userSession.getUid());
            upFile.setIp(getRemoteAddr());
        }
        upFile.setHash(FileUtil.getFileGuid(file, UploadFileAction.hashType));
        if (uploadFileDAO != null) {

            uploadFileDAO.save(upFile);

            //已经上传成功的,还没有的上传上去的就上传上去
            if (FileSuffixUtil.isImageSuffix(upFile.getFileType())) {
                //如果是图片就得到宽高
                BufferedImage image = ImageIO.read(file);
                int w = image.getWidth();
                int h = image.getHeight();
                if (image.getWidth() > maxImageWidth) {
                    h = (maxImageWidth / w) * h;
                    File tempFile = FileUtil.createFile(new File(file.getParent(), System.currentTimeMillis() + "_thumbnail.tmp"));
                    boolean repair = ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(tempFile), suffix, maxImageWidth, h) && FileUtil.copy(tempFile, file, true);
                    state.putInfo("repair", repair + "");
                }
                upFile.setAttributes("width=" + w + "\r\nheight=" + h);
                String thumbnailImg = "s_" + upFile.getFileName();
                int width = config.getInt("thumbnailWidth", 400);
                int height = config.getInt("thumbnailHeight", 400);
                if (thumbnail && ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(new File(file.getParent(), thumbnailImg)), suffix, width, height)) {
                    state.putInfo("fileName", thumbnailImg);
                    state.putInfo("name", FileUtil.getNamePart(thumbnailImg));
                    state.putInfo("thumbnail", 1);
                    state.putInfo("type", suffix);
                    state.putInfo("url", '/' + uploadFileDAO.getNamespace() + '/' + FileUtil.mendPath(FileUtil.getDecrease(setupPath, file.getParent())) + thumbnailImg);
                    return state;
                }
            }
        }
        return state;
    }

    private static boolean validSize(byte[] data, long length) {
        return data.length <= length;
    }

    @Override
    public String execute() throws Exception {
        configManager = ConfigManager.getInstance(Dispatcher.getRealPath(), uploadFileDAO.getNamespace());
        String callbackName = getString("callback");
        String result;
        if (!StringUtil.isNull(callbackName)) {
            if (!validCallbackName(callbackName)) {
                result = new BaseState(false, AppInfo.ILLEGAL).toJsonString();
            } else {
                result = callbackName + "(" + this.invoke() + ");";
            }
        } else {
            result = this.invoke();
        }
        TXWebUtil.print(result, WebOutEnumType.JSON.getValue(), response);
        return NONE;
    }

}

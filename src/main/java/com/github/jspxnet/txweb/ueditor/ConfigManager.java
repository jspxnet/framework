package com.github.jspxnet.txweb.ueditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.ueditor.define.ActionMap;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * 百度编辑器配置管理器
 *
 * @author hancong03@baidu.com
 */
public final class ConfigManager {

    private final String rootPath;
    private final String configPath;
    //private final String namespace;
    private static final String configFileName = "upload.json";
    private JSONObject jsonConfig = null;
    // 涂鸦上传filename定义
    private final static String SCRAWL_FILE_NAME = "scrawl";
    // 远程图片抓取filename定义
    private final static String REMOTE_FILE_NAME = "remote";

    /*
     * 通过一个给定的路径构建一个配置管理器， 该管理器要求地址路径所在目录下必须存在config.properties文件
     */
    private ConfigManager(String rootPath, String namespace) throws IOException {
        this.rootPath = rootPath;
        this.configPath = FileUtil.mendPath(this.rootPath + namespace);
        this.initEnv();
    }

    /**
     * 配置管理器构造工厂
     *
     * @param rootPath    服务器根路径
     * @param contextPath 服务器所在项目路径
     * @return 配置管理器实例或者null
     */
    public static ConfigManager getInstance(String rootPath, String contextPath) {
        try {
            return new ConfigManager(rootPath, contextPath);
        } catch (Exception e) {
            return null;
        }

    }

    // 验证配置文件加载是否正确
    public boolean valid() {
        return this.jsonConfig != null;
    }

    public JSONObject getAllConfig() {

        return this.jsonConfig;

    }

    public Map<String, Object> getConfig(int type) {

        Map<String, Object> conf = new HashMap<>();
        String savePath = null;

        switch (type) {

            case ActionMap.UPLOAD_FILE:
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("fileMaxSize"));
                conf.put("allowFiles", repairFileType(this.getArray("fileAllowFiles")));
                conf.put("fieldName", this.jsonConfig.getString("fileFieldName"));
                savePath = this.jsonConfig.getString("filePathFormat");
                break;

            case ActionMap.UPLOAD_IMAGE:
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("imageMaxSize"));
                conf.put("allowFiles", repairFileType(this.getArray("imageAllowFiles")));
                conf.put("fieldName", this.jsonConfig.getString("imageFieldName"));
                conf.put("imageActionName", this.jsonConfig.getString("imageActionName"));
                savePath = this.jsonConfig.getString("imagePathFormat");
                break;

            case ActionMap.UPLOAD_VIDEO:
                conf.put("maxSize", this.jsonConfig.getLong("videoMaxSize"));
                conf.put("allowFiles", repairFileType(this.getArray("videoAllowFiles")));
                conf.put("fieldName", this.jsonConfig.getString("videoFieldName"));
                savePath = this.jsonConfig.getString("videoPathFormat");
                break;

            case ActionMap.UPLOAD_SCRAWL:
                conf.put("filename", ConfigManager.SCRAWL_FILE_NAME);
                conf.put("maxSize", this.jsonConfig.getLong("scrawlMaxSize"));
                conf.put("fieldName", this.jsonConfig.getString("scrawlFieldName"));
                conf.put("isBase64", "true");
                savePath = this.jsonConfig.getString("scrawlPathFormat");
                break;

            case ActionMap.CATCH_IMAGE:
                conf.put("filename", ConfigManager.REMOTE_FILE_NAME);
                conf.put("filter", this.getArray("catcherLocalDomain"));
                conf.put("maxSize", this.jsonConfig.getLong("catcherMaxSize"));
                conf.put("allowFiles", repairFileType(this.getArray("catcherAllowFiles")));
                conf.put("fieldName", this.jsonConfig.getString("catcherFieldName") + "[]");
                savePath = this.jsonConfig.getString("catcherPathFormat");
                break;

            case ActionMap.LIST_IMAGE:
                conf.put("allowFiles", repairFileType(this.getArray("imageManagerAllowFiles")));
                conf.put("dir", this.jsonConfig.getString("imageManagerListPath"));
                conf.put("count", this.jsonConfig.getInt("imageManagerListSize"));
                break;

            case ActionMap.LIST_FILE:
                conf.put("allowFiles", repairFileType(this.getArray("fileManagerAllowFiles")));
                conf.put("dir", this.jsonConfig.getString("fileManagerListPath"));
                conf.put("count", this.jsonConfig.getInt("fileManagerListSize"));
                break;
        }
        conf.put("savePath", savePath);
        conf.put("rootPath", this.rootPath);
        return conf;

    }

    private void initEnv() throws IOException {

        File file = this.getConfigPath();
        if (!file.exists()) {
            return;
        }
        String configContent = readFile(file);
        try {
            this.jsonConfig = new JSONObject(configContent);
        } catch (Exception e) {
            this.jsonConfig = null;
        }
    }

    private File getConfigPath() {
        return new File(this.configPath, ConfigManager.configFileName);
    }

    private String[] getArray(String key) {
        JSONArray jsonArray = this.jsonConfig.getJSONArray(key);
        String[] result = new String[jsonArray.length()];
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            result[i] = jsonArray.getString(i);
        }
        return result;

    }

    private String readFile(File path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), Environment.defaultEncode));
            String tmpContent = null;
            while ((tmpContent = bfReader.readLine()) != null) {
                builder.append(tmpContent);
            }
            bfReader.close();
        } catch (UnsupportedEncodingException e) {
            // 忽略
        }
        return this.filter(builder.toString());

    }

    // 过滤输入字符串, 剔除多行注释以及替换掉反斜杠
    private String filter(String input) {
        return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");
    }

    private static String[] repairFileType(String[] fileType) {
        if (ArrayUtil.isEmpty(fileType)) {
            return new String[0];
        }
        for (int i = 0; i < fileType.length; i++) {
            fileType[i] = StringUtil.trim(StringUtil.replace(fileType[i], StringUtil.DOT, ""));
        }
        return fileType;
    }
}

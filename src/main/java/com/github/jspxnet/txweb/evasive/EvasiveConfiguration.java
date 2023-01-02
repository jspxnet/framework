package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by ChenYuan on 2017/6/14.
 */
@Slf4j
public class EvasiveConfiguration implements Configuration {
    final private List<EvasiveRule> evasiveRuleList = new ArrayList<>();
    private List<ResultConfigBean> resultConfigList = new ArrayList<>();
    //白名单
    private String[] whiteList = null;
    //黑名单
    private String[] blackList = null;

    //黑名后缀
    private String[] blackSuffixList = null;

    //密码访问目录
    private Map<String,String> passwordFolderList = null;

    //不安全的url
    private String[] insecureUrlKeys = null;
    //不安全的外来参数
    private String[] insecureQueryStringKeys = null;
    //规则列表
    final private List<QueryBlack> queryBlackRuleList = new ArrayList<QueryBlack>();
    //包含的文件,用作判断，如果已经载入的文件将不再载入
    private String[] includeFiles = null;
    //默认载入的文件名
    private String fileName = "evasive.xml";

    @Override
    public List<EvasiveRule> getEvasiveRuleList() {
        return evasiveRuleList;
    }

    @Override
    public String[] getWhiteList() {
        return whiteList;
    }

    @Override
    public String[] getBlackList() {
        return blackList;
    }

    @Override
    public List<QueryBlack> getQueryBlackRuleList() {
        return queryBlackRuleList;
    }

    @Override
    public List<ResultConfigBean> getResultConfigList() {
        return resultConfigList;
    }

    @Override
    public String[] getInsecureUrlKeys() {
        return insecureUrlKeys;
    }

    @Override
    public String[] getInsecureQueryStringKeys() {
        return insecureQueryStringKeys;
    }

    @Override
    public String[] getBlackSuffixList() {
        return blackSuffixList;
    }

    @Override
    public Map<String, String> getPasswordFolderList() {
        return passwordFolderList;
    }

    private static Configuration instance = null;

    public static Configuration getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (EvasiveConfiguration.class) {
            if (instance == null) {
                instance = new EvasiveConfiguration();
            }
        }
        return instance;
    }

    private EvasiveConfiguration() {

    }

    @Override
    public String getFileName() {
        return fileName;
    }


    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (!StringUtil.isNull(this.fileName)) {
            try {
                reload();
            } catch (Exception e) {
                log.error(" evasive reload config fail", e);
                e.printStackTrace();
            }
        }
    }


    @Override
    public void reload() throws Exception {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        boolean useEvasive = envTemplate.getBoolean(Environment.useEvasive);
        if (!useEvasive) {
            return;
        }

        //默认环境
        String defaultFile = fileName;
        if (!FileUtil.isFileExist(defaultFile)) {

            String temp = envTemplate.getString(Environment.defaultPath);
            if (StringUtil.isNull(temp)) {
                temp = StringUtil.empty;
            }
            defaultFile = temp + fileName;
        }

        if (!FileUtil.isFileExist(defaultFile)) {
            URL url = Environment.class.getResource("/" + fileName);
            if (url != null) {
                defaultFile = URLUtil.getUrlDecoder(url.getPath(), Environment.defaultEncode);
            }
        }

        if (!FileUtil.isFileExist(defaultFile)) {
            URL url = Environment.class.getResource("/resources/" + fileName);
            if (url != null) {
                defaultFile = URLUtil.getUrlDecoder(url.getPath(), Environment.defaultEncode);
            }
        }


        if (!FileUtil.isFileExist(defaultFile)) {
            URL url = Environment.class.getResource(fileName);
            if (url != null) {
                defaultFile = URLUtil.getUrlDecoder(url.getPath(), Environment.defaultEncode);
            }
        }

        if (!FileUtil.isFileExist(defaultFile)) {
            throw new Exception("not find evasive config file:" + defaultFile);
        }

        evasiveRuleList.clear();
        fileName = defaultFile;
        includeFiles = null;
        String defaultPath = FileUtil.getParentPath(fileName);
        if (StringUtil.isNull(defaultPath)) {
            defaultPath = envTemplate.getString(Environment.defaultPath);
        }
        readIncludeFile(defaultPath, new String[]{fileName});

    }

    /**
     * @param defaultPath 默认路径
     * @param include     包含的路径
     * @throws Exception 异常
     */
    private void readIncludeFile(String defaultPath, String[] include) throws Exception {
        if (include == null) {
            return;
        }
        for (String file : include) {

            if (StringUtil.isNull(file)) {
                continue;
            }

            File readFile = EnvFactory.getFile(file);
            if (readFile == null) {
                continue;
            }

            String fileId = readFile.getName() + "_" + readFile.length();
            if (ArrayUtil.inArray(includeFiles, fileId, true)) {
                continue;
            }
            includeFiles = ArrayUtil.add(includeFiles, fileId);
            String txt = IoUtil.autoReadText(readFile);
            log.info("evasive load config file:" + file);
            readIncludeContent(txt, defaultPath);
        }
    }


    public void readIncludeContent(String txt, String defaultPath) throws Exception {
        if (StringUtil.isEmpty(txt)) {
            return;
        }

        ReadEvasiveRuleConfig readConfig = new ReadEvasiveRuleConfig();
        if (XMLUtil.parseXmlString(readConfig, txt)) {
            String[] includeFixedFiles = null;
            String[] iFiles = readConfig.getInclude();

            evasiveRuleList.addAll(readConfig.getEvasiveRuleList());
            this.insecureUrlKeys = ArrayUtil.join(this.insecureUrlKeys, readConfig.getInsecureUrlKeys());
            this.insecureQueryStringKeys = ArrayUtil.join(this.insecureQueryStringKeys, readConfig.getInsecureQueryStringKeys());
            this.whiteList = ArrayUtil.join(this.whiteList, readConfig.getWhiteList());
            this.blackList = ArrayUtil.join(this.blackList, readConfig.getBlackList());
            this.blackSuffixList = ArrayUtil.join(this.blackSuffixList, readConfig.getBlackSuffixList());
            this.queryBlackRuleList.addAll(readConfig.getQueryBlackRuleList());
            this.resultConfigList = readConfig.getResultConfigList();
            this.passwordFolderList = readConfig.getPasswordFolderList();

            if (iFiles != null) {
                for (String mif : iFiles) {
                    if (FileUtil.isPatternFileName(mif)) {
                        List<File> fileName = FileUtil.getPatternFiles(defaultPath, mif);
                        if (defaultPath != null) {
                            fileName.addAll(FileUtil.getPatternFiles(null, mif));
                        }
                        for (File f : fileName) {
                            includeFixedFiles = ArrayUtil.add(includeFixedFiles, f.getName());
                        }
                    } else {
                        includeFixedFiles = ArrayUtil.add(includeFixedFiles, mif);
                    }
                }
            }

            readIncludeFile(defaultPath, includeFixedFiles);
        } else {
            log.error("evasive load config file xml error:" + txt);
        }
    }

    @Override
    public void shutdown() {
        evasiveRuleList.clear();
        queryBlackRuleList.clear();
        whiteList = null;
        blackList = null;
    }
}


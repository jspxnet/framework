package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by ChenYuan on 2017/6/14.
 */
@Slf4j
public class EvasiveConfiguration implements Configuration {
    final private List<EvasiveRule> evasiveRuleList = new ArrayList<EvasiveRule>();
    private List<ResultConfigBean> resultConfigList = new ArrayList<ResultConfigBean>();
    private String[] whiteList = null;
    private String[] blackList = null;
    //黑名单
    final private List<String> blacklist = new ArrayList<String>();
    private String[] insecureUrlKeys = null;
    private String[] insecureQueryStringKeys = null;
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

    private static Configuration instance = new EvasiveConfiguration();

    public static Configuration getInstance() {
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
                defaultFile = url.getPath();
            }
        }

        if (!FileUtil.isFileExist(defaultFile)) {
            URL url = Environment.class.getResource("/resources/"+fileName);
            if (url != null) {
                defaultFile = url.getPath();
            }
        }


        if (!FileUtil.isFileExist(defaultFile)) {
            URL url = Environment.class.getResource(fileName);
            if (url != null) {
                defaultFile = url.getPath();
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
        readIncludeFile(defaultPath,new String[]{fileName} );

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
            log.info("evasive load config file:" + file);

            File readFile = EnvFactory.getFile(file);
            if (readFile==null) {
                continue;
            }

            String fileId = readFile.getName()+"_" + readFile.length();
            if (ArrayUtil.inArray(includeFiles, fileId, true)) {
                continue;
            }
            includeFiles = ArrayUtil.add(includeFiles, fileId);


            String txt = IoUtil.autoReadText(readFile);
            ReadEvasiveRuleConfig readConfig = new ReadEvasiveRuleConfig();
            if (XMLUtil.parseXmlString(readConfig, txt)) {
                String[] includeFixedFiles = null;
                String[] iFiles = readConfig.getInclude();

                evasiveRuleList.addAll(readConfig.getEvasiveRuleList());
                this.insecureUrlKeys = ArrayUtil.join(this.insecureUrlKeys, readConfig.getInsecureUrlKeys());
                this.insecureQueryStringKeys = ArrayUtil.join(this.insecureQueryStringKeys, readConfig.getInsecureQueryStringKeys());
                this.whiteList = ArrayUtil.join(this.whiteList, readConfig.getWhiteList());
                this.blackList = ArrayUtil.join(this.blackList, readConfig.getBlackList());
                this.queryBlackRuleList.addAll(readConfig.getQueryBlackRuleList());
                this.resultConfigList = readConfig.getResultConfigList();

                if (iFiles != null) {
                    for (String mif : iFiles) {
                        if (FileUtil.isPatternFileName(mif)) {
                            List<File> fileName = FileUtil.getPatternFiles(defaultPath, mif);
                            if (defaultPath!=null)
                            {
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
                log.error("evasive load config file xml error:" + file);
            }
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


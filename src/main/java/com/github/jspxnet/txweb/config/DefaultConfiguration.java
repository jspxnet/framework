/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Environment;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-5
 * Time: 15:02:05
 */
@Slf4j
public class DefaultConfiguration implements Configuration {


    //包含的文件,用作判断，如果已经载入的文件将不再载入
    private List<String> includeFiles = new ArrayList<>() ;
    //默认载入的文件名
    private String fileName = "jspx.txweb.xml";

    //默认环境
    private EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
    private Map<String, String> extendMap = new Hashtable<String, String>();
    //每个命名空间里边的默认拦截器列表
    private Map<String, List<DefaultInterceptorBean>> defaultInterceptorMap = new Hashtable<>();
    private Map<String, List<ResultConfigBean>> defaultResultMap = new HashMap<>();

    //文件配置中要扫描的目录
    private List<ScanConfig> scanPackageList = new ArrayList<>();

    private static Configuration instance;

    public static Configuration getInstance() {
        if (instance==null)
        {
            synchronized (DefaultConfiguration.class)
            {
                instance = new DefaultConfiguration();
            }
        }
        return instance;
    }

    private DefaultConfiguration() {

    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<String, List<DefaultInterceptorBean>> getDefaultInterceptorMap() {
        return defaultInterceptorMap;
    }

    @Override
    public Map<String, String> getExtendMap() {
        return extendMap;
    }

    @Override
    public Map<String, List<ResultConfigBean>> getDefaultResultMap() {
        return defaultResultMap;
    }

    @Override
    public Map<String, Map<String, ActionConfigBean>> loadConfigMap() throws Exception {

        final Map<String, Map<String, ActionConfigBean>> actionConfigMap = new Hashtable<String, Map<String, ActionConfigBean>>();
        if (!StringUtil.isNull(envTemplate.getString(Environment.TXWebConfig))) {
            fileName = envTemplate.getString(Environment.TXWebConfig);
        }
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
            URL url = Environment.class.getResource("/resources/" + fileName);
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
            throw new Exception("not find TXWeb config file:" + defaultFile);
        }

        fileName = defaultFile;

        String defaultPath = FileUtil.getParentPath(fileName);
        if (StringUtil.isNull(defaultPath)) {
            defaultPath = envTemplate.getString(Environment.defaultPath);
        }
        String[] includeFixedFiles = new String[1];
        includeFixedFiles[0] = fileName;
        readIncludeFile(defaultPath, includeFixedFiles, actionConfigMap);
        includeFiles.clear();
        return actionConfigMap;
    }

    /**
     * @param defaultPath     默认路径
     * @param include         包含的路径
     * @param actionConfigMap 全局配置列表
     * @throws Exception 异常
     */
    private void readIncludeFile(String defaultPath, final String[] include, Map<String, Map<String, ActionConfigBean>> actionConfigMap) throws Exception {
        if (include == null) {
            return;
        }

        for (String includeFile : include) {
            if (StringUtil.isNull(includeFile)) {
                continue;
            }

            File readFile = null;
            if (FileUtil.isFileExist(includeFile)) {
                readFile = new File(includeFile);
            }

            if (readFile==null) {
                readFile = new File(defaultPath ,includeFile);
                if (!FileUtil.isFileExist(readFile)) {
                    readFile = null;
                }
            }
            if (readFile==null) {
                readFile = EnvFactory.getFile(includeFile);
            }
            if (readFile==null) {
                log.error("txweb not found file:{}",includeFile);
                throw new FileNotFoundException(includeFile);
            }

            String path = readFile.getPath();
            if (includeFiles.contains(path))
            {
                continue;
            }
            includeFiles.add(path);
            if (log.isDebugEnabled()) {
                log.debug("TXWeb load config file:" + includeFile);
            }

            String configString = IoUtil.autoReadText(path,envTemplate.getString(Environment.encode, Environment.defaultEncode));

            XmlEngine xmlEngine = new XmlEngineImpl();
            xmlEngine.putTag(LoadElement.tagName, LoadElement.class.getName());
            List<TagNode> results = xmlEngine.getTagNodes(configString);
            for (TagNode tNode : results) {
                LoadElement se = (LoadElement) tNode;
                String loadFile = se.getFile();
                Map<String, Object> valueMap = envTemplate.getVariableMap();

                for (String attName : se.getAttributeName()) {
                    String value = XMLUtil.deleteQuote(se.getStringAttribute(attName));
                    valueMap.put(attName, value);
                }
                String encode = se.getEncode();
                if (StringUtil.isNull(encode)) {
                    encode = Environment.defaultEncode;
                }
                File file = EnvFactory.getFile(loadFile);
                if (file==null) {
                    log.error("ioc not found file:" + loadFile);
                    throw new FileNotFoundException(loadFile);
                }

                String readCont = IoUtil.autoReadText(file.getPath(),encode);
                int headPost = StringUtil.indexIgnoreCaseOf(readCont, "<?xml");
                if (headPost != -1) {
                    readCont = StringUtil.substringAfter(readCont, ">");
                }
                String readTxt = EnvFactory.getPlaceholder().processTemplate(valueMap, readCont);
                readTxt = StringUtil.trim(StringUtil.substringBeforeLast(StringUtil.substringAfter(readTxt, ">"), "<"));
                configString = StringUtil.replace(configString, se.getSource(), readTxt);

            }

            ReadConfig readConfig = new ReadConfig(actionConfigMap, extendMap, defaultInterceptorMap, defaultResultMap);
            if (XMLUtil.parseXmlString(readConfig, configString)) {
                String[] includeFixedFiles = null;
                String[] iFiles = readConfig.getInclude();
                if (iFiles != null) {
                    for (String findName : iFiles) {
                        if (FileUtil.isPatternFileName(findName)) {
                            List<File> fileList = FileUtil.getPatternFiles(defaultPath, findName);
                            //都没找到,说明在jar包里边
                            if (fileList==null||fileList.isEmpty())
                            {

                                fileList.addAll(FileUtil.getPatternFiles(null, findName));
                            }
                            if (fileList!=null)
                            {
                                for (File f : fileList) {
                                    includeFixedFiles = ArrayUtil.add(includeFixedFiles, f.getPath());
                                }
                            }
                        }
                       else {
                            includeFixedFiles = ArrayUtil.add(includeFixedFiles, findName);
                        }
                    }
                }
                if (includeFixedFiles!=null)
                {
                    readIncludeFile(defaultPath, includeFixedFiles, actionConfigMap);
                }

                List<ScanConfig> scanConfigs = readConfig.getScanConfigList();
                if (!scanConfigs.isEmpty()) {
                    scanPackageList.addAll(scanConfigs);
                }

            } else {
                log.error("TXWeb load config file xml error:" + includeFile);
            }
        }
    }

    @Override
    public List<ScanConfig> getScanPackageList() {
        return scanPackageList;
    }

}
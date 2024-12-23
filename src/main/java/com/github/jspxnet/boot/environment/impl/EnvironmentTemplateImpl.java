/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.environment.impl;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.security.symmetry.impl.XOREncrypt;
import com.github.jspxnet.sioc.CatalinaObject;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-4
 * Time: 0:19:29
 */
@Slf4j
public class EnvironmentTemplateImpl implements EnvironmentTemplate {
    private static final Map<String, Object> VALUE_MAP = new Hashtable<>();
    public EnvironmentTemplateImpl() {

    }

    /**
     *
     * @return 得到环境变量表
     */
    @Override
    public Map<String, Object> getVariableMap() {
        return new Hashtable<>(VALUE_MAP);
    }


    @Override
    public Object get(String keys) {
        return VALUE_MAP.get(keys);
    }

    /**
     *
     * @param key 变量名称
     * @return 得到环境变量
     */
    @Override
    public String getString(String key) {
        Object o = VALUE_MAP.get(key);
        if (o == null) {
            return System.getProperty(key);
        }
        return o.toString();
    }

    @Override
    public boolean containsName(String key) {
        return VALUE_MAP.containsKey(key);
    }

    /**
     *
     * @param keys 变量名称
     * @param def 默认值
     * @return 得到环境变量
     */
    @Override
    public String getString(String keys, String def) {
        String result = getString(keys);
        if (!StringUtil.hasLength(result)) {
            return def;
        }
        return result;
    }

    @Override
    public boolean getBoolean(String keys) {
        return StringUtil.toBoolean(getString(keys));
    }

    @Override
    public int getInt(String keys) {
        return StringUtil.toInt(getString(keys));
    }

    @Override
    public int getInt(String keys, int def) {
        return StringUtil.toInt(getString(keys), def);
    }

    @Override
    public long getLong(String keys, long def) {
        return StringUtil.toLong(getString(keys), def);
    }

    private String processTemplate(String templateString) {
        Placeholder placeholder = new PlaceholderImpl();
        return placeholder.processTemplate(VALUE_MAP, templateString);
    }

    /**
     * 创建默认环境
     *
     * @param defaultPath 默认路径
     */
    @Override
    public void createPathEnv(String defaultPath) {
        if (defaultPath == null) {
            log.error("系统不能找{},文件所在路径,将不能正常运行:", Environment.config_file);
            defaultPath = System.getProperty("user.dir");
        }
        VALUE_MAP.put(Environment.defaultPath, defaultPath);
        VALUE_MAP.put(Environment.ConfigFile, defaultPath + Environment.config_file);
        String webInfPath = null;
        if (SystemUtil.isAndroid()) {
            //安卓方式目录环境
            String tmpDir = FileUtil.mendPath(System.getProperty("java.io.tmpdir"));
            VALUE_MAP.put(Environment.templatePath, defaultPath);
            VALUE_MAP.put(Environment.resPath, defaultPath);
            VALUE_MAP.put(Environment.tempPath, tmpDir);
        }
        else
        {
            //WebInfPath

            if (defaultPath != null && defaultPath.toLowerCase().contains("file-inf")) {
                ///jsp方式
                webInfPath = defaultPath.substring(0, defaultPath.toLowerCase().indexOf("file-inf/") + 8);
            }
            else if (defaultPath != null && defaultPath.contains(".jar") || defaultPath != null && defaultPath.contains(".zip") || defaultPath != null && defaultPath.contains(".apk")) {
                //////////文件在jar目录中的情况
                webInfPath = FileUtil.getParentPath(FileUtil.getPathPart(StringUtil.substringBefore(defaultPath, "!/")));
                if (webInfPath.length()<4)
                {
                    webInfPath = new File(defaultPath).getParent();
                }
            } else {
                webInfPath = FileUtil.getParentPath(defaultPath);
            }
            webInfPath = FileUtil.mendPath(webInfPath);

            boolean createWebInf = webInfPath.toLowerCase().contains("web-inf");
            VALUE_MAP.put(Environment.webInfPath, webInfPath);
            String tempDir = webInfPath + "template/";
            if (createWebInf&&!FileUtil.isDirectory(tempDir)) {
                FileUtil.makeDirectory(tempDir);
            }
            VALUE_MAP.put("lucenePath", webInfPath+"/lucene");

            //修复路径支持本级下
            File file = new File(tempDir);
            String[] files = file.list();
            if (ArrayUtil.isEmpty(files) && FileUtil.isDirectory(new File(defaultPath, "template/").getPath())) {
                tempDir = new File(defaultPath, "template/").getPath();
            } else if (ArrayUtil.isEmpty(files) && FileUtil.isDirectory(new File(defaultPath, "resources/template/").getPath())) {
                tempDir = new File(defaultPath, "resources/template/").getPath();
            }
            if (!FileUtil.isDirectory(tempDir)&&defaultPath!=null&&defaultPath.contains("classes")&&defaultPath.toLowerCase().contains("web-inf"))
            {
                tempDir = new File( new File(defaultPath).getPath(), "template/").getPath();
            }

            if (!VALUE_MAP.containsKey(Environment.templatePath)) {
                VALUE_MAP.put(Environment.templatePath, tempDir);
            }
            //loader路径
            //tempDir = webInfPath + "loader/";
            //FileUtil.makeDirectory(tempDir);
            //VALUE_MAP.put(Environment.loaderPath, tempDir);


            //本地数据库默认路径
            tempDir = webInfPath + "database/";
            if (createWebInf)
            {
                //为本地应用小型化数据库使用
                FileUtil.makeDirectory(tempDir);
                VALUE_MAP.put(Environment.databasePath, tempDir);
            }

            //LibPath    得到库文件路径
            tempDir = webInfPath + "lib/";
            if (createWebInf)
            {
                FileUtil.makeDirectory(tempDir);
            }

            VALUE_MAP.put(Environment.libPath, tempDir);
            if (!VALUE_MAP.containsKey(Environment.fontsPath)) {
                if (SystemUtil.OS==SystemUtil.WINDOWS)
                {
                    VALUE_MAP.put(Environment.fontsPath, "C:/Windows/Fonts");
                } else {
                    VALUE_MAP.put(Environment.fontsPath, "/usr/share/fonts");
                }
            }

            //ResPath    资源目录
            tempDir = webInfPath + "reslib/";
            if (createWebInf)
            {
                FileUtil.makeDirectory(tempDir);
            }
            VALUE_MAP.put(Environment.resPath, tempDir);

            //TempPath    临时路径
            tempDir = webInfPath + "tmp/";
            if (createWebInf&&FileUtil.isDirectory(tempDir)) {
                VALUE_MAP.put(Environment.tempPath, tempDir);
            } else {
                VALUE_MAP.put(Environment.tempPath, FileUtil.mendPath(System.getProperty("java.io.tmpdir")));
            }


        }
        //LogPath
        if (StringUtil.isNull(webInfPath))
        {
            webInfPath = defaultPath;
        }

        String logPath = getLogPath((String) VALUE_MAP.get(Environment.logPath),new File(webInfPath,"logs").getPath());
        VALUE_MAP.put(Environment.logPath, logPath);
        VALUE_MAP.put(Environment.logInfoFile, logPath + Environment.log_info_file);
        VALUE_MAP.put(Environment.logErrorFile, logPath + Environment.log_error_file);
    }

    private String getEncode() {

        String encode = (String) VALUE_MAP.get(Environment.encode);
        if (StringUtil.isNull(encode)) {
            encode = System.getProperty("file.encoding");
        }
        if (StringUtil.isNull(encode)) {
            return Environment.defaultEncode;
        }
        return encode;
    }

    private String getTimezone() {
        return (String) VALUE_MAP.get(Environment.timezone);
    }

    private String getAwtToolkit() {
        String awtToolkit = (String) VALUE_MAP.get("awt.toolkit");
        if (awtToolkit == null || awtToolkit.length() < 1) {
            return System.getProperty("awt.toolkit");
        }
        return awtToolkit;
    }

    private Object getJava2dUsePlatformFont() {
        if (VALUE_MAP.containsKey("java2d.font.usePlatformFont")) {
            return VALUE_MAP.get("java2d.font.usePlatformFont");
        }
        return System.getProperty("java2d.font.usePlatformFont");
    }

    private Object getGraphicsEnv() {
        if (VALUE_MAP.containsKey("java.awt.graphicsenv")) {
            return VALUE_MAP.get("java.awt.graphicsenv");
        }
        return System.getProperty("java.awt.graphicsenv");
    }

    private String getLogPath(String logPath,String defaultPath) {
        if (StringUtil.isEmpty(logPath)) {
            logPath = defaultPath;
        }
        File f = new File(logPath);
        if (f.isDirectory()) {
            return FileUtil.mendPath(f.getPath());
        }
        if (!StringUtil.isNull(logPath) && logPath.contains("$")) {
            return processTemplate(logPath);
        }
        return defaultPath;
    }

    /**
     * 正相和反相的设置环境变量
     */
    @Override
    public void createSystemEnv() {
        //encode
        if (!SystemUtil.isAndroid()) {
            System.setProperty("file.encoding", getEncode());
        }


        System.setProperty("javax.xml.soap.character-set-encoding", getEncode());
        //awtToolkit
        String awtToolkit = getAwtToolkit();
        if (!StringUtil.isNull(awtToolkit)) {
            System.setProperty("awt.toolkit", awtToolkit);
        }
        //java2dUsePlatformFont
        String usePlatformFont = (String) getJava2dUsePlatformFont();
        if (!StringUtil.isNull(usePlatformFont)) {
            System.setProperty("java2d.font.usePlatformFont", usePlatformFont);
        }
        //graphicsenv
        String getGraphicsenv = (String) getGraphicsEnv();
        if (!StringUtil.isNull(getGraphicsenv)) {
            System.setProperty("java.awt.graphicsenv", getGraphicsenv);
        }

        //设置时区
        String timezone = getTimezone();
        if (!StringUtil.isNull(timezone)) {
            System.setProperty("user.timezone", timezone);
        }

        //系统密钥
        if (VALUE_MAP.containsKey(Environment.secretKey)) {
            System.setProperty(Environment.secretKey, (String) VALUE_MAP.get(Environment.secretKey));
        }

        //对称加密算法
        if (VALUE_MAP.containsKey(Environment.symmetryAlgorithm)) {
            System.setProperty(Environment.symmetryAlgorithm, (String) VALUE_MAP.get(Environment.symmetryAlgorithm));
        }

        //对称加密算法偏移量
        if (VALUE_MAP.containsKey(Environment.cipherIv)) {
            System.setProperty(Environment.cipherIv, (String) VALUE_MAP.get(Environment.cipherIv));
        }

        //非对称加密算法
        if (VALUE_MAP.containsKey(Environment.asymmetricAlgorithm)) {
            System.setProperty(Environment.asymmetricAlgorithm, (String) VALUE_MAP.get(Environment.asymmetricAlgorithm));
        }

        //非对称验证算法  如 SHA1WithRSA
        if (VALUE_MAP.containsKey(Environment.signAlgorithm)) {
            System.setProperty(Environment.signAlgorithm, (String) VALUE_MAP.get(Environment.signAlgorithm));
        }

        //验证加密算法 如md5
        if (VALUE_MAP.containsKey(Environment.hashAlgorithm)) {
            System.setProperty(Environment.hashAlgorithm, (String) VALUE_MAP.get(Environment.hashAlgorithm));
        }


        Enumeration<Object> enumeration = System.getProperties().keys();
        while (enumeration.hasMoreElements()) {
            String keys = (String) enumeration.nextElement();
            if (keys == null) {
                continue;
            }
            VALUE_MAP.put(keys, System.getProperty(keys, ""));
        }



        //监测当前的web 服务器 begin
        if (Environment.auto.equalsIgnoreCase((String) VALUE_MAP.get(Environment.repairEncode)) || !VALUE_MAP.containsKey(Environment.repairEncode)) {
            try {
                Class.forName("org.apache.catalina.Server", true, Thread.currentThread().getContextClassLoader());
                VALUE_MAP.put(Environment.httpServerName, Environment.webServerTomcat);
                VALUE_MAP.put(Environment.repairEncode, "true");
            } catch (Exception e) {
                VALUE_MAP.put(Environment.repairEncode, "false");
                VALUE_MAP.put(Environment.httpServerName, Environment.webServerResin);
            }
        }
        //监测当前的web 服务器 end
        try {
            Class<?> cls = Class.forName("org.apache.xerces.parsers.SAXParser");
            System.setProperty("org.xml.sax.driver", cls.getName());
        } catch (Exception e) {
            try {
                Class<?> cls = Class.forName("com.sun.org.apache.xerces.internal.parsers.SAXParser");
                System.setProperty("org.xml.sax.driver", cls.getName());
            } catch (Exception e2) {
                log.info("com.sun.xml.internal.stream.XMLInputFactoryImpl " + e.getLocalizedMessage());
            }
        }
        try {
            Class<?> cls = Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl");
            System.setProperty("javax.xml.stream.XMLInputFactory", cls.getName());
        } catch (Exception e2) {
            log.info("com.sun.xml.internal.stream.XMLInputFactoryImpl " + e2.getLocalizedMessage());
        }

        LogBackConfigUtil.createConfig();
    }

    /**
     * 读取当前原始的启动配置文件
     *
     * @param fileName 文件路径
     * @return 得到配置属性, 注意这里只是在内存中
     */
    @Override
    public Map<String,String> readDefaultProperties(String fileName) {

        if (!FileUtil.isFileExist(fileName))
        {
            return new HashMap<>(0);
        }
        try {
            String cont = IoUtil.autoReadText(fileName);
            XOREncrypt encrypt = new XOREncrypt();
            encrypt.setSecretKey(Environment.defaultDrug);
            if (encrypt.isEncrypt(cont)) {
                cont = encrypt.getDecode(cont);
            }

            //这里只是为了劲量的修复系统变量.的 写法
            String[] varNameList = StringUtil.getFreeMarkerVar(cont);
            if (!ObjectUtil.isEmpty(varNameList))
            {
                for (String varName:varNameList)
                {
                    if (System.getProperties().containsKey(varName))
                    {
                        cont = StringUtil.replace(cont,"${" + varName + "}",System.getProperty(varName,StringUtil.empty));
                    }
                }
            }
            //放入系统变量end
            StringMap<String,String> valueMap = new StringMap<>();
            valueMap.setKeySplit(StringUtil.EQUAL);
            valueMap.setLineSplit(StringUtil.CRLF);
            valueMap.setString(cont);
            return valueMap;
        } catch (Exception e) {
            log.info("create Jspx.net Env fileName=" + fileName + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        //创建配置 begin
        return new HashMap<>(0);
    }

    @Override
    public void createJspxEnv(String fileName) {
        try {
            String cont = IoUtil.autoReadText(fileName);
            XOREncrypt encrypt = new XOREncrypt();
            encrypt.setSecretKey(Environment.defaultDrug);
            if (encrypt.isEncrypt(cont)) {
                cont = encrypt.getDecode(cont);
            }

            //放入系统变量begin
            //这里只是为了劲量的修复系统变量.的 写法
            String[] varNameList = StringUtil.getFreeMarkerVar(cont);
            if (!ObjectUtil.isEmpty(varNameList))
            {
                for (String varName:varNameList)
                {
                    if (System.getProperties().containsKey(varName))
                    {
                        cont = StringUtil.replace(cont,"${" + varName + "}",System.getProperty(varName,StringUtil.empty));
                    }
                }
            }
            //放入系统变量end

            StringMap<String,String> valueMap = new StringMap<>();
            valueMap.setKeySplit(StringUtil.EQUAL);
            valueMap.setLineSplit(StringUtil.CRLF);
            valueMap.setString(cont);

            VALUE_MAP.putAll(valueMap);

            VALUE_MAP.put(Environment.jspxProperties, fileName);
            if (!VALUE_MAP.containsKey("jspxDebug"))
            {
                VALUE_MAP.put("jspxDebug",valueMap.getString(Environment.DEBUG,"false"));
            }
            if (!VALUE_MAP.containsKey(Environment.DEBUG))
            {
                VALUE_MAP.put(Environment.DEBUG,valueMap.getString(Environment.DEBUG,"false"));
            }

            VALUE_MAP.put("catalina",new CatalinaObject());
            if (!VALUE_MAP.containsKey("catalina.base")) {
                VALUE_MAP.put("catalina.base", System.getProperty("CATALINA_HOME", System.getProperty("user.dir")));
            }
            if (!VALUE_MAP.containsKey("catalina.home")) {
                VALUE_MAP.put("catalina.home", System.getProperty("CATALINA_HOME", System.getProperty("user.dir")));
            }
            valueMap.clear();
        } catch (Exception e) {
            log.info("create Jspx.net Env fileName=" + fileName + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        //创建配置 end


        //放入模版默认变量
        Configurable configurable = TemplateConfigurable.getInstance();
        configurable.put(ScriptmarkEnv.NumberFormat,VALUE_MAP.getOrDefault(ScriptmarkEnv.NumberFormat, "####.##"));
        configurable.put(ScriptmarkEnv.DateTimeFormat,VALUE_MAP.getOrDefault(ScriptmarkEnv.DateTimeFormat, "yyyy-MM-dd HH:mm"));
        configurable.put(ScriptmarkEnv.TimeFormat,VALUE_MAP.getOrDefault(ScriptmarkEnv.TimeFormat, "HH:mm:ss"));
    }

    @Override
    public void put(String keys, Object value) {
        VALUE_MAP.put(keys, value);
    }


    @Override
    public void deleteEnv(String key) {
        VALUE_MAP.remove(key);
    }

    @Override
    public void restorePlaceholder() {

        for (String key : VALUE_MAP.keySet()) {
            if (StringUtil.isNull(key)) {
                continue;
            }
            Object o = VALUE_MAP.get(key);
            if (o != null) {
                String value = o+"";
                if (value.contains("${")) {
                    try {
                        VALUE_MAP.put(key, processTemplate(value));
                    } catch (Exception e) {
                        log.error("检查配置key:{},value:{}", key, value);
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    @Override
    public Properties getProperties() {
        Properties p = new Properties();
        for (String key : VALUE_MAP.keySet()) {
            Object o = VALUE_MAP.get(key);
            if (StringUtil.isNull(key)) {
                continue;
            }
            if (o != null) {
                p.setProperty(key, o.toString());
            }
        }
        return p;
    }

    @Override
    public void debugPrint() {

        StringBuilder sb = new StringBuilder();
        for (String key : VALUE_MAP.keySet()) {
            Object o = VALUE_MAP.get(key);
            if (StringUtil.isNull(key)) {
                continue;
            }
            if (o != null) {
                sb.append(key).append(StringUtil.EQUAL).append(o).append("\r\n");
            }
        }
        log.debug(sb.toString());
    }

}
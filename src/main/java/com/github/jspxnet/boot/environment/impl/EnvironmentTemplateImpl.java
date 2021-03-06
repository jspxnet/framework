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
import com.github.jspxnet.security.symmetry.impl.XOREncrypt;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-4
 * Time: 0:19:29
 */
@Slf4j
public class EnvironmentTemplateImpl implements EnvironmentTemplate {
    private static final Map<String, Object> VALUE_MAP = new Hashtable<>();

    public EnvironmentTemplateImpl() {


    }



    @Override
    public Map<String, Object> getVariableMap() {
        return new Hashtable<>(VALUE_MAP);
    }

    @Override
    public void put(String keys, Object value) {
        VALUE_MAP.put(keys, value);
    }

    @Override
    public Object get(String keys) {
        return VALUE_MAP.get(keys);
    }

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

    @Override
    public String processTemplate(String templateString) {
        Placeholder placeholder = new PlaceholderImpl();
        return placeholder.processTemplate(VALUE_MAP, templateString);
    }

    /**
     * 创建默认环境
     *
     * @param defaultPath 默认路径
     */
    @Override
    public void createPathEnv(String defaultPath)  {
        if (defaultPath==null)
        {
            log.error("系统不能找{},文件所在路径,将不能正常运行:",Environment.config_file );
            defaultPath = System.getProperty("user.dir");
        }
        VALUE_MAP.put(Environment.defaultPath, defaultPath);
        VALUE_MAP.put(Environment.ConfigFile, defaultPath + Environment.config_file);
        String logPath = System.getProperty("java.io.tmpdir");
        if (SystemUtil.isAndroid()) {
            //安卓方式目录环境
            String tmpDir = FileUtil.mendPath(System.getProperty("java.io.tmpdir"));
            logPath = tmpDir;
            VALUE_MAP.put(Environment.templatePath, defaultPath);
            VALUE_MAP.put(Environment.loaderPath, defaultPath);
            VALUE_MAP.put(Environment.resPath, defaultPath);
            VALUE_MAP.put(Environment.cachePath, tmpDir);
            VALUE_MAP.put(Environment.tempPath, tmpDir);

        } else {
            //WebInfPath
            String webInfPath;
            if (defaultPath != null && defaultPath.toLowerCase().contains("file-inf")) {
                ///jsp方式
                webInfPath = defaultPath.substring(0, defaultPath.toLowerCase().indexOf("file-inf/") + 8);
            } else if (defaultPath != null && defaultPath.contains(".jar") || defaultPath != null && defaultPath.contains(".zip") || defaultPath != null && defaultPath.contains(".apk")) {
                //////////文件在jar目录中的情况
                webInfPath = FileUtil.getParentPath(FileUtil.getPathPart(StringUtil.substringBefore(defaultPath, "!/")));
            } else {
                webInfPath = FileUtil.getParentPath(defaultPath);
            }
            webInfPath = FileUtil.mendPath(webInfPath);
            VALUE_MAP.put(Environment.webInfPath, webInfPath);
            String tempDir = webInfPath + "template/";
            if (!FileUtil.isDirectory(tempDir)) {
                FileUtil.makeDirectory(tempDir);
            }


            //修复路径支持本级下
            File file = new File(tempDir);
            String[] files = file.list();
            if (ArrayUtil.isEmpty(files) && FileUtil.isDirectory(new File(defaultPath, "template/").getAbsolutePath())) {
                tempDir = new File(defaultPath, "template/").getAbsolutePath();
            }
            if (!VALUE_MAP.containsKey(Environment.templatePath)) {
                VALUE_MAP.put(Environment.templatePath, tempDir);
            }
            //loader路径
            tempDir = webInfPath + "loader/";
            //FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.loaderPath, tempDir);

            //LucenePath     得到默认的Lucene 目录,应为文件放在外部不安全
            tempDir = webInfPath + "lucene/";
            //FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.lucenePath, tempDir);

            //本地数据库默认路径
            tempDir = webInfPath + "database/";
            FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.databasePath, tempDir);

            //LibPath    得到库文件路径
            tempDir = webInfPath + "lib/";
            FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.libPath, tempDir);

            //PluginsPath    插件目录
            tempDir = webInfPath + "plugins/";
            //FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.pluginsPath, tempDir);

            //fontsPath    字体目录
            tempDir = webInfPath + "fonts/";
            //FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.fontsPath, tempDir);

            //ResPath    资源目录
            tempDir = webInfPath + "reslib/";
            FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.resPath, tempDir);

            //CachePath    缓存目录
            tempDir = webInfPath + "cache/";
            FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.cachePath, tempDir);

            //升级文件保存目录
            tempDir = webInfPath + "upgrade/";
            //FileUtil.makeDirectory(tempDir);
            VALUE_MAP.put(Environment.upgradePath, tempDir);

            //TempPath    临时路径
            tempDir = webInfPath + "tmp/";
            if (FileUtil.isDirectory(tempDir)) {
                VALUE_MAP.put(Environment.tempPath, tempDir);
            } else {
                VALUE_MAP.put(Environment.tempPath, FileUtil.mendPath(System.getProperty("java.io.tmpdir")));
            }

            //Log4jPath
            logPath = getLog4jPath((String) VALUE_MAP.get(Environment.logPath));
            if (!StringUtil.isNull(logPath)) {
                VALUE_MAP.put(Environment.log4jPath, logPath);
            } else {
                //LogPath    日志保存目录
                logPath = webInfPath + "logs/";
                if (!FileUtil.isDirectory(tempDir)) {
                    FileUtil.makeDirectory(tempDir);
                }
                VALUE_MAP.put(Environment.logPath, logPath);
            }
        }

        VALUE_MAP.put(Environment.logInfoFile, logPath + Environment.log_info_file);

        VALUE_MAP.put(Environment.logErrorFile, logPath + Environment.log_error_file);

        VALUE_MAP.put(Environment.logJspxInfoFile, logPath + Environment.log_jspx_info_file);

        VALUE_MAP.put(Environment.logJspxDebugFile, logPath + Environment.log_jspx_debug_file);

        VALUE_MAP.put(Environment.logJspxErrorFile, logPath + Environment.log_jspx_error_file);
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

    private Object getGraphicsenv() {
        if (VALUE_MAP.containsKey("java.awt.graphicsenv")) {
            return VALUE_MAP.get("java.awt.graphicsenv");
        }
        return System.getProperty("java.awt.graphicsenv");
    }

    private String getLog4jPath(String log4jPath) {
        if (log4jPath == null) {
            return null;
        }
        File f = new File(log4jPath);
        if (f.isDirectory()) {
            return FileUtil.mendPath(f.getAbsolutePath());
        }
        if (!StringUtil.isNull(log4jPath) && log4jPath.contains("$")) {
            return processTemplate(log4jPath);
        }
        return null;
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
        String getGraphicsenv = (String) getGraphicsenv();
        if (!StringUtil.isNull(getGraphicsenv)) {
            System.setProperty("java.awt.graphicsenv", getGraphicsenv);
        }

        //设置时区
        String timezone = getTimezone();
        if (!StringUtil.isNull(timezone)) {
            System.setProperty("user.timezone", timezone);
        }

        if (VALUE_MAP.containsKey(Environment.cachePath)) {
            System.setProperty(Environment.cachePath, (String) VALUE_MAP.get(Environment.cachePath));
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
            if (System.getProperty(keys) != null) {
                VALUE_MAP.put(keys, System.getProperty(keys));
            }
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
            System.setProperty("org.xml.sax.driver",cls.getName());
        } catch (Exception e) {
            try {
                Class<?> cls = Class.forName("com.sun.org.apache.xerces.internal.parsers.SAXParser");
                System.setProperty("org.xml.sax.driver",cls.getName());
            } catch (Exception e2) {
                log.info("com.sun.xml.internal.stream.XMLInputFactoryImpl " + e.getLocalizedMessage());
            }
        }
        try {
            Class<?> cls = Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl");
            System.setProperty("javax.xml.stream.XMLInputFactory",cls.getName());
        } catch (Exception e2) {
            log.info("com.sun.xml.internal.stream.XMLInputFactoryImpl " + e2.getLocalizedMessage());
        }
    }

    /**
     * 读取当前原始的启动配置文件
     * @param fileName 文件路径
     * @return 得到配置属性,注意这里只是在内存中
     */
    @Override
    public Properties readDefaultProperties(String fileName) {
        Properties p = new Properties();
        if (!FileUtil.isFileExist(fileName))
        {
            return p;
        }
        try {
            String cont = IoUtil.autoReadText(fileName);
            XOREncrypt encrypt = new XOREncrypt();
            encrypt.setSecretKey(Environment.defaultDrug);
            if (encrypt.isEncrypt(cont)) {
                cont = encrypt.getDecode(cont);
            }
            p.load(new StringReader(cont));
        } catch (Exception e) {
            log.info("create Jspx.net Env fileName=" + fileName + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        //创建配置 begin
        return p;
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
            Properties p = new Properties();
            p.load(new StringReader(cont));
            for (Object key : p.keySet()) {
                Object o = p.get(key);
                if (o == null) {
                    continue;
                }
                VALUE_MAP.put((String) key, o);
            }
            VALUE_MAP.put(Environment.jspxProperties, fileName);
            p.clear();
        } catch (Exception e) {
            log.info("create Jspx.net Env fileName=" + fileName + " " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        //创建配置 begin
    }

    @Override
    public void putEnv(String key, String value) {
          VALUE_MAP.put(key, value);
    }

    @Override
    public void deleteEnv(String key) {
        VALUE_MAP.remove(key);
    }

    @Override
    public void restorePlaceholder()  {
        for (String key : VALUE_MAP.keySet()) {
            Object o = VALUE_MAP.get(key);
            if (StringUtil.isNull(key)) {
                continue;
            }
            if (o != null) {
                String value = o.toString();
                VALUE_MAP.put(key, processTemplate(value));
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
        for (String key : VALUE_MAP.keySet()) {
            Object o = VALUE_MAP.get(key);
            if (StringUtil.isNull(key)) {
                continue;
            }
            if (o != null) {
                log.info(key + "=" + o.toString());
            }
        }

    }
}
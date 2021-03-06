package com.github.jspxnet.txweb.helper;

import com.github.jspxnet.boot.EnvFactory;
import org.slf4j.Logger;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.txweb.helper.tag.HelpElement;
import com.github.jspxnet.txweb.helper.tag.HelperElement;
import com.github.jspxnet.utils.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by chenyuan on 15-3-30.
 * 页面帮助提示
 */
public class PageHelper implements Helper, Serializable {

    private static final Logger log = LoggerFactory.getLogger(PageHelper.class);
    private String configFile = "help.xml";
    public static final String none = "none";
    public static final String Tag_HELP = "help";
    public static final String Tag_Helper = "helper";
    private String id = PageHelper.none;
    private String encode = Environment.defaultEncode;
    private String path = StringUtil.empty;

    @Override
    public String getEncode() {
        return encode;
    }


    @Override
    public void setEncode(String encode) {
        this.encode = encode;
    }

    @Override
    public String getConfigFile() {
        return configFile;
    }

    @Override
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 文件路径支持多种方式,
     * 1.如有路径默认在 软件页面目录下查找，推荐方式
     * 2.如果找不到就在class对应的目录找
     * 3.在更目录下找
     *
     * @return 得到配置文件目录
     */
    private String getFileName() {
        File file = new File(path, configFile);
        if (file.isFile()) {
            return file.getPath();
        }
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        file = new File(environmentTemplate.getString(Environment.defaultPath), configFile);
        if (file.isFile()) {
            return file.getPath();
        }
        file = new File(environmentTemplate.getString(Environment.defaultPath), FileUtil.getFileName(configFile));
        return file.getPath();
    }

    private HelpElement getConfig() throws Exception {
        if (!StringUtil.hasLength(id)) {
            id = PageHelper.none;
        }
        String fileNamePath = getFileName();
        if (!FileUtil.isFileExist(fileNamePath)) {
            throw new IOException("not find help XML file " + fileNamePath + "  帮助配置XML文件没有找到");
        } else {
            log.info("help xml file is " + fileNamePath);
        }

        //载入配置 begin
        AutoReadTextFile readTextFile = new AutoReadTextFile();
        readTextFile.setEncode(encode);
        readTextFile.setFile(fileNamePath);

        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(Tag_Helper, HelperElement.class.getName());
        List<TagNode> nodes = xmlEngine.getTagNodes(readTextFile.getContent());
        for (TagNode node : nodes) {
            HelperElement helperElement = (HelperElement) node;
            for (TagNode tagNode : helperElement.getHelpElements()) {
                HelpElement helpElement = (HelpElement) tagNode;
                if (helpElement.getId().equalsIgnoreCase(id)) {
                    return helpElement;
                }
            }
        }
        return null;
    }


    @Override
    public String getXML() throws Exception {
        HelpElement helpElement = getConfig();
        if (helpElement == null) {
            return StringUtil.empty;
        }

        StringBuilder sb = new StringBuilder("<help id=\"" + helpElement.getId() + "\" >\r\n");
        if (ArrayUtil.inArray(new String[]{"md", "markdown"}, helpElement.getType(), true) || StringUtil.isNull(helpElement.getType())) {
            sb.append("<![CDATA[").append(ScriptMarkUtil.getMarkdownHtml(helpElement.getBody())).append("]]>\r\n");
        } else if (ArrayUtil.inArray(new String[]{"txt", "text"}, helpElement.getType(), true)) {
            sb.append("<![CDATA[").append(StringUtil.toBrLine(helpElement.getBody())).append("]]>\r\n");
        } else {
            sb.append("<![CDATA[").append(helpElement.getBody()).append("]]>\r\n");
        }

        sb.append("<![CDATA[").append(helpElement.getBody()).append("]]>\r\n");
        sb.append("</help>");
        return sb.toString();
    }

    @Override
    public String getJson() throws Exception {
        HelpElement helpElement = getConfig();
        if (helpElement == null) {
            return StringUtil.empty;
        }
        JSONObject ja = new JSONObject();
        ja.put("id", helpElement.getId());
        if (ArrayUtil.inArray(new String[]{"md", "markdown"}, helpElement.getType(), true) || StringUtil.isNull(helpElement.getType())) {
            ja.put("content", ScriptMarkUtil.getMarkdownHtml(helpElement.getBody()));
        } else if (ArrayUtil.inArray(new String[]{"txt", "text"}, helpElement.getType(), true)) {
            ja.put("content", StringUtil.toBrLine(helpElement.getBody()));
        } else {
            ja.put("content", helpElement.getBody());
        }
        return ja.toString();
    }

}

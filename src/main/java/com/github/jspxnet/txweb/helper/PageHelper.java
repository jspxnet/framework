package com.github.jspxnet.txweb.helper;

import com.github.jspxnet.boot.EnvFactory;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;

import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.txweb.helper.tag.HelpElement;
import com.github.jspxnet.txweb.helper.tag.HelperElement;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by chenyuan on 15-3-30.
 * 页面帮助提示
 */
@Slf4j
public class PageHelper implements Serializable {
    private String configFile = "help.xml";
    public static final String none = "none";
    public static final String Tag_HELP = "help";
    public static final String Tag_Helper = "helper";
    private String id = PageHelper.none;
    private String path = StringUtil.empty;

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
        if (FileUtil.isFileExist(file))
        {
            return file.getPath();
        }
        EnvironmentTemplate environmentTemplate = EnvFactory.getEnvironmentTemplate();
        file = new File(environmentTemplate.getString(Environment.defaultPath), configFile);
        if (FileUtil.isFileExist(file))
        {
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
        readTextFile.setEncode(Environment.defaultEncode);
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

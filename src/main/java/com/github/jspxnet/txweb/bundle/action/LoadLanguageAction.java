package com.github.jspxnet.txweb.bundle.action;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;

/**
 * Created by chenyuan on 14-7-3.
 * 倒入语言配置
 * com.github.jspxnet.txweb.bundle.action.LoadLanguageAction
 */

@HttpMethod(caption = "语言")
public class LoadLanguageAction extends ActionSupport {
    private final static EnvironmentTemplate ENVIRONMENT_TEMPLATE = EnvFactory.getEnvironmentTemplate();

    public LoadLanguageAction() {
    }

    public String[] getLanguages()
    {

        String defaultPath = ENVIRONMENT_TEMPLATE.getString(Environment.defaultPath);

        String[] result = null;
        String[] files = StringUtil.split(FileUtil.getFileList(defaultPath, "@@@", "properties", false), "@@@");

        for (String name : files) {
            String lang = FileUtil.getNamePart(name);
            if (lang.contains("language")) {
                if (lang.contains("_")) {
                    lang = StringUtil.substringAfter(lang, "_");
                }
                result = ArrayUtil.add(result, lang);
            }
        }
        return result;
    }


    private String getLanguageFile(String language) {
        String defaultPath = ENVIRONMENT_TEMPLATE.getString(Environment.defaultPath);
        String[] files = StringUtil.split(FileUtil.getFileList(defaultPath, "@@@", "properties", false), "@@@");
        for (String name : files) {
            String lang = FileUtil.getNamePart(name);
            if (lang.contains("language")) {
                if (lang.contains("_")) {
                    lang = StringUtil.substringAfter(lang, "_");
                }
                if (lang.equalsIgnoreCase(language)) {
                    return name;
                }
            }
        }
        return null;
    }


    private String lang = "";

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    //提交表单中 post 不为空才运行 execute 方法
    //作用  if (hasFieldInfo())  save();
    @Operate(caption = "导入")
    public void save() throws Exception {
        if (StringUtil.isNull(lang)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        String fileName = getLanguageFile(lang);
        if (StringUtil.isNull(fileName)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.languageFileNotFind));
            return;
        }
        File file = new File(fileName);
        if (!file.isFile() || !file.canRead()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.readFileError));
            return;
        }

        StringMap<String, String> map = new StringMap<>();
        map.setEncode(Environment.defaultEncode);
        map.setKeySplit("=");
        map.setLineSplit(StringUtil.CRLF);
        map.loadFile(fileName);
        if (StringUtil.isNull(map.get("language"))) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileFormatError));
            return;
        }
        boolean deleteOk = language.deleteAll();
        for (String key : map.keySet()) {
            if (!StringUtil.hasLength(key)) {
                continue;
            }
            String value = map.get(key);
            if (!StringUtil.hasLength(value)) {
                continue;
            }
            language.save(key, value);
        }
        language.flush();
        if (deleteOk) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        }

    }

}

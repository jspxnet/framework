package com.github.jspxnet.scriptmark.config;

import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.Phrase;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.core.HtmlEngineImpl;
import com.github.jspxnet.scriptmark.core.block.*;
import com.github.jspxnet.scriptmark.core.block.sqlmap.*;
import com.github.jspxnet.scriptmark.core.dispose.*;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.DateUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/14 0:17
 * description: jspbox
 **/
public class SqlMapTemplateConfigurable implements Configurable, Cloneable {
    private final static Map<String, MacroBlock> regMacro = new HashMap<>();
    private Map<String, String> tagMap = new HashMap<>();
    private Map<String, Object> hashMap = new HashMap<>();
    private String[] autoImports = null;
    private String[] autoIncludes = null;
    private String[] staticModels = null;
    private String[] searchPath = null;
    private Map<String, Object> globalMap = new HashMap<>();
    private final Map<String, Phrase> phrases = new HashMap<>(20);



    final static private Configurable instance = new SqlMapTemplateConfigurable();

    static public Configurable getInstance() {
        return instance;
    }

    public SqlMapTemplateConfigurable() {
        ///////////
        hashMap.put(ScriptmarkEnv.NumberFormat, "####.##");
        hashMap.put(ScriptmarkEnv.DateFormat, DateUtil.DAY_FORMAT);
        hashMap.put(ScriptmarkEnv.DateTimeFormat, DateUtil.CURRENCY_ST_FORMAT);
        hashMap.put(ScriptmarkEnv.TimeFormat, "HH:mm");

        hashMap.put(ScriptmarkEnv.Template_update_delay, 360);
        hashMap.put(ScriptmarkEnv.Template_cache_size, 120);

        hashMap.put(ScriptmarkEnv.MacroCallTag, "@");
        hashMap.put(ScriptmarkEnv.Language, "JavaScript");
        hashMap.put(ScriptmarkEnv.Syncopate, "<>");
        hashMap.put(ScriptmarkEnv.VariableBegin, "${");
        hashMap.put(ScriptmarkEnv.VariableSafeBegin, "#{");
        hashMap.put(ScriptmarkEnv.VariableEnd, "}");
        hashMap.put(ScriptmarkEnv.escapeVariable, "\\");
        hashMap.put(ScriptmarkEnv.BreakBlockName, "break");
        hashMap.put(ScriptmarkEnv.ContinueBlockName, "continue");
        hashMap.put(ScriptmarkEnv.CompressBlockName, "compress");
        hashMap.put(ScriptmarkEnv.htmlExtType, true);
        hashMap.put(ScriptmarkEnv.xmlEscapeClean, true);
        //////////


        ////////////Tag配置 begin
        tagMap.put("setting", SettingBlock.class.getName());
        tagMap.put("assign", AssignBlock.class.getName());
        tagMap.put("if", IfBlock.class.getName());
        tagMap.put("list", ListBlock.class.getName());
        tagMap.put("macro", MacroBlock.class.getName());
        tagMap.put("switch", SwitchBlock.class.getName());
        tagMap.put("include", IncludeBlock.class.getName());
        tagMap.put("script", ScriptBlock.class.getName());
        tagMap.put("compress", CompressBlock.class.getName());
        tagMap.put("try", TryBlock.class.getName());
        tagMap.put("break", BreakBlock.class.getName());
        tagMap.put("continue", ContinueBlock.class.getName());
        tagMap.put("!--#", CommentBlock.class.getName());  //兼容HTML注释   <!--#注释说明#-->
        ////////////Tag配置 end

        //解析关系 begin
        phrases.put(HtmlEngineImpl.NONE_TAG, new NonePhrase());
        phrases.put(CommentBlock.class.getName(), new CommentPhrase());
        phrases.put(CompressBlock.class.getName(), new CompressPhrase());
        phrases.put(AssignBlock.class.getName(), new AssignPhrase());
        phrases.put(CallBlock.class.getName(), new CallPhrase());
        phrases.put(IfBlock.class.getName(), new IfPhrase());
        phrases.put(IncludeBlock.class.getName(), new IncludePhrase());
        phrases.put(ListBlock.class.getName(), new ListPhrase());
        phrases.put(ScriptBlock.class.getName(), new ScriptPhrase());
        phrases.put(SettingBlock.class.getName(), new SettingPhrase());
        phrases.put(SwitchBlock.class.getName(), new SwitchPhrase());
        phrases.put(TryBlock.class.getName(), new TryPhrase());
        phrases.put(ElseBlock.class.getName(), new ElsePhrase());
        phrases.put(CaseBlock.class.getName(), new ElsePhrase());
        phrases.put(DefaultBlock.class.getName(), new ElsePhrase());
        phrases.put(MacroBlock.class.getName(), new MacroPhrase());
        //解析关系 end


        ////////////////默认为当前系统默认目录
        hashMap.put(ScriptmarkEnv.BasePath, System.getProperty("user.dir"));

        if (instance != null) {
            autoImports = instance.getAutoImports();
            autoIncludes = instance.getAutoIncludes();
            staticModels = instance.getStaticModels();
        }
    }

    public static void regMacro(String name, MacroBlock macroBlock) {
        regMacro.put(name, macroBlock);
    }

    public static MacroBlock getMacro(String name) {
        return regMacro.get(name);
    }

    @Override
    public String[] getSearchPath() {
        return searchPath;
    }

    @Override
    public void setSearchPath(String[] searchPath) {
        this.searchPath = searchPath;
    }

    @Override
    public String[] getAutoIncludes() {
        return autoIncludes;
    }

    @Override
    public void addAutoIncludes(String file) {
        if (!ArrayUtil.inArray(autoIncludes, file, true)) {
            autoIncludes = ArrayUtil.add(autoIncludes, file);
        }
    }

    @Override
    public void setTag(String name, String className) {
        tagMap.put(name, className);
    }

    @Override
    public String removeTag(String name) {
        return tagMap.remove(name);
    }

    @Override
    public Map<String, String> getTagMap() {
        return tagMap;
    }

    @Override
    public void put(String name, Object o) {
        hashMap.put(name, o);
    }

    @Override
    public String getString(String name) {
        return (String) hashMap.get(name);
    }

    @Override
    public boolean getBoolean(String name) {
        return (Boolean) hashMap.get(name);
    }

    @Override
    public int getInt(String name) {
        Object o = hashMap.get(name);
        if (o == null) {
            return 0;
        }
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            return Integer.getInteger(o.toString(), 0);
        }
    }

    @Override
    public long getLong(String name) {
        Object o = hashMap.get(name);
        if (o == null) {
            return 0;
        }
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            return Long.parseLong(o.toString());
        }
    }

    @Override
    public Map<String, Object> getGlobalMap() {
        return globalMap;
    }

    @Override
    public void setGlobalMap(Map<String, Object> globalMap) {
        this.globalMap = globalMap;
    }

    @Override
    public void setTagMap(Map<String, String> tagMap) {
        this.tagMap = tagMap;
    }

    @Override
    public Map<String, Phrase> getPhrases() {
        return phrases;
    }

    @Override
    public void setAutoIncludes(String[] autoIncludes) {
        this.autoIncludes = autoIncludes;
    }

    @Override
    public void setHashMap(Map<String, Object> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public String[] getStaticModels() {
        return staticModels;
    }

    @Override
    public void setStaticModels(String[] staticModels) {
        this.staticModels = staticModels;
    }

    @Override
    public void addStaticModels(String str) {
        if (!ArrayUtil.inArray(staticModels, str, true)) {
            staticModels = ArrayUtil.add(staticModels, str);
        }
    }

    @Override
    public String[] getAutoImports() {
        return autoImports;
    }

    @Override
    public void addAutoImports(String str) {
        if (!ArrayUtil.inArray(autoImports, str, true)) {
            autoImports = ArrayUtil.add(autoImports, str);
        }
    }

    @Override
    public void setAutoImports(String[] autoImports) {
        this.autoImports = autoImports;
    }

    @Override
    public Configurable copy() {
        Configurable tc = new SqlMapTemplateConfigurable();
        tc.setTagMap(new HashMap<>(tagMap));
        tc.setHashMap(new HashMap<>(hashMap));
        tc.setAutoIncludes(autoIncludes);
        tc.setAutoImports(autoImports);
        tc.setGlobalMap(new HashMap<>(globalMap));
        tc.setStaticModels(staticModels);
        return tc;
    }
}
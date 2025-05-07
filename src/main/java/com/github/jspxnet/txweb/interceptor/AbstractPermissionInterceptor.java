package com.github.jspxnet.txweb.interceptor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.model.dto.AccessUrlRuleDto;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 替代BasePermissionInterceptor 拦截器:
 * 合并配置采用yaml格式，容器为AccessUrlRuleDto
 * 有特殊问题
 * 例如：
 * {@code
 *   user:
 *   where: "${userType}>0 && ${userType}<5"
 *   url:
 *     - "/voluntary/sj/*"
 *     - /voluntary/examinee/info
 *     - /voluntary/college/list
 * }
 */
@Slf4j
public abstract class AbstractPermissionInterceptor extends InterceptorSupport{
    public final static String KEY_DEFAULT_PAGE_RULES_FILE = "page.rules.yaml";
    public static final String KEY_CACHE_PAGE_RULES = "page:rules:conf";

    public static final String KEY_WHITE_RULES = "white";
    public static final ScriptEngineManager engineManager = new ScriptEngineManager();
    public static final ScriptEngine engine = engineManager.getEngineByName("JavaScript");
    @Setter
    @Getter
    protected String pageRulesFile = KEY_DEFAULT_PAGE_RULES_FILE;

    @Setter
    protected boolean permission = true;

    @Setter
    protected boolean autoOrganizeId = true;

    public void loadConfig()
    {
        File file = null;
        try {
            if (pageRulesFile != null) {
                file = EnvFactory.getFile(pageRulesFile);
            }

            if (file != null) {
                log.info("载入pageRulesFile:{}", file);
                String text = IoUtil.autoReadText(file);
                if (StringUtil.isNullOrWhiteSpace(text))
                {
                    return;
                }
                Yaml yaml = new Yaml();
                Map<String, Object> yamlData = yaml.load(text);
                // 4. 解析为 Java 对象
                Map<String, AccessUrlRuleDto> rules = JSON.parseObject(JSON.toJSONString(yamlData)).toJavaObject(
                        new TypeReference<Map<String, AccessUrlRuleDto>>() {}
                );

                for (AccessUrlRuleDto rule:rules.values())
                {
                    rule.setWhere(XMLUtil.deleteQuote(rule.getWhere()));
                    List<String> urls = rule.getUrl();
                    urls.replaceAll(XMLUtil::deleteQuote);
                }
                JSCacheManager.put(DefaultCache.class,KEY_CACHE_PAGE_RULES,rules);

            } else
            {
                log.error("pageRulesFile:{},没有找到",pageRulesFile);
            }
        } catch (Exception e) {
            log.error("AbstractPermissionInterceptor",e);
        }
    }


    /**
     *
     * @param url 判断地址
     * @return 判断是否为白名单
     */
    public boolean inAccessWhite(String url) {

        //载入配置
        Map<String, AccessUrlRuleDto> rules = (Map<String, AccessUrlRuleDto>) JSCacheManager.get(DefaultCache.class, KEY_CACHE_PAGE_RULES);
        if (ObjectUtil.isEmpty(rules)) {
            loadConfig();
        }
        if (ObjectUtil.isEmpty(rules)) {
            return false;
        }
        Bindings bindings = engine.createBindings();
        bindings.put("userType", UserEnumType.NONE.getValue());
        bindings.put("name", "guest");
        bindings.put("congealType", 0);
        bindings.put("namespace", StringUtil.empty);
        bindings.put("organizeId", StringUtil.empty);

        AccessUrlRuleDto rule = rules.get(KEY_WHITE_RULES);
        if (rule == null || ObjectUtil.isEmpty(rule.getWhere()) || ObjectUtil.isEmpty(rule.getUrl())) {
            return false;
        }
        if (StringUtil.isNullOrWhiteSpace(rule.getWhere()))
        {
            return false;
        }
        try {
            Object result = engine.eval("if (" + rule.getWhere() + ") true; else false;", bindings);
            if (ObjectUtil.toBoolean(result)) {
                for (String ruleUrl : rule.getUrl()) {
                    if (ruleUrl.equals(url)) {
                        return true;
                    }
                    if (ruleUrl.startsWith("!") && StringUtil.getPatternFind(url, StringUtil.substringAfter(ruleUrl,"!")))
                    {
                        return false;
                    }
                    if (StringUtil.getPatternFind(url, ruleUrl)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            bindings.clear();
        }
        return false;
    }
    /**
     * 添加条件解析方法
     * @param role 角色
     * @param url url地址
     * @return 判断是否可执行
     */
    public boolean checkAccessRule(IRole role,String url)
    {

        //载入配置
        Map<String, AccessUrlRuleDto> rules = (Map<String, AccessUrlRuleDto>)JSCacheManager.get(DefaultCache.class,KEY_CACHE_PAGE_RULES);
        if (ObjectUtil.isEmpty(rules))
        {
            loadConfig();
        }
        if (ObjectUtil.isEmpty(rules))
        {
            return false;
        }

        Bindings bindings = engine.createBindings();

        if (role==null)
        {
            bindings.put("userType", UserEnumType.NONE.getValue());
            bindings.put("name", "guest");
            bindings.put("congealType", 0);
            bindings.put("namespace", StringUtil.empty);
            bindings.put("organizeId", StringUtil.empty);
        } else {
            bindings.put("userType", role.getUserType());
            bindings.put("name", role.getName());
            bindings.put("congealType", role.getCongealType());
            bindings.put("namespace", role.getNamespace());
            bindings.put("organizeId", role.getOrganizeId());
        }

        try {
            for (AccessUrlRuleDto rule:rules.values())
            {
                if (rule==null||ObjectUtil.isEmpty(rule.getWhere())||ObjectUtil.isEmpty(rule.getUrl()))
                {
                    continue;
                }
                if (StringUtil.isNullOrWhiteSpace(rule.getWhere()))
                {
                    return false;
                }
                Object result = engine.eval("if ("+rule.getWhere()+") true; else false;",bindings);
                if (ObjectUtil.toBoolean(result))
                {
                    for (String ruleUrl : rule.getUrl()) {
                        if (ruleUrl.equals(url)) {
                            return true;
                        }
                        if (ruleUrl.startsWith("!") && StringUtil.getPatternFind(url, StringUtil.substringAfter(ruleUrl,"!")))
                        {
                            return false;
                        }
                        if (StringUtil.getPatternFind(url, ruleUrl)) {
                            return true;
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        finally {
            bindings.clear();
        }

        return false;
    }

}

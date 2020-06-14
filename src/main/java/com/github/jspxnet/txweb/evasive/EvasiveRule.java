package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenYuan on 2017/6/14.
 * 转发器，一般补保存到数据库，目前只是
 */
@Table(name = "jspx_evasive_rule", caption = "页面回避规则", cache = true, create = false)
public class EvasiveRule implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "拦截器名称", length = 200, notNull = true)
    private String name = StringUtil.ASTERISK;

    //单位为秒,300默认为5分钟
    @Column(caption = "间隔时间", notNull = true, defaultValue = "300")
    private int interval = 300;

    @Column(caption = "最大次数", notNull = true, defaultValue = "300")
    private int maxTimes = 300;

    @Column(caption = "请求到方法", length = 10, notNull = true, defaultValue = "method")
    private String method = "POST";

    @Column(caption = "请求名称", length = 100, notNull = false)
    private String url = "";

    @Column(caption = "缓冲长度", defaultValue = "1000", notNull = false)
    private int cacheSize = 1000;

    @Column(caption = "监禁时间", defaultValue = "1000", notNull = false)
    private int imprisonSecond = 1000;

    @Column(caption = "请求名称", length = 16, notNull = false)
    private String logic = "or";

    @Column(caption = "返回", length = 50, notNull = false)
    private String result = "";

    private List<Condition> conditions = new ArrayList<Condition>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public void addConditions(Condition conditions) {
        this.conditions.add(conditions);
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public int getImprisonSecond() {
        return imprisonSecond;
    }

    public void setImprisonSecond(int imprisonSecond) {
        this.imprisonSecond = imprisonSecond;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    /*
    public List<ResultConfigBean> getResultConfigs() {
        return resultConfigs;
    }

    public void setResultConfigs(List<ResultConfigBean> resultConfigs) {
        this.resultConfigs = resultConfigs;
    }

    public void addResultConfigs(ResultConfigBean resultConfigBean) {
        this.resultConfigs.add(resultConfigBean);
    }
 */

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<evasive name=\"" + StringUtil.replace(name, "\"", "&quot;") + "\" interval=\"" + interval + "\" maxTimes=\"" + maxTimes + "\" method=\"" + method + "\" url=\"" + url + "\" imprisonSecond=\"" + imprisonSecond + "\" logic=\"" + logic + "\">\n");

        if (conditions != null) {
            for (Condition condition : conditions) {
                sb.append("        <condition type=\"" + condition.getRuleType() + "\"><![CDATA[" + condition.getScript() + "]]></condition>\n");
            }
        }

        sb.append("</evasive>");
        return sb.toString();
    }


}

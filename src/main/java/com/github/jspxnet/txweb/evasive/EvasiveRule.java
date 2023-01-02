package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenYuan on 2017/6/14.
 * 转发器，一般补保存到数据库，目前只是
 */
@Data
@Table(name = "jspx_evasive_rule", caption = "页面回避规则",  create = false)
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

    @Column(caption = "请求名称", length = 100)
    private String url = "";

    @Column(caption = "缓冲长度", defaultValue = "1000")
    private int cacheSize = 1000;

    @Column(caption = "监禁时间", defaultValue = "1000")
    private int imprisonSecond = 1000;

    @Column(caption = "请求名称", length = 16)
    private String logic = "or";

    @Column(caption = "返回", length = 50)
    private String result = "";

    private List<Condition> conditions = new ArrayList<>();

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public void addConditions(Condition conditions) {
        this.conditions.add(conditions);
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

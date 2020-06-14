package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;


@Table(name = "jspx_evasive_query_black", caption = "查询方式", cache = true, create = false)
public class QueryBlack implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "拦截器名称", length = 200, notNull = true)
    private String name = "";

    @Column(caption = "ip字段", length = 50, notNull = true)
    private String ipField = "ip";

    @Column(caption = "次数字段", length = 50, notNull = true)
    private String timesField = "times";

    @Column(caption = "最小次数", notNull = true, defaultValue = "100")
    private int minTimes = 100;

    @Column(caption = "黑名单个数", length = 3, notNull = true, defaultValue = "3")
    private int blackSize = 3;

    @Column(caption = "监禁时间", defaultValue = "1000", notNull = false)
    private int imprisonSecond = 1000;

    @Column(caption = "监禁时间", length = 250, notNull = false)
    private String sql = "";

    @Column(caption = "最后执行时间", notNull = false)
    private long lastQueryTimeMillis = 0;

    @Column(caption = "返回方式", length = 250, notNull = false)
    private String result = "";

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

    public int getMinTimes() {
        return minTimes;
    }

    public void setMinTimes(int minTimes) {
        this.minTimes = minTimes;
    }

    public int getBlackSize() {
        return blackSize;
    }

    public void setBlackSize(int blackSize) {
        this.blackSize = blackSize;
    }

    public int getImprisonSecond() {
        return imprisonSecond;
    }

    public void setImprisonSecond(int imprisonSecond) {
        this.imprisonSecond = imprisonSecond;
    }

    public String getIpField() {
        return ipField;
    }

    public void setIpField(String ipField) {
        this.ipField = ipField;
    }

    public String getTimesField() {
        return timesField;
    }

    public void setTimesField(String timesField) {
        this.timesField = timesField;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getLastQueryTimeMillis() {
        return lastQueryTimeMillis;
    }

    public void setLastQueryTimeMillis(long lastQueryTimeMillis) {
        this.lastQueryTimeMillis = lastQueryTimeMillis;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<queryBlack name=\"" + StringUtil.replace(name, "\"", "&quot;") + "\" ipField=\"" + ipField + "\" timesField=\"" + timesField + "\" minTimes=\"" + minTimes + "\" blackSize=\"" + blackSize + "\" imprisonSecond=\"" + imprisonSecond + "\" result=\"" + result + "\">\n");
        sb.append("<![CDATA[" + sql + "]]></queryBlack>");
        return sb.toString();
    }
}

package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by chenyuan on 2016/1/14.
 */
@Table(name = "jspx_download_file_client", caption = "附件下载记录")
public class DownloadFileClient extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "附件ID", notNull = true)
    private long fid = 0;

    @Column(caption = "名称", dataType = "isLengthBetween(1,250)", length = 250, notNull = true)
    private String url = StringUtil.empty;

    @Column(caption = "浏览器", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String browser = StringUtil.empty;

    @Column(caption = "操作系统", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String system = StringUtil.empty;

    @Column(caption = "位置", dataType = "isLengthBetween(1,100)", length = 100)
    private String location = StringUtil.empty;

    @Column(caption = "网络类型", dataType = "isLengthBetween(1,20)", length = 20)
    private String netType = StringUtil.empty;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

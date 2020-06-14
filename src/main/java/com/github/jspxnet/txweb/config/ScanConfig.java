package com.github.jspxnet.txweb.config;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;

@Table(caption = "action扫描载入", create = false)
public class ScanConfig {

    @Column(caption = "扫描载入的包", length = 200, notNull = true)
    private String packageName = StringUtil.empty;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Column(caption = "命名空间", length = 200, notNull = true)
    private String namespace = "";

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

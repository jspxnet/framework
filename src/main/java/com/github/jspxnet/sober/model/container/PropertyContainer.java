package com.github.jspxnet.sober.model.container;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 可扩展的表对象
 *
 * @author chenyuan
 */
@Data
public abstract class PropertyContainer implements Serializable {
    protected final Map<String, Object> values = new HashMap<>();

    @Column(caption = "操作人", length = 50, notNull = true)
    protected String putName = StringUtil.empty;

    @Column(caption = "操作人ID", notNull = true)
    protected long putUid = 0;

    @Column(caption = "IP地址", length = 48, notNull = true, defaultValue = "127.0.0.1")
    protected String ip = "127.0.0.1";

    @Column(caption = "创建时间", notNull = true)
    protected Date createDate = new Date();

    public void setIp(String ip) {
        if (ip != null && ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        this.ip = ip;
    }

    public boolean isNull(String key) {
        return !values.containsKey(key)|| values.get(key) == null;
    }

    public boolean containsKey(String key)
    {
        return values.containsKey(key);
    }

    public Object put(String key,Object v)
    {
        return values.put(key,v);
    }

    public Object get(String key) {
            return values.get(key);
    }

    public void putAll(java.util.Map<String,Object> inmap) {
        values.putAll(inmap);
    }

    @Override
    public String toString() {
        return ObjectUtil.toString(this);
    }
}

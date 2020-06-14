package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by ChenYuan on 2017/6/15.
 */
@Table(name = "jspx_evasive_condition", caption = "页面回避条件", cache = true, create = false)
public class Condition implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "逻辑关系", length = 20)
    private String logic = "or";

    @Column(caption = "规则类型", length = 20)
    private String ruleType = StringUtil.empty;

    @Column(caption = "规则脚本", length = 1000)
    private String script = StringUtil.empty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}

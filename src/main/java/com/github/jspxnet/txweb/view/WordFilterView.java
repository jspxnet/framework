package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.DFAFilter;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.Set;

/**
 * Created by yuan on 2015/6/16 0016.
 */
public class WordFilterView extends ActionSupport {
    ///////////////载入IOC DAO 对象 begin
    protected DFAFilter filter;
    public void setFilter(DFAFilter filter) {
        this.filter = filter;
    }

    ///////////////载入IOC DAO 对象 end
    public int matchType = 1;      //最小匹配规则

    @Param(caption = "最小匹配规则", min = 1, max = 2)
    public void setMatchType(int matchType) {
        if (matchType == 1 || matchType == 2) {
            this.matchType = matchType;
        }
    }

    private String text;

    @Param(caption = "文档", max = 10000)
    public void setText(String text) {
        this.text = text;
    }

    @Operate(caption = "搜索")
    public Set<String> getSearch( @Param(caption = "文档", max = 10000,required = true) String text) throws Exception {
        Set<String> result = filter.search(text, matchType);
        if (!result.isEmpty()) {
            filter.updateTimes(result);
        }
        return filter.getOriginal(result);
    }

    public String replace() {
        return filter.replace(text, matchType, "*");
    }


}

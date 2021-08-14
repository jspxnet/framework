package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.support.HelperAction;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by chenyuan on 15-4-1.
 * 帮助接口提供resetful方式
 * 格式标准：
 * jcms/help/admin/aboutme       admin/aboutme 为ID号
 */
@HttpMethod(caption = "帮助")
public class HelperView extends HelperAction {

    @Override
    public String execute() throws Exception {
        String url = request.getRequestURI();
        if (StringUtil.countMatches(url, "/") < 3) {
            TXWebUtil.print("<div style=\"float:left; text-align:left; padding-left:20px; margin:10px; border:#F00 solid 1px; width:94%; font-size:14px; color:#000; background-color:#FFF;\">\n"
                    + "<h4>ERROR：</h4>\n" + "<blockquote>不存在的帮助信息</blockquote>"
                    + "</div>", WebOutEnumType.HTML.getValue(), response);
            return NONE;
        }
        url = StringUtil.substringAfter(url, "/");
        url = StringUtil.substringBetween(StringUtil.substringAfter(url, "/"), "/", StringUtil.DOT + getEnv(Environment.filterSuffix));
        super.setId(url);
        return super.execute();
    }

}

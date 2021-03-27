package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.List;

/**
 * 提供默认的验证信息，方便菜单判断哪些显示
 */
@HttpMethod(caption = "菜单页面")
public class MenuView extends ActionSupport {

    public IUserSession getMember() {
        return getUserSession();
    }

    public List<String> getSoftList()  {
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        return webConfigManager.getSoftList();
    }

}

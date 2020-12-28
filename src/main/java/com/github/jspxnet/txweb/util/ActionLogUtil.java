package com.github.jspxnet.txweb.util;

import com.github.jspxnet.scriptmark.util.ScriptConverter;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.utils.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/25 23:22
 * description: 动作日志封装工厂
 **/
public class ActionLogUtil {
    private ActionLogUtil()
    {

    }

    public static ActionLog createActionLog(ActionSupport action)
    {
        ActionLog actionLog = new ActionLog();
        String id = null;
        if (ClassUtil.isDeclaredMethod(action.getClass(), "getId")) {
            Object o = BeanUtil.getProperty(action, "getId");
            if (o!=null)
            {
                id = ObjectUtil.toString(o);
            }
        } else {
            id = action.getString("id");
        }
        if (id==null) {
            String[] ids = action.getArray("id", false);
            id = ArrayUtil.toString(ids, ";");
        }
        actionLog.setObjectId(id);
        actionLog.setClassName(action.getClass().getName());
        actionLog.setObjectType(StringUtil.substringBefore(actionLog.getClassName(), "Action"));
        actionLog.setTitle(action.getActionLogTitle());
        Object logObject = action.getActionLogContent();
        if (logObject instanceof String) {
            actionLog.setContent((String) logObject);
        } else if (logObject instanceof Boolean || logObject instanceof StringBuilder || logObject instanceof StringBuffer || logObject instanceof Long || logObject instanceof Integer) {
            actionLog.setContent(logObject.toString());
        } else {
            actionLog.setContent(ScriptConverter.toXml(logObject));
        }
        actionLog.setActionResult(action.getActionResult());
        actionLog.setIp(action.getRemoteAddr());
        String organizeId = action.getEnv(ActionEnv.KEY_organizeId);
        if (action.getSession()!=null&&StringUtil.isEmpty(organizeId)) {
            organizeId = ObjectUtil.toString(action.getSession().getAttribute(ActionEnv.KEY_organizeId));
        }
        actionLog.setOrganizeId(organizeId);

        actionLog.setUrl(action.getRequest().getRequestURI());

        IUserSession userSession = action.getUserSession();
        actionLog.setPutName(userSession.getName());
        actionLog.setPutUid(userSession.getUid());

        actionLog.setNamespace(action.getRootNamespace());
        return actionLog;
    }

}

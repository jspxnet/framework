package com.github.jspxnet.sober.queue.cmd;

import com.github.jspxnet.json.GsonUtil;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.queue.CmdContainer;
import com.github.jspxnet.sober.queue.CmdRun;
import com.github.jspxnet.sober.table.StoreQueueStatus;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.google.gson.Gson;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/24 22:54
 * description: jspbox
 **/
public abstract class BaseCmdRun implements CmdRun {
    protected final static Gson gson = GsonUtil.createGson();
    final static protected String SUCCEED = "succeed";
    final static protected String FAIL = "fail";
    protected final static StoreQueueStatus storeStatus = new StoreQueueStatus();
    protected GenericDAO genericDAO;
    @Override
    public void setGenericDAO(GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    protected CmdContainer cmdContainer;
    @Override
    public void setCmdContainer(CmdContainer cmdContainer) {
        this.cmdContainer = cmdContainer;
    }

    protected boolean saveSucceedLog;
    @Override
    public void setSaveSucceedLog(boolean saveSucceedLog) {
        this.saveSucceedLog = saveSucceedLog;
    }

    @Override
    public abstract void execute() throws Exception;
}

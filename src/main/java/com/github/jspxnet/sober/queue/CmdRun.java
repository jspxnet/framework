package com.github.jspxnet.sober.queue;

import com.github.jspxnet.txweb.dao.GenericDAO;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/24 22:53
 * description: jspbox
 **/
public interface CmdRun {
    /**
     *
     * @param genericDAO DOA对象
     */
    void setGenericDAO(GenericDAO genericDAO);

    /**
     *
     * @param cmdContainer 命令容器
     */
    void setCmdContainer(CmdContainer cmdContainer);

    /**
     *
     * @param saveSucceedLog 是否保存成功日志
     */
    void setSaveSucceedLog(boolean saveSucceedLog);

    /**
     *
     * @throws Exception 执行动作
     */
    void execute() throws Exception;
}

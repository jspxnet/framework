package com.github.jspxnet.sober.queue.cmd;

import com.github.jspxnet.sober.queue.BaseRedisStoreQueue;
import com.github.jspxnet.sober.table.StoreQueueStatus;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/9/24 22:53
 * description: jspbox
 **/
@Slf4j
public class UpdateSqlCmd extends BaseCmdRun {
    public final static String name = BaseRedisStoreQueue.CMD_UPDATE_SQL;

    @Override
    public void execute() throws Exception {
        String sql = cmdContainer.getDataJson();
        if (sql == null) {
            return;
        }
        if (StringUtil.isEmpty(sql)) {
            return;
        }
        String className = cmdContainer.getClassName();
        if (!String.class.getName().equals(className)) {
            return;
        }
        //如果保存成功就不保存日志信息了，这里没有处理redis断电的情况
        StoreQueueStatus storeQueueStatus = BeanUtil.copy(storeStatus,StoreQueueStatus.class);
        storeQueueStatus.setObjectData(sql);
        storeQueueStatus.setClassName(className);
        storeQueueStatus.setCmd(name);
        try {

            storeQueueStatus.setResult(genericDAO.update(sql));
            if (storeQueueStatus.getResult() > 0 && saveSucceedLog) {
                //保存成功
                storeQueueStatus.setStatus(SUCCEED);
                genericDAO.save(storeQueueStatus);
            }
        } catch (Exception e) {
            log.error("store queue {} error data:{},info:{}", name, sql, e.getMessage());
            //如果没有保存成功，也没用异常的情况
            storeQueueStatus.setStatus(FAIL);
            genericDAO.save(storeQueueStatus);
        }
    }
}

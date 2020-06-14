package com.github.jspxnet.sober.queue.cmd;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.queue.BaseRedisStoreQueue;
import com.github.jspxnet.sober.table.StoreQueueStatus;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/24 22:52
 * description: jspbox
 **/
@Slf4j
public class UpdateObjectCmd extends BaseCmdRun {
    public final static String name = BaseRedisStoreQueue.CMD_UPDATE;

    @Override
    public void execute() throws Exception {
        String dataJson = cmdContainer.getDataJson();

        if (StringUtil.isEmpty(dataJson)) {
            return;
        }
        Class<?> cls = ClassUtil.loadClass(cmdContainer.getClassName());
        if (ClassUtil.isStandardType(cls))
        {
            StoreQueueStatus storeQueueStatus = BeanUtil.copy(storeStatus,StoreQueueStatus.class);
            storeQueueStatus.setObjectData(dataJson);
            storeQueueStatus.setClassName(cls.getName());
            storeQueueStatus.setCmd(name);
            storeQueueStatus.setStatus(FAIL);
            storeQueueStatus.setException("非对象不能保存");
            genericDAO.save(storeQueueStatus);
            return;
        }

        Table table = cls.getAnnotation(Table.class);
        if (table==null)
        {
            StoreQueueStatus storeQueueStatus = BeanUtil.copy(storeStatus,StoreQueueStatus.class);
            storeQueueStatus.setObjectData(dataJson);
            storeQueueStatus.setClassName(cls.getName());
            storeQueueStatus.setCmd(name);
            storeQueueStatus.setStatus(FAIL);
            storeQueueStatus.setException("没有table注释保存那个表");
            genericDAO.save(storeQueueStatus);
            return;
        }

        Object obj = gson.fromJson(dataJson,cls);
        //如果保存成功就不保存日志信息了，这里没有处理redis断电的情况
        StoreQueueStatus storeQueueStatus = BeanUtil.copy(storeStatus,StoreQueueStatus.class);
        storeQueueStatus.setObjectData(new JSONObject(obj).toString());
        storeQueueStatus.setClassName(obj.getClass().getName());
        storeQueueStatus.setCmd(name);
        try {
            storeQueueStatus.setResult(genericDAO.update(obj));
            if (storeQueueStatus.getResult() > 0 && saveSucceedLog) {
                //保存成功
                storeQueueStatus.setStatus(SUCCEED);
                genericDAO.save(storeQueueStatus);
            }
        } catch (Exception e) {
            log.error("store queue {} error data:{},info:{}",name,new JSONObject(obj).toString(), e.getMessage());
            //如果没有保存成功，也没用异常的情况
            storeQueueStatus.setStatus(FAIL);
            genericDAO.save(storeQueueStatus);
        }
    }
}

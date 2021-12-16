package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.txweb.dao.SyncIndexDAO;
import com.github.jspxnet.txweb.table.SyncIndex;
import com.github.jspxnet.utils.StringUtil;


/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/12/4 12:54
 * description: 同步是记录已经同步的数据id,从而得到被删除的数据
 *
 *
 *
 **/

public class SyncIndexDAOImpl extends JdbcOperations implements SyncIndexDAO {

    /**
     * 清理数据
     * @param cls 类对象
     * @param <T> 类型
     */
    @Override
    public <T> void clear(Class<T> cls)
    {
        super.delete(SyncIndex.class,"className",cls.getName());
    }

    /**
     * 保存同步的索引
     * @param cls 类
     * @param keyField 索引字段
     * @param <T> 类型
     */
    @Override
    public <T> void deleteNoKeyData(Class<T> cls, String keyField)
    {
        String masterTableName = getTableName(cls);
        String keyTableName = getTableName(SyncIndex.class);
        try {
            super.execute( "DELETE FROM "+masterTableName+" WHERE "+keyField+" NOT IN (SELECT keyValue FROM "+keyTableName+" WHERE className="+ StringUtil.quoteSql(cls.getName()) +")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





/*
  使用例子
    /**
     * 获取云星空，并转存到中间库
     * 手工修改本地数据,不会被更新,需要修改md5才会被更新
     */
/*
private <T> void requestSave(Class<T> cls,String filter) {
    int index = 0;
    int count = 0;
    int insert = 0;
    int update = 0;

    try {
        syncIndexDAO.clear(cls);
        //执行单据查询 查询设备卡片所有字段数据
        K3CloudApiClient  client = getK3Client();
        JSONObject trem = k3cloudService.createQuery(cls,filter,index);
        List<List<Object>> lists = client.executeBillQuery(trem.toString());
        while (!ObjectUtil.isEmpty(lists))
        {
            if (lists.size()==1)
            {
                JSONObject result = new JSONObject(lists.get(0));
                if (result.toString().contains("\"IsSuccess\":false"))
                {
                    log.error("K3cloud请求发送异常:{}",result);
                    break;
                }

*//*
{"data":[{"Result":{"ResponseStatus":{
    "ErrorCode":500,
    "IsSuccess":false,
    "Errors":[{
        "FieldName":null,
        "Message":"data数据包中的参数FormId必传。",
        "DIndex":0
    }],
    "SuccessEntitys":[],
    "SuccessMessages":[],
    "MsgCode":8
}}}]}
*//*
            }

            count += lists.size();
            List<T> dtoList = k3cloudService.copyList(lists,cls);
            if (ObjectUtil.isEmpty(dtoList)) {
                return;
            }

            String keyField = k3cloudService.getKey(cls);
            //将dots存入数据库
            for (T dto : dtoList) {
                //取出数据库中匹配设备数据
                String hashMd5 = BeanUtil.getFieldValue(dto,"hashMd5",true);
                SyncIndex syncIndex = new SyncIndex();
                syncIndex.setClassName(cls.getName());
                syncIndex.setKeyValue(hashMd5);
                syncIndexDAO.save(syncIndex);

                Serializable keyValue = BeanUtil.getFieldValue(dto,keyField,true);
                T old = syncIndexDAO.get(cls, keyField, keyValue, false);
                try {
                    if (old != null)
                    {

                        String oldHashMd5 = BeanUtil.getFieldValue(old,"hashMd5",true);
                        Long oldId = BeanUtil.getFieldValue(old,"id",true);
                        if (hashMd5!=null&&!hashMd5.equals(oldHashMd5)) {
                            BeanUtil.setFieldValue(dto,"id",oldId);
                            syncIndexDAO.update(dto);
                            update++;
                        }
                    } else {
                        syncIndexDAO.save(dto);
                        insert++;
                    }
                    //对待存数据进行对比
                } catch (Exception e) {
                    log.error("写入设备卡片出错", e);
                    return;
                }
            }
            if (lists.size()<500)
            {
                break;
            }
            index += lists.size();
            trem = k3cloudService.createQuery(cls,StringUtil.empty,index);
            lists = client.executeBillQuery(trem.toString());
        }
        //删除被删除的数据
        syncIndexDAO.deleteNoKeyData(cls,"hashMd5");
    } catch (Exception e) {
        log.error("---保存k3数据出错---", e);
        return;
    }
    log.info("新增" + insert + "条数据," + "更新" + update + "条数据,size为" +count + "条");
}
*/

}

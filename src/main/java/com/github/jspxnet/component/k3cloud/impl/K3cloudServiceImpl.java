package com.github.jspxnet.component.k3cloud.impl;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.component.k3cloud.*;
import com.github.jspxnet.component.k3cloud.element.KingdeeAccountElement;
import com.github.jspxnet.component.k3cloud.element.TableElement;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/30 0:12
 * description: com.github.jspxnet.component.k3cloud.impl.K3cloudServiceImpl
 **/
@Slf4j
public class K3cloudServiceImpl implements K3cloudService {

    private final Map<String, K3TableConf> tableMap = new LinkedHashMap<>();
    private KingdeeAccount kingdeeAccount;
    private String configFile = "kingdee.table.xml";
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Init
    public void init()
    {
        try {
            loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 载入账号配置
     * @throws Exception 异常
     */
    private void loadConfig() throws Exception {

        File file = EnvFactory.getFile(configFile);
        if (file==null)
        {
            log.error("K3cloudService 不能读取配置文件:{}",configFile);
            throw new Exception("K3cloudService 不能读取配置文件"+configFile);
        }
        String configString = IoUtil.autoReadText(file);
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(KingdeeAccountElement.TAG_NAME, KingdeeAccountElement.class.getName());
        List<TagNode> results = xmlEngine.getTagNodes(configString);
        for (TagNode tNode : results) {
            KingdeeAccountElement node = (KingdeeAccountElement)tNode;

            kingdeeAccount = new KingdeeAccount();
            kingdeeAccount.setAcctId(node.getAcctId());
            kingdeeAccount.setUserName(node.getUserName());
            kingdeeAccount.setAppId(node.getAppId());
            kingdeeAccount.setAppSec(node.getAppSec());
            kingdeeAccount.setServerUrl(node.getServerUrl());
            kingdeeAccount.setLcid(StringUtil.toInt(node.getLcid()));
            kingdeeAccount.setPwd(node.getPwd());
            break;
        }

        xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(TableElement.TAG_NAME, TableElement.class.getName());
        results = xmlEngine.getTagNodes(configString);
        for (TagNode tNode : results) {
            TableElement node = (TableElement)tNode;
            K3TableConf tableConf = new K3TableConf();
            tableConf.setTableId(node.getId());
            tableConf.setCaption(node.getCaption());
            tableConf.setClassName(node.getClassName());
            tableConf.setContent(node.getBody());
            tableConf.setKey(node.getKey());
            tableMap.put(tableConf.getClassName(),tableConf);
        }
    }

    /**
     *
     * @return 得到配置的账号
     */
    @Override
    public KingdeeAccount getAccount() {
        return kingdeeAccount;
    }

    /**
     *
     * @param className 类名称
     * @return 得到配置的表
     */
    @Override
    public K3TableConf getK3TableConf(String className)
    {
        return tableMap.get(className);
    }

    /**
     *
     * @param cls 表类
     * @return 得到配置的表
     */
    @Override
    public K3TableConf getK3TableConf(Class<?> cls)
    {
        return tableMap.get(cls.getName());
    }

    /**
     *
     * @param cls 表类
     * @return 得到字段映射
     */
    @Override
    public Map<String, KingdeeField> getFieldMap(Class<?> cls)
    {
        K3TableConf k3TableConf =  tableMap.get(cls.getName());
        if (k3TableConf==null)
        {
            return null;
        }
        return KingdeeUtil.getFieldMap(k3TableConf.getContent());
    }

    /**
     *
     * @param cls 表类
     * @return 得到k3查询字段列表
     */
    @Override
    public String getFieldKeys(Class<?> cls)
    {
        K3TableConf k3TableConf = tableMap.get(cls.getName());
        if (k3TableConf==null)
        {
            return null;
        }
        return KingdeeUtil.getFieldKeys(k3TableConf.getContent());
    }

    /**
     *
     * @param cls 表类
     * @return 得到配置的映射关系
     */
    @Override
    public String[] getBeanFields(Class<?> cls)
    {
        K3TableConf k3TableConf = tableMap.get(cls.getName());
        if (k3TableConf==null)
        {
            return null;
        }
        return KingdeeUtil.getBeanFields(k3TableConf.getContent());
    }

    /**
     *
     * @param cls 表类
     * @return 得到字段
     */
    @Override
    public String createBeanFields(Class<?> cls)
    {
        K3TableConf k3TableConf =  tableMap.get(cls.getName());
        if (k3TableConf==null)
        {
            return null;
        }
        return KingdeeUtil.createBeanFields(k3TableConf.getContent());
    }

    @Override
    public String getKey(Class<?> cls)
    {
        K3TableConf k3TableConf =  tableMap.get(cls.getName());
        if (k3TableConf==null)
        {
            return null;
        }
        return k3TableConf.getKey();
    }

    /**
     *
     * @param cls 类型对象
     * @param filter 过滤条件
     * @param limit 返回个数
     * @return 对象列表
     */
    public JSONObject createQuery(Class<?> cls, String filter,int limit) {
        return createQuery(cls,  filter,StringUtil.empty, 0,limit);
    }

    /**
     *
     * @param cls 类型对象
     * @param filter 过滤条件
     * @param index 开始行
     * @param limit 返回个数
     * @return 对象列表
     */
    public JSONObject createQuery(Class<?> cls, String filter,int index, int limit) {
        return createQuery(cls,  filter,StringUtil.empty, index,limit);
    }
    /**
     *
     * @param cls 表类
     * @param filter  过滤条件
     * @param orderString  排序
     * @param index  开始条数， 每次500条
     * @param limit  每次500条
     * @return 得到请求列表
     */
    @Override
    public JSONObject createQuery(Class<?> cls, String filter, String orderString, int index, int limit) {
        K3TableConf k3TableConf =  tableMap.get(cls.getName());
        if (k3TableConf==null)
        {
            return null;
        }
        String fieldKeys =  KingdeeUtil.getFieldKeys(k3TableConf.getContent());
        return KingdeeUtil.createQuery(k3TableConf.getTableId(),fieldKeys,filter,orderString,index,limit);
    }
    /**
     *
     * @param list k3返回的列表
     * @param cls  表类
     * @param <T> 表类
     * @return 得到实体列表
     */
    @Override
    public <T> List<T> copyList(List<List<Object>> list, Class<T> cls)
    {
        return copyList(list, cls, true);
    }

    /**
     *
     * @param list k3返回的列表
     * @param cls 表类
     * @param hashMd5 是否填充md5
     * @param <T> 表类
     * @return 得到实体列表
     */
    @Override
    public <T> List<T> copyList(List<List<Object>> list, Class<T> cls, boolean hashMd5)
    {
        if (ObjectUtil.isEmpty(list))
        {
            return new ArrayList<>(0);
        }
        List<T> result = new ArrayList<>();
        String[] fieldNameArray = getBeanFields(cls);
        JSONArray jsonArray = new JSONArray(list);
        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String,Object> beanMap = new HashMap<>();
            JSONArray lines = jsonArray.getJSONArray(i);
            if (ObjectUtil.isEmpty(lines))
            {
                continue;
            }
            for (int j = 0; j < lines.size(); j++) {
                String name = fieldNameArray[j];
                beanMap.put(name, lines.get(j));
            }
            T copy = BeanUtil.copy(beanMap, cls);
            if (hashMd5)
            {
                String hashTxt = EncryptUtil.getMd5(ObjectUtil.toString(copy));
                if (ClassUtil.isDeclaredField(cls,"hashMd5"))
                {
                    BeanUtil.setFieldValue(copy,"hashMd5",hashTxt);
                }
            }
            result.add(copy);
        }

        Field[]  fields = ClassUtil.getDeclaredFields(cls);
        for (T dto:result)
        {
            for (Field field:fields)
            {
                Column column = field.getAnnotation(Column.class);
                if (column==null)
                {
                    continue;
                }
                if (StringUtil.isNull(column.option()))
                {
                    continue;
                }
                StringMap<String,String> stringMap = new StringMap<>();
                stringMap.setKeySplit(":");
                stringMap.setLineSplit(";");
                stringMap.setString(column.option());
                String key = BeanUtil.getFieldValue(dto,field.getName(),true);
                BeanUtil.setFieldValue(dto,field.getName()+"Name",StringUtil.trim(stringMap.getString(key)));
            }
        }
        return result;
    }



}

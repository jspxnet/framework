package com.github.jspxnet.component.k3cloud;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ListUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.*;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/29 22:55
 * description: thermo-model
 **/
public final class KingdeeUtil {

    /**
     * 金蝶API接口字段解析
     * @param str api字段
     * @return 得到对应字段
     */
    public static Map<String,KingdeeField> getFieldMap(String str)
    {
        Map<String,KingdeeField> fieldMap = new LinkedHashMap<>();
        String[] lines = StringUtil.split(str,StringUtil.CR);
        for (String line:lines)
        {
            if (StringUtil.isEmpty(line))
            {
                continue;
            }
            if (line.trim().endsWith("(必填项)"))
            {
                line = StringUtil.replace(line,"(必填项)","");
            }
            String field = null;
            if (line.contains("[")&&line.contains("]"))
            {
                field = StringUtil.substringBetween(line,"[","]");
                line = StringUtil.substringBefore(line,"[");
            }
            line = StringUtil.trim(line);
            if (StringUtil.isEmpty(line))
            {
                continue;
            }
            String name = StringUtil.replace(StringUtil.substringBefore(line,"："),"/","");
            name = StringUtil.replace(name,"（","");
            name = StringUtil.replace(name,"）","");
            String kingField = StringUtil.substringAfter(line,"：");
            if (StringUtil.isEmpty(field))
            {
                field = StringUtil.underlineToCamel(kingField);
            }
            KingdeeField kingdeeField = new KingdeeField();
            kingdeeField.setField(field);
            kingdeeField.setKingdeeField(kingField);
            kingdeeField.setName(name);
            fieldMap.put(field,kingdeeField);
        }
        return fieldMap;
    }

    /**
     *
     * @param str  api字段
     * @return 得到查询字段
     */
    public static String getFieldKeys(String str)
    {
        Map<String,KingdeeField> fieldMap = getFieldMap(str);
        List<String> kindFieldList = BeanUtil.copyFieldList(fieldMap.values(),"kingdeeField");
        return ListUtil.toString(kindFieldList,StringUtil.COMMAS);
    }


    public static String[] getBeanFields(String str)
    {
        Map<String,KingdeeField> fieldMap = getFieldMap(str);
        return fieldMap.keySet().toArray(new String[0]);
    }
    /**
     *
     * @param str  api字段
     * @return 创建java bean 字段表
     */
    public static String createBeanFields(String str)
    {
        String templateString = "@Column(caption = \"${k.name}\", length = 100)\r\nprivate String ${k.field} = StringUtil.empty;";
        String templateDate = "@Column(caption = \"${k.name}\")\r\nprivate Date ${k.field};";
        String templateBool = "@Column(caption = \"${k.name}\", length = 10)\r\nprivate String ${k.field};";

        Placeholder placeholder = EnvFactory.getPlaceholder();
        Map<String,KingdeeField> fieldMap = getFieldMap(str);
        StringBuilder sb = new StringBuilder();
        for (KingdeeField kingdeeField:fieldMap.values())
        {
            Map<String,Object> valueMap = new HashMap<>();
            valueMap.put("k",kingdeeField);
            String kingField = kingdeeField.getKingdeeField();
            if (kingField.endsWith("Date")||kingField.substring(0,kingField.length()-1).endsWith("Date"))
            {
                sb.append(placeholder.processTemplate(valueMap,templateDate)).append("\r\n");
            } else
            if (kingField.startsWith("F_IS"))
            {
                sb.append(placeholder.processTemplate(valueMap,templateBool)).append("\r\n");
            } else
            {
                sb.append(placeholder.processTemplate(valueMap,templateString)).append("\r\n");
            }
        }
        return sb.toString();
    }

    public static JSONObject createQuery(String tableId,String fieldKeys,String filter,int index)
    {
        JSONObject data = new JSONObject();

        //FormId：业务对象表单Id（必录）
        data.put("FormId", tableId);

        data.put("FieldKeys", fieldKeys);

        //FilterString：过滤条件，字符串类型（非必录）
        data.put("FilterString", filter);

        //OrderString：排序字段，字符串类型（非必录）
        data.put("OrderString", "");

        //TopRowCount：返回总行数，整型（非必录）
        data.put("TopRowCount", "0");

        //StartRow：开始行索引，整型（非必录）
        data.put("StartRow", index);

        //Limit：最大行数，整型，不能超过2000（非必录）
        data.put("Limit", "500");
        return data;
    }

}

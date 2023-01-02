package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.config.SoberCalcUnique;
import com.github.jspxnet.sober.config.SoberNexus;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class SoberTableDto implements Serializable {

    //数据库名 ，这里不是数据库类型
    private String databaseName = StringUtil.empty;
    //数据库表名
    private String name = StringUtil.empty;
    //表别名
    private String caption = StringUtil.empty;

    //是否动态创建表
    private boolean create = true;

    //cache
    private boolean useCache = true;

    //实体,具体的类
    @JsonIgnore
    private Class<?> entity;
    //关键字名
    private String primary = StringUtil.empty;
    //是否自动生成ID
    private boolean autoId = true;
    //是否使用数据库自增
    private String idType = StringUtil.empty;
    //映射对应关系
    private Map<String, SoberNexus> nexusMap = new LinkedHashMap<>();
    //字段
    private List<SoberColumnDto> columns = new LinkedList<>();
    //字段
    private Map<String, SoberCalcUnique> calcUniqueMap = new LinkedHashMap<>();

    //可扩展
    private boolean canExtend = false;

    //最后访问时间
    private long lastDate = System.currentTimeMillis();

    /**
     * 得到表名
     *
     * @return String
     */

    public String getName() {
        if (StringUtil.isNull(name)) {
            name = entity.getSimpleName();
        }
        return name;
    }

    public String getCaption() {
        if (StringUtil.isNull(caption)) {
            return getName();
        }
        return caption;
    }


    public boolean isSerial() {
        return IDType.serial.equalsIgnoreCase(idType);
    }

    public boolean isAutoId() {
        return autoId;
    }


    public void setAutoId(boolean autoId) {
        this.autoId = autoId;
    }


    public SoberColumnDto getColumn(String keys) {
        for (SoberColumnDto column : columns) {
            if (column.getName().equalsIgnoreCase(keys)) {
                return column;
            }
        }
        return null;
    }


    public String[] getFieldArray() {
        String[] fieldArray = null;
        for (SoberColumnDto column : columns) {
            if (IDType.serial.equalsIgnoreCase(idType) && column.getName().equals(primary)) {
                continue;
            }
            fieldArray = ArrayUtil.add(fieldArray, column.getName());
        }
        return fieldArray;
    }

    public String[] getFullFieldArray() {

        String[] fieldArray = null;
        for (SoberColumnDto column : columns) {
            fieldArray = ArrayUtil.add(fieldArray, column.getName());
        }
        return fieldArray;
    }


    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return new JSONObject(this, false).toString();
    }


    public boolean equals(TableModels models) {
        return (this.toString()).equals(models.toString());
    }

    @JsonField(name = "className")
    public String getClassName() {
        return entity.getName();
    }

    @JsonField(name = "isCanExtend")

    public boolean isCanExtend() {
        return canExtend;
    }

    public void setCanExtend(boolean canExtend) {
        this.canExtend = canExtend;
    }

    @JsonField(caption = "id")
    public String getId() {
        JSONObject json = new JSONObject();
        json.put("d", databaseName);
        json.put("n", name);
        json.put("p", primary);
        json.put("c", columns.size());
        return EncryptUtil.getMd5(json.toString());
    }

}
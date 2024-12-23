package com.github.jspxnet.sober.model.container;

import com.github.jspxnet.sober.IPropertyChange;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.utils.ObjectUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author chenyuan
 */

public abstract class AbstractObjectValue extends PropertyContainer implements IPropertyChange {
    private transient TableModels tableModels;
    protected transient DataMap<String, Object> oldValues = null;

    //这里是为了多语言实现

    private boolean available = true;

    private boolean isLoaded;

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public Object put(String key, Object value) {
        Object objectValue = super.put(key, value);
        if (this.oldValues == null) {
            this.oldValues = new DataMap<>();
        }
        if (!ObjectUtil.compare(objectValue, value)) {
            this.oldValues.put(key, objectValue);
        }
        return objectValue;
    }

    /**
     * 拷贝数据到老的里边
     */
    @Override
    public void copyNewToOld() {
        this.oldValues.clear();
        Map<String, Object> result = new HashMap<>(getValues());
        this.oldValues.putAll(result);
    }

    public boolean compareNewToOld() {
        return ObjectUtil.compare(this, this.oldValues);
    }

    @Override
    public boolean isValueChange() {
        if (ObjectUtil.isEmpty(this.oldValues)) {
            return false;
        }
        return !ObjectUtil.compare(this.oldValues, values);
    }

    /**
     * 还原,将老的值还原回来
     */
    @Override
    public void resetValue()
    {
        if (isAvailable()) {
            if (this.oldValues != null) {
                for (Object value: this.oldValues.values())
                {
                    if (value instanceof IPropertyChange) {
                        ((IPropertyChange)value).resetValue();
                    }
                }
                this.values.clear();
                this.values.putAll(this.oldValues);
            }
        }
    }
    @Override
    public void setAvailable(boolean var1) {
        this.available = var1;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public TableModels getTableModels() {
        return tableModels;
    }

    public void setTableModels(TableModels tableModels) {
        this.tableModels = tableModels;
    }
}

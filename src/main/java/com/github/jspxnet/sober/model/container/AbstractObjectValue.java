package com.github.jspxnet.sober.model.container;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.IPropertyChange;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.util.DataMap;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenyuan
 */

public abstract class AbstractObjectValue extends PropertyContainer implements IPropertyChange {
    @Setter
    @Getter
    private transient TableModels tableModels;

    @JsonIgnore
    protected transient Map<String, Object> oldValues = new HashMap<>();

    @JsonIgnore
    private boolean available = true;

    @Setter
    @JsonIgnore
    private boolean isLoaded;

    public boolean isLoaded() {
        return this.isLoaded;
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
        this.oldValues.putAll(getValues());
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

}

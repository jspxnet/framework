package com.github.jspxnet.sober;


/**
 * 识别数据是否改变,方便在业务保存的时候对应生成业务sql
 */
public interface IPropertyChange {
    //值是否改变
    boolean isValueChange();

    //还原,将老的值还原回来
    void resetValue();

    //新的列表,拷贝到老值列表
    void copyNewToOld();

    //是否可获取,目前手动控制
    void setAvailable(boolean var1);

    boolean isAvailable();
}

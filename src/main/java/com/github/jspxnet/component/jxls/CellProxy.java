package com.github.jspxnet.component.jxls;

import java.io.Serializable;


public interface CellProxy extends Serializable {
    int getMergerRows();

    void setMergerRows(int mergerRows);
}

package com.github.jspxnet.txweb.action;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.CreateTableParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.view.TableMetaView;

public class TableMetaAction extends TableMetaView {

    public RocResponse<?> createTable(@Param(caption = "建表参数",required = true) CreateTableParam param)
    {
        return RocResponse.success();
    }
}

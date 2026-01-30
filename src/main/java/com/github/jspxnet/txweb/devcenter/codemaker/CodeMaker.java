package com.github.jspxnet.txweb.devcenter.codemaker;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.txweb.result.RocResponse;
import java.util.List;
import java.util.Map;

public interface CodeMaker {
    /**
     *
     * @param templateName 模版
     * @param modelId 类ID
     * @param jumpFields 跳过字段
     * @return 生成添加编辑窗体
     */

    RocResponse<String> builderPage(String templateName, long modelId, List<String> jumpFields);

    /**
     *
     * @param tableModels 数据模型
     * @param jumpFields 跳过字段
     * @return 生成js 的表格 json信息
     */
    JSONArray builderColumn(TableModels tableModels, List<String> jumpFields);

    /**
     *
     * @return 的大命名空间列表
     */
    List<String>  getNamespaceList();
    /**
     *
     * @param dto 是否包含DTO 是否保护dto
     * @return 的大命名空间列表
     */
    Map<Long, TableModels>  getSoberTableList(boolean dto);
}

package com.github.jspxnet.txweb.devcenter.codemaker;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.sober.TableModels;
<<<<<<< HEAD
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.txweb.result.RocResponse;

=======
import com.github.jspxnet.txweb.result.RocResponse;
>>>>>>> dev
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
    RocResponse<String> builderPage(String templateName, String modelId, List<String> jumpFields);
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
<<<<<<< HEAD
     * @param dto 是否包含DTO
     * @return 的命名空间列表
     */
    Map<String, TableModels>   getSoberTableList(boolean dto);
=======
     * @param dto 是否包含DTO 是否保护dto
     * @param extend  0:所有;1:可扩展;2:不可扩展
     * @return 的大命名空间列表
     */
    Map<String, TableModels>   getSoberTableList(boolean dto,int extend);
>>>>>>> dev
}

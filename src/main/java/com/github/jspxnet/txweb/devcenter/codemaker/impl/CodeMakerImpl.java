package com.github.jspxnet.txweb.devcenter.codemaker.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.scriptmark.load.StringSource;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.devcenter.codemaker.CodeMaker;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.PageCodeMaker;
import com.github.jspxnet.txweb.table.UiTemplate;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

@Slf4j
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class CodeMakerImpl implements CodeMaker {
    public static final String varClassName = "className";
    //public static final String varPackageName = "packageName";
/*    public static final String varDate = "date";
    public static final String varEncode = "encode";
    public static final String varNamespace = "namespace";*/
    //public static final String varFilePath = "filePath";
    public static final String varName = "varName";

    private static final String validation_templet = "validation.ftl";
    private static final String BUILDER_CLASS = "builderClass";


    @Ref
    protected GenericDAO genericDAO;

    public PageCodeMaker getPageCodeMakerLastVersion(String urlId, String namespace) {
        return genericDAO.createCriteria(PageCodeMaker.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("urlId", urlId))
                .addOrder(Order.desc("version")).objectUniqueResult(false);
    }

    public UiTemplate getUiTemplateLastVersion(String name) {
        return genericDAO.createCriteria(UiTemplate.class)
                .add(Expression.eq("name", name))
                .addOrder(Order.desc("version")).objectUniqueResult(false);
    }

    public UiTemplate getUiTemplate(long id) {
        return genericDAO.load(UiTemplate.class,id);
    }


    public static  String callScriptMarkEngine(Source source, Map<String, Object> map) throws Exception {
        ScriptMarkEngine scriptMarkEngine = new ScriptMarkEngine(ScriptmarkEnv.noCache, source, null);
        Writer writer = new StringWriter();
        scriptMarkEngine.process(writer, map);
        writer.close();
        return writer.toString();
    }

    private static String getClassVarName(Class<?> cla)
    {
        return StringUtil.substringAfterLast(cla.getName(), ".");
    }

    /**
     *
     * @param templateName 模版
     * @param modelId 类ID
     * @param jumpFields 跳过字段
     * @return 生成添加编辑窗体
     */
    @Override
    public RocResponse<String> builderPage(String templateName, String modelId, List<String> jumpFields)
    {
        TableModels tableModels = genericDAO.getAllTableModels(true,0).get(modelId);
        UiTemplate template = getUiTemplateLastVersion(templateName);
        if (template==null||template.getId()<=0)
        {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),"模版不存在" + templateName);
        }
        Source source = new StringSource(template.getContent());
        try {
            Map<String, Object> valueMap = createValueMap( tableModels,jumpFields);
            JSONArray columnModels = builderColumn(tableModels, jumpFields);
            JSONObject json = new JSONObject();
            json.put("name","id");
            json.put("caption","选择");
            json.put("type","string");
            json.put("edit","true");
            json.put("input","selectbox");
            json.put("width",30);
            columnModels.addFirst(json);
            valueMap.put("columnModels",columnModels);
            valueMap.put("modelId",modelId);
            String html = callScriptMarkEngine(source,valueMap);
            html = StringUtil.removeEmptyLine(html);
            return RocResponse.success(html) ;
        } catch (Exception e) {
            log.error(ObjectUtil.toString(tableModels));
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(),e.getMessage());
        }
    }

    private static String getVarName(Class<?> cla)
    {
        return StringUtil.uncapitalize(StringUtil.substringAfterLast(cla.getName(), "."));
    }

    /**
     *
     * @param tableModels 数据模型
     * @param jumpFields 跳过字段
     * @return 生成js 的表格 json信息
     */
    @Override
    public JSONArray builderColumn(TableModels tableModels, List<String> jumpFields)
    {
        if (tableModels==null)
        {
            return new JSONArray();
        }
        Class<?>  builderClass = tableModels.getEntity();
        AssertException.isNull(builderClass,"类模型ID对应的模型为空");

        List<SoberColumn> soberColumns = tableModels.getColumns();
        JSONArray jsonArray = new JSONArray();
        for (SoberColumn column : soberColumns) {
            if (column.isHidden() || jumpFields.contains(column.getName())) {
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", column.getName());
            jsonObject.put("caption", column.getCaption());
            jsonObject.put("input", column.getInput());
            if (ArrayUtil.contains(new String[]{"int", "long", "float", "double"}, column.getClassType().getSimpleName())) {
                jsonObject.put("type", "number");
            } else {
                jsonObject.put("type", column.getClassType().getSimpleName());
            }
            if (column.getCaption().contains("标题"))
            {
                jsonObject.put("width",200);
            } else
            if (column.getInput().contains("select")||column.getInput().contains("radio")||column.getInput().contains("checkbox"))
            {
                jsonObject.put("width",100);
            }
            else {
                jsonObject.put("width",140);
            }

            if (!StringUtil.isNull(column.getOption())) {
                //jsonObject.put("option", column.getOptionList());
                jsonObject.put("option", new ArrayList<>(0));
            }
            jsonObject.put("edit", "id".equalsIgnoreCase(column.getName()));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
     }


    /**
     *
     * @param tableModels 类模型
     * @param jumpFields  排除字段
     * @return 创建列表
     * @throws Exception 异常
     */
    private static Map<String, Object> createValueMap(TableModels tableModels,List<String> jumpFields)  {
        if (tableModels==null)
        {
            return new HashMap<>(0);
        }
        Class<?>  builderClass = tableModels.getEntity();
        List<SoberColumn> soberColumns = tableModels.getColumns();
        boolean haveDate = false;
        for (SoberColumn soberColumn:soberColumns)
        {
            if (soberColumn.getClassType()==Date.class)
            {
                haveDate = true;
                break;
            }
        }
        Map<String, Object> valueMap = new HashMap<>();
        String title = tableModels.getCaption();
        valueMap.put("title", title);
        valueMap.put("haveDate", haveDate);
        valueMap.put("columns", soberColumns);
        valueMap.put("jumpFields", jumpFields);
        valueMap.put("modelId", tableModels.getId());
        valueMap.put(BUILDER_CLASS, builderClass);
        valueMap.put(varName,getVarName(builderClass));
        valueMap.put(varClassName,getClassVarName(builderClass));
        return valueMap;
    }

    /**
     *
     * @return 的大命名空间列表
     */
    @Override
    public  List<String>  getNamespaceList() {
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        return webConfigManager.getSoftList();
    }
    /**
     *
     * @param dto 是否包含DTO 是否保护dto
     * @param extend  0:所有;1:可扩展;2:不可扩展
     * @return 的大命名空间列表
     */
    @Override
    public  Map<String, TableModels>  getSoberTableList(boolean dto,int extend) {
        return genericDAO.getAllTableModels(dto,extend);
    }


}

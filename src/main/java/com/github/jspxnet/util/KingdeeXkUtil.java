package com.github.jspxnet.util;

import com.github.jspxnet.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KingdeeXkUtil {
    private KingdeeXkUtil()
    {}

    private static  final Map<String,String> fieldNameList = new HashMap<>();

    private static  final List<String> wordList = new ArrayList<>();

    public static final Map<String,String> fixWrodMap = new HashMap<>();

    static
    {
        fieldNameList.put("物料ID","materialId");
        fieldNameList.put("物料编码","materialNumber");
        fieldNameList.put("物料名称","materialName");
        fieldNameList.put("单位","unit");
        fieldNameList.put("ENTRYID","entryId");
        fieldNameList.put("FENTRYID","entryId");

        fieldNameList.put("产品编码","productNumber");
        fieldNameList.put("产品名称","productName");
        fieldNameList.put("组织编码","orgNumber");
        fieldNameList.put("规格型号","specification");
        fieldNameList.put("工艺路线ID","routeId");
        fieldNameList.put("下达日期","conveyDate");
        fieldNameList.put("开工日期","startDate");
        fieldNameList.put("完工日期","finishDate");
        fieldNameList.put("结案日期","closeDate");
        fieldNameList.put("结算日期","costDate");
        fieldNameList.put("图号","routeName");
        fieldNameList.put("工艺路线名称","routeName");
        fieldNameList.put("描述","description");
        fieldNameList.put("计划跟踪号","mtoNo");
        fieldNameList.put("项目编号","projectNo");
        fieldNameList.put("成本权重","costWeight");
        fieldNameList.put("生产日期","produceDate");
        fieldNameList.put("有效期至","expiryDate");
        fieldNameList.put("产品类型","productType");
        fieldNameList.put("预计产出数量","aiExpectoutputQty");
        fieldNameList.put("批号ID","lotId");


        wordList.add("Word");
        wordList.add("Type");
        wordList.add("Weight");
        wordList.add("Project");
        wordList.add("Route");
        wordList.add("Name");
        wordList.add("Cost");
        wordList.add("Bill");
        wordList.add("Qty");
        wordList.add("Unit");
        wordList.add("Control");
        wordList.add("Time");
        wordList.add("Text");
        wordList.add("Sale");
        wordList.add("Supplier");
        wordList.add("Entry");
        wordList.add("Array");
        wordList.add("List");
        wordList.add("File");
        wordList.add("Work");
        wordList.add("Trans");
        wordList.add("Serial");
        wordList.add("Note");
        wordList.add("Stock");
        wordList.add("Real");
        wordList.add("Keeper");
        wordList.add("Flag");
        wordList.add("Must");
        wordList.add("Owner");
        wordList.add("Name");
        wordList.add("Put");
        wordList.add("Out");
        wordList.add("Process");
        wordList.add("Shop");

        fixWrodMap.put("fPale","pale");
        fixWrodMap.put("fentryid","entryId");
        fixWrodMap.put("fdeptid","deptId");
        fixWrodMap.put("fBaseStockInQuaSelQty","baseStockInQuaSelQty");
        fixWrodMap.put("fAiDecimal","aiDecimal");
        fixWrodMap.put("fAiQty","aiQty");
        fixWrodMap.put("fAiSrc","aiSrc");
        fixWrodMap.put("fdetailid","detailId");
        fixWrodMap.put("fsnQty","snQty");
        fixWrodMap.put("fAiStockQty","aiStockQty");
        fixWrodMap.put("fmoBillNo","moBillNo");
        fixWrodMap.put("fmoEntryseq","moEntryseq");
    }


    public static String getFileName(String caption)
    {
        return fieldNameList.get(caption);
    }

    public static String fixWordName(String fieldName)
    {
        for (String w:wordList)
        {
            if (fieldName.startsWith("f"+w.toLowerCase()))
            {
                fieldName = fieldName.substring(1);
            }
            if (fieldName.endsWith(w.toLowerCase()+"id"))
            {
                fieldName = StringUtil.replace(fieldName,w.toLowerCase()+"id",w.toLowerCase()+"Id");
            }
            if (fieldName.endsWith(w.toLowerCase()+"no"))
            {
                fieldName = StringUtil.replace(fieldName,w.toLowerCase()+"no",w.toLowerCase()+"No");
            }
            fieldName = StringUtil.replace(fieldName,w.toLowerCase(),w);
        }
        return replaceWordName(fieldName);
    }


    private static String replaceWordName(String fieldName)
    {
        for (String w:fixWrodMap.keySet())
        {
            String v = fixWrodMap.get(w);
            fieldName = StringUtil.replace(fieldName,w,v);
        }
        return fieldName;
    }

}

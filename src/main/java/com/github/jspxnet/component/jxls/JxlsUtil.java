package com.github.jspxnet.component.jxls;

import com.github.jspxnet.utils.*;
import com.github.jspxnet.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.poi.ss.usermodel.*;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.jdbc.JdbcHelper;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;


@Slf4j
public final class JxlsUtil {
    private JxlsUtil()
    {

    }

    /**
     * 导出EXCEL
     *
     * @param is    excel文件流
     * @param os    生成模版输出流
     * @param model 模版中填充的数据
     * @param conn  数据库中的连接，支持sql查询,是用完后记得自己关闭
     * @param fixEva 强制修复公式计算
     * @throws IOException 一次
     */
    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model,boolean fixEva,Connection conn) throws IOException {
        Context context = PoiTransformer.createInitialContext();
        if (model != null) {
            for (String key : model.keySet()) {
                //这里转换一下支持dto对象
                Object obj = model.get(key);
                boolean isMap = false;
                if (obj instanceof Collection)
                {
                    Collection<Object> objCol = (Collection)obj;
                    Iterator<Object> iterator = objCol.iterator();
                    if (iterator.hasNext())
                    {
                        if (iterator.next() instanceof Map)
                        {
                            isMap = true;
                        }
                    }
                }
                if (!isMap)
                {
                    //转换城Map对象
                    List<Map<String,Object>> list = new ArrayList<>();
                    Collection<Object> objCol = (Collection)obj;
                    if (objCol!=null)
                    {
                        for (Object o : objCol) {
                            list.add(ObjectUtil.getMap(o));
                        }
                    }
                    context.putVar(key, list);
                } else
                {
                    context.putVar(key, obj);
                }

            }
        }
        if (conn != null) {
            JdbcHelper jdbcHelper = new JdbcHelper(conn);
            context.putVar("jdbc", jdbcHelper);
        }


        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        if (model!=null)
        {
            jxlsHelper.setEvaluateFormulas(ObjectUtil.toBoolean(model.get("EvaluateFormulas")));
            jxlsHelper.setProcessFormulas(ObjectUtil.toBoolean(model.get("ProcessFormulas")));
            jxlsHelper.setUseFastFormulaProcessor(ObjectUtil.toBoolean(model.get("UseFastFormulaProcessor")));
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Transformer transformer = null;
        if (fixEva)
        {
            transformer = jxlsHelper.createTransformer(is, byteArrayOutputStream);
        } else
        {
            transformer = jxlsHelper.createTransformer(is, os);
        }

        //获得配置
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
        //自定义功能
        Map<String, Object> funcs = new HashMap<>(2);
        //添加自定义功能
        funcs.put("jspx", new JxlsFunction());
        JexlEngine customJexlEngine = new JexlBuilder().namespaces(funcs).create();
        evaluator.setJexlEngine(customJexlEngine);


        //必须要这个，否者表格函数统计会错乱
        jxlsHelper.processTemplate(context, transformer);
        if (!fixEva)
        {
            return;
        }
        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        workbook.write(os);
        byteArrayOutputStream.close();
        workbook.close();
    }

    /**
     *
     * @param is    excel文件流
     * @param os    生成模版输出流
     * @param model 模版中填充的数据
     * @param fixEva  强制执行公司
    * @throws IOException 异常
     */
    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model,boolean fixEva) throws IOException {
        exportExcel(is, os, model, fixEva,null);
    }

    /**
     * 导出EXCEL
     *
     * @param is    excel文件流
     * @param os    生成模版输出流
     * @param model 模版中填充的数据 不能用dto对象
     * @throws IOException 一次
     */
    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException {
        exportExcel(is, os, model, false,null);
    }

    /**
     *<pre>{@code
     *         String templatePath = "E:/jspx_role.xlsx";
     *         InputStream in = new FileInputStream(templatePath);
     *         Map<Integer,String> fieldMap = new HashMap<>();
     *         fieldMap.put(0,"id");
     *         fieldMap.put(1,"name");
     *         List<Role> list = JxlsUtil.getExcelBean(in,0,0,0,fieldMap, Role.class);
     *         System.out.println(ObjectUtil.toString(list));
     *}</pre>
     * @param in 输入流
     * @param sheetIndex 第几个sheet  0 开始
     * @param startRow  数据开始行
     * @param startCol  数据开始列
     * @param beanFiled  字段映射关系  例如:{1:name,2:old}
     * @param cla bean类型
     * @param <T> cla bean类型
     * @return 对象列表
     * @throws Exception 异常
     */
      public static <T> List<T> getExcelBean(InputStream in,int sheetIndex, int startRow, int startCol,Map<Integer,String> beanFiled,Class<T> cla) throws Exception
      {
        Workbook workbook = WorkbookFactory.create(in);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        List<T> list = new ArrayList<>();
        // 取得最后一行的行号
        int rowNum = sheet.getLastRowNum() + 1;
        // 行循环开始
        for (int i = startRow; i < rowNum; i++) {
            T obj = null;
            if (cla.equals(Map.class))
            {
                obj = (T)new HashMap<>();
            } else
            {
                obj = cla.newInstance();
            }
            // 行
            Row row = sheet.getRow(i);
            // 每行的最后一个单元格位置
            int cellNum = sheet.getRow(i).getLastCellNum();
            // 列循环开始
            for (int j = startCol; j < cellNum; j++) {
                Cell cell = row.getCell(j);
                if (null == cell)
                {
                    continue;
                }
                Object cellValue = null;
                short format = cell.getCellStyle().getDataFormat();
                // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                if (CellType.NUMERIC==cell.getCellType())
                {
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        if (format == 20 || format == 32) {
                            Date date = cell.getDateCellValue();
                            cellValue = DateUtil.toString(date,DateUtil.TIME_FORMAT);
                        } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                            double value = cell.getNumericCellValue();
                            Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                            cellValue =  DateUtil.toString(date,DateUtil.DAY_FORMAT);
                        }else {// 日期
                            Date date = cell.getDateCellValue();
                            cellValue = DateUtil.toString(date, DateUtil.FULL_ST_FORMAT);
                        }

                    } else {
                        BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
                        cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
                    }
                } else
                {
                    cellValue = cell.getStringCellValue();
                }
                String filedName = beanFiled.get(j);
                if (filedName!=null)
                {
                    if (cla.equals(Map.class))
                    {
                        BeanUtil.invoke(obj,"put",filedName,cellValue);

                    } else
                    {
                        BeanUtil.setFieldValue(obj,filedName,cellValue);
                    }
                }
            }
            list.add(obj);
        }
        return list;
    }

    public static int getNumberOfSheets(InputStream in)  {
        try (Workbook workbook = WorkbookFactory.create(in)){
            return workbook.getNumberOfSheets();
        } catch (Exception e) {
            //..
            return 0;
        }
        finally {
            if (in!=null)
            {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static <T> List<T> getMap(InputStream in,int sheetIndex, int startRow, int startCol, Class<T> cla) throws Exception
    {
        List<T> list = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(in);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            /**
             * 取得最后一行的行号
             * 第一行作为表头字段
             */
            Row headRow = sheet.getRow(0);
            int rowNum = sheet.getLastRowNum() + 1;
            Map<Integer,String> headMap = new LinkedHashMap<>();
            // 每行的最后一个单元格位置
            int cellNum = headRow.getLastCellNum();
            // 列循环开始
            for (int j = startCol; j < cellNum; j++) {
                Cell cell = headRow.getCell(j);
                if (null == cell) {
                    continue;
                }
                Object cellValue = null;
                short format = cell.getCellStyle().getDataFormat();
                // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                if (CellType.NUMERIC == cell.getCellType()) {
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        if (format == 20 || format == 32) {
                            Date date = cell.getDateCellValue();
                            cellValue = DateUtil.toString(date,DateUtil.TIME_FORMAT);
                        } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                            double value = cell.getNumericCellValue();
                            Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                            cellValue =  DateUtil.toString(date,DateUtil.DAY_FORMAT);
                        }else {// 日期
                            Date date = cell.getDateCellValue();
                            cellValue = DateUtil.toString(date, DateUtil.FULL_ST_FORMAT);
                        }

                    } else {
                        BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
                        cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
                    }
                }
                else if (CellType.FORMULA == cell.getCellType()) {
                    cellValue =  cell.getCellFormula();
                }
                else if (CellType.BLANK == cell.getCellType()) {
                    cellValue = StringUtil.empty;
                }
                else {
                    cellValue = cell.getStringCellValue();
                }
                if (cellValue==null)
                {
                    continue;
                }
                headMap.put(j,cellValue.toString());

            }
            // 行循环开始
            for (int i = startRow; i < rowNum; i++) {
                T obj = null;
                if (cla.equals(Map.class))
                {
                    obj = (T)new LinkedHashMap<>();
                } else
                {
                    obj = cla.newInstance();
                }
                // 行
                Row row = sheet.getRow(i);
                // 每行的最后一个单元格位置

                // 列循环开始
                for (int j = startCol; j < sheet.getRow(i).getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (null == cell)
                    {
                        continue;
                    }
                    Object cellValue = null;

                    short format = cell.getCellStyle().getDataFormat();
                    // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                    try {
                        if (CellType.NUMERIC == cell.getCellType()) {
                            if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                if (format == 20 || format == 32) {
                                    Date date = cell.getDateCellValue();
                                    cellValue = DateUtil.toString(date,DateUtil.TIME_FORMAT);
                                } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                                    double value = cell.getNumericCellValue();
                                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                                    cellValue =  DateUtil.toString(date,DateUtil.DAY_FORMAT);
                                }else {// 日期
                                    Date date = cell.getDateCellValue();
                                    cellValue = DateUtil.toString(date, DateUtil.FULL_ST_FORMAT);
                                }

                            } else {
                                BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
                                cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
                            }
                        }
                        else if (CellType.FORMULA == cell.getCellType()) {
                            cellValue =  cell.getCellFormula();
                        }
                        else if (CellType.BLANK == cell.getCellType()) {
                            cellValue = StringUtil.empty;
                        }
                        else {
                            cellValue = cell.getStringCellValue();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        log.error(headMap.get(j) +",取值错误,不要使用引用方式的值",e);
                    }

                    String filedName = headMap.get(j);
                    if (filedName!=null)
                    {
                        if (cla.equals(Map.class))
                        {
                            Map map = (Map)obj;
                            map.put(filedName,cellValue);

                        } else
                        {
                            BeanUtil.setFieldValue(obj,filedName,cellValue);
                        }
                    }
                }
                list.add(obj);
            }
        } finally {
            in.close();
        }

        return list;
    }

    /**
     * 合并算法
     * 是用方法:JxlsUtil.getMergeValue(list,"acs98","mergerRows")
     * 在excel单元格里边
     *
     * @param objectList 对象
     * @param field  需要合并的字段
     * @param mergeNumFieldName  合并算法的数字字段,动态生成的字段
     *                          例如:单元里边填写的  $ {jspx:mergeCell(d.acs98,d.mergerRows)}  mergerRows 就是
     * @return 合并算法,算法计算后添加一个字段保存算法数据
     */
    public static List<?> getMergeValue(List<?> objectList, String field,String mergeNumFieldName) {
        if (objectList == null || objectList.isEmpty()) {
            return new ArrayList<>(0);
        }
        int[] rowValue = new int[objectList.size()];
        int j = 0;
        rowValue[0] = 0;
        String name = BeanUtil.getFieldValue(objectList.get(0), field,true);
        for (int i = 1; i <objectList.size(); i++) {
            if (name != null && name.equals(BeanUtil.getFieldValue(objectList.get(i), field,true))) {
                j++;
                rowValue[i] = 0;
            } else {
                rowValue[i - 1] = j;
                j = 0;
            }
            if (objectList.size()-1==i && name != null && name.equals(BeanUtil.getFieldValue(objectList.get(i), field,true)))
            {
                rowValue[i] = j;
            }
            name =  BeanUtil.getFieldValue(objectList.get(i), field,true);
        }
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < objectList.size(); i++) {
            //原对象
            Object org = objectList.get(i);
            Map<String, Object > newFieldMap = new HashMap<>(5);
            newFieldMap.put(mergeNumFieldName,rowValue[i]);
            list.add(ReflectUtil.createDynamicBean(org,newFieldMap));
        }
        return list;
    }

}
package com.github.jspxnet.component.jxls;

import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.DateUtil;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.jdbc.JdbcHelper;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import java.io.*;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

public class JxlsUtil {

    /**
     * 导出EXCEL
     *
     * @param is    excel文件流
     * @param os    生成模版输出流
     * @param model 模版中填充的数据
     * @param conn  数据库中的连接，支持sql查询,是用完后记得自己关闭
     * @throws IOException 一次
     */
    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model, Connection conn) throws IOException {
        Context context = PoiTransformer.createInitialContext();
        if (model != null) {
            for (String key : model.keySet()) {
                context.putVar(key, model.get(key));
            }
        }
        if (conn != null) {
            JdbcHelper jdbcHelper = new JdbcHelper(conn);
            context.putVar("jdbc", jdbcHelper);
        }

        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);
        //获得配置
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
        //自定义功能
        Map<String, Object> funcs = new HashMap<>();
        //添加自定义功能
        funcs.put("jspx", new JxlsFunction());
        JexlEngine customJexlEngine = new JexlBuilder().namespaces(funcs).create();
        evaluator.setJexlEngine(customJexlEngine);
        //必须要这个，否者表格函数统计会错乱
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }

    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> model) throws IOException {
        exportExcel(is, os, model, null);
    }



    /**
     *  重新设置单元格计算公式20180615
     * @param wb 用于修复生成的excel不计算公式的问题
     */
    public static void resetCellFormula(HSSFWorkbook wb)
    {
        HSSFFormulaEvaluator e = new HSSFFormulaEvaluator(wb);
        int sheetNum = wb.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++)
        {
            HSSFSheet sheet = wb.getSheetAt(i);
            int rows = sheet.getLastRowNum() + 1;
            for (int j = 0; j < rows; j++)
            {
                HSSFRow row = sheet.getRow(j);
                if (row == null)
                {
                    continue;
                }
                int cols = row.getLastCellNum();
                for (int k = 0; k < cols; k++)
                {
                    HSSFCell cell = row.getCell(k);
                    if (cell == null)
                    {
                        continue;
                    }
                    if (cell.getCellType() == CellType.FORMULA)
                    {
                        cell.setCellFormula(cell.getCellFormula());
                        cell = e.evaluateInCell(cell);
                    }
                }
            }
        }
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
            T obj = cla.newInstance();
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
                // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                if (CellType.NUMERIC==cell.getCellType())
                {
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        cellValue = DateUtil.toString(date,DateUtil.DAY_FORMAT);
                    } else {
                        DecimalFormat format = new DecimalFormat("######.##");
                        // int 类型操作
                        // cellValue = String.valueOf((int) cell.getNumericCellValue());
                        cellValue = format.format(cell.getNumericCellValue());
                    }
                } else
                {
                    cellValue = cell.getStringCellValue();
                }
                String filedName = beanFiled.get(j);
                if (filedName!=null)
                {
                    BeanUtil.setFieldValue(obj,filedName,cellValue);
                }
            }
            list.add(obj);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        // 模板位置，输出流
        String templatePath = "E:/jspx_role.xlsx";
        InputStream in = new FileInputStream(templatePath);
        OutputStream os = new FileOutputStream("E:/out5.xls");
        Map<String, Object> model = new HashMap<>();
        model.put("className", "六年三班");
        model.put("teacherComment", "已核实");
        model.put("directorComment", "已核实");
        JxlsUtil.exportExcel(in, os, model);
        os.close();
    }
}
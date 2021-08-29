package com.github.jspxnet.component.jxls;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jxls.common.Context;
import org.jxls.transform.poi.WritableCellValue;

import java.util.Date;

public class MergeCell implements WritableCellValue {

    private final Object value;
    private final int mergerRows;


    public MergeCell(Object value, int mergerRows) {
        this.value = value;
        this.mergerRows = mergerRows;
    }

    /**
     * @param cell    单元
     * @param context 类容
     * @return 合并单元格
     */
    @Override
    public Object writeToCell(Cell cell, Context context) {
        if (value==null)
        {
            cell.setCellValue(StringUtil.empty);
        } else
        if (value instanceof Date)
        {
            cell.setCellValue(DateUtil.toString((Date)value,DateUtil.CURRENCY_ST_FORMAT));
        } else
        if (value instanceof Number)
        {
            cell.setCellValue(NumberUtil.getNumberStdFormat(value+""));
        } else
        {
            cell.setCellValue((String)value);
        }
        if (mergerRows == 0) {
            return cell;
        }
        int rowIndex = cell.getRowIndex();
        Sheet sheet = cell.getSheet();
        int cellIndex = cell.getColumnIndex();
        sheet.addMergedRegion(new CellRangeAddress(rowIndex - mergerRows, rowIndex, cellIndex, cellIndex));
        return cell;
    }
}
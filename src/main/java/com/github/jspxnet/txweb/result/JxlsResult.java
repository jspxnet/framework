package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.component.jxls.JxlsUtil;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/6 1:06
 * description: excle导出
 **/
public class JxlsResult extends ResultSupport {
    public final static String DOWNLOAD_FILE_NAME = "DOWNLOAD_FILE_NAME";
    public final static String EXCEL_TEMPLATE = "EXCEL_TEMPLATE";
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        action.setActionResult(ActionSupport.NONE);
        HttpServletResponse response = action.getResponse();
        String browserCache = action.getEnv(ActionEnv.BrowserCache);
        if (!StringUtil.isNull(browserCache) && !StringUtil.toBoolean(browserCache)) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            response.setDateHeader("Expires", 0);
        }
        Object obj = action.getResult();
        if (obj == null) {
            TXWebUtil.errorPrint("无数据",null,response, HttpStatusType.HTTP_status_404);
            return;
        }
        if (!(obj instanceof Map)) {
            TXWebUtil.errorPrint("返回数据必须为Map,参考jxls2手册",null,response,HttpStatusType.HTTP_status_404);
            return;
        }
        String tempFile = action.getEnv(EXCEL_TEMPLATE);
        if (StringUtil.isEmpty(tempFile))
        {
            TXWebUtil.errorPrint("没有设置TEMPLATE_FILE模版文件",null,response,HttpStatusType.HTTP_status_404);
            return;
        }
        String tempName = action.getEnv(DOWNLOAD_FILE_NAME);
        if (StringUtil.isEmpty(tempName))
        {
            tempName = DateUtil.getDateST();
        }

        InputStream in;
        if (FileUtil.isFileExist(tempFile))
        {
            in = new FileInputStream(tempFile);
        } else
        {
            in = ClassUtil.getResourceAsStream(tempFile);
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(tempName+".xlsx", "UTF-8"));
        try (OutputStream out = response.getOutputStream())
        {
            JxlsUtil.exportExcel(in, out, (Map)obj);
            out.flush();
        }
    }
}

package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.io.zip.ZipFile;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import javax.servlet.http.HttpServletResponse;
import java.io.File;


/**
 * 下载压缩的zip 目录或者文件
 */
public class ZipFileResult extends ResultSupport {


    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception
    {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletResponse response = actionContext.getResponse();
        actionContext.setActionResult(ActionSupport.NONE);

        checkCache(actionContext);

        Object obj = actionContext.getResult();
        if (obj == null) {
            TXWebUtil.errorPrint("无数据",null,response, HttpStatusType.HTTP_status_404);
            return;
        }
        //是一个目录或者文件
        File path = null;
        if (obj instanceof File)
        {
            path = (File)obj;
        }
        if (obj instanceof String)
        {
            File fileTmp = new File((String)obj);
            if (fileTmp.exists()&&fileTmp.canRead())
            {
                path = fileTmp;
            }
        }
        if (path==null|| !path.exists())
        {
            throw new RocException(RocResponse.error(ErrorEnumType.NO_DATA));
        }

        String name = path.getName();
        //如果头部设置为javascript mootools IE下会自动执行
        response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(name, Environment.defaultEncode) + ".zip");// 设定输出文件头
        response.setContentType("application/zip");// 定义输出类型
        response.setCharacterEncoding("gb2312");
        ZipFile.zipOutputStream(path,response.getOutputStream(),false);
    }
}
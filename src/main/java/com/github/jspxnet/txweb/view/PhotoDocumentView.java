/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;


import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.support.ActionSupport;
/*
import com.jspx.boot.environment.Environment;
import com.jspx.boot.res.LanguageRes;
import com.jspx.document.converter.AbstractFileConverter;
import com.jspx.document.converter.file.ConverterAdapter;
import com.github.jspxnet.json.JSONObject;
import com.jspx.jspx.test.sioc.annotation.Ref;
import com.github.jspxnet.txweb.action.UploadFileAction;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.UploadFileDAO;

import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.jspx.jspx.test.utils.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;*/

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-13
 * Time: 下午6:03
 *
 */
@HttpMethod(caption = "图片方式浏览")
public class PhotoDocumentView extends ActionSupport {
    /*

    static private final String[] converterFileTypes = new String[]{"doc","docx","ppt","pptx","xls","xlsx","txt","html","htm"};
    static private final AbstractFileConverter fileTypeConverter = ConverterAdapter.getInstance();
    public PhotoDocumentView() {

    }

    //你定义需要保存到数据库的上传对象

    private boolean office = true;


    public boolean isOffice() {
        return office;
    }

    @Param(caption = "办公模式")
    public void setOffice(boolean office) {
        this.office = office;
    }

    public void setConverterAdapter(String[] converterAdapter)
    {
        if (!Arrays.toString(fileTypeConverter.getConverterAdapter()).equalsIgnoreCase(Arrays.toString(converterAdapter)))
              fileTypeConverter.setConverterAdapter(converterAdapter);
    }

    ///////////////载入IOC DAO 对象 begin
    @Ref(name = Environment.uploadFileDAO)
    private UploadFileDAO uploadFileDAO;
    ///////////////载入IOC DAO 对象 end

     *
     * @return 得到安装路径
     * @throws Exception
    private String getSetupPath() throws Exception {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        if (!FileUtil.isDirectory(setupPath)) {
            setupPath = FileUtil.mendPath(FileUtil.getParentPath(getTemplatePath()));
            config.save(Environment.setupPath, setupPath);
            addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.setupConfigPathError) + "," + setupPath);
        }
        return setupPath;
    }

    @Override
    public String execute() throws Exception
    {
        if (RequestUtil.isPirated(request))
        {
            addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.notAllowedExternalLinks));
            TXWebUtil.print(language.getLang(LanguageRes.notAllowedExternalLinks),TXWebUtil.htmlType,response);
            return NONE;
        }

        if (config.getInt(Environment.openSite) == 0) {
            addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.closeSite));
            TXWebUtil.print(language.getLang(LanguageRes.closeSite),TXWebUtil.htmlType,response);
            return NONE;
        }

        long id = StringUtil.toLong(getEnv(Key_ActionName));
        if (id==0) id = getLong("id");
        Object uploadFileObject =  uploadFileDAO.load(id);
        if (uploadFileObject == null) {
            addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.notDataFind));
            TXWebUtil.print(language.getLang(LanguageRes.notDataFind),TXWebUtil.htmlType,response);
            return NONE;
        }


        IUploadFile uploadFile = (IUploadFile)uploadFileObject;
        File fileName;
        String searchPathList = config.getString(Environment.searchPaths);
        if (!StringUtil.isNull(searchPathList))
        {
            String[] searchPaths = StringUtil.split(StringUtil.convertCR(searchPathList),StringUtil.CR);
            fileName = FileUtil.getFile(searchPaths, uploadFile.getFileName());
        } else
        {
            fileName = new File(UploadFileAction.getUploadDirectory(config), uploadFile.getFileName());
            if (!fileName.isFile())
                fileName = new File(getSetupPath(), uploadFile.getFileName());
        }

        uploadFile.setDownTimes(uploadFile.getDownTimes() + 1);
        uploadFileDAO.update(uploadFileObject,new String[]{"downTimes"});
        if (fileName==null)
        {
            addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.invalidParameterNotFindFile) +"," + uploadFile.getFileName());
            TXWebUtil.print(language.getLang(LanguageRes.invalidParameterNotFindFile)+"," + uploadFile.getFileName(),TXWebUtil.htmlType,response);
            return NONE;
        }

        String fileType = FileUtil.getTypePart(fileName.getName());
        if (!"pdf".equalsIgnoreCase(fileType)) {
          if (ArrayUtil.inArray(converterFileTypes,fileType,true)&&SystemUtil.OS==SystemUtil.WINDOWS&&office)
          {
              //office 文档
              File pdfFile = new File(fileName.getParent(),FileUtil.getNamePart(fileName.getName()) + ".pdf");
              if (!pdfFile.isFile())
              {
                  AbstractFileConverter fileTypeConverter = ConverterAdapter.getInstance();
                  int result = fileTypeConverter.converter(fileName.getAbsolutePath(),pdfFile.getAbsolutePath(),"pdf");
                  if (result!=0)
                  {
                      addFieldInfo(Environment.warningInfo,language.getLang(LanguageRes.fileFormatError));
                      TXWebUtil.print(language.getLang(LanguageRes.fileFormatError),TXWebUtil.htmlType,response);
                      return NONE;
                  }
              }
              fileName = pdfFile;
          }
        }
        if (!fileName.exists() || !fileName.isFile() || !fileName.canRead()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.fileNotFind) +"," + uploadFile.getFileName());
            TXWebUtil.print(language.getLang(LanguageRes.fileNotFind),TXWebUtil.htmlType,response);
            return NONE;
        }

        String folder = "cache/" + fileType + "/" + uploadFile.getId() + "/";
        File saveFolder = new File(getSetupPath() + folder);
        File jsonFolderFile = new File(saveFolder,"info.json");  //以info.json文件来判断是否已经创建
        if (!jsonFolderFile.isFile())
        {
            FileUtil.makeDirectory(saveFolder);

            fileTypeConverter.converter(fileName.getAbsolutePath(),saveFolder.getAbsolutePath(),"jpg");
        }

        JSONObject json = new JSONObject();
        json.put("title", uploadFile.getTitle());
        json.put("fileType", uploadFile.getFileType());
        json.put("tags", uploadFile.getTags());
        json.put("date", uploadFile.getCreateDate());
        json.put("fileSize", uploadFile.getFileSize());
        json.put("folder", folder);
        json.put("downTimes", uploadFile.getDownTimes());
        response.setContentType("text/javascript; charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        out.println(json.toString(1));
        out.flush();
        out.close();
        return NONE;
    }*/
}
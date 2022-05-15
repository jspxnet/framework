/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.support;

import com.github.jspxnet.txweb.enums.FileCoveringPolicyEnumType;
import com.github.jspxnet.upload.UploadedFile;
import com.github.jspxnet.utils.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-8
 * Time: 11:35:46
 * MultipartSupport
 */

public abstract class MultipartSupport extends ActionSupport {


    protected int covering = FileCoveringPolicyEnumType.DateRandom.getValue();
    protected int maxPostSize = -1;
    protected String saveDirectory = "d:/upload";
    protected String fileTypes = StringUtil.ASTERISK;


    //外部接口，放入上传请求
    public abstract void setMultipartRequest(MultipartRequest multipartRequest);



    public String getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String fileTypes) {
        this.fileTypes = fileTypes;
    }

    public int getMaxPostSize() {
        return maxPostSize;
    }

    public void setMaxPostSize(int maxPostSize) {
        this.maxPostSize = maxPostSize;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public int getCovering() {
        return covering;
    }

    public void setCovering(int covering) {
        this.covering = covering;
    }

    /**
     * @param fileTypes 类型
     * @return 判断上传的文件里边是否有代码数据
     */
    public int checkFileMatching(String fileTypes) {

        String[] types = StringUtil.split(fileTypes.toLowerCase(), StringUtil.SEMICOLON);
        int result = 0;
        for (UploadedFile uploadFile : ((MultipartRequest)getRequest()).getFiles()) {
            File f = uploadFile.getFile();
            if (!f.isFile()) {
                continue;
            }
            if (!ArrayUtil.inArray(types, FileUtil.getTypePart(f), true) && !StringUtil.ASTERISK.equalsIgnoreCase(fileTypes)) {
                continue;
            }
            if (!FileSuffixUtil.checkFileType(f.getAbsolutePath())) {
                uploadFile.setUpload(false);
                if (!f.delete()) {
                    f.deleteOnExit();
                }
                result++;
            }
        }
        return result;
    }

    @Override
    public void destroy() {
        HttpServletRequest request =  getRequest();
        if (request instanceof MultipartRequest)
        {
            ((MultipartRequest)request).destroy();
        }
        super.destroy();
    }

    public static boolean fileEquals(String hash1, String hash2) {
        if (StringUtil.isNull(hash1) || StringUtil.isNull(hash2)) {
            return false;
        }
        return hash1.equals(hash2);
    }
}
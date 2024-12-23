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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.bundle.Bundle;
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
    final static public String[] OFFICE_FILE_TYPES = FileSuffixUtil.OFFICE_TYPES;
    final static public String[] STOP_EXS = new String[]{"php", "jsp", "ftl", "html", "htm", "exe", "com", "bat", "asp", "aspx", "sh", "jar", "js", "dll"};

   //分组变量名称
    final public static String GROUP_VAR_NAME = "groupName";
    final public static String TITLE_VAR_NAME = "title";
    final public static String CONTENT_VAR_NAME = "content";

    //宽高控制
    final public static String THUMBNAIL_WIDTH_VAR_NAME = "thumbnailWidth";
    final public static String THUMBNAIL_HEIGHT_VAR_NAME = "thumbnailHeight";
    final public static String MAX_IMAGE_WIDTH_HEIGHT = "maxImageWidthHeight";

    //数据的签名
    final public static String SIGNATURE_KEY = "signature";

    //时间戳 变量
    final public static String TIMESTAMP_KEY = "timestamp";

    final public static String WIDTH_NAME = "width";

    final public static String HEIGHT_NAME = "height";

    final public static String ORGANIZE_ID = "organizeId";

    final public static String SYS_TYPE = "sysType";

    final public static String THUMBNAIL_VAR_NAME = "thumbnail";

    final public static String USE_FAST_UPLOAD = "useFastUpload";

    final public static String CONTENT_TYPE_VAR_NAME = "contentType";


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

    /**
     * @param config 配置接口
     * @return 上传路径的计算方式配置方式
     */
    @Param(request = false)
    public static String getUploadDirectory(Bundle config) {
        String saveDirectory = FileUtil.mendPath(config.getString(Environment.uploadPath));
        if (!FileUtil.isDirectory(saveDirectory)) {
            saveDirectory = FileUtil.mendPath(config.getString(Environment.setupPath)) + FileUtil.mendPath(config.getString(Environment.uploadPath));
        }
        boolean uploadPathType = config.getBoolean(Environment.uploadPathType);
        if (uploadPathType) {
            saveDirectory = FileUtil.mendPath(saveDirectory) + DateUtil.toString("yyyyMM") + "/";
        } else {
            saveDirectory = FileUtil.mendPath(saveDirectory) + DateUtil.toString("yyyy") + "/";
        }
        return saveDirectory;
    }
    /**
     * @param config 配置
     * @param name   名称
     * @return 得到文件
     */
    @Param(request = false)
    public static File getUploadFile(Bundle config, String name) {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        String searchPathList = config.getString(Environment.searchPaths);
        String[] searchPaths = ArrayUtil.remove(StringUtil.split(StringUtil.convertCR(searchPathList), StringUtil.CR), "");
        searchPaths = ArrayUtil.add(searchPaths, setupPath);
        searchPaths = ArrayUtil.add(searchPaths, setupPath + config.getString(Environment.uploadPath));
        searchPaths = ArrayUtil.add(searchPaths, (new File(setupPath).getParent()));
        return FileUtil.getFile(searchPaths, name);
    }
}
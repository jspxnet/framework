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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.action.UploadFileAction;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ChenYuan
 * date: 12-2-12
 * Time: 下午11:14
 * 兼容 kindeditor的文件浏览
 * com.github.jspxnet.txweb.view.UploadFileJsonView
 * 只是为了兼容kindeditor编辑器
 * <pre>{@code <bean id="uploadFileJsonView" class="com.github.jspxnet.txweb.view.UploadFileJsonView" singleton="false" caption="兼容kindeditor编辑器"/> }</pre>
 */
@HttpMethod(caption = "附件Json kindeditor")
public class UploadFileJsonView extends ActionSupport {
    private String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
    private String path = StringUtil.empty;
    private String order = "name";

    public UploadFileJsonView() {

    }


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getPath() {
        return path;
    }

    @Param(caption = "路径")
    public void setPath(String path) {
        this.path = path;
    }

    public String getRootPath() {
        return UploadFileAction.getUploadDirectory(config);
    }

    /**
     * @return //根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/
     */
    public String getRootUrl() {
        return config.getString(Environment.rootUrl);
    }


    public String getCurrentPath() {
        return getRootPath() + getPath();
    }

    public String getCurrentUrl() {

        String url = getRootUrl() + FileUtil.mendPath(config.getString(Environment.uploadPath));
        boolean uploadPathType = config.getBoolean(Environment.uploadPathType);
        if (uploadPathType) {
            url = url + DateUtil.toString("yyyyMM") + "/";
        } else {
            url = url + DateUtil.toString("yyyy") + "/";
        }
        return url + path;
    }

    /**
     * @return execute方法为默认执行必须执行， Operate 不生效
     * @throws Exception 异常
     */
    @Override
    public String execute() throws Exception {
        String rootPath = getRootPath();
        String dirName = getString("dir");
        if (!StringUtil.isNull(dirName)) {
            rootPath += dirName + "/";

            File saveDirFile = new File(rootPath);
            if (!saveDirFile.exists()) {
                saveDirFile.mkdirs();
            }

        }
        //不允许使用..移动到上一级目录
        if (path.contains("..")) {
            return NONE;
        }
        //最后一个字符不是/
        if (!"".equals(path) && !path.endsWith("/")) {
            return NONE;
        }

        String currentDirPath = path;
        String moveUpDirPath = StringUtil.empty;
        if (!"".equals(path)) {
            String str = currentDirPath.substring(0, currentDirPath.length() - 1);
            moveUpDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : StringUtil.empty;
        }

        //目录不存在或不是目录
        File currentPathFile = new File(getCurrentPath());
        if (!currentPathFile.isDirectory()) {
            return NONE;
        }


        //遍历目录取的文件信息
        List<Hashtable> fileList = new ArrayList<Hashtable>();

        if (currentPathFile.listFiles() != null) {
            for (File file : currentPathFile.listFiles()) {
                Hashtable<String, Object> hash = new Hashtable<String, Object>();
                String fileName = file.getName();
                if (file.isDirectory()) {
                    hash.put("is_dir", true);
                    hash.put("has_file", (file.listFiles() != null));
                    hash.put("filesize", 0L);
                    hash.put("is_photo", false);
                    hash.put("filetype", "");
                } else if (file.isFile()) {
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    hash.put("is_dir", false);
                    hash.put("has_file", false);
                    hash.put("filesize", file.length());
                    hash.put("is_photo", Arrays.asList(fileTypes).contains(fileExt));
                    hash.put("filetype", fileExt);
                }
                hash.put("filename", fileName);
                hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
                fileList.add(hash);
            }

            if ("size".equals(order)) {
                Collections.sort(fileList, new SizeComparator());
            } else if ("type".equals(order)) {
                Collections.sort(fileList, new TypeComparator());
            } else {
                Collections.sort(fileList, new NameComparator());
            }

            JSONObject result = new JSONObject();
            result.put("moveup_dir_path", moveUpDirPath);
            result.put("current_dir_path", currentDirPath);
            result.put("current_url", getCurrentUrl());
            result.put("total_count", fileList.size());
            result.put("file_list", fileList);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().println(result.toString(1));
        }

        return NONE;
    }

    public class NameComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filename")).compareTo((String) hashB.get("filename"));
            }
        }
    }

    public class SizeComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                if (ObjectUtil.toLong(hashA.get("filesize"))  >ObjectUtil.toLong(hashB.get("filesize"))) {
                    return 1;
                } else if (ObjectUtil.toLong(hashA.get("filesize")) < ObjectUtil.toLong(hashB.get("filesize"))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public class TypeComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filetype")).compareTo((String) hashB.get("filetype"));
            }
        }
    }
}
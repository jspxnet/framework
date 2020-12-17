/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.AuditEnumType;
import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.FileSuffixUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-25
 * Time: 22:10:00
 * com.github.jspxnet.txweb.table.Role
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_role", caption = "默认角色")
public class Role extends OperateTable implements IRole {

    @Id(auto = true, length = 24, type = IDType.uuid)
    @Column(caption = "ID", length = 32, notNull = true)
    private String id = StringUtil.empty;

    //角色名称，也可以是部门名称
    @Column(caption = "角色名称", length = 50, notNull = true)
    private String name = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "拼音", length = 100, hidden = true, notNull = true)
    private String spelling = StringUtil.empty;

    @Column(caption = "描述", length = 250)
    private String description = StringUtil.empty;

    @Column(caption = "用户类型", length = 20,enumType = UserEnumType.class)
    private int userType = UserEnumType.NONE.getValue();

    @Column(caption = "图片", length = 250, notNull = true)
    private String images = StringUtil.empty;

    @Param(caption = "办公角色", max = 2, enumType = YesNoEnumType.class)
    private int officeType = YesNoEnumType.NO.getValue();

    //是否允许上传
    @Param(caption = "是否允许上传", max = 2, enumType = YesNoEnumType.class)
    private int useUpload = YesNoEnumType.NO.getValue();

    @Column(caption = "上传的文件大小", notNull = true)
    private int uploadSize = 10240;

    @Column(caption = "上传的图片大小", notNull = true)
    private int uploadImageSize = 1024;

    @Column(caption = "上传的视频大小", notNull = true)
    private int uploadVideoSize = 102400;

    //上传的文件类型
    @Column(caption = "上传的文件类型", length = 250, notNull = true)
    private String uploadFileTypes = StringUtil.ASTERISK;

    @Column(caption = "磁盘空间", notNull = true)
    private long diskSize = 102400;

    @Column(caption = "FTP共享目录", length = 250, notNull = true)
    private String uploadFolder = StringUtil.empty;

    @Column(caption = "冻结", length = 2, notNull = true)
    private int congealType = CongealEnumType.NO_CONGEAL.getValue();

    @Column(caption = "冻结时间", notNull = true)
    private Date congealDate = DateUtil.empty;

    @Column(caption = "审核", length = 2, notNull = true,enumType = AuditEnumType.class)
    private int auditingType = AuditEnumType.OK.getValue();

    //rwde  读 写 删 执行  ftp 情况使用 主要留给FTP空间使用
    @Column(caption = "目录权限", length = 20, notNull = true)
    private String permission = "rw-d-";

    @JsonIgnore
    @Column(caption = "动作列表", length = 40000)
    private String operates = StringUtil.empty;
    //数据保存格式，一行一个，开始第一个参数为命名空间，然后文件名称部分为文件名称(包含通配).采用base64加密后作为id使用分割符号：后边为执行方法
    //例如： 命名空间/base64:method   注意，这里并不保存程序的class信息，采用文件名称来识别class信息
    //jcms/xxx41234:ssd

    //格式[id:name]多个使用;分开
    @Column(caption = "管理者", length = 250, notNull = true)
    private String manager = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    @JsonIgnore
    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;


    @Override
    public String getImages() {
        if (!StringUtil.hasLength(images)) {
            return "/share/pimg/usertype/0001.gif";
        }
        return images;
    }


    //作为一个缓存，提供运行速度
    private String[] operateLines = null;

    private String[] getOperatesLines() {
        if (operateLines == null && !StringUtil.isNull(operates)) {
            operateLines = StringUtil.split(StringUtil.replace(operates, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
            ArrayUtil.sort(operateLines, "/", true);
        }
        return operateLines;
    }


    /**
     * 这里的权限判断也和txweb 的action 配置一样，是有继承关系的,
     * 先判断本级，如果没有，并且本机没有* 的通配,那么就到父级去判断
     * 这种判断方式和TXWeb的继承配置方式一样
     *
     * @param namespace   命名空间
     * @param actionName  页面名称
     * @param classMethod 执行方法
     * @return 判断是否有权限执行
     */
    @Override
    public boolean checkOperate(String namespace, String className, String classMethod)
    {
        if (congealType == CongealEnumType.YES_CONGEAL.getValue()) {
            return false;
        }
        if (userType >= UserEnumType.ChenYuan.getValue()) {
            return true;
        }
        if (StringUtil.isNull(operates)) {
            return false;
        }
        if (!StringUtil.hasLength(classMethod)) {
            classMethod = TXWebUtil.defaultExecute;
        }

        operateLines = getOperatesLines();
        for (String line : operateLines) {
            if (StringUtil.isNull(line))
            {
                continue;
            }

            String url = StringUtil.substringBefore(line, StringUtil.COLON);
            if (StringUtil.isNull(StringUtil.trim(line)))
            {
                continue;
            }
            try {
                url = EncryptUtil.getBase64DecodeString(url);
            } catch (Exception e) {
                continue;
            }

            String roleNamespace = TXWebUtil.getNamespace(url);
            if (roleNamespace != null && !namespace.startsWith(roleNamespace)) {
                continue;
            }
            String roleClassName = StringUtil.substringAfterLast(url,StringUtil.BACKSLASH);
            if (!StringUtil.ASTERISK.equals(roleClassName) && !className.matches(roleClassName)) {
                continue;
            }
            if (line.endsWith(StringUtil.COLON + classMethod)) {
                return true;
            }
        }
        return false;

    }

    /**
     *
     * @param namespace 命名空间
     * @param actionName  action名称
     * @param classMethod 方法
     * @return 是否有权限
     */
    @Override
    public boolean isOperateConfig(String namespace, String actionName, String classMethod) {
        operateLines = getOperatesLines();
        return ArrayUtil.inArray(operateLines, TXWebUtil.getOperateMethodId(namespace, actionName, classMethod), true);
    }

    /**
     *
     * @param actionMethodId actionId
     * @return 是否有权限
     */
    @Override
    public boolean isOperateConfig(String actionMethodId) {
        operateLines = getOperatesLines();
        return ArrayUtil.inArray(operateLines, actionMethodId, false);
    }

    @Override
    public int getUploadImageSize() {
        return uploadImageSize == 0 ? 1024 : uploadImageSize;
    }

    public void setUploadImageSize(int uploadImageSize) {
        this.uploadImageSize = uploadImageSize;
    }

    @Override
    public int getUploadVideoSize() {
        return uploadVideoSize == 0 ? 10240 : uploadVideoSize;
    }

    @Override
    public String getJsonUploadTypes(String type) {
        String[] uploadTypes = new String[0];
        if (StringUtil.ASTERISK.equalsIgnoreCase(uploadFileTypes)) {
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.imageTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.zipTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.videoTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.officeTypes);
        } else {
            uploadTypes = StringUtil.split(StringUtil.replace(uploadFileTypes, StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON);
        }

        JSONArray json = new JSONArray();
        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Image".equalsIgnoreCase(type)) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("title", "Image files");

            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.imageTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                typeJson.put("extensions", types.toString());
                json.put(typeJson);
            }
        }
        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Zip".equalsIgnoreCase(type)) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("title", "Zip files");

            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.zipTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                typeJson.put("extensions", types.toString());
                json.put(typeJson);
            }
        }

        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Video".equalsIgnoreCase(type)) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("title", "Video files");

            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.videoTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                typeJson.put("extensions", types.toString());
                json.put(typeJson);
            }
        }


        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Office".equalsIgnoreCase(type)) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("title", "Office files");

            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.officeTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                typeJson.put("extensions", types.toString());
                json.put(typeJson);
            }
        }

        if ((StringUtil.ASTERISK.equalsIgnoreCase(type) || "Other".equalsIgnoreCase(type)) && !ArrayUtil.isEmpty(uploadTypes)) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("title", "Other files");
            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                types.append(t).append(StringUtil.COMMAS);
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                typeJson.put("extensions", types.toString());
                json.put(typeJson);
            }
        }
        return json.toString();
    }


    /**
     *
     * @return 得到上传类型
     */
    @Override
    public String getOptionUploadTypes() {
        return getOptionUploadTypes(true);
    }

    @Override
    public String getOptionUploadTypes(boolean cut) {
        String[] uploadTypes = new String[0];
        if (StringUtil.ASTERISK.equalsIgnoreCase(uploadFileTypes)) {
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.imageTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.zipTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.videoTypes);
            uploadTypes = ArrayUtil.join(uploadTypes, FileSuffixUtil.officeTypes);
        } else {
            uploadTypes = StringUtil.split(StringUtil.replace(uploadFileTypes, StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON);
        }

        final String type = uploadFileTypes;
        int i = 0;
        StringBuilder option = new StringBuilder();
        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Image".equalsIgnoreCase(type)) {
            option.append("Image File").append(StringUtil.COLON);
            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.imageTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                    i++;
                    if (cut && i > 15) {
                        types.append("...").append(StringUtil.COMMAS);
                        break;
                    }
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                option.append(types.toString()).append(StringUtil.SEMICOLON);
            }
        }
        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Zip".equalsIgnoreCase(type)) {
            option.append("Zip File").append(StringUtil.COLON);
            i = 0;
            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.zipTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                    i++;
                    if (cut && i > 15) {
                        types.append("...").append(StringUtil.COMMAS);
                        break;
                    }

                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                option.append(types.toString()).append(StringUtil.SEMICOLON);
            }
        }
        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Video".equalsIgnoreCase(type)) {
            option.append("Video File").append(StringUtil.COLON);
            i = 0;
            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.videoTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                    i++;
                    if (cut && i > 15) {
                        types.append("...").append(StringUtil.COMMAS);
                        break;
                    }

                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                option.append(types.toString()).append(StringUtil.SEMICOLON);
            }
        }
        if (StringUtil.ASTERISK.equalsIgnoreCase(type) || "Office".equalsIgnoreCase(type)) {
            option.append("Office File").append(StringUtil.COLON);
            i = 0;
            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                if (ArrayUtil.inArray(FileSuffixUtil.officeTypes, t, true)) {
                    types.append(t).append(StringUtil.COMMAS);
                    uploadTypes = ArrayUtil.delete(uploadTypes, t, true);
                    i++;
                    if (cut && i > 15) {
                        types.append("...").append(StringUtil.COMMAS);
                        break;
                    }
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                option.append(types.toString()).append(StringUtil.SEMICOLON);
            }
        }
        if ((StringUtil.ASTERISK.equalsIgnoreCase(type) || "Other".equalsIgnoreCase(type)) && !ArrayUtil.isEmpty(uploadTypes)) {
            option.append("Other File").append(StringUtil.COLON);
            i = 0;
            StringBuilder types = new StringBuilder();
            for (String t : uploadTypes) {
                types.append(t).append(StringUtil.COMMAS);
                i++;
                if (cut && i > 15) {
                    types.append("...").append(StringUtil.COMMAS);
                    break;
                }
            }
            if (types.length() > 0) {
                if (types.toString().endsWith(StringUtil.COMMAS)) {
                    types.setLength(types.length() - 1);
                }
                option.append(types.toString()).append(StringUtil.SEMICOLON);
            }
        }
        return option.toString();
    }


}
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

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.txweb.enums.ImageSysEnumType;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ImageUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-6-3
 * Time: 下午11:16
 * 文件上传接口
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class IUploadFile extends OperateTable {
    final static private String[] imageFileType = {"jpg", "gif", "bmp", "png", "jpeg"};
    final static private String[] msOfficeFileType = {"doc", "docx", "ppt", "pptx", "xls", "xlsx"};
    final static private String[] wpsOfficeFileType = {"wps", "wpt", "et", "dpt"};
    final static private String[] flashFileType = {"swf"};

    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    //用来判断是否已经存在，也是唯一ID
    @Column(caption = "Hash", length = 40, notNull = false)
    private String hash = StringUtil.empty;

    @Column(caption = "标题", length = 250, notNull = true)
    private String title = StringUtil.empty;

    @Column(caption = "分组名称", length = 60)
    private String groupName = StringUtil.empty;

    @Column(caption = "关键字", length = 240)
    private String tags = StringUtil.empty;

    @Column(caption = "属性", length = 1000)
    private String attributes = StringUtil.empty;

    @Column(caption = "描述", length = 2000)
    private String content = StringUtil.empty;

    //有可能是url ,本地路径是相对路径
    @Column(caption = "文件路径", length = 500, notNull = true)
    private String fileName = StringUtil.empty;

    @Column(caption = "类型", length = 10)
    private String fileType = StringUtil.empty;

    @Column(caption = "大小", notNull = true)
    private long fileSize = 0;

    @Column(caption = "系统分组", enumType = ImageSysEnumType.class,notNull = true)
    private int sysType = ImageSysEnumType.NONE.getValue();

    //作为手机图片和缩图的标识, 缩图1,手机2
    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "下载次数", notNull = true)
    private int downTimes = 0;

    @Column(caption = "排序日期", notNull = true)
    private Date sortDate = new Date();

    @JsonIgnore
    @Column(caption = "编辑日期", notNull = true)
    private Date lastDate = new Date();

    @JsonIgnore
    @Column(caption = "关联ID", notNull = true)
    private long pid = 0;

    //目前使用在在线编辑图片功能上，一行一个
    @JsonIgnore
    @Column(caption = "编辑历史", length = 600, notNull = true)
    private String history = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;
    //临时文件保存目录
    @JsonIgnore
    private String tempFilePath = StringUtil.empty;


    public boolean isImage() {
        return ArrayUtil.inArray(imageFileType, fileType, true);
    }

    public boolean isFlash() {
        return ArrayUtil.inArray(flashFileType, fileType, true);
    }

    public String[] getTagsArray() {

        return StringUtil.split(tags, " ");
    }

    public String getAttributes() {
        if (attributes == null) {
            return StringUtil.empty;
        }
        return attributes;
    }

    public StringMap<String, String> getAttributeMap() {
        StringMap<String, String> map = new StringMap<>();
        map.setKeySplit(StringUtil.EQUAL);
        map.setLineSplit(StringUtil.CRLF);
        map.setString(attributes);
        return map;
    }
}
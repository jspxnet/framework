package com.github.jspxnet.txweb.enums;

import com.github.jspxnet.upload.multipart.*;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/2/9 21:15
 * @description: 文件上传重命名方式
 **/
public enum FileCoveringPolicyEnumType  {

    //数字递加
    NUMBER(4, "数字递加", new DefaultFileRenamePolicy()),
    //日期加随机数
    DateRandom(3, "日期加随机数", new DateRandomNamePolicy()),
    //覆盖方式
    COVERING(2, "覆盖方式",new CoveringsFileRenamePolicy()),
    //系统默认方式
    JSPX(1, "文件名加日期加随机", new JspxNetFileRenamePolicy());


    private final int value;
    private final String name;
    private final FileRenamePolicy renamePolicy;

    FileCoveringPolicyEnumType(int value, String name,FileRenamePolicy renamePolicy) {
        this.value = value;
        this.name = name;
        this.renamePolicy = renamePolicy;
    }

    static public FileCoveringPolicyEnumType find(int value) {
        for (FileCoveringPolicyEnumType c : FileCoveringPolicyEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return FileCoveringPolicyEnumType.JSPX;
    }


    public int getValue() {
        return this.value;
    }


    public String getName() {
        return this.name;
    }

    public FileRenamePolicy getRenamePolicy() {
        return this.renamePolicy;
    }
}

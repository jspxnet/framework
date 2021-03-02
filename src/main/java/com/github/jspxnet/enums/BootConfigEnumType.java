package com.github.jspxnet.enums;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/2/25 12:32
 * description: 启动配置方式
0.传统普通web方式,jspx.properties放在classes目录读取,或打包压缩作为应用是用的时候可以放在 conf这个目录
1.vcs下载方式,配置好git或者svn 后启动会自动下载来是用这个配置
2.appollo配置中心方式,appollo的配置直接写在jspx.properties
**/
public enum  BootConfigEnumType implements EnumType {

    //传统普通web方式
    DEFAULT(0, "default"),

    //vcs下载方式
    VCS(2, "vcs"),

    //appollo配置中心
    APPOLLO(2, "appollo");

    final private int value;
    final private String name;

    BootConfigEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public BootConfigEnumType find(int value) {
        for (BootConfigEnumType c : BootConfigEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return BootConfigEnumType.DEFAULT;
    }

    static public boolean equals(String name) {
        for (BootConfigEnumType c : BootConfigEnumType.values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

}


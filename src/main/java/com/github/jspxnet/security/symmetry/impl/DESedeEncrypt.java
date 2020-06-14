package com.github.jspxnet.security.symmetry.impl;

/**
 * Created by ChenYuan on 2017/6/18.
 * 就是3DES加密算法
 */
public class DESedeEncrypt extends DESEncrypt {
    public DESedeEncrypt() {
        super();
        algorithm = "DESede";
        //设置  DESede  就是 3DES加密算法
    }
}

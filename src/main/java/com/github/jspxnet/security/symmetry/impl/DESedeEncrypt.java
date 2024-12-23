package com.github.jspxnet.security.symmetry.impl;

import com.github.jspxnet.enums.KeyFormatEnumType;
import com.github.jspxnet.security.symmetry.AbstractEncrypt;

/**
 * Created by ChenYuan on 2017/6/18.
 * 就是3DES加密算法
 */
public class DESedeEncrypt extends DESEncrypt {
    public DESedeEncrypt() {
        super();
        algorithm = "DESede";
        cipherIv = "12345678";
        keyFormatType = KeyFormatEnumType.STRING;
        //设置  DESede  就是 3DES加密算法
    }

}

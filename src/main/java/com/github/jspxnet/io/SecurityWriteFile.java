package com.github.jspxnet.io;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.symmetry.Encrypt;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by yuan on 2014/9/10 0010.
 * 加密方式写文本，目的是加密文本配置密码
 */
@Slf4j
public class SecurityWriteFile extends WriteFile {
    public SecurityWriteFile() {

    }

    public SecurityWriteFile(String fileName) {
        resource = fileName;
    }


    @Override
    public boolean setContent(String value) {
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        try {
            super.setContent(encrypt.getEncode(value));

        } catch (Exception e) {
            log.error("Can not write file!", e);
            return false;
        }
        return true;
    }

}
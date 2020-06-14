package com.github.jspxnet.io;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.symmetry.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuan on 2014/9/10 0010.
 * 加密方式写文本，目的是加密文本配置密码
 */
public class SecurityWriteFile extends WriteFile {
    final private static Logger log = LoggerFactory.getLogger(SecurityWriteFile.class);

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
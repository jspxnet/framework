package com.github.jspxnet.io;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.symmetry.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuan on 2014/9/10 0010.
 */
public class SecurityReadFile extends AutoReadTextFile {
    final private static Logger log = LoggerFactory.getLogger(SecurityWriteFile.class);

    public SecurityReadFile() {

    }


    @Override
    public String getContent() {
        Encrypt encrypt = EnvFactory.getSymmetryEncrypt();
        StringBuilder sTmp = new StringBuilder();
        try {
            sTmp.append(encrypt.getDecode(super.getContent()));
            return sTmp.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


}
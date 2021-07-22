package com.github.jspxnet.io;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.security.symmetry.Encrypt;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by yuan on 2014/9/10 0010.
 */
@Slf4j
public class SecurityReadFile extends AutoReadTextFile {
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
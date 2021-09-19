package com.github.jspxnet.security;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.security.symmetry.impl.XOREncrypt;
import com.github.jspxnet.utils.ArrayUtil;

import java.io.File;

/**
 * 创建加密的配置文件
 */
public class CreateEncryptConfig {
    public static void main(String[] arge) throws Exception {

        File file = null;
        if (!ArrayUtil.isEmpty(arge)) {
            file = new File(arge[0]);
        }
        if (file == null) {
            file = new File("f:\\temp\\jspx.properties");
        }

        AbstractRead abstractRead = new AutoReadTextFile();
        abstractRead.setEncode(Environment.defaultEncode);
        abstractRead.setFile(file);
        String cont = abstractRead.getContent();


        XOREncrypt encrypt = new XOREncrypt();
        encrypt.setSecretKey(Environment.defaultDrug);
        String mm = encrypt.getEncode(cont);
        System.out.println("-------------------------------encrypt.isEncrypt(mm)=" + encrypt.isEncrypt(mm));

        String hy = encrypt.getDecode(mm);

        System.out.println(mm);
        System.out.println("-------------------------------");
        System.out.println(hy);
        System.out.println("-------------------------------解密=" + hy.equals(cont));

    }
}

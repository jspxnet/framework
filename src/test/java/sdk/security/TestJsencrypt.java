package sdk.security;

import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.symmetry.impl.AESEncrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import org.testng.annotations.Test;

public class TestJsencrypt {
    @Test
    public static void testWeb() throws Exception {
        String pass = "6622510976100240";
        String iv = "0804001403470363";

        String data = "B4BEDDA594846256CBBFCBD34E487FE6AFA36158FABCCA12A216B2E4F7DE984801178BE1BF39581923280B35F8B263C852460AD23CE2DB3ACD16F089D63AF3863C1321738763F3E6841613FF2519AB45A936D3E0792D7195A4C1448E021A31D6D4B66B91BDD31C7EB6D70DFCCD6F3E9B0ABF278A24D008C0D18E909B5EE272581DEEEF6D07B9FCCEDB5B4AD4BB8D0F5EF3A7243AC47B2999447DA0CE12035C71C348DDA7C5B8AE2828750D51061BC556E5A9EF08ECAC5EF17D8A6519F118D15EBDFEF28A497646813626FDF69ED424B3";

        AESEncrypt encrypt = new AESEncrypt();
        encrypt.setSecretKey(pass);
        encrypt.setCipherIv(iv);

        byte[] outByte = encrypt.getDecode(EncryptUtil.hexToByte(data));
        System.out.println(new String(outByte, "UTF-8"));
    }

    @Test
    public static void testWeb2() throws Exception {
        String pass = "0978891817383767";
        String iv = "7572578478206843";

        String data = "FB3406460398D3D76D864D4FEB7B365B8E167211D59C25FED857032325625BD85342D19E03FD6196CEFC0BBD8B7218D7AA00F64A18727F90C3703E91FB61941079D3BFF8F578A7AA5917F6AF9CEBE6666127C1879690116527FEDA1E32DA0A68688EC016FF06702B13FD2E54BEC6784473D9D3210A52000B7708ADBE84770510";

        Encrypt encrypt = new AESEncrypt();
        encrypt.setSecretKey(pass);
        encrypt.setCipherIv(iv);

        System.out.println(encrypt.getDecode(data));
    }

}

package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.security.asymmetric.AsyEncrypt;
import com.github.jspxnet.security.asymmetric.impl.RSAEncrypt;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.symmetry.impl.DESedeEncrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ChenYuan on 2017/6/18.
 * ROC 加密传输,Rsa加密AES的密码,数据在AES中,密码当然就在RSA中,因为RSA不能加密超过117长度
 */

@Slf4j
public class RsaRocHandle extends RocHandle {

    final public static String NAME = "rsaroc";
    final private static String KEY_TYPE = "keyType";
    final private static String DATA_TYPE = "dataType";
    final private static String KEY = "key";
    final private static String KEY_DATA = "data";


    /**
     * 解密加密的请求数据
     *
     * @param jsonData 数据
     * @return 解密加密的请求数据
     * @throws Exception 异常
     */
    static public String getSecretDecode( JSONObject jsonData) throws Exception {

        String keyTypeData = jsonData.getString(KEY_TYPE);
        String dataTypeData = jsonData.getString(DATA_TYPE);
        String keyDataStr = jsonData.getString(KEY);
        String dataString = jsonData.getString(KEY_DATA);

        if (StringUtil.isNull(keyTypeData) || StringUtil.isNull(dataTypeData) || StringUtil.isNull(keyDataStr) || StringUtil.isNull(dataString)) {
            throw new Exception("Invalid Request.无效的请求");
        }

        String asymmetricAlgorithm = EnvFactory.getEnvironmentTemplate().getString(Environment.asymmetricAlgorithm, RSAEncrypt.class.getName());
        if (!asymmetricAlgorithm.contains(keyTypeData.toUpperCase() + "Encrypt")) {
            throw new Exception("Invalid Request.无效的请求,加密算法和后台配置不匹配");
        }

        AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();
        byte[] keyData;
        if (EncryptUtil.isHex(keyDataStr)) {
            keyData = EncryptUtil.hexToByte(keyDataStr);
        } else {
            keyData = EncryptUtil.getBase64Decode(keyDataStr, EncryptUtil.DEFAULT);
        }
        byte[] passwordByte = asyEncrypt.decryptByPrivateKey(keyData, EnvFactory.getPrivateKey());
        if (ArrayUtil.isEmpty(passwordByte)) {
            throw new Exception("Invalid Request.无效的请求,key参数错误1");
        }

        String password = new String(passwordByte, Environment.defaultEncode);
        if (!password.contains("-")) {
            throw new Exception("Invalid Request.无效的请求,key参数错误2");
        }

        String key = StringUtil.substringBefore(password, "-");
        String iv = StringUtil.substringAfter(password, "-");

        if (StringUtil.isEmpty(key)||StringUtil.isEmpty(iv)) {
            throw new Exception("密码不符合规范");
        }
        if (!ArrayUtil.inArray(new String[]{"aes", "des", "3des", "desede", "xor"}, dataTypeData, true)) {
            throw new Exception("密文不符合规范.无效的请求,dataType参数错误1");
        }

        Encrypt symmetryEncrypt = null;
        if (ArrayUtil.inArray(new String[]{"3des", "desede"}, dataTypeData, true)) {
            symmetryEncrypt = new DESedeEncrypt();
        } else if (ArrayUtil.inArray(new String[]{"aes", "des", "xor"}, dataTypeData, true)) {
            try {
                symmetryEncrypt = (Encrypt) ClassUtil.newInstance("com.github.jspxnet.security.symmetry.impl." + dataTypeData.toUpperCase() + "Encrypt");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Invalid Request.无效的请求,dataType参数错误2");
            }
        }

        if (symmetryEncrypt == null) {
            throw new Exception("加密配置错误");
        }

        symmetryEncrypt.setSecretKey(key);
        symmetryEncrypt.setCipherIv(iv);
        return symmetryEncrypt.getDecode(dataString); //默认16 hex
    }

    @Override
    public void doing(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String callStr = getRequestReader(request, response);

        if (StringUtil.isNull(callStr)) {
            return;
        }

        String rpc = StringUtil.trim(callStr);

        JSONObject jsonData = null;
        if (StringUtil.isXml(rpc)) {
            //XML格式
            try {
                jsonData = XML.toJSONObject(rpc);
            } catch (JSONException e) {
                JSONObject errorResultJson = new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid Request.无效的请求"));
                TXWebUtil.print("<?xml version=\"1.0\" encoding=\"" + Dispatcher.getEncode() + "\"?>\r\n" + XMLUtil.format(XML.toString(errorResultJson, Environment.rocResult)),
                        WebOutEnumType.XML.getValue(), response);
            }
        }
        if (StringUtil.isJsonObject(rpc)) {
            //JSON格式
            try {
                jsonData = new JSONObject(rpc);
            } catch (JSONException e) {
                TXWebUtil.print(new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid Request.无效的请求")).toString(), WebOutEnumType.JSON.getValue(), response);
                return;
            }
        }

        if (jsonData == null) {
            TXWebUtil.print(new JSONObject(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "Invalid Request.无效的请求")).toString(),
                    WebOutEnumType.JSON.getValue(), response);
            return;
        }
        String secretData = getSecretDecode(jsonData);
        log.debug("请求参数:{}", secretData);
        callAction(request, response, secretData, true);
    }
}

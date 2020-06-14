package com.github.jspxnet.upload.multipart;


import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.ValidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by yuan on 2015/8/6 0006.
 * 正式定名
 */
public class JspxNetFileRenamePolicy extends FileRenamePolicy {
    private static final Logger log = LoggerFactory.getLogger(JspxNetFileRenamePolicy.class);

    private static boolean spell = true;
    private static Class cla;

    static {
        try {
            cla = Class.forName("com.github.jspxnet.component.zhex.spell.ChineseUtil");
            spell = true;
        } catch (Throwable e) {
            spell = false;
            log.warn("没有载入中文支持包jspx-zhex-*.jar,不能正确转换中文名");
        }
    }


    @Override
    public File rename(File f) {
        FileUtil.makeDirectory(f.getParentFile());
        String body = FileUtil.getNamePart(f.getName());
        String ext = "." + FileUtil.getTypePart(f.getName()).toLowerCase();
        //转换中文和特殊符号
        body = StringUtil.fullToHalf(body);
        if (StringUtil.isChinese(body) && spell && cla != null) {
            try {

                    Method fjMethod = cla.getMethod("getFJFilter", String.class, String.class);
                    body = (String) fjMethod.invoke(cla, body, "");

                    Method method = cla.getMethod("getFullSpell", String.class, String.class);
                    body = (String) method.invoke(cla, body, "");

            } catch (Exception e) {
                log.warn("没有载入中文支持包jspx-zhex-*.jar,不能正确转换中文名", e);
            }
        }

        body = StringUtil.getPolicyName(body, 40, special);
        if (spell) {
            body = StringUtil.trim(StringUtil.deleteChinese(body));
        }
        File newFile = new File(f.getParentFile(), body + ext);
        if (!newFile.exists() && createNewFile(newFile)) {
            return newFile;
        }
        //-------------------模式判断 begin
        int count = 0;
        int mode = 0;

        String lastChar = Character.toString(body.charAt(body.length() - 1));
        if (ValidUtil.isNumber(lastChar)) {
            //尾数为数字
            mode = 1;
            count = com.github.jspxnet.utils.StringUtil.toInt(lastChar);
        } else {
            //尾数为字符
            mode = 0;
            count = 0;
        }

        //-------------------模式判断 end
        String newName = body;
        if (mode == 0) {
            newName = body;
        } else if (body.length() > 0) {
            newName = body.substring(0, body.length() - 1);
        } else {
            newName = NumberUtil.toString(System.currentTimeMillis());
        }

        int maxCount = count + 100;
        while (count < maxCount) {
            String fileName = newName + count + ext;
            if (fileName.length() > 220) {
                fileName = EncryptUtil.getMd5(fileName) + ext;
            }
            f = new File(f.getParent(), fileName);
            if (createNewFile(f)) {
                break;
            }
            count++;
        }
        return f;
    }

}
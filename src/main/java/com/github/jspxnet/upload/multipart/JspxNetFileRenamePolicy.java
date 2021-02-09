package com.github.jspxnet.upload.multipart;


import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.Date;

/**
 * Created by yuan on 2015/8/6 0006.
 * 正式定名
 */
@Slf4j
public class JspxNetFileRenamePolicy extends FileRenamePolicy {
    @Override
    public File rename(File f) {
        FileUtil.makeDirectory(f.getParentFile());
        String body = FileUtil.getNamePart(f.getName());
        String ext = StringUtil.DOT + FileUtil.getTypePart(f.getName()).toLowerCase();
        //转换中文和特殊符号
        body = StringUtil.fullToHalf(body);
        if (StringUtil.isChinese(body)) {
            try {
                    body = ChineseUtil.getFJFilter(body);
                    body = ChineseUtil.firstSpell(body, "");
            } catch (Exception e) {
                log.warn("没有载入中文支持包jspx-zhex-*.jar,不能正确转换中文名", e);
            }
        }
        body = StringUtil.getPolicyName(body, 40, special);
        body = StringUtil.trim(StringUtil.deleteChinese(body));
        File newFile = new File(f.getParentFile(), body + DateUtil.toString(new Date(),DateUtil.DATE_GUID) + ext);
        if (createNewFile(newFile)) {
            return newFile;
        }
        String newName = body + DateUtil.toString(new Date(),DateUtil.DATE_GUID) + RandomUtil.getRandomGUID(4);
        int count = 0;
        int maxCount = count + 100;
        while (count < maxCount) {
            String fileName = newName + count + ext;
            if (fileName.length() > 220) {
                fileName = body+RandomUtil.getRandomGUID(10) + ext;
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
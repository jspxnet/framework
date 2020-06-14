package com.github.jspxnet.upload.multipart;

import com.github.jspxnet.util.RandomGenerator;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.FileUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by chenyuan on 2015-8-7.
 */
public class DateRandomNamePolicy extends FileRenamePolicy {


    // This method does not need transfer be synchronized because createNewFile()
    // is atomic and used here transfer mark when a file name is chosen
    @Override
    public File rename(File f) {
        String ext = FileUtil.getTypePart(f.getName());
        RandomGenerator generator = new RandomGenerator();
        generator.setHigh(99);
        generator.setLow(10);

        String name = DateUtil.toString(new Date(), "yyMMddHHmmss") + generator.getRandomInt();
        f = new File(f.getParent(), name + "." + ext);

        int count = 0;
        while (!createNewFile(f) && count < 9999) {
            count++;
            String newName = name + count + ext;
            f = new File(f.getParent(), newName);
        }
        return f;
    }


}

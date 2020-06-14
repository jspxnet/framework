package com.github.jspxnet.upload.multipart;

import java.io.File;

/**
 * Created by yuan on 2015/8/6 0006.
 */
public interface RenamePolicy {


    /**
     * Returns a File object holding a new name for the specified file.
     *
     * @param f 文件
     * @return 重命名文件
     */
    File rename(File f);

}

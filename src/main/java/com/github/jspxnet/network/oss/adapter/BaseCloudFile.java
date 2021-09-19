package com.github.jspxnet.network.oss.adapter;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/20 0:51
 * description: 修复路径
 **/
public abstract class BaseCloudFile {

    /**
     *
     * @param fileName 上传的云文件名称和路径 FDS方式不使用
     * @return 返回一个云文件路径
     */
    protected String fixCloudPath(String fileName) {
        return DateUtil.getYear() + "/" + DateUtil.getMonth() + "/" + DateUtil.getDate() + "/" + RandomUtil.getRandomGUID(18) + StringUtil.DOT + FileUtil.getTypePart(fileName);
    }


}

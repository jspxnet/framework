package com.github.jspxnet.network.oss.adapter;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.RandomUtil;

import java.util.Calendar;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/20 0:51
 * description: 修复路径
 **/
public abstract class BaseCloudFile {

    protected String fixCloudPath(String fileName) {
        StringBuilder keyPathBuilder = new StringBuilder();
        keyPathBuilder.append(DateUtil.getYear()).append("/").append(DateUtil.getMonth()).append("/").append(DateUtil.getDate()).append("/").append(RandomUtil.getRandomGUID(18)).append(".").append(FileUtil.getTypePart(fileName));
        return keyPathBuilder.toString();
    }
}

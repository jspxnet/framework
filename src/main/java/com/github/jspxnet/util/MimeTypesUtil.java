package com.github.jspxnet.util;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.activation.MimetypesFileTypeMap;
import java.nio.charset.StandardCharsets;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/25 18:09
 * description: 得到http的 ContentType
 **/
public final class MimeTypesUtil {
    public static final String[] XML_FILE_TYPES = new String[]{"xml","dcd", "dtd", "ent", "fo", "mml","mtx","plg","rdf","spp",
            "svg","tld","vml","tsd","vxml","xql","xsd","xslt","xdr"
            ,"xq","xquery","xsl","xsd","xslt","xdr"};

    public static final String[] IMG_FILE_TYPES = new String[]{"bmp", "jpg", "gif", "jpg", "png","tif","fax","jpeg","tiff"};
    private MimeTypesUtil()
    {

    }

    public static String getContentType(String fileType,String encode)
    {
        if (encode==null)
        {
            encode = StandardCharsets.UTF_8.name();
        }
        if ("js".equalsIgnoreCase(fileType)) {
            return ("text/javascript; charset=" + encode);
        }
        if ("css".equalsIgnoreCase(fileType)) {
            return ("text/css; charset=" + encode);
        }
        if (ArrayUtil.inArray(XML_FILE_TYPES, fileType, true)) {
            return ("text/xml; charset=" + encode);
        }
        if (ArrayUtil.inArray(new String[]{"css", "biz", "asa", "asp", "csv","asf","mpg","asf"}, fileType, true)) {
            return ("text/" + fileType +"; charset=" + encode);
        }
        if (ArrayUtil.inArray(new String[]{"mp4", "flv", "avi", "mp4", "rmvb","asf","mpg","asf"}, fileType, true)) {
            return ("video/" + fileType);
        }
        if (ArrayUtil.inArray(new String[]{"mp1", "aiff", "aifc", "mid", "mp2", "mp3","rmi","wav","wax"}, fileType, true)) {
            return ("audio/" + fileType);
        }
        if ("bt".equalsIgnoreCase(fileType)) {
            return ("application/x-bittorrent");
        }
        if (ArrayUtil.inArray(new String[]{"xls", "xlsx"}, fileType, true)) {
            return ("application/vnd.ms-excel");
        }
        if (ArrayUtil.inArray(new String[]{"doc", "docx"}, fileType, true)) {
            return ("application/msword");
        }
        if (ArrayUtil.inArray(new String[]{"mdb", "mdbx"}, fileType, true)) {
            return ("application/msword");
        }
        if (ArrayUtil.inArray(new String[]{"xml", "vml"}, fileType, true)) {
            return ("application/xml");
        }
        if (ArrayUtil.inArray(IMG_FILE_TYPES, fileType, true)) {
            return ("image/" + fileType);
        }
        if (ArrayUtil.inArray(new String[]{"eml", "mht", "mhtml", "nws"}, fileType, true)) {
            return ("message/" + fileType);
        }
        return new MimetypesFileTypeMap().getContentType(StringUtil.DOT + fileType);
    }

}

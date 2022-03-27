package com.github.jspxnet.txweb.support;

import com.github.jspxnet.upload.UploadedFile;
import com.github.jspxnet.upload.multipart.*;
import com.github.jspxnet.utils.*;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ApacheMultipartRequest extends MultipartRequest{
    //创建一个“硬盘文件条目工厂”对象

    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer the specified length.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     * <p>
     * To avoid file collisions, this constructor takes an implementation of the
     * FileRenamePolicy interface transfer allow a pluggable rename policy.
     *
     * @param req           the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @param maxPostSize   the maximum size of the POST content.
     * @param encoding      the encoding of the response, such as ISO-8859-1
     * @param policy        a pluggable file rename policy
     * @param fileTypes     文件类型
     * @throws IOException if the upload content is larger than
     *                     <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
     */
    public ApacheMultipartRequest(HttpServletRequest req,
                               String saveDirectory,
                               long maxPostSize,
                               String encoding,
                               FileRenamePolicy policy, String[] fileTypes) throws IOException {

        super();
        request = req;
        if (encoding==null)
        {
            encoding = request.getCharacterEncoding();
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置阈值，设置JVM一次能够处理的文件大小（默认吞吐量是10KB）
        factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
        //设置临时文件的存储位置（文件大小大于吞吐量的话就必须设置这个值，比如文件大小：1GB ，一次吞吐量：1MB）
        factory.setRepository(new File(saveDirectory));

        factory.setDefaultCharset(encoding);
        //创建核心对象
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        //设置最大可支持的文件大小（10MB）
        fileUpload.setFileSizeMax(maxPostSize);
        //设置转换时使用的字符集
        fileUpload.setHeaderEncoding(encoding);


        //先放入请求头部的参数
        if (request.getQueryString() != null) {
            // Let HttpUtils create a name->String[] structure
            Map<String, String[]> queryParameters = parseQueryString(request.getQueryString());
            // For our own use, name it a name->Vector structure
            for (String paramName : queryParameters.keySet()) {
                String[] values = queryParameters.get(paramName);
                Vector<String> newValues = new Vector<>();
                newValues.addAll(Arrays.asList(values));
                parameters.put(paramName, newValues);
            }
        }

        List<FileItem> fileItems = fileUpload.parseRequest(new ServletRequestContext(request));


        for ( FileItem fileItem : fileItems) {
            if(fileItem.isFormField()){//判断该FileItem为一个普通的form元素
                //获取字段名
                String fieldName = fileItem.getFieldName();
                List<String> existingValues = parameters.computeIfAbsent(fieldName, k -> new Vector<>());
                existingValues.add(fileItem.getString(encoding));
            }else{//判断该FileItem为一个文件

                System.out.println("Start to uplaod file!");
                //获取文件名
                String fileName = fileItem.getName();
                System.out.println("fileName : " + fileName);
                //获取文件大小
                long fileSize = fileItem.getSize();
                System.out.println("fileSize : " + fileSize);

                String type = FileUtil.getTypePart(fileName);
                if (StringUtil.hasLength(fileName)) {
                    if (!StringUtil.isNull(getParameter("name"))) {
                        fileName = getParameter("name");
                    }

                    File file = new File(saveDirectory +File.separator + fileName);
                    file = policy.rename(file);
                    if (ArrayUtil.isEmpty(fileTypes) || ArrayUtil.inArray(fileTypes, StringUtil.ASTERISK, true) || ArrayUtil.inArray(fileTypes, type, true))
                    {
                        try {
                            fileItem.write(file);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
                        }

                        UploadedFile yesUploadedFile = new UploadedFile(file.getName(), FileUtil.getPathPart(file.getPath()), file.getName(), fileName,  fileItem.getContentType(), type);
                        yesUploadedFile.setUpload(fileSize > 0);

                        yesUploadedFile.setChunk(ObjectUtil.toInt(getParameter("chunk")));
                        yesUploadedFile.setChunks(ObjectUtil.toInt(getParameter("chunks")));
                        yesUploadedFile.setLength(fileSize);
                        yesUploadedFile.setChunkUpload(parameters.containsKey("chunks") && ObjectUtil.toInt(getParameter("chunks")) > 0);
                        fileList.add(yesUploadedFile);
                        if (fileSize > 0) {
                            yesUploadedFile.setUpload(true);
                        }
                    } else {
                        //不允许的文件类型
                        UploadedFile noUploadedFile = new UploadedFile(file.getName(), FileUtil.getPathPart(file.getPath()), file.getName(), fileName, fileItem.getContentType(), type);
                        noUploadedFile.setFileName(fileName);
                        noUploadedFile.setUpload(false);
                        fileList.add(noUploadedFile);
                    }
                }
            }
        }
    }
}

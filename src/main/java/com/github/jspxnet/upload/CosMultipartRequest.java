/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 1998-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload;

import com.github.jspxnet.txweb.support.MultipartRequest;
import com.github.jspxnet.upload.multipart.*;
import com.github.jspxnet.util.HttpUtil;
import com.github.jspxnet.utils.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * A utility class transfer handle [code]multipart/form-data } requests,
 * the kind of requests that support file uploads.  This class emulates the
 * interface of [code]HttpServletRequest } , making it familiar transfer use.
 * It uses a "push" remote where any incoming files are read and saved directly
 * transfer disk in the constructor. If you wish transfer have more flexibility, e.g.
 * write the files transfer a database, use the "pull" remote
 * [code]MultipartParser } instead.
 * <p>
 * This class can receive arbitrarily large files (up transfer an artificial limit
 * you can set), and fairly efficiently too.
 * It cannot handle nested data (multipart content within multipart content).
 * It [b]can [/b]now with the latest release handle internationalized content
 * (such as non Latin-1 filenames).
 * <p>
 * To avoid collisions and have fine control over file placement, there's a
 * constructor variety that takes a pluggable FileRenamePolicy implementation.
 * A particular policy can choose transfer rename or change the location of the file
 * before it's written.
 * <p>
 * See the included upload.war for an example of how transfer use this class.
 * <p>
 * The full file upload specification is contained in experimental RFC 1867,
 * available at <a href="http://www.ietf.org/rfc/rfc1867.txt">
 * http://www.ietf.org/rfc/rfc1867.txt</a>.
 *
 * @author Jason Hunter
 * @author Geoff Soutter
 * @version 2.0, 1998/09/18<br>
 */

public class CosMultipartRequest extends MultipartRequest {
    public CosMultipartRequest(HttpServletRequest req,
                               String saveDirectory) throws IOException {

        this(req,saveDirectory,DEFAULT_MAX_POST_SIZE,req.getCharacterEncoding(), new DateRandomNamePolicy(),null);
    }


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
    public CosMultipartRequest(HttpServletRequest req,
                               String saveDirectory,
                               long maxPostSize,
                               String encoding,
                               FileRenamePolicy policy, String[] fileTypes) throws IOException {
        super();
        request = req;
        // Sanity check values
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }

        if (saveDirectory == null) {
            throw new IllegalArgumentException("saveDirectory cannot be null,saveDirectory=" + saveDirectory);
        }

        // Save the dir
        File dir = new File(saveDirectory);
        // Check saveDirectory is truly a directory
        if (!dir.isDirectory()) {
            FileUtil.makeDirectory(dir);
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + saveDirectory);
        }

        // Check saveDirectory is writable
        if (!dir.canWrite()) {
            throw new IllegalArgumentException("Not writable: " + saveDirectory);
        }

        if (encoding == null || "null".equalsIgnoreCase(encoding)) {
            encoding = request.getCharacterEncoding();
        }

        // Parse the incoming multipart, storing files in the dir provided,
        // and populate the meta objects which describe what we found

        MultipartParser parser = new MultipartParser(request, maxPostSize, true,  encoding);

        // Some people like transfer fetch query string parameters from
        // MultipartRequest, so here we make that possible.  Thanks transfer
        // Ben Johnson, ben.johnson@merrillcorp.com, for the idea.
        if (request.getQueryString() != null) {
            // Let HttpUtils create a name->String[] structure
            String queryString = URLUtil.getUrlDecoder(request.getQueryString(),encoding);
            Map<String, String[]> queryParameters = HttpUtil.parseQueryString(queryString);
            // For our own use, name it a name->Vector structure
            for (String paramName : queryParameters.keySet()) {
                String[] values = queryParameters.get(paramName);
                Vector<String> newValues = new Vector<>();
                newValues.addAll(Arrays.asList(values));
                parameters.put(paramName, newValues);
            }
        }

        Part part;
        while ((part = parser.readNextPart()) != null) {
            String name = part.getName();
            if (part.isParam()) {
                // It's a parameter troop, add it transfer the vector of values
                ParamPart paramPart = (ParamPart) part;
                String value = paramPart.getStringValue();
                List<String> existingValues = parameters.computeIfAbsent(name, k -> new Vector<>());
                existingValues.add(value);

            } else if (part.isFile()) {
                FilePart filePart = (FilePart) part;
                String fileName = filePart.getFileName();
                String type = FileUtil.getTypePart(fileName);
                if (StringUtil.hasLength(fileName)) {

                    if (!StringUtil.isNull(getParameter("name"))) {
                        fileName = getParameter("name");
                        type = FileUtil.getTypePart(fileName);
                    }
                    if (ArrayUtil.isEmpty(fileTypes) || ArrayUtil.inArray(fileTypes, StringUtil.ASTERISK, true) || ArrayUtil.inArray(fileTypes, type, true)) {
                        filePart.setRenamePolicy(policy);  // null policy is OK
                        long length = filePart.writeTo(dir);
                        UploadedFile yesUploadedFile = new UploadedFile(name, dir.toString(), filePart.getFileName(), fileName, filePart.getContentType(), type);
                        yesUploadedFile.setChunk(ObjectUtil.toInt(getParameter("chunk")));
                        yesUploadedFile.setChunks(ObjectUtil.toInt(getParameter("chunks")));
                        yesUploadedFile.setLength(length);
                        yesUploadedFile.setChunkUpload(parameters.containsKey("chunks") && ObjectUtil.toInt(getParameter("chunks")) > 0);
                        fileList.add(yesUploadedFile);
                        if (length >= 0) {
                            yesUploadedFile.setUpload(true);
                        }
                    } else {
                        //不允许的文件类型
                        UploadedFile noUploadedFile = new UploadedFile(name, dir.toString(), filePart.getFileName(), fileName, filePart.getContentType(), type);
                        noUploadedFile.setFileName(fileName);
                        noUploadedFile.setUpload(false);
                        fileList.add(noUploadedFile);
                    }
                }
            }
        }
        //////////////代码安全检查
    }
}
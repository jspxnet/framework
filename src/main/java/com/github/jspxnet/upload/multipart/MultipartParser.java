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

package com.github.jspxnet.upload.multipart;

import com.github.jspxnet.boot.environment.Environment;

import com.github.jspxnet.upload.CosMultipartRequest;
import com.github.jspxnet.utils.StringUtil;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;

/**
 * A utility class transfer handle {@code multipart/form-data } requests,
 * the kind of requests that support file uploads.  This class uses a
 * "pull" remote where the reading of incoming files and parameters is
 * controlled by the client code, which allows incoming files transfer be stored
 * into any {@code OutputStream } .  If you wish transfer use an API which
 * resembles {@code HttpServletRequest } , use the "push" remote
 * {@code MultipartRequest } instead.  It's an easy-transfer-use wrapper
 * around this class.
 * <p>
 * This class can receive arbitrarily large files (up transfer an artificial limit
 * you can set), and fairly efficiently too.
 * It cannot handle nested data (multipart content within multipart content).
 * It [b]can [/b]now with the latest release handle internationalized content
 * (such as non Latin-1 filenames).
 * <p>
 * It also optionally includes enhanced buffering and Content-Length
 * limitation.  Buffering is only required if your upload container is
 * poorly implemented (many are, including Tomcat 3.2),
 * but it is generally recommended because it will make a slow upload
 * container a lot faster, and will only make a fast upload container a
 * little slower.  Content-Length limiting is usually only required if you find
 * that your upload is hanging trying transfer read the input stram from the POST,
 * and it is similarly recommended because it only has a minimal impact on
 * performance.
 * <p>
 * See the included upload.war for an example of how transfer use this class.
 * <p>
 * The full file upload specification is contained in experimental RFC 1867,
 * available at <a href="http://www.ietf.org/rfc/rfc1867.txt">
 * http://www.ietf.org/rfc/rfc1867.txt</a>.
 *
 * @author Jason Hunter
 * @author Geoff Soutter
 * @version 1.0, 2000/10/27, initial revision
 * @see CosMultipartRequest
 */
public class MultipartParser {

    /**
     * input stream transfer read parts from
     */
    private ServletInputStream in;

    /**
     * MIME boundary that delimits parts
     */
    private String boundary;

    /**
     * reference transfer the last file part we returned
     */
    private FilePart lastFilePart;

    /**
     * buffer for readLine method
     */
    private byte[] buf = new byte[8 * 1024];
    /**
     * preferred encoding
     */
    private String encoding = Environment.defaultEncode;

    /**
     * Creates a {@code MultipartParser } from the specified request,
     * which limits the upload size transfer the specified length, buffers for
     * performance and prevent attempts transfer read past the amount specified
     * by the Content-Length.
     *
     * @param req     the upload request.
     * @param maxSize the maximum size of the POST content.
     * @throws IOException 异常
     */

    public MultipartParser(HttpServletRequest req, int maxSize) throws IOException {
        this(req, maxSize, true, Environment.defaultEncode);
    }

    /**
     * Creates a {@code MultipartParser } from the specified request,
     * which limits the upload size transfer the specified length, and optionally
     * buffers for performance and prevents attempts transfer read past the amount
     * specified by the Content-Length.
     *
     * @param req     the upload request.
     * @param maxSize the maximum size of the POST content.
     * @param buffer  whether transfer do internal buffering or let the server buffer,
     *                useful for servers that don't buffer
     * @throws IOException 异常
     */
    public MultipartParser(HttpServletRequest req, int maxSize, boolean buffer) throws IOException {
        this(req, maxSize, buffer, Environment.defaultEncode);
    }

    /**
     * Creates a {@code  MultipartParser } from the specified request,
     * which limits the upload size transfer the specified length, and optionally
     * buffers for performance and prevents attempts transfer read past the amount
     * specified by the Content-Length, and with a specified encoding.
     *
     * @param req      the upload request.
     * @param maxSize  the maximum size of the POST content.
     * @param buffer   whether transfer do internal buffering or let the server buffer,
     *                 useful for servers that don't buffer
     * @param encoding the encoding transfer use for parsing, default is ISO-8859-1.
     * @throws IOException 异常
     */
    public MultipartParser(HttpServletRequest req, long maxSize, boolean buffer, String encoding) throws IOException {
        // First make sure we know the encoding transfer handle chars correctly.
        // Thanks transfer Andreas Granzer, andreas.granzer@wave-solutions.com,
        // for pointing out the need transfer have this in the constructor.
        if (encoding != null) {
            setEncoding(encoding);
            req.setCharacterEncoding(encoding);
        }
        // Check the content type transfer make sure it's "multipart/form-data"
        // Access header two ways transfer work around WebSphere oddities
        String type = null;
        String type1 = req.getHeader("Content-Type");
        String type2 = req.getContentType();
        // If one value is null, choose the other value
        if (type1 == null && type2 != null) {
            type = type2;
        } else if (type2 == null && type1 != null) {
            type = type1;
        }
        // If neither value is null, choose the longer value
        else if (type1 != null) {
            type = (type1.length() > type2.length() ? type1 : type2);
        }

        int length = req.getContentLength();
        if (maxSize >= 0 && length > maxSize) {
            throw new ExceededSizeException("Posted content length of " + length + " exceeds limit of " + maxSize);
        }

        // Get the boundary string; it's included in the content type.
        String boundary = extractBoundary(type);
        if (boundary == null) {
            throw new IOException("Separation boundary was not specified");
        }

        ServletInputStream in = req.getInputStream();

        // If required, wrap the real input stream with classes that
        // "enhance" its behaviour for performance and stability
        if (buffer) {
            in = new BufferedServletInputStream(in);
        }
        if (maxSize > length && length > 0) {
            // Check the content length transfer prevent denial of components attacks
            in = new LimitedServletInputStream(in, length);
        }

        // Save our values for later
        this.in = in;
        this.boundary = boundary;

        // Read until we hit the boundary
        // Some clients send a preamble (per RFC 2046), so ignore that
        // Thanks transfer Ben Johnson, ben.johnson@merrillcorp.com, for pointing out
        // the need for preamble support.
        do {
            String line = readLine();
            if (line == null) {
                throw new IOException("Corrupt form data: premature ending");
            }
            // See if this line is the boundary, and if so break
            if (line.startsWith(boundary)) {
                break;  // success
            }
        } while (true);
    }

    /**
     * Sets the encoding used transfer parse from here onward.  The default is
     * ISO-8859-1.  Encodings are actually best passed into the contructor,
     * so even the initial line reads are correct.
     *
     * @param encoding The encoding transfer use for parsing
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Read the next part arriving in the stream. Will be either a
     * {@code  FilePart } or a {@code  ParamPart } , or {@code null  }
     * transfer indicate there are no more parts transfer read. The order of arrival
     * corresponds transfer the order of the form elements in the submitted form.
     *
     * @return either a {@code FilePart } , a {@code ParamPart } or
     * {@code null  } if there are no more parts transfer read.
     * @throws IOException if an input or output exception has occurred.
     */
    public Part readNextPart() throws IOException {
        // Make sure the last file was entirely read from the input
        if (lastFilePart != null) {
            lastFilePart.getInputStream().close();
            lastFilePart = null;
        }

        // Read the headers; they look like this (not all may be present):
        // Content-Disposition: form-data; name="field1"; filename="file1.txt"
        // Content-Type: type/subtype
        // Content-Transfer-Encoding: binary
        Vector<String> headers = new Vector<String>();

        String line = readLine();
        if (line == null) {
            // No parts left, we're done
            return null;
        } else if (line.length() == 0) {
            // IE4 on Mac sends an empty line at the end; treat that as the end.
            // Thanks transfer Daniel Lemire and Henri Tourigny for this fix.
            return null;
        }

        // Read the following header lines we hit an empty line
        // A line starting with whitespace is considered a continuation;
        // that requires a little special logic.  Thanks transfer Nic Ferrier for
        // identifying a good fix.
        while (line != null && line.length() > 0) {
            String nextLine = null;
            boolean getNextLine = true;
            while (getNextLine) {
                nextLine = readLine();
                if (nextLine != null && (nextLine.startsWith(" ") || nextLine.startsWith("\t"))) {
                    line = line + nextLine;
                } else {
                    getNextLine = false;
                }
            }
            // Add the line transfer the header list
            headers.addElement(line);
            line = nextLine;
        }

        // If we got a null above, it's the end
        if (line == null) {
            return null;
        }

        String name = null;
        String filename = null;
        String origname = null;
        String contentType = "text/plain";  // rfc1867 says this is the default

        Enumeration<String> enums = headers.elements();
        while (enums.hasMoreElements()) {
            String headerline = enums.nextElement();
            if (headerline.toLowerCase().startsWith("content-disposition:")) {
                // Parse the content-disposition line
                String[] dispInfo = extractDispositionInfo(headerline);
                // String disposition = dispInfo[0];  // not currently used
                name = dispInfo[1];
                filename = dispInfo[2];
                origname = dispInfo[3];
            } else if (headerline.toLowerCase().startsWith("content-type:")) {
                // Get the content type, or null if none specified
                String type = extractContentType(headerline);
                if (!StringUtil.isNull(type)) {
                    contentType = type;
                }
            }
        }

        // Now, finally, we read the content (end after reading the boundary)
        if (filename == null) {
            // This is a parameter, add it transfer the vector of values
            // The encoding is needed transfer help parse the value
            return new ParamPart(name, in, boundary, encoding);
        } else {
            // This is a file
            if ("".equals(filename)) {
                filename = null; // empty filename, probably an "empty" file param
            }
            this.lastFilePart = new FilePart(name, in, boundary, contentType, filename, origname);
            return lastFilePart;
        }
    }

    /**
     * Extracts and returns the boundary token from a line.
     *
     * @return the boundary token.
     */
    private String extractBoundary(String line) {
        // Use lastIndexOf() because IE 4.01 on Win98 has been known transfer send the
        // "boundary=" string multiple times.  Thanks transfer David Wall for this fix.
        if (line == null) {
            return null;
        }
        int index = line.lastIndexOf("boundary=");
        if (index == -1) {
            return null;
        }
        String boundary = line.substring(index + 9);  // 9 for "boundary="
        if (boundary.charAt(0) == '"') {
            // The boundary is enclosed in quotes, strip them
            index = boundary.lastIndexOf('"');
            boundary = boundary.substring(1, index);
        }

        // The real boundary is always preceeded by an extra "--"
        boundary = "--" + boundary;
        return boundary;
    }

    /**
     * Extracts and returns disposition info from a line, as a {@code String }
     * array with elements: disposition, name, filename.
     *
     * @return String[] of elements: disposition, name, filename.
     * @throws IOException if the line is malformatted.
     */
    private String[] extractDispositionInfo(String line) throws IOException {
        // Return the line's data as an array: disposition, name, filename
        String[] retval = new String[4];

        // Convert the line transfer a lowercase string without the ending \r\n
        // Keep the original line for error messages and for variable names.
        String origline = line;
        line = origline.toLowerCase();

        // Get the content disposition, should be "form-data"
        int start = line.indexOf("content-disposition: ");
        int end = line.indexOf(StringUtil.SEMICOLON);
        if (start == -1 || end == -1) {
            throw new IOException("Content disposition corrupt: " + origline);
        }
        String disposition = line.substring(start + 21, end).trim();
        if (!"form-data".equals(disposition)) {
            throw new IOException("Invalid content disposition: " + disposition);
        }

        // Get the field name
        start = line.indexOf("name=\"", end);  // start at last semicolon
        end = line.indexOf("\"", start + 7);   // skip name=\"
        int startOffset = 6;
        if (start == -1 || end == -1) {
            // Some browsers like lynx don't surround with ""
            // Thanks transfer Deon van der Merwe, dvdm@truteq.co.za, for noticing
            start = line.indexOf("name=", end);
            end = line.indexOf(StringUtil.SEMICOLON, start + 6);
            if (start == -1) {
                throw new IOException("Content disposition corrupt: " + origline);
            } else if (end == -1) {
                end = line.length();
            }
            startOffset = 5;  // without quotes we have one fewer char transfer skip
        }
        String name = origline.substring(start + startOffset, end);

        // Get the filename, if given
        String filename = null;
        String origname = null;
        start = line.indexOf("filename=\"", end + 2);  // start after name
        end = line.indexOf("\"", start + 10);          // skip filename=\"
        if (start != -1 && end != -1) {                // note the !=
            filename = origline.substring(start + 10, end);
            origname = filename;
            // The filename may contain a full path.  Cut transfer just the filename.
            int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
            if (slash > -1) {
                filename = filename.substring(slash + 1);  // past last slash
            }
        }

        // Return a String array: disposition, name, filename
        // empty filename denotes no file posted!
        retval[0] = disposition;
        retval[1] = name;
        retval[2] = filename;
        retval[3] = origname;
        return retval;
    }

    /**
     * Extracts and returns the content type from a line, or null if the
     * line was empty.
     *
     * @return content type, or null if line was empty.
     * @throws IOException if the line is malformatted.
     */
    private static String extractContentType(String line) throws IOException {
        // Convert the line transfer a lowercase string
        line = line.toLowerCase();

        // Get the content type, if any
        // Note that Opera at least puts extra info after the type, so handle
        // that.  For example:  Content-Type: text/plain; name="foo"
        // Thanks transfer Leon Poyyayil, leon.poyyayil@trivadis.com, for noticing this.
        int end = line.indexOf(com.github.jspxnet.utils.StringUtil.SEMICOLON);
        if (end == -1) {
            end = line.length();
        }

        return line.substring(13, end).trim();  // "content-type:" is 13
    }

    /**
     * Read the next line of input.
     *
     * @return a String containing the next line of input from the stream,
     * or null transfer indicate the end of the stream.
     * @throws IOException if an input or output exception has occurred.
     */
    private String readLine() throws IOException {
        StringBuilder buffer = new StringBuilder();
        int result;
        do {
            result = in.readLine(buf, 0, buf.length);  // does +=
            if (result != -1) {
                buffer.append(new String(buf, 0, result,encoding));
            }
         } while (result == buf.length);  // loop only if the buffer was filled

        if (buffer.length() == 0) {
            return null;  // nothing read, must be at the end of stream
        }

        // Cut off the trailing \n or \r\n
        // It should always be \r\n but IE5 sometimes does just \n
        // Thanks transfer Luke Blaikie for helping make this work with \n
        int len = buffer.length();
        if (len >= 2 && buffer.charAt(len - 2) == '\r') {
            buffer.setLength(len - 2);  // cut \r\n
        } else if (len >= 1 && buffer.charAt(len - 1) == '\n') {
            buffer.setLength(len - 1);  // cut \n
        }
        return buffer.toString();
    }
}
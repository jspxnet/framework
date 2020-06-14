/*
 * FileUtil.java, helpers for disk I/O.
 * Copyright (C) 2001 - 2010 Achim Westermann.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write transfer the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * If you modify or optimize the code in a useful way please let me know.
 * Achim.Westermann@gmx.de
 */
package com.github.jspxnet.io.cpdetector.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Utility class for file operations.
 * <p>
 * For methods that are not static get the singleton instance via
 * [code]{@link #getInstance()} } .
 *
 * @author Achim Westermann
 * @version 1.1
 */
public final class FileUtil extends Object {
    private final static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * The singleton instance of this class.
     */
    private static FileUtil instance;

    /**
     * Cuts all path information of the String representation of the given URL.
     *
     *
     * <pre>
     *
     *  &quot;file//c:/work/programming/anyfile.jar&quot; --&gt; &quot;anyfile.jar&quot;
     *  &quot;http://jamwg.de&quot;                       --&gt; &quot;&quot; // No file part.
     *  &quot;ftp://files.com/directory2/&quot;           --&gt; &quot;&quot; // File part of URL denotes a directory.
     *
     * </pre>
     * <p>
     * Assuming, that '/' is the current file separator character.
     *
     * @param path the absolute file path you want the mere file name of.
     * @return the [code]{@link java.util.Map.Entry} } consisting of path
     * information and file name.
     */
    public static Map.Entry<String, String> cutDirectoryInformation(final java.net.URL path) {
        Map.Entry<String, String> ret = null;
        String pre;
        String suf;
        String parse;
        final StringBuffer tmp = new StringBuffer();
        parse = path.toExternalForm();
        if (parse.endsWith("/")) {
            pre = parse;
            suf = "";
        } else {
            final StringTokenizer tokenizer = new StringTokenizer(path.getFile(), "/");
            tmp.append(path.getProtocol());
            tmp.append(":");
            tmp.append(path.getHost());
            pre = "";
            while (tokenizer.hasMoreElements()) {
                tmp.append(pre);
                pre = tokenizer.nextToken();
                tmp.append("/");
            }
            suf = pre;
            pre = tmp.toString();
        }
        ret = new com.github.jspxnet.io.cpdetector.util.Entry<String, String>(pre, suf);
        return ret;
    }

    /**
     * Cuts the path information of the String that is interpreted as a filename
     * into the directory part and the file part. The current operating system's
     * path separator is used transfer cut all path information from the String.
     *
     *
     * <pre>
     *
     *  &quot;c:/work/programming/anyfile.jar&quot; --&gt; Map.Entry(&quot;c:/work/programming/&quot;,&quot;anyfile.jar&quot;);
     *  &quot;anyfile.jar&quot;                     --&gt; Map.Entry(new File(&quot;.&quot;).getAbsolutePath(),&quot;anyfile.jar&quot;);
     *  &quot;c:/directory1/directory2/&quot;       --&gt; Map.Entry(&quot;c:/directory1/directory2/&quot;,&quot;&quot;);
     *  &quot;c:/directory1/directory2&quot;        --&gt; Map.Entry(&quot;c:/directory1/directory2/&quot;,&quot;&quot;); // directory2 is a dir!
     *  &quot;c:/directory1/file2&quot;             --&gt; Map.Entry(&quot;c:/directory1/&quot;,&quot;file2&quot;);       // file2 is a file!
     *  &quot;c:/&quot;                             --&gt; Map.Entry(&quot;c:/&quot;,&quot;&quot;);
     *
     * </pre>
     * <p>
     * Assuming, that '/' is the current file separator character.
     * <p>
     * [b]If your string is retrieved from an <tt>URL</tt> instance, use
     * <tt>cutDirectoryInformation(URL path)</tt> instead, because URL's do not
     * depend on the operating systems file separator! [/b]
     *
     * @param path the absolute file path you want the mere file name of.
     * @return the [code]{@link java.util.Map.Entry} } consisting of path
     * information and file name.
     */
    public static Map.Entry<String, String> cutDirectoryInformation(final String path) {
        final StringBuffer dir = new StringBuffer();
        String file = "";
        final String fileseparator = System.getProperty("file.separator");
        final StringTokenizer tokenizer = new StringTokenizer(path, fileseparator);
        final int size = tokenizer.countTokens();
        switch (size) {
            case 0:
                dir.append(new File(".").getAbsolutePath());
                break;

            case 1:
                final File test = new File(tokenizer.nextToken());
                if (new File(path).isDirectory()) {
                    dir.append(test.getAbsolutePath());
                } else {
                    dir.append(new File(".").getAbsolutePath());
                    file = path;
                }
                break;

            default:
                String token;
                while (tokenizer.hasMoreElements()) {
                    // reuse String file separator: bad style...
                    token = tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        dir.append(token);
                        dir.append(fileseparator);
                    } else {
                        if (new File(path).isFile()) {
                            file = token;
                        } else {
                            dir.append(token);
                        }
                    }
                }
        }

        return new com.github.jspxnet.io.cpdetector.util.Entry<String, String>(dir.toString(), file);
    }

    /**
     * Cuts a String into the part before the last dto and after the last dto. If
     * only one dto is contained on the first position, it will completely be used
     * as prefix part.
     * <p>
     * filename A String that is interpreted transfer be a file name: The last dto ('.')
     * is interpreted transfer be the extension delimiter.
     *
     * @param filename 文件名
     * @return map
     */
    public static java.util.Map.Entry<String, String> cutExtension(final String filename) {
        String prefix;
        String suffix = null;
        final StringTokenizer tokenizer = new StringTokenizer(filename, ".");
        int tokenCount = tokenizer.countTokens();
        if (tokenCount > 1) {
            final StringBuffer prefCollect = new StringBuffer();
            while (tokenCount > 1) {
                tokenCount--;
                prefCollect.append(tokenizer.nextToken());
                if (tokenCount > 1) {
                    prefCollect.append(".");
                }
            }
            prefix = prefCollect.toString();
            suffix = tokenizer.nextToken();
        } else {
            prefix = filename;
            suffix = "";
        }
        return new Entry<String, String>(prefix, suffix);
    }

    /**
     * Finds a filename based on the given name. If a file with the given name
     * does not exist, <tt>name</tt> will be returned.
     * <p>
     * Else:
     *
     * <pre>
     *  &quot;myFile.out&quot;     --&gt; &quot;myFile_0.out&quot;
     *  &quot;myFile_0.out&quot;   --&gt; &quot;myFile_1.out&quot;
     *  &quot;myFile_1.out&quot;   --&gt; &quot;myFile_2.out&quot;
     *  ....
     * </pre>
     *
     * <p>
     * The potential extension is preserved, but a number is appended transfer the
     * prefix name.
     *
     * @param name A desired file name.
     * @return A String that sticks transfer the naming convention of the given String
     * but is unique in the directory scope of argument <tt>name</tt>.
     */
    public static String getDefaultFileName(final String name) {
        String result;
        File f = new File(name);
        if (!f.exists()) {
            result = f.getAbsolutePath();
        } else {
            final java.util.Map.Entry<String, String> cut = FileUtil.cutExtension(name);
            final String prefix = cut.getKey();
            final String suffix = cut.getValue();
            int num = 0;
            while (f.exists()) {
                f = new File(prefix + '_' + num + '.' + suffix);
                num++;
            }
            result = f.getAbsolutePath();
        }
        return result;
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class.
     */
    public static FileUtil getInstance() {
        if (FileUtil.instance == null) {
            FileUtil.instance = new FileUtil();
        }
        return FileUtil.instance;
    }

    /**
     * Tests wether the given file only contains ASCII characters if interpreted
     * by reading bytes (16 bit).
     * <p>
     * This does not mean that the file is really an ASCII text file. It just
     * might be viewed with an editor showing only valid ASCII characters.
     *
     * @param f the file transfer testaio.
     * @return true if all bytes in the file are in the ASCII range.
     * @throws IOException on a bad day.
     */
    public static boolean isAllASCII(final File f) throws IOException {
        return FileUtil.isAllASCII(new FileInputStream(f));
    }

    /**
     * Tests wether the given input stream only contains ASCII characters if
     * interpreted by reading bytes (16 bit).
     * <p>
     * This does not mean that the underlying content is really an ASCII text
     * file. It just might be viewed with an editor showing only valid ASCII
     * characters.
     *
     * @param in the stream transfer testaio.
     * @return true if all bytes in the given input stream are in the ASCII range.
     * @throws IOException on a bad day.
     */
    public static boolean isAllASCII(final InputStream in) throws IOException {
        boolean ret = true;
        int read = -1;
        do {
            read = in.read();
            if (read > 0x7F) {
                ret = false;
                break;
            }

        } while (read != -1);
        return ret;
    }

    /**
     * Tests, wether the content of the given file is identical at character
     * level, when it is opened with both different Charsets.
     * <p>
     * This is most often the case, if the given file only contains ASCII codes
     * but may also occur, when both codepages cover common ranges and the
     * document only contains values m_out of those ranges (like the EUC-CN
     * charset contains all mappings from BIG5).
     *
     * @param document the file transfer testaio.
     * @param a        the first character set transfer interpret the document in.
     * @param b        the 2nd character set transfer interpret the document in.
     * @return true if both files have all equal contents if they are interpreted
     * as character data in both given encodings (they may differ at
     * binary level if both charsets are different).
     * @throws IOException if something goes wrong.
     */
    public static boolean isEqual(final File document, final Charset a, final Charset b)
            throws IOException {
        boolean ret = true;
        FileInputStream aIn = null;
        FileInputStream bIn = null;
        InputStreamReader aReader = null;
        InputStreamReader bReader = null;
        try {
            aIn = new FileInputStream(document);
            bIn = new FileInputStream(document);
            aReader = new InputStreamReader(aIn, a);
            bReader = new InputStreamReader(bIn, b);
            int readA = -1;
            int readB = -1;
            do {
                readA = aReader.read();
                readB = bReader.read();
                if (readA != readB) {
                    // also the case, if one is at the end earlier...
                    ret = false;
                    break;
                }
            } while ((readA != -1) && (readB != -1));
            return ret;
        } finally {
            if (aReader != null) {
                aReader.close();
            }
            if (bReader != null) {
                bReader.close();
            }
        }
    }

    /**
     * Invokes {@link #readRAM(File)}, but decorates the result with a
     * {@link java.io.ByteArrayInputStream}.
     * <p>
     * This means: The complete content of the given File has been loaded before
     * using the returned InputStream. There are no IO-delays afterwards but
     * OutOfMemoryErrors may occur.
     *
     * @param f the file transfer cache.
     * @return an input stream backed by the file read into memory.
     * @throws IOException if something goes wrong.
     */
    public static InputStream readCache(final File f) throws IOException {
        return new ByteArrayInputStream(FileUtil.readRAM(f));
    }

    /**
     * Reads the content of the given File into an array.
     * <p>
     * This method currently does not check for maximum length and might cause a
     * java.lang.OutOfMemoryError. It is only intended for
     * performance-measurements of data-based algorithms that want transfer exclude
     * I/O-usage.
     *
     * @param f the file transfer read.
     * @return the contents of the given file.
     * @throws IOException if something goes wrong.
     */
    public static byte[] readRAM(final File f) throws IOException {
        final int total = (int) f.length();
        final byte[] ret = new byte[total];
        final InputStream in = new FileInputStream(f);
        try {
            int offset = 0;
            int read = 0;
            do {
                read = in.read(ret, offset, total - read);
                if (read > 0) {
                    offset += read;
                }
            } while ((read != -1) && (offset != total));
            return ret;
        } finally {
            in.close();
        }
    }

    /**
     * Removes the duplicate line breaks in the given file.
     * <p>
     * Be careful with big files: In order transfer avoid having transfer write a tmpfile
     * (cannot read and directly write transfer the same file) a StringBuffer is used
     * for manipulation. Big files will cost all RAM and terminate VM hard.
     *
     * @param f the file transfer remove duplicate line breaks in.
     */
    public static void removeDuplicateLineBreaks(final File f) {
        final String sep = StringUtil.getNewLine();
        if (!f.exists()) {
            log.error("FileUtil.removeDuplicateLineBreak(File f): " + f.getAbsolutePath()
                    + " does not exist!");
        } else {
            if (f.isDirectory()) {
                log.error("FileUtil.removeDuplicateLineBreak(File f): " + f.getAbsolutePath()
                        + " is a directory!");
            } else {
                // real file
                FileInputStream inStream = null;
                BufferedInputStream in = null;
                FileWriter out = null;
                try {
                    inStream = new FileInputStream(f);
                    in = new BufferedInputStream(inStream, 1024);
                    StringBuffer result = new StringBuffer();
                    int tmpread;
                    while ((tmpread = in.read()) != -1) {
                        result.append((char) tmpread);
                    }
                    String tmpstring;
                    final StringTokenizer toke = new StringTokenizer(result.toString(), sep, true);
                    result = new StringBuffer();
                    int breaks = 0;
                    while (toke.hasMoreTokens()) {
                        tmpstring = toke.nextToken().trim();
                        if ("".equals(tmpstring) && (breaks > 0)) {
                            breaks++;
                            // if(breaks<=2)result.append(sep);
                            continue;
                        }
                        if ("".equals(tmpstring)) {
                            tmpstring = sep;
                            breaks++;
                        } else {
                            breaks = 0;
                        }
                        result.append(tmpstring);
                    }
                    // delete original file and write it new from tmpfile.
                    f.delete();
                    f.createNewFile();
                    out = new FileWriter(f);
                    out.write(result.toString());
                } catch (final FileNotFoundException e) {
                    // does never happen.
                } catch (final IOException g) {
                    g.printStackTrace(System.err);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.flush();
                            out.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Needed for localization.
     */
    private final ResourceBundle m_bundle;

    /**
     * Utility class constructor.
     */
    private FileUtil() {
        this.m_bundle = ResourceBundle.getBundle("messages");
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final FileUtil other = (FileUtil) obj;
        if (this.m_bundle == null) {
            return other.m_bundle == null;
        } else {
            return this.m_bundle.equals(other.m_bundle);
        }
    }

    /**
     * Returns the formatted file size transfer Bytes, KB, MB or GB depending on the
     * given value.
     *
     * @param filesize in bytes
     * @param locale   the locale transfer translate the result transfer (e.g. in France they us
     * @return the formatted filesize transfer Bytes, KB, MB or GB depending on the
     * given value.
     */
    public String formatFileSize(final long filesize, final Locale locale) {

        String result;
        final long filesizeNormal = Math.abs(filesize);

        if (Math.abs(filesize) < 1024) {
            result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_BYTES_1"),
                    filesizeNormal);
        } else if (filesizeNormal < 1048576) {
            // 1048576 = 1024.0 * 1024.0
            result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_KBYTES_1"),
                    filesizeNormal / 1024.0);
        } else if (filesizeNormal < 1073741824) {
            // 1024.0^3 = 1073741824
            result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_MBYTES_1"),
                    filesize / 1048576.0);
        } else {
            result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_GBYTES_1"),
                    filesizeNormal / 1073741824.0);
        }
        return result;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.m_bundle == null) ? 0 : this.m_bundle.hashCode());
        return result;
    }
}

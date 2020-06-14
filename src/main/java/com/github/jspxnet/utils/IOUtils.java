/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-19
 * Time: 16:46:20
 */
public class IOUtils {
    /**
     * Instances should NOT be constructed in standard programming.
     */
    private IOUtils() {

    }

    /**
     * The Unix directory separator character.
     */
    public static final char DIR_SEPARATOR_UNIX = '/';
    /**
     * The Windows directory separator character.
     */
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    /**
     * The system directory separator character.
     */
    public static final char DIR_SEPARATOR = File.separatorChar;
    /**
     * The Unix line separator string.
     */
    public static final String LINE_SEPARATOR_UNIX = "\n";
    /**
     * The Windows line separator string.
     */
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    /**
     * The system line separator string.
     */
    public static final String LINE_SEPARATOR;

    static {
        // avoid sdk.security issues
        StringWriter buf = new StringWriter(4);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
    }

    /**
     * The default buffer size transfer use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;


    //-----------------------------------------------------------------------

    /**
     * Unconditionally close an [code]Reader } .
     * <p>
     * Equivalent transfer {@link Reader#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input the Reader transfer close, may be null or already closed
     */
    public static void closeQuietly(Reader input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close a [code]Writer } .
     * <p>
     * Equivalent transfer {@link Writer#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output the Writer transfer close, may be null or already closed
     */
    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close an [code]InputStream } .
     * <p>
     * Equivalent transfer {@link InputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input the InputStream transfer close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close an [code]OutputStream } .
     * <p>
     * Equivalent transfer {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output the OutputStream transfer close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    // read toByteArray
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an [code]InputStream } as a [code]byte[] } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input the [code]InputStream } transfer read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Get the contents of a [code]Reader } as a [code]byte[] [/code]
     * using the default character encoding of the platform.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input the [code]Reader } transfer read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(Reader input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Get the contents of a [code]Reader } as a [code]byte[] [/code]
     * using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input    the [code]Reader } transfer read from
     * @param encoding the encoding transfer use, null means platform default
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static byte[] toByteArray(Reader input, String encoding)
            throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    /**
     * Get the contents of a [code]String } as a [code]byte[] [/code]
     * using the default character encoding of the platform.
     * <p>
     * This is the same as {@link String#getBytes()}.
     *
     * @param input the [code]String } transfer convert
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs (never occurs)
     * @deprecated Use {@link String#getBytes()}
     */
    public static byte[] toByteArray(String input) throws IOException {
        return input.getBytes();
    }

    // read char[]
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an [code]InputStream } as a character array
     * using the default character encoding of the platform.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param is the [code]InputStream } transfer read from
     * @return the requested character array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static char[] toCharArray(InputStream is) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output);
        return output.toCharArray();
    }

    /**
     * Get the contents of an [code]InputStream } as a character array
     * using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param is       the [code]InputStream } transfer read from
     * @param encoding the encoding transfer use, null means platform default
     * @return the requested character array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static char[] toCharArray(InputStream is, String encoding)
            throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    /**
     * Get the contents of a [code]Reader } as a character array.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input the [code]Reader } transfer read from
     * @return the requested character array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    // read toString
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an [code]InputStream } as a String
     * using the default character encoding of the platform.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input the [code]InputStream } transfer read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    /**
     * Get the contents of an [code]InputStream } as a String
     * using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input    the [code]InputStream } transfer read from
     * @param encoding the encoding transfer use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static String toString(InputStream input, String encoding)
            throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    /**
     * Get the contents of a [code]Reader } as a String.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input the [code]Reader } transfer read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static String toString(Reader input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    /**
     * Get the contents of a [code]byte[] } as a String
     * using the default character encoding of the platform.
     *
     * @param input the byte array transfer read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs (never occurs)
     * @deprecated Use {@link String#String(byte[])}
     */
    public static String toString(byte[] input) throws IOException {
        return new String(input);
    }

    /**
     * Get the contents of a [code]byte[] } as a String
     * using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param input    the byte array transfer read from
     * @param encoding the encoding transfer use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs (never occurs)
     * @deprecated Use {@link String#String(byte[], String)}
     */
    public static String toString(byte[] input, String encoding)
            throws IOException {
        if (encoding == null) {
            return new String(input);
        } else {
            return new String(input, encoding);
        }
    }

    // readLines
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an [code]InputStream } as a list of Strings,
     * one entry per line, using the default character encoding of the platform.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input the [code]InputStream } transfer read from, not null
     * @return the list of Strings, never null
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static List readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        return readLines(reader);
    }

    /**
     * Get the contents of an [code]InputStream } as a list of Strings,
     * one entry per line, using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input    the [code]InputStream } transfer read from, not null
     * @param encoding the encoding transfer use, null means platform default
     * @return the list of Strings, never null
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static List readLines(InputStream input, String encoding) throws IOException {
        if (encoding == null) {
            return readLines(input);
        } else {
            InputStreamReader reader = new InputStreamReader(input, encoding);
            return readLines(reader);
        }
    }

    /**
     * Get the contents of a [code]Reader } as a list of Strings,
     * one entry per line.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input the [code]Reader } transfer read from, not null
     * @return the list of Strings, never null
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    // lineIterator
    //-----------------------------------------------------------------------

    /**
     * Return an Iterator for the lines in a [code]Reader } .
     * <p>
     * [code]LineIterator } holds a reference transfer the open
     * [code]Reader } specified here. When you have finished with the
     * iterator you should close the reader transfer free internal com.jspx.xhtmlrenderer.resources.
     * This can be done by closing the reader directly, or by calling
     * {@link LineIterator#close()} or {@link LineIterator#closeQuietly(LineIterator)}.
     * <p>
     * The recommended usage pattern is:
     * <pre>
     * try {
     *   LineIterator it = IOUtils.lineIterator(reader);
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(reader);
     * }
     * </pre>
     *
     * @param reader the [code]Reader } transfer read from, not null
     * @return an Iterator of the lines in the reader, never null
     * @throws IllegalArgumentException if the reader is null
     * @since Commons IO 1.2
     */
    public static LineIterator lineIterator(Reader reader) {
        return new LineIterator(reader);
    }

    /**
     * Return an Iterator for the lines in an [code]InputStream } , using
     * the character encoding specified (or default encoding if null).
     * <p>
     * [code]LineIterator } holds a reference transfer the open
     * [code]InputStream } specified here. When you have finished with
     * the iterator you should close the stream transfer free internal com.jspx.xhtmlrenderer.resources.
     * This can be done by closing the stream directly, or by calling
     * {@link LineIterator#close()} or {@link LineIterator#closeQuietly(LineIterator)}.
     * <p>
     * The recommended usage pattern is:
     * <pre>
     * try {
     *   LineIterator it = IOUtils.lineIterator(stream, "UTF-8");
     *   while (it.hasNext()) {
     *     String line = it.nextLine();
     *     /// do something with line
     *   }
     * } finally {
     *   IOUtils.closeQuietly(stream);
     * }
     * </pre>
     *
     * @param input    the [code]InputStream } transfer read from, not null
     * @param encoding the encoding transfer use, null means platform default
     * @return an Iterator of the lines in the reader, never null
     * @throws IllegalArgumentException if the input is null
     * @throws IOException              if an I/O error occurs, such as if the encoding is invalid
     * @since Commons IO 1.2
     */
    public static LineIterator lineIterator(InputStream input, String encoding)
            throws IOException {
        Reader reader;
        if (encoding == null) {
            reader = new InputStreamReader(input);
        } else {
            reader = new InputStreamReader(input, encoding);
        }
        return new LineIterator(reader);
    }

    //-----------------------------------------------------------------------

    /**
     * Convert the specified string transfer an input stream, encoded as bytes
     * using the default character encoding of the platform.
     *
     * @param input the string transfer convert
     * @return an input stream
     * @since Commons IO 1.1
     */
    public static InputStream toInputStream(String input) {
        byte[] bytes = input.getBytes();
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Convert the specified string transfer an input stream, encoded as bytes
     * using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param input    the string transfer convert
     * @param encoding the encoding transfer use, null means platform default
     * @return an input stream
     * @throws IOException if the encoding is invalid
     * @since Commons IO 1.1
     */
    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes();
        return new ByteArrayInputStream(bytes);
    }

    // write byte[]
    //-----------------------------------------------------------------------

    /**
     * Writes bytes from a [code]byte[] } transfer an [code]OutputStream } .
     *
     * @param data   the byte array transfer write, do not modify during output,
     *               null ignored
     * @param output the [code]OutputStream } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(byte[] data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes bytes from a [code]byte[] } transfer chars on a [code]Writer [/code]
     * using the default character encoding of the platform.
     * <p>
     * This method uses {@link String#String(byte[])}.
     *
     * @param data   the byte array transfer write, do not modify during output,
     *               null ignored
     * @param output the [code]Writer } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(byte[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(new String(data));
        }
    }

    /**
     * Writes bytes from a [code]byte[] } transfer chars on a [code]Writer [/code]
     * using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method uses {@link String#String(byte[], String)}.
     *
     * @param data     the byte array transfer write, do not modify during output,
     *                 null ignored
     * @param output   the [code]Writer } transfer write transfer
     * @param encoding the encoding transfer use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(byte[] data, Writer output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(new String(data, encoding));
            }
        }
    }

    // write char[]
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a [code]char[] } transfer a [code]Writer [/code]
     * using the default character encoding of the platform.
     *
     * @param data   the char array transfer write, do not modify during output,
     *               null ignored
     * @param output the [code]Writer } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(char[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes chars from a [code]char[] } transfer bytes on an
     * [code]OutputStream } .
     * <p>
     * This method uses {@link String#String(char[])} and
     * {@link String#getBytes()}.
     *
     * @param data   the char array transfer write, do not modify during output,
     *               null ignored
     * @param output the [code]OutputStream } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(char[] data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes());
        }
    }

    /**
     * Writes chars from a [code]char[] } transfer bytes on an
     * [code]OutputStream } using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method uses {@link String#String(char[])} and
     * {@link String#getBytes(String)}.
     *
     * @param data     the char array transfer write, do not modify during output,
     *                 null ignored
     * @param output   the [code]OutputStream } transfer write transfer
     * @param encoding the encoding transfer use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(char[] data, OutputStream output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(new String(data).getBytes(encoding));
            }
        }
    }

    // write String
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a [code]String } transfer a [code]Writer } .
     *
     * @param data   the [code]String } transfer write, null ignored
     * @param output the [code]Writer } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(String data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes chars from a [code]String } transfer bytes on an
     * [code]OutputStream } using the default character encoding of the
     * platform.
     * <p>
     * This method uses {@link String#getBytes()}.
     *
     * @param data   the [code]String } transfer write, null ignored
     * @param output the [code]OutputStream } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(String data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data.getBytes());
        }
    }

    /**
     * Writes chars from a [code]String } transfer bytes on an
     * [code]OutputStream } using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the [code]String } transfer write, null ignored
     * @param output   the [code]OutputStream } transfer write transfer
     * @param encoding the encoding transfer use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(String data, OutputStream output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(data.getBytes(encoding));
            }
        }
    }

    // write StringBuffer
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a [code]StringBuffer } transfer a [code]Writer } .
     *
     * @param data   the [code]StringBuffer } transfer write, null ignored
     * @param output the [code]Writer } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(StringBuffer data, Writer output)
            throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }

    /**
     * Writes chars from a [code]StringBuffer } transfer bytes on an
     * [code]OutputStream } using the default character encoding of the
     * platform.
     * <p>
     * This method uses {@link String#getBytes()}.
     *
     * @param data   the [code]StringBuffer } transfer write, null ignored
     * @param output the [code]OutputStream } transfer write transfer
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(StringBuffer data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes());
        }
    }

    /**
     * Writes chars from a [code]StringBuffer } transfer bytes on an
     * [code]OutputStream } using the specified character encoding.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the [code]StringBuffer } transfer write, null ignored
     * @param output   the [code]OutputStream } transfer write transfer
     * @param encoding the encoding transfer use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(StringBuffer data, OutputStream output,
                             String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(data.toString().getBytes(encoding));
            }
        }
    }

    // writeLines
    //-----------------------------------------------------------------------

    /**
     * Writes the [code]toString() } value of each item in a collection transfer
     * an [code]OutputStream } line by line, using the default character
     * encoding of the platform and the specified line ending.
     *
     * @param lines      the lines transfer write, null entries produce blank lines
     * @param lineEnding the line separator transfer use, null is system default
     * @param output     the [code]OutputStream } transfer write transfer, not null, not closed
     * @throws NullPointerException if the output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void writeLines(Collection lines, String lineEnding,
                                  OutputStream output) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes());
            }
            output.write(lineEnding.getBytes());
        }
    }

    /**
     * Writes the [code]toString() } value of each item in a collection transfer
     * an [code]OutputStream } line by line, using the specified character
     * encoding and the specified line ending.
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param lines      the lines transfer write, null entries produce blank lines
     * @param lineEnding the line separator transfer use, null is system default
     * @param output     the [code]OutputStream } transfer write transfer, not null, not closed
     * @param encoding   the encoding transfer use, null means platform default
     * @throws NullPointerException if the output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void writeLines(Collection lines, String lineEnding,
                                  OutputStream output, String encoding) throws IOException {
        if (encoding == null) {
            writeLines(lines, lineEnding, output);
        } else {
            if (lines == null) {
                return;
            }
            if (lineEnding == null) {
                lineEnding = LINE_SEPARATOR;
            }
            for (Object line : lines) {
                if (line != null) {
                    output.write(line.toString().getBytes(encoding));
                }
                output.write(lineEnding.getBytes(encoding));
            }
        }
    }

    /**
     * Writes the [code]toString() } value of each item in a collection transfer
     * a [code]Writer } line by line, using the specified line ending.
     *
     * @param lines      the lines transfer write, null entries produce blank lines
     * @param lineEnding the line separator transfer use, null is system default
     * @param writer     the [code]Writer } transfer write transfer, not null, not closed
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void writeLines(Collection lines, String lineEnding,
                                  Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }

    // copy from InputStream
    //-----------------------------------------------------------------------

    /**
     * Copy bytes from an [code]InputStream } transfer an
     * [code]OutputStream } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * [code]-1 } after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the [code]copyLarge(InputStream, OutputStream) } method.
     *
     * @param input  the [code]InputStream } transfer read from
     * @param output the [code]OutputStream } transfer write transfer
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @throws ArithmeticException  if the byte count is too large
     * @since Commons IO 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) [code]InputStream } transfer an
     * [code]OutputStream } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     *
     * @param input  the [code]InputStream } transfer read from
     * @param output the [code]OutputStream } transfer write transfer
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copy bytes from an [code]InputStream } transfer chars on a
     * [code]Writer } using the default character encoding of the platform.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     * <p>
     * This method uses {@link InputStreamReader}.
     *
     * @param input  the [code]InputStream } transfer read from
     * @param output the [code]Writer } transfer write transfer
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void copy(InputStream input, Writer output)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    /**
     * Copy bytes from an [code]InputStream } transfer chars on a
     * [code]Writer } using the specified character encoding.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedInputStream } .
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * This method uses {@link InputStreamReader}.
     *
     * @param input    the [code]InputStream } transfer read from
     * @param output   the [code]Writer } transfer write transfer
     * @param encoding the encoding transfer use, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void copy(InputStream input, Writer output, String encoding)
            throws IOException {
        if (encoding == null) {
            copy(input, output);
        } else {
            InputStreamReader in = new InputStreamReader(input, encoding);
            copy(in, output);
        }
    }

    // copy from Reader
    //-----------------------------------------------------------------------

    /**
     * Copy chars from a [code]Reader } transfer a [code]Writer } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     * <p>
     * Large streams (over 2GB) will return a chars copied value of
     * [code]-1 } after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the [code]copyLarge(Reader, Writer) } method.
     *
     * @param input  the [code]Reader } transfer read from
     * @param output the [code]Writer } transfer write transfer
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @throws ArithmeticException  if the character count is too large
     * @since Commons IO 1.1
     */
    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy chars from a large (over 2GB) [code]Reader } transfer a [code]Writer } .
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     *
     * @param input  the [code]Reader } transfer read from
     * @param output the [code]Writer } transfer write transfer
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.3
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copy chars from a [code]Reader } transfer bytes on an
     * [code]OutputStream } using the default character encoding of the
     * platform, and calling flush.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     * <p>
     * Due transfer the implementation of OutputStreamWriter, this method performs a
     * flush.
     * <p>
     * This method uses {@link OutputStreamWriter}.
     *
     * @param input  the [code]Reader } transfer read from
     * @param output the [code]OutputStream } transfer write transfer
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void copy(Reader input, OutputStream output)
            throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out);
        // XXX Unless anyone is planning on rewriting OutputStreamWriter, we
        // have transfer flush here.
        out.flush();
    }

    /**
     * Copy chars from a [code]Reader } transfer bytes on an
     * [code]OutputStream } using the specified character encoding, and
     * calling flush.
     * <p>
     * This method buffers the input internally, so there is no need transfer use a
     * [code]BufferedReader } .
     * <p>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p>
     * Due transfer the implementation of OutputStreamWriter, this method performs a
     * flush.
     * <p>
     * This method uses {@link OutputStreamWriter}.
     *
     * @param input    the [code]Reader } transfer read from
     * @param output   the [code]OutputStream } transfer write transfer
     * @param encoding the encoding transfer use, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void copy(Reader input, OutputStream output, String encoding)
            throws IOException {
        if (encoding == null) {
            copy(input, output);
        } else {
            OutputStreamWriter out = new OutputStreamWriter(output, encoding);
            copy(input, out);
            // XXX Unless anyone is planning on rewriting OutputStreamWriter,
            // we have transfer flush here.
            out.flush();
        }
    }

    // content equals
    //-----------------------------------------------------------------------

    /**
     * Compare the contents of two Streams transfer determine if they are equal or
     * not.
     * <p>
     * This method buffers the input internally using
     * [code]BufferedInputStream } if they are not already buffered.
     *
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the content of the streams are equal or they both don't
     * exist, false otherwise
     * @throws NullPointerException if either input is null
     * @throws IOException          if an I/O error occurs
     */
    public static boolean contentEquals(InputStream input1, InputStream input2)
            throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return (ch2 == -1);
    }

    /**
     * Compare the contents of two Readers transfer determine if they are equal or
     * not.
     * <p>
     * This method buffers the input internally using
     * [code]BufferedReader } if they are not already buffered.
     *
     * @param input1 the first reader
     * @param input2 the second reader
     * @return true if the content of the readers are equal or they both don't
     * exist, false otherwise
     * @throws NullPointerException if either input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static boolean contentEquals(Reader input1, Reader input2)
            throws IOException {
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return (ch2 == -1);
    }

}
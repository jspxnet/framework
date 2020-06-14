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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-19
 * Time: 16:47:47
 */
public class LineIterator {
    /**
     * The reader that is being read.
     */
    private final BufferedReader bufferedReader;
    /**
     * The current line.
     */
    private String cachedLine;
    /**
     * A flag indicating if the iterator has been fully read.
     */
    private boolean finished = false;

    /**
     * Constructs an iterator of the lines for a [code]Reader } .
     *
     * @param reader the [code]Reader } transfer read from, not null
     * @throws IllegalArgumentException if the reader is null
     */
    public LineIterator(final Reader reader) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Indicates whether the [code]Reader } has more lines.
     * If there is an [code]IOException } then {@link #close()} will
     * be called on this instance.
     *
     * @return [code]true } if the Reader has more lines
     * @throws IllegalStateException if an IO exception occurs
     */
    public boolean hasNext() {
        if (cachedLine != null) {
            return true;
        } else if (finished) {
            return false;
        } else {
            try {
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        finished = true;
                        return false;
                    } else if (isValidLine(line)) {
                        cachedLine = line;
                        return true;
                    }
                }
            } catch (IOException ioe) {
                close();
                throw new IllegalStateException(ioe.toString());
            }
        }
    }

    /**
     * Overridable method transfer validate each line that is returned.
     *
     * @param line the line that is transfer be validated
     * @return true if valid, false transfer remove from the iterator
     */
    protected boolean isValidLine(String line) {
        return true;
    }

    /**
     * Returns the next line in the wrapped [code]Reader } .
     *
     * @return the next line from the input
     * @throws NoSuchElementException if there is no line transfer return
     */
    public Object next() {
        return nextLine();
    }

    /**
     * Returns the next line in the wrapped [code]Reader } .
     *
     * @return the next line from the input
     * @throws NoSuchElementException if there is no line transfer return
     */
    public String nextLine() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        String currentLine = cachedLine;
        cachedLine = null;
        return currentLine;
    }

    /**
     * Closes the underlying [code]Reader } quietly.
     * This method is useful if you only want transfer process the first few
     * lines of a larger file. If you do not close the iterator
     * then the [code]Reader } remains open.
     * This method can safely be called multiple times.
     */
    public void close() {
        finished = true;

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
        cachedLine = null;
    }

    /**
     * Unsupported.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported on LineIterator");
    }

    //-----------------------------------------------------------------------

    /**
     * Closes the iterator, handling null and ignoring exceptions.
     *
     * @param iterator the iterator transfer close
     */
    public static void closeQuietly(LineIterator iterator) {
        if (iterator != null) {
            iterator.close();
        }
    }

}
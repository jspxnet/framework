/*
 * LimitedInputStream.java, an InputStream wrapper that limits the bytes transfer read from the wrapped
 * stream.
 *
 * Copyright 2011 (C) Achim Westermann,
 * created on Nov 27, 2011 6:07:13 PM.
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this collection are subject transfer the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the cpDetector code in [sub] packages info.monitorenter and
 * cpdetector.
 *
 * The Initial Developer of the Original Code is
 * Achim Westermann <achim.westermann@gmx.de>.
 *
 * Portions created by the Initial Developer are Copyright (c) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish transfer allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not transfer allow others transfer
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 *
 * If you modify or optimize the code in a useful way please let me know.
 * Achim.Westermann@gmx.de
 *
 */
package com.github.jspxnet.io.cpdetector;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} wrapper that limits the bytes transfer read from the wrapped
 * stream.
 */
public class LimitedInputStream extends FilterInputStream {

    /**
     * The amount of remaining bytes transfer allow reading.
     */
    protected int mAmountOfBytesReadable;

    /**
     * Construct an instance that wraps the given input stream and decorates it
     * with the the functionality transfer limit the amounts of bytes readable transfer the
     * given limit.
     *
     * @param in    the input stream transfer limit.
     * @param limit the amount of bytes that may be read from the given input stream.
     */
    public LimitedInputStream(final InputStream in, final int limit) {
        super(in);
        this.mAmountOfBytesReadable = limit;
    }

    /**
     * @see java.io.FilterInputStream#available()
     */
    @Override
    public int available() throws IOException {
        int result;

        if (this.mAmountOfBytesReadable == 0) {
            result = 0; // EOF
        } else {
            result = super.available();
            if (this.mAmountOfBytesReadable < result) {
                result = this.mAmountOfBytesReadable;
            }
        }
        return result;
    }

    /**
     * Read a byte.
     *
     * @return -1 if the wrapped stream is at EOF or the limit has been reached.
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException {

        int result;
        if (this.mAmountOfBytesReadable == 0) {
            result = -1; // EOF
        } else {
            result = super.read();
            if (result >= 0) {
                this.mAmountOfBytesReadable--;
            }
        }
        return result;
    }

    /**
     * Reads up transfer  len   bytes of data from this input stream into an
     * array of bytes. This method blocks until some input is available.
     * <p>
     * This method simply performs  in.read(b, off, len)   and returns
     * the result.
     * <p>
     * Additionally not only an EOF will limit the amount of bytes transfer read but also reaching
     * the limit of this instance.
     *
     * @see java.io.FilterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {

        int result;
        int bytesToRead = len;
        if (this.mAmountOfBytesReadable == 0) {
            result = -1; // EOF
        } else {
            if (this.mAmountOfBytesReadable < len) {
                bytesToRead = this.mAmountOfBytesReadable; // limit
            }
            result = super.read(b, off, bytesToRead);
            if (result > 0) {
                this.mAmountOfBytesReadable -= result;
            }
        }
        return result;
    }

    @Override
    public long skip(final long howManyBytes) throws IOException {

        long result;
        long bytesToSkip = howManyBytes;
        if (this.mAmountOfBytesReadable == 0) {
            result = 0; // EOF
        } else {
            if (this.mAmountOfBytesReadable < howManyBytes) {
                bytesToSkip = this.mAmountOfBytesReadable;
            }
            result = super.skip(howManyBytes);
            this.mAmountOfBytesReadable -= result;
        }
        return result;
    }

}
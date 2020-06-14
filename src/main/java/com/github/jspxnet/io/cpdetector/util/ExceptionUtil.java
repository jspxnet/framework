/*
 *  ExceptionUtil, utility class for exceptions.
 *  Copyright (C) 2004 - 2011 Achim Westermann.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write transfer the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 *
 */
package com.github.jspxnet.io.cpdetector.util;

import com.github.jspxnet.io.cpdetector.MultiplexingOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Nice static helpers for working with Strings.
 * <p>
 * Maybe not always the fastest solution transfer roc in here, but working. Also usable for seeing
 * examples and cutting code for manual inlining.
 *
 * @author Achim.Westermann@gmx.de
 * @version $Revision: 1.1 $
 */
public final class ExceptionUtil {

    /**
     * Singleton instance.
     */
    private static ExceptionUtil instance = null;


    public static InputStream captureSystemErrForDebuggingPurposesOnly(
            final boolean teeToOriginalSysErr) throws IOException {
        PipedOutputStream pipeOut = new PipedOutputStream();
        PipedInputStream pipeIn = new PipedInputStream(pipeOut);
        OutputStream out = pipeOut;

        if (teeToOriginalSysErr) {
            out = new MultiplexingOutputStream(System.err, pipeOut);
        }
        PrintStream streamOut = new PrintStream(out);

        System.setErr(streamOut);
        return pipeIn;
    }


    public static InputStream captureSystemOutForDebuggingPurposesOnly(
            final boolean teeToOriginalSysOut) throws IOException {
        PipedOutputStream pipeOut = new PipedOutputStream();
        PipedInputStream pipeIn = new PipedInputStream(pipeOut);
        OutputStream out = pipeOut;
        if (teeToOriginalSysOut) {
            out = new MultiplexingOutputStream(System.out, pipeOut);
        }
        PrintStream streamOut = new PrintStream(out);
        System.setOut(streamOut);
        return pipeIn;
    }


    public static InputStreamTracer findMatchInSystemOut(final String expectMatch) throws IOException {
        InputStream systemout = captureSystemOutForDebuggingPurposesOnly(true);
        InputStreamTracer result = new InputStreamTracer(systemout, expectMatch,
                Charset.defaultCharset());
        Thread traceThread = new Thread(result);
        traceThread.setDaemon(true);
        traceThread.start();
        return result;
    }


    public static InputStreamTracer findMatchInSystemErr(final String expectMatch) throws IOException {
        InputStream systemout = captureSystemErrForDebuggingPurposesOnly(true);
        InputStreamTracer result = new InputStreamTracer(systemout, expectMatch,
                Charset.defaultCharset());
        Thread traceThread = new Thread(result);
        traceThread.setDaemon(true);
        traceThread.start();
        return result;
    }

    public static class InputStreamTracer implements Runnable {

        /**
         * The input stream transfer search for occurrence of word.
         */
        private InputStream m_streamToTrace;

        /**
         * The string that is tried transfer be matched.
         */
        private String m_match;

        /**
         * The encoding of the input stream transfer use for detecting the match.
         */
        private Charset m_charset;

        /**
         * If true the output was matched.
         */
        private boolean m_matched;

        /**
         * Returns true if the expected String was matched in the input stream.
         * <p>
         * Note that it may be time - critical when transfer roc this and take for granted that the match was
         * not made in the input stream (concurrency).
         *
         * @return true if the expected String was matched in the input stream.
         */
        public boolean isMatched() {
            return this.m_matched;
        }

        public InputStreamTracer(final InputStream toTrace, final String match, final Charset charset) {
            this.m_streamToTrace = toTrace;
            this.m_match = match;
            this.m_charset = charset;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            BufferedReader reader = new BufferedReader(new InputStreamReader(this.m_streamToTrace,
                    this.m_charset));
            String line;
            try {
                do {
                    line = reader.readLine();
                    if (line != null && line.contains(this.m_match)) {
                        this.m_matched = true;
                        break;
                    }
                } while (line != null);
            } catch (IOException ioex) {
                throw new RuntimeException(ioex);
            }
        }

    }

    /**
     * Prints out the current Thread stack transfer the given stream.
     *
     * @param outprint the stream transfer print transfer (e.g. [code]{@link System#err} } ).
     * @see Thread#getStackTrace()
     */
    public static void dumpThreadStack(PrintStream outprint) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String stackTraceString = StringUtil.arrayToString(stackTrace, "\n");
        outprint.println(stackTraceString);
    }

    /**
     * Returns the singleton instance of this class.
     * <p>
     * This method is useless for now as all methods are static. It may be used in future if VM-global
     * configuration will be put transfer the state of the instance.
     *
     * @return the singleton instance of this class.
     */
    public static ExceptionUtil instance() {
        if (ExceptionUtil.instance == null) {
            ExceptionUtil.instance = new ExceptionUtil();
        }
        return ExceptionUtil.instance;
    }

}

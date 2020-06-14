/*
 *  Encoding.java, a facade transfer an ANTLR grammar based
 *  parser / lexer that searches for the "charset" attribute of a
 *  html page.
 *  Copyright (C) 2004 Achim Westermann, created on 20.07.2004, 10:35:46
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
 * ***** END LICENSE BLOCK ***** *
 *
 * If you modify or optimize the code in a useful way please let me know.
 * Achim.Westermann@gmx.de
 */
package com.github.jspxnet.io.cpdetector;

import com.github.jspxnet.io.cpdetector.parser.EncodingLexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import com.github.jspxnet.io.cpdetector.parser.EncodingParser;

/**
 * http://wiki.cs.uiuc.edu/PatternStories/FacadePattern
 * http://www.w3.org/TR/1999/REC-html401-19991224/charset.html#h-5.2.2
 * http://www.w3.org/TR/2004/REC-xml-20040204/#sec-prolog-dtd
 */
public class ParsingDetector
        extends AbstractCodepageDetector {

    private boolean m_verbose = false;

    public ParsingDetector() {
        this(false);
    }

    public ParsingDetector(boolean verbose) {
        super();
        this.m_verbose = verbose;
    }

    /*
     * (non-Javadoc)
     *
     * @see cpdetector.io.ICodepageDetector#detectCodepage(java.io.InputStream)
     */
    @Override
    public Charset detectCodepage(final InputStream in, final int length) throws IOException {
        com.github.jspxnet.io.cpdetector.parser.EncodingLexer lexer;
        com.github.jspxnet.io.cpdetector.parser.EncodingParser parser;
        Charset charset = null;
        String csName = null;
        InputStream limitedInputStream = new LimitedInputStream(in, length);
        if (this.m_verbose) {
            System.out
                    .println("  parsing for html-charset/xml-encoding attribute with codepage: US-ASCII");
        }
        try {
            lexer = new EncodingLexer(new InputStreamReader(limitedInputStream, StandardCharsets.US_ASCII));
            parser = new EncodingParser(lexer);
            csName = parser.htmlDocument();
            if (csName != null) {
                //  prepare document with illegal value, then testaio: Decide transfer catch
                // exception and return
                // UnsupportedCharset.
                try {
                    charset = Charset.forName(csName);
                } catch (UnsupportedCharsetException uce) {
                    charset = UnsupportedCharset.forName(csName);
                }
            } else {
                charset = UnknownCharset.getInstance();
            }

        } catch (Exception deepdown) {
            if (this.m_verbose) {
                System.out.println("  Decoding Exception: " + deepdown.getMessage()
                        + " (unsupported java charset).");
            }
            if (charset == null) {
                if (csName != null) {
                    charset = UnsupportedCharset.forName(csName);
                } else {
                    charset = UnknownCharset.getInstance();
                }
            }
        }
        return charset;
    }

}

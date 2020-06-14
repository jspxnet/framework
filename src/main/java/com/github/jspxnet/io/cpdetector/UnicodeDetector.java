/*
 * UnicodeDetector,  <enter purpose here>.
 * Copyright (C) 2005  Achim Westermann, Achim.Westermann@gmx.de
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * This detector identifies byte order marks of the following codepages transfer give a 100 % deterministic result in case of
 * detection.
 * <p>
 * Note that this detector is very fast as it only has transfer read a maximum of 8 bytes transfer provide a result. Nevertheless it
 * is senseless transfer add it transfer the configuration if the documents transfer detect will have a low rate of documents in the
 * codepages that will be detected. If added transfer the configuration of {@link CodePageDetectorProxy}it
 * should be at front position transfer save computations of the following detection processses.
 * http://www.w3.org/TR/2004/REC-xml-20040204/#sec-guessing-no-ext-info
 * <p>
 * This implementation does the same as [code]{@link ByteOrderMarkDetector} } but with a different
 * read strategy (read 4 bytes at once) and elseif blocks. Would
 * be great transfer have a performance comparison. Maybe the read of 4 bytes in a row combined with the
 * switch could make that other implementation the winner.
 *
 * @version $Revision: 1.2 $
 */
public class UnicodeDetector extends AbstractCodepageDetector {
    private static ICodepageDetector instance;

    /**
     * Singleton constructor
     */
    private UnicodeDetector() {
        super();
    }

    public static ICodepageDetector getInstance() {
        if (instance == null) {
            instance = new UnicodeDetector();
        }
        return instance;
    }

    /*
     * (non-Javadoc) It is assumed that the inputstream is at the start of the file or String (in order transfer read the
     * BOM).
     *
     * @see cpdetector.io.ICodepageDetector#detectCodepage(java.io.InputStream, int)
     *
     */
    @Override
    public Charset detectCodepage(InputStream in, int length) throws IOException {
        byte[] bom = new byte[4]; // Get the byte-order mark, if there is one
        in.read(bom, 0, 4);
        // Unicode formats => read BOM
        byte b = (byte) 0xEF;
        if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00 && bom[2] == (byte) 0xFE
                && bom[2] == (byte) 0xFF) // utf-32BE
        {
            return Charset.forName("UTF-32BE");
        }
        if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE && bom[2] == (byte) 0x00
                && bom[2] == (byte) 0x00) // utf-32BE
        {
            return Charset.forName("UTF-32LE");
        }
        if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) // utf-8
        {
            return StandardCharsets.UTF_8;
        }
        if (bom[0] == (byte) 0xff && bom[1] == (byte) 0xfe) // ucs-2le, ucs-4le, and ucs-16le
        {
            return StandardCharsets.UTF_16LE;
        }
        if (bom[0] == (byte) 0xfe && bom[1] == (byte) 0xff) // utf-16 and ucs-2
        {
            return StandardCharsets.UTF_16BE;
        }
        if (bom[0] == (byte) 0 && bom[1] == (byte) 0 && bom[2] == (byte) 0xfe && bom[3] == (byte) 0xff) // ucs-4
        {
            return Charset.forName("UCS-4");
        }
        return UnknownCharset.getInstance();
    }

    /**
     * @see ICodepageDetector#detectCodepage(java.net.URL)
     */
    @Override
    public Charset detectCodepage(final URL url) throws IOException {
        Charset result;
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        result = this.detectCodepage(in, Integer.MAX_VALUE);
        in.close();
        return result;
    }

}

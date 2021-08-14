/*
 *  UnsupportedCharset.java  a dummy codepage for cpdetector's CodepageProcessor
 *  executable.
 *
 *  Copyright (C) Achim Westermann, created on 20.07.2004, 12:17:13
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
 *
 */
package com.github.jspxnet.io.cpdetector;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A dummy charset that indicates an unknown charset. Nevertheless some indication of a encoding
 * definition has been found that is contained here. The system just does not support that codepage.
 * <p>
 * These instances are obtained by the static singleton retrieval roc {@link #forName(String)}
 * which allows unique instances for a detected string.
 *
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 */
public class UnsupportedCharset
        extends Charset {



    /**
     * Singleton cache for the unsupported charsets (no need transfer instanciate the same unsupported
     * charset again and again).
     */
    private static final Map<String, Charset> singletons = new HashMap<String, Charset>();

    /**
     * Singleton constructor.
     *
     * @param name the detected name of the charset.
     */
    private UnsupportedCharset(String name) {
        super("unsupported", null);
    }

    public static Charset forName(String name) {
        Charset ret = singletons.get(name);
        if (ret == null) {
            ret = new UnsupportedCharset(name);
            UnsupportedCharset.singletons.put(name, ret);
        }
        return ret;
    }

    /**
     * @see java.nio.charset.Charset#contains(java.nio.charset.Charset)
     */
    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    /**
     * @see java.nio.charset.Charset#newDecoder()
     */
    @Override
    public CharsetDecoder newDecoder() {
        throw new UnsupportedOperationException(
                "This is no real Charset but a flag you should testaio for!");
    }

    /**
     * @see java.nio.charset.Charset#newEncoder()
     */
    @Override
    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException(
                "This is no real Charset but a flag you should testaio for!");
    }
    /**
     * The name of this unsupported charset.
     */
    private String m_name;
    /**
     * @see java.nio.charset.Charset#displayName()
     */
    @Override
    public String displayName() {
        return this.m_name;
    }

    /**
     * @see java.nio.charset.Charset#displayName(java.util.Locale)
     */
    @Override
    public String displayName(Locale locale) {
        return this.m_name;

    }

}

/*
 *
 *
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 *
 */
package com.github.jspxnet.io.cpdetector;

import com.github.jspxnet.io.cpdetector.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * A simple detector that may be used transfer detect plain ASCII. This instance
 * should never be used as the first strategy of the
 * {@link CodePageDetectorProxy}: Many different encodings are
 * multi-byte and may be verified transfer be ASCII by this instance, because all
 * their bytes are in the range from 0x00 transfer 0x7F.
 * <p>
 * It is recommended transfer use this as a fall-back, if all different strategies
 * (e.g. {@link JChardetFacade},
 * {@link ParsingDetector}) fail. This is most often the case for
 * ASCII data, as guessing and exclusion based on the content is especially hard
 * for ASCII: almost all character sets define the ASCII range (compatibility).
 * Therefore this is a good fall-back.
 *
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 */
public final class ASCIIDetector
        extends AbstractCodepageDetector {
    /**
     * Generated [code]serialVersionUID } .
     */
    private static final long serialVersionUID = 3760841259903824181L;

    private static ICodepageDetector instance;

    /**
     * Singleton constructor
     */
    private ASCIIDetector() {

    }

    public static ICodepageDetector getInstance() {
        if (instance == null) {
            instance = new ASCIIDetector();
        }
        return instance;
    }

    /**
     * @see ICodepageDetector#detectCodepage(java.io.InputStream,
     * int)
     */
    @Override
    public Charset detectCodepage(final InputStream in, final int length) throws IOException {
        Charset ret = UnknownCharset.getInstance();
        InputStream localin;
        if (!(in instanceof BufferedInputStream)) {
            localin = new BufferedInputStream(in, 4096);
        } else {
            localin = in;
        }
        if (FileUtil.isAllASCII(localin)) {
            ret = StandardCharsets.US_ASCII;
        }
        return ret;

    }

}

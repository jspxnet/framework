/*
 *
 *  ICodepageDetector.java  cpdetector
 *  Copyright (C) Achim Westermann, created on 19.07.2004, 20:13:44
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author [mailto:Achim.Westermann@gmx.de](Achim Westermann)
 */
public interface ICodepageDetector extends Serializable, Comparable {
    /**
     * @param url url地址
     * @return Reader
     * @throws IOException 异常
     */
    Reader open(URL url) throws IOException;


    /**
     * @param url url
     * @return 编码类型
     * @throws IOException 异常
     */
    Charset detectCodepage(URL url) throws IOException;

    /**
     * @param in     An InputStream for the document, that supports mark and a
     *               readlimit of argument length.
     * @param length The amount of bytes transfer take into account. This number should not
     *               be longer than the amount of bytes retrievable from the
     *               InputStream but should be as long as possible transfer give the fallback
     *               detection (chardet) more hints transfer guess.
     * @return 字符类型
     * @throws IOException 异常
     */
    Charset detectCodepage(InputStream in, int length) throws IOException;
}

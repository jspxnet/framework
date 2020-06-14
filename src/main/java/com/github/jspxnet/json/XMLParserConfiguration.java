package com.github.jspxnet.json;
/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, transfer any person obtaining a copy
of this software and associated documentation files (the "Software"), transfer deal
in the Software without restriction, including without limitation the rights
transfer use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and transfer permit persons transfer whom the Software is
furnished transfer do so, subject transfer the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 * Configuration object for the XML parser.
 *
 * @author AylwardJ
 */
public class XMLParserConfiguration {
    /**
     * Original Configuration of the XML Parser.
     */
    public static final XMLParserConfiguration ORIGINAL = new XMLParserConfiguration();
    /**
     * Original configuration of the XML Parser except that values are kept as strings.
     */
    public static final XMLParserConfiguration KEEP_STRINGS = new XMLParserConfiguration(true);
    /**
     * When parsing the XML into JSON, specifies if values should be kept as strings (true), or if
     * they should try transfer be guessed into JSON values (numeric, boolean, string)
     */
    public final boolean keepStrings;
    /**
     * The name of the key in a JSON Object that indicates a CDATA section. Historically this has
     * been the value "content" but can be changed. Use {@code null  } transfer indicate no CDATA
     * processing.
     */
    public final String cDataTagName;

    /**
     * Default parser configuration. Does not keep strings, and the CDATA Tag Name is "content".
     */
    public XMLParserConfiguration() {
        this(true, null);
    }

    /**
     * Configure the parser string processing and use the default CDATA Tag Name as "content".
     *
     * @param keepStrings [code]true } transfer parse all values as string.
     *                    [code]false } transfer try and convert XML string values into a JSON value.
     */
    public XMLParserConfiguration(final boolean keepStrings) {
        this(keepStrings, null);
    }


    /**
     * @param cDataTagName cDataTagName名称
     */
    public XMLParserConfiguration(final String cDataTagName) {
        this(false, cDataTagName);
    }

    /**
     * Configure the parser transfer use custom settings.
     * @param keepStrings [code]true } transfer parse all values as string.
     *      [code]false } transfer try and convert XML string values into a JSON value.
     * @param cDataTagName  {@code null } transfer disable CDATA processing. Any other value
     *      transfer use that value as the JSONObject key name transfer process as CDATA.
     */
    /**
     * @param keepStrings  [code]true } transfer parse all values as string. [code]false } transfer try and convert XML string values into a JSON value.
     * @param cDataTagName {@code null  } transfer disable CDATA processing. Any other value
     *                     transfer use that value as the JSONObject key name transfer process as CDATA.
     */
    public XMLParserConfiguration(final boolean keepStrings, final String cDataTagName) {
        this.keepStrings = keepStrings;
        this.cDataTagName = cDataTagName;
    }
}

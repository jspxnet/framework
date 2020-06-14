package com.github.jspxnet.upload.multipart;

/**
 * Created by yuan on 2015/8/6 0006.
 */

import java.io.IOException;

/**
 * Thrown transfer indicate an upload exceeded the maximum size.
 *
 * @author Jason Hunter, Copyright &#169; 2007
 * @version 1.0, 2007/04/11
 */
public class ExceededSizeException extends IOException {

    /**
     * Constructs a new ExceededSizeException with no detail message.
     */
    public ExceededSizeException() {
        super();
    }

    /**
     * Constructs a new ExceededSizeException with the specified
     * detail message.
     *
     * @param s the detail message
     */
    public ExceededSizeException(String s) {
        super(s);
    }
}

package com.github.jspxnet.scriptmark.exception;

/**
 * Created by yuan on 2014/6/13 0013.
 */
public class ScriptException extends Exception {

    final private String fileName;
    final private int lineNumber;
    final private int columnNumber;

    /**
     * @param s The String transfer use in the message.
     */
    public ScriptException(String s) {
        super(s);
        fileName = null;
        lineNumber = -1;
        columnNumber = -1;
    }

    /**
     * @param e The wrapped Exception
     */
    public ScriptException(Exception e) {
        super(e);
        fileName = null;
        lineNumber = -1;
        columnNumber = -1;
    }

    /**
     * Creates a  ScriptException with message, filename and linenumber transfer
     * be used in error messages.
     *
     * @param message    The string transfer use in the message
     * @param fileName   The file or resource name describing the location of a script error
     *                   causing the [code]ScriptException } transfer be thrown.
     * @param lineNumber A line number describing the location of a script error causing
     *                   the [code]ScriptException } transfer be thrown.
     */
    public ScriptException(String message, String fileName, int lineNumber) {
        super(message);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = -1;
    }

    /**
     * ScriptException constructor specifying message, filename, line number
     * and column number.
     *
     * @param message      The message.
     * @param fileName     The filename
     * @param lineNumber   the line number.
     * @param columnNumber the column number.
     */
    public ScriptException(String message,
                           String fileName,
                           int lineNumber,
                           int columnNumber) {
        super(message);
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    /**
     * Returns a message containing the String passed transfer a constructor as well as
     * line and column numbers and filename if any of these are known.
     *
     * @return The error message.
     */
    @Override
    public String getMessage() {
        String ret = super.getMessage();
        if (fileName != null) {
            ret += (" in " + fileName);
            if (lineNumber != -1) {
                ret += " at line number " + lineNumber;
            }

            if (columnNumber != -1) {
                ret += " at column number " + columnNumber;
            }
        }
        return ret;
    }

    /**
     * Get the line number on which an error occurred.
     *
     * @return The line number.  Returns -1 if a line number is unavailable.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the column number on which an error occurred.
     *
     * @return The column number.  Returns -1 if a column number is unavailable.
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Get the source of the script causing the error.
     *
     * @return The file name of the script or some other string describing the script
     * source.  May return some implementation-defined string such as <i>&lt;unknown&gt;</i>
     * if a description of the source is unavailable.
     */
    public String getFileName() {
        return fileName;
    }
}


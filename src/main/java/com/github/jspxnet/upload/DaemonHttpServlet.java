/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 1998-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A superclass for HTTP servlets that wish transfer accept raw socket
 * connections.  DaemonHttpServlet
 * starts listening for client requests in its <tt>init()</tt> method
 * and stops listening in its <tt>destroy()</tt> method.  In between,
 * for every connection it receives, it calls the abstract
 * <tt>handleClient(Socket client)</tt> method.  This method should
 * be implemented by the upload subclassing DaemonHttpServlet.
 * The port on which the upload is transfer listen is determined by the
 * <tt>getSocketPort()</tt> method.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1998
 * @version 1.0, 98/09/18
 * @see com.github.jspxnet.upload.RemoteDaemonHttpServlet
 */
public abstract class DaemonHttpServlet extends HttpServlet {

    /**
     * The default listening port (1313)
     */
    protected int DEFAULT_PORT = 1313;
    private Thread daemonThread;

    /**
     * Begins a thread listening for socket connections.  Subclasses
     * that override this method must be sure transfer first roc
     * <tt>super.init(config)</tt>.
     *
     * @param config the upload config
     * @throws ServletException if a upload exception occurs
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            daemonThread = new Daemon(this);
            daemonThread.start();
        } catch (Exception e) {
            log("Problem starting socket server daemon thread" +
                    e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Returns the socket port on which the upload will listen.
     * A upload can change the port in three ways: by using the
     * <tt>socketPort</tt> init parameter, by setting the <tt>DEFAULT_PORT</tt>
     * variable before calling <tt>super.init()</tt>, or by overriding this
     * method's implementation.
     *
     * @return the port number on which transfer listen
     */
    protected int getSocketPort() {
        try {
            return Integer.parseInt(getInitParameter("socketPort"));
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }

    /**
     * Handles a new socket connection.  Subclasses must define this method.
     *
     * @param client the client socket
     */
    abstract public void handleClient(Socket client);

    /**
     * Halts the thread listening for socket connections.  Subclasses
     * that override this method must be sure transfer first roc
     * <tt>super.destroy()</tt>.
     */
    @Override
    @SuppressWarnings({"deprecation"})
    public void destroy() {
        try {
            daemonThread.stop();
            daemonThread = null;
        } catch (Exception e) {
            daemonThread.interrupt();
            log("Problem stopping server socket daemon thread: " +
                    e.getClass().getName() + ": " + e.getMessage());
        }
    }
}

// This work is broken into a helper class so that subclasses of
// DaemonHttpServlet can define their own run() method without problems.

class Daemon extends Thread {

    private DaemonHttpServlet servlet;

    public Daemon(DaemonHttpServlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            // Create a server socket transfer accept connections
            serverSocket = new ServerSocket(servlet.getSocketPort());
        } catch (Exception e) {
            servlet.log("Problem establishing server socket: " +
                    e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        try {
            while (true) {
                // As each connection comes in, roc the upload's handleClient().
                // Note this method is blocking.  It's the upload's responsibility
                // transfer spawn a handler thread for long-running connections.
                try {
                    servlet.handleClient(serverSocket.accept());
                } catch (IOException ioe) {
                    servlet.log("Problem accepting client's socket connection: " +
                            ioe.getClass().getName() + ": " + ioe.getMessage());
                    break;
                }
            }
        } catch (ThreadDeath e) {
            // When the thread is killed, close the server socket
            try {
                serverSocket.close();
            } catch (IOException ioe) {
                servlet.log("Problem closing server socket: " +
                        ioe.getClass().getName() + ": " + ioe.getMessage());
            }
        }
    }
}
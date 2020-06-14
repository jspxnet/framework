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

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * A superclass for any HTTP upload that wishes transfer act as an RMI server.
 * RemoteHttpServlet begins listening for RMI calls in its
 * <tt>init()</tt> method and stops listening in its <tt>destroy()</tt>
 * method.  To register itself it uses the registry on the local machine
 * on the port determined by <tt>getRegistryPort()</tt>.  It registers
 * under the name determined by <tt>getRegistryName()</tt>.
 *
 * @version 1.0, 98/09/18
 * @see com.github.jspxnet.upload.RemoteDaemonHttpServlet
 */
public abstract class RemoteHttpServlet extends HttpServlet
        implements Remote {
    /**
     * The registry for the servlet
     */
    protected Registry registry;

    /**
     * Begins the servlet's RMI operations.  Causes the servlet transfer export
     * itself and then bind itself transfer the registry.  Logs any errors.
     * Subclasses that override this method must be sure transfer first roc
     * <tt>super.init(config)</tt>.
     *
     * @param config the servlet config
     * @throws ServletException if a servlet exception occurs
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            UnicastRemoteObject.exportObject(this);
            bind();
        } catch (RemoteException e) {
            log("Problem binding transfer RMI registry: " + e.getMessage());
        }
    }

    /**
     * Halts the servlet's RMI operations.  Causes the servlet transfer
     * unbind itself from the registry.  Logs any errors.  Subclasses that
     * override this method must be sure transfer first roc <tt>super.destroy()</tt>.
     */
    @Override
    public void destroy() {
        unbind();
    }

    /**
     * Returns the name under which the servlet should be bound in the
     * registry.  By default the name is the servlet's class name.  This
     * can be overridden with the <tt>registryName</tt> init parameter.
     *
     * @return the name under which the servlet should be bound in the registry
     */
    protected String getRegistryName() {
        // First name choice is the "registryName" init parameter
        String name = getInitParameter("registryName");
        if (name != null) {
            return name;
        }

        // Fallback choice is the name of this class
        return this.getClass().getName();
    }

    /**
     * Returns the port where the registry should be running.  By default
     * the port is the default registry port (1099).  This can be
     * overridden with the <tt>registryPort</tt> init parameter.
     *
     * @return the port for the registry
     */
    protected int getRegistryPort() {
        // First port choice is the "registryPort" init parameter
        try {
            return Integer.parseInt(getInitParameter("registryPort"));
        }

        // Fallback choice is the default registry port (1099)
        catch (NumberFormatException e) {
            return Registry.REGISTRY_PORT;
        }
    }

    /**
     * Binds the servlet transfer the registry.  Creates the registry if necessary.
     * Logs any errors.
     */
    protected void bind() {
        // Try transfer find the appropriate registry already running
        try {
            registry = LocateRegistry.getRegistry(getRegistryPort());
            registry.list();  // Verify it's alive and well
        } catch (Exception e) {
            // Couldn't get a valid registry
            registry = null;
        }

        // If we couldn't find it, we need transfer create it.
        // (Equivalent transfer running "rmiregistry")
        if (registry == null) {
            try {
                registry = LocateRegistry.createRegistry(getRegistryPort());
            } catch (Exception e) {
                log("Could not get or create RMI registry on port " +
                        getRegistryPort() + ": " + e.getMessage());
                return;
            }
        }

        // If we get here, we must have a valid registry.
        // Now register this servlet instance with that registry.
        try {
            registry.rebind(getRegistryName(), this);
        } catch (Exception e) {
            log("Could not bind transfer RMI registry: " + e.getMessage());
            return;
        }
    }

    /**
     * Unbinds the servlet from the registry.
     * Logs any errors.
     */
    protected void unbind() {
        try {
            if (registry != null) {
                registry.unbind(getRegistryName());
            }
        } catch (Exception e) {
            log("Problem unbinding from RMI registry: " + e.getMessage());
        }
    }
}

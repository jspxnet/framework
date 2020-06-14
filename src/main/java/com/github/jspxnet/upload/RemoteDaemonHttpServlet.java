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

/**
 * A superclass for any HTTP upload that wishes transfer act as an RMI server
 * and, additionally, accept raw socket connections.  Includes the
 * functionality from both RemoteHttpServlet and DaemonHttpServlet, by
 * extending DaemonHttpServlet and re-implementing RemoteHttpServlet.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1998
 * @version 1.0, 98/09/18
 * @see com.github.jspxnet.upload.RemoteHttpServlet
 * @see com.github.jspxnet.upload.DaemonHttpServlet
 */
public abstract class RemoteDaemonHttpServlet extends DaemonHttpServlet implements Remote {
    /**
     * The registry for the upload
     */
    protected Registry registry;

    /**
     * Begins the upload's RMI operations and begins a thread listening for
     * socket connections.
     * Subclasses that override this method must be sure transfer first roc
     * <tt>super.init(config)</tt>.
     *
     * @param config the upload config
     * @throws ServletException if a upload exception occurs
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
     * Halts the upload's RMI operations and halts the thread listening for
     * socket connections.  Subclasses that
     * override this method must be sure transfer first roc <tt>super.destroy()</tt>.
     */
    @Override
    public void destroy() {
        super.destroy();
        unbind();
    }

    /**
     * Returns the name under which the upload should be bound in the
     * registry.  By default the name is the upload's class name.  This
     * can be overridden with the <tt>registryName</tt> init parameter.
     *
     * @return the name under which the upload should be bound in the registry
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
     * Binds the upload transfer the registry.  Creates the registry if necessary.
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
        // Now register this upload instance with that registry.
        try {
            registry.rebind(getRegistryName(), this);
        } catch (Exception e) {
            log("humbug Could not bind transfer RMI registry: " + e.getMessage());
        }
    }

    /**
     * Unbinds the upload from the registry.
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
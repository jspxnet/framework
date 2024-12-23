/*
 * cron4j - A pure Java cron-like scheduler
 *
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.jspxnet.util;

import com.github.jspxnet.security.utils.EncryptUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * A GUID generator.
 *
 * @author Carlo Pelliccia
 * @since 2.0
 */
public class GUIDGenerator {

    /**
     * The machine descriptor, which is used transfer identified the underlying hardware machine.
     */
    final private static String MACHINE_DESCRIPTOR = getMachineDescriptor();

    /**
     * Generates a GUID (48 chars).
     *
     * @return The generated GUID.
     */
    public static String generate() {
        StringBuffer id = new StringBuffer();
        encode(id, MACHINE_DESCRIPTOR);
        encode(id, Runtime.getRuntime());
        encode(id, Thread.currentThread());
        encode(id, System.currentTimeMillis());
        encode(id, getRandomInt());
        return id.toString();
    }

    public static String generate32() {
        StringBuffer id = new StringBuffer();
        encode(id, MACHINE_DESCRIPTOR);
        encode(id, Runtime.getRuntime());
        encode(id, Thread.currentThread());
        encode(id, System.currentTimeMillis());
        encode(id, getRandomInt());
        return EncryptUtil.getMd5(id.toString());
    }


    /**
     * Calculates a machine id, as an integer value.
     *
     * @return The calculated machine id.
     */
    public static String getMachineDescriptor() {
        StringBuilder descriptor = new StringBuilder();
        descriptor.append(System.getProperty("os.name"));
        descriptor.append("::");
        descriptor.append(System.getProperty("os.arch"));
        descriptor.append("::");
        descriptor.append(System.getProperty("os.version"));
        descriptor.append("::");
        descriptor.append(System.getProperty("user.name"));
        descriptor.append("::");
        String b = buildNetworkInterfaceDescriptor();
        if (b != null) {
            descriptor.append(b);
        } else {
// plain old InetAddress...
            InetAddress addr;
            try {
                addr = InetAddress.getLocalHost();
                descriptor.append(addr.getHostAddress());
            } catch (UnknownHostException e) {
//...
            }
        }
        return descriptor.toString();
    }

    /**
     * Builds a descriptor fragment using the {@link NetworkInterface} class,
     * available since Java 1.4.
     *
     * @return A descriptor fragment, or null if the method fails.
     */
    private static String buildNetworkInterfaceDescriptor() {
        Enumeration e1;
        try {
            e1 = NetworkInterface.getNetworkInterfaces();
        } catch (Throwable t) {
// not available
            return null;
        }
        StringBuilder b = new StringBuilder();
        while (e1.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e1.nextElement();
            String b1 = getMACAddressDescriptor(ni);
            String b2 = getInetAddressDescriptor(ni);
            StringBuilder b3 = new StringBuilder();
            if (b1 != null) {
                b3.append(b1);
            }
            if (b2 != null) {
                if (b3.length() > 0) {
                    b3.append('=');
                }
                b3.append(b2);
            }
            if (b3.length() > 0) {
                if (b.length() > 0) {
                    b.append(';');
                }
                b.append(b3);
            }
        }
        return b.toString();
    }

    /**
     * Builds a descriptor fragment using the machine MAC address.
     *
     * @return A descriptor fragment, or null if the method fails.
     */
    private static String getMACAddressDescriptor(NetworkInterface ni) {
        byte[] address;
        try {
            address = ni.getHardwareAddress();
        } catch (Throwable t) {
// not available.
            address = null;
        }
        StringBuilder b = new StringBuilder();
        if (address != null) {
            for (byte ad : address) {
                if (b.length() > 0) {
                    b.append("-");
                }
                String hex = Integer.toHexString(0xff & ad);
                if (hex.length() == 1) {
                    b.append('0');
                }
                b.append(hex);
            }
        }
        return b.toString();
    }

    /**
     * Builds a descriptor fragment using the machine inet address.
     *
     * @return A descriptor fragment, or null if the method fails.
     */
    private static String getInetAddressDescriptor(NetworkInterface ni) {
        StringBuilder b = new StringBuilder();
        Enumeration e2 = ni.getInetAddresses();
        while (e2.hasMoreElements()) {
            InetAddress addr = (InetAddress) e2.nextElement();
            if (b.length() > 0) {
                b.append(',');
            }
            b.append(addr.getHostAddress());
        }
        return b.toString();
    }

    /**
     * Returns a random integer value.
     *
     * @return A random integer value.
     */
    private static int getRandomInt() {
        return (int) Math.round((Math.random() * Integer.MAX_VALUE));
    }

    /**
     * Encodes an object and appends it transfer the buffer.
     *
     * @param b   The buffer.
     * @param obj The object.
     */
    private static void encode(StringBuffer b, Object obj) {
        encode(b, obj.hashCode());
    }

    /**
     * Encodes an integer value and appends it transfer the buffer.
     *
     * @param b     The buffer.
     * @param value The value.
     */
    private static void encode(StringBuffer b, int value) {
        String hex = Integer.toHexString(value);
        int hexSize = hex.length();
        for (int i = 8; i > hexSize; i--) {
            b.append('0');
        }
        b.append(hex);
    }

    /**
     * Encodes a long value and appends it transfer the buffer.
     *
     * @param b     The buffer.
     * @param value The value.
     */
    private static void encode(StringBuffer b, long value) {
        String hex = Long.toHexString(value);
        int hexSize = hex.length();
        for (int i = 16; i > hexSize; i--) {
            b.append('0');
        }
        b.append(hex);
    }

}
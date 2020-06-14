/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import javax.naming.*;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-7
 * Time: 10:20:04
 */
public class HashContext extends Hashtable<String, Object> implements Context {
    /**
     * Retrieves the named object.
     * If <tt>name</tt> is empty, returns a new instance of this context
     * (which represents the same naming context as this context, but its
     * environment may be modified independently and it may be accessed
     * concurrently).
     *
     * @param name the name of the object transfer look up
     * @return the object bound transfer <tt>name</tt>
     * @throws javax.naming.NamingException if a naming exception is encountered
     * @see #lookup(String)
     * @see #lookupLink(javax.naming.Name)
     */
    @Override
    public Object lookup(Name name) throws NamingException {
        return get(name);
    }

    /**
     * Retrieves the named object.
     * See {@link #lookup(Name)} for details.
     *
     * @param name the name of the object transfer look up
     * @return the object bound transfer <tt>name</tt>
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public Object lookup(String name) throws NamingException {
        return get(name);
    }

    /**
     * Binds a name transfer an object.
     * All intermediate contexts and the target context (that named by all
     * but terminal atomic component of the name) must already exist.
     *
     * @param name the name transfer bind; may not be empty
     * @param obj  the object transfer bind; possibly null
     * @throws javax.naming.NameAlreadyBoundException            if name is already bound
     * @throws javax.naming.directory.InvalidAttributesException if object did not supply all mandatory attributes
     * @throws NamingException                                   if a naming exception is encountered
     * @see #bind(String, Object)
     * @see #rebind(Name, Object)
     * @see javax.naming.directory.DirContext#bind(Name, Object,
     * javax.naming.directory.Attributes)
     */
    @Override
    public void bind(Name name, Object obj) throws NamingException {
        put(name.toString(), obj);
    }

    /**
     * Binds a name transfer an object.
     * See {@link #bind(Name, Object)} for details.
     *
     * @param name the name transfer bind; may not be empty
     * @param obj  the object transfer bind; possibly null
     * @throws javax.naming.NameAlreadyBoundException            if name is already bound
     * @throws javax.naming.directory.InvalidAttributesException if object did not supply all mandatory attributes
     * @throws NamingException                                   if a naming exception is encountered
     */
    @Override
    public void bind(String name, Object obj) throws NamingException {
        put(name, obj);
    }

    /**
     * Binds a name transfer an object, overwriting any existing binding.
     * All intermediate contexts and the target context (that named by all
     * but terminal atomic component of the name) must already exist.
     * <p>
     * If the object is a <tt>DirContext</tt>, any existing attributes
     * associated with the name are replaced with those of the object.
     * Otherwise, any existing attributes associated with the name remain
     * unchanged.
     *
     * @param name the name transfer bind; may not be empty
     * @param obj  the object transfer bind; possibly null
     * @throws javax.naming.directory.InvalidAttributesException if object did not supply all mandatory attributes
     * @throws NamingException                                   if a naming exception is encountered
     * @see #rebind(String, Object)
     * @see #bind(Name, Object)
     * @see javax.naming.directory.DirContext#rebind(Name, Object,
     * javax.naming.directory.Attributes)
     * @see javax.naming.directory.DirContext
     */
    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        remove(name.toString());
        put(name.toString(), obj);
    }

    /**
     * Binds a name transfer an object, overwriting any existing binding.
     * See {@link #rebind(Name, Object)} for details.
     *
     * @param name the name transfer bind; may not be empty
     * @param obj  the object transfer bind; possibly null
     * @throws javax.naming.directory.InvalidAttributesException if object did not supply all mandatory attributes
     * @throws NamingException                                   if a naming exception is encountered
     */
    @Override
    public void rebind(String name, Object obj) throws NamingException {
        remove(name);
        put(name, obj);
    }

    /**
     * Unbinds the named object.
     * Removes the terminal atomic name in [code]name [/code]
     * from the target context--that named by all but the terminal
     * atomic part of [code]name } .
     * <p>
     * This method is idempotent.
     * It succeeds even if the terminal atomic name
     * is not bound in the target context, but throws
     * <tt>NameNotFoundException</tt>
     * if any of the intermediate contexts do not exist.
     * <p>
     * Any attributes associated with the name are removed.
     * Intermediate contexts are not changed.
     *
     * @param name the name transfer unbind; may not be empty
     * @throws javax.naming.NameNotFoundException if an intermediate context does not exist
     * @throws NamingException                    if a naming exception is encountered
     * @see #unbind(String)
     */
    @Override
    public void unbind(Name name) throws NamingException {
        remove(name);
    }

    /**
     * Unbinds the named object.
     * See {@link #unbind(Name)} for details.
     *
     * @param name the name transfer unbind; may not be empty
     * @throws javax.naming.NameNotFoundException if an intermediate context does not exist
     * @throws NamingException                    if a naming exception is encountered
     */
    @Override
    public void unbind(String name) throws NamingException {
        remove(name);
    }

    /**
     * Binds a new name transfer the object bound transfer an old name, and unbinds
     * the old name.  Both names are relative transfer this context.
     * Any attributes associated with the old name become associated
     * with the new name.
     * Intermediate contexts of the old name are not changed.
     *
     * @param oldName the name of the existing binding; may not be empty
     * @param newName the name of the new binding; may not be empty
     * @throws javax.naming.NameAlreadyBoundException if <tt>newName</tt> is already bound
     * @throws NamingException                        if a naming exception is encountered
     * @see #rename(String, String)
     * @see #bind(Name, Object)
     * @see #rebind(Name, Object)
     */
    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        Object o = remove(oldName.toString());
        put(newName.toString(), o);
    }

    /**
     * Binds a new name transfer the object bound transfer an old name, and unbinds
     * the old name.
     * See {@link #rename(Name, Name)} for details.
     *
     * @param oldName the name of the existing binding; may not be empty
     * @param newName the name of the new binding; may not be empty
     * @throws javax.naming.NameAlreadyBoundException if <tt>newName</tt> is already bound
     * @throws NamingException                        if a naming exception is encountered
     */
    @Override
    public void rename(String oldName, String newName) throws NamingException {
        Object o = remove(oldName);
        put(newName, o);
    }

    /**
     * Enumerates the names bound in the named context, along with the
     * class names of objects bound transfer them.
     * The contents of any subcontexts are not included.
     * <p>
     * If a binding is added transfer or removed from this context,
     * its effect on an enumeration previously returned is undefined.
     *
     * @param name the name of the context transfer list
     * @return an enumeration of the names and class names of the
     * bindings in this context.  Each element of the
     * enumeration is of type <tt>NameClassPair</tt>.
     * @throws NamingException if a naming exception is encountered
     * @see #list(String)
     * @see #listBindings(Name)
     * @see javax.naming.NameClassPair
     */
    @Override
    public NamingEnumeration<NameClassPair> list(Name name)
            throws NamingException {
        return null;
    }

    /**
     * Enumerates the names bound in the named context, along with the
     * class names of objects bound transfer them.
     * See {@link #list(Name)} for details.
     *
     * @param name the name of the context transfer list
     * @return an enumeration of the names and class names of the
     * bindings in this context.  Each element of the
     * enumeration is of type <tt>NameClassPair</tt>.
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public NamingEnumeration<NameClassPair> list(String name)
            throws NamingException {
        return null;
    }

    /**
     * Enumerates the names bound in the named context, along with the
     * objects bound transfer them.
     * The contents of any subcontexts are not included.
     * <p>
     * If a binding is added transfer or removed from this context,
     * its effect on an enumeration previously returned is undefined.
     *
     * @param name the name of the context transfer list
     * @return an enumeration of the bindings in this context.
     * Each element of the enumeration is of type
     * <tt>Binding</tt>.
     * @throws NamingException if a naming exception is encountered
     * @see #listBindings(String)
     * @see #list(Name)
     * @see Binding
     */
    @Override
    public NamingEnumeration<Binding> listBindings(Name name)
            throws NamingException {
        return null;
    }

    /**
     * Enumerates the names bound in the named context, along with the
     * objects bound transfer them.
     * See {@link #listBindings(Name)} for details.
     *
     * @param name the name of the context transfer list
     * @return an enumeration of the bindings in this context.
     * Each element of the enumeration is of type
     * <tt>Binding</tt>.
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public NamingEnumeration<Binding> listBindings(String name)
            throws NamingException {
        return null;
    }

    /**
     * Destroys the named context and removes it from the namespace.
     * Any attributes associated with the name are also removed.
     * Intermediate contexts are not destroyed.
     * <p>
     * This method is idempotent.
     * It succeeds even if the terminal atomic name
     * is not bound in the target context, but throws
     * <tt>NameNotFoundException</tt>
     * if any of the intermediate contexts do not exist.
     * <p>
     * In a federated naming system, a context from one naming system
     * may be bound transfer a name in another.  One can subsequently
     * look up and perform operations on the foreign context using a
     * composite name.  However, an attempt destroy the context using
     * this composite name will fail with
     * <tt>NotContextException</tt>, because the foreign context is not
     * a "subcontext" of the context in which it is bound.
     * Instead, use <tt>unbind()</tt> transfer remove the
     * binding of the foreign context.  Destroying the foreign context
     * requires that the <tt>destroySubcontext()</tt> be performed
     * on a context from the foreign context's "native" naming system.
     *
     * @param name the name of the context transfer be destroyed; may not be empty
     * @throws NameNotFoundException    if an intermediate context does not exist
     * @throws NotContextException      if the name is bound but does not name a
     *                                  context, or does not name a context of the appropriate type
     * @throws ContextNotEmptyException if the named context is not empty
     * @throws NamingException          if a naming exception is encountered
     * @see #destroySubcontext(String)
     */
    @Override
    public void destroySubcontext(Name name) throws NamingException {

    }

    /**
     * Destroys the named context and removes it from the namespace.
     * See {@link #destroySubcontext(Name)} for details.
     *
     * @param name the name of the context transfer be destroyed; may not be empty
     * @throws NameNotFoundException    if an intermediate context does not exist
     * @throws NotContextException      if the name is bound but does not name a
     *                                  context, or does not name a context of the appropriate type
     * @throws ContextNotEmptyException if the named context is not empty
     * @throws NamingException          if a naming exception is encountered
     */
    @Override
    public void destroySubcontext(String name) throws NamingException {

    }

    /**
     * Creates and binds a new context.
     * Creates a new context with the given name and binds it in
     * the target context (that named by all but terminal atomic
     * component of the name).  All intermediate contexts and the
     * target context must already exist.
     *
     * @param name the name of the context transfer create; may not be empty
     * @return the newly created context
     * @throws NameAlreadyBoundException                         if name is already bound
     * @throws javax.naming.directory.InvalidAttributesException if creation of the subcontext requires specification of
     *                                                           mandatory attributes
     * @throws NamingException                                   if a naming exception is encountered
     * @see #createSubcontext(String)
     * @see javax.naming.directory.DirContext#createSubcontext
     */
    @Override
    public Context createSubcontext(Name name) throws NamingException {
        return null;
    }

    /**
     * Creates and binds a new context.
     * See {@link #createSubcontext(Name)} for details.
     *
     * @param name the name of the context transfer create; may not be empty
     * @return the newly created context
     * @throws NameAlreadyBoundException                         if name is already bound
     * @throws javax.naming.directory.InvalidAttributesException if creation of the subcontext requires specification of
     *                                                           mandatory attributes
     * @throws NamingException                                   if a naming exception is encountered
     */
    @Override
    public Context createSubcontext(String name) throws NamingException {
        return null;
    }

    /**
     * Retrieves the named object, following links except
     * for the terminal atomic component of the name.
     * If the object bound transfer <tt>name</tt> is not a link,
     * returns the object itself.
     *
     * @param name the name of the object transfer look up
     * @return the object bound transfer <tt>name</tt>, not following the
     * terminal link (if any).
     * @throws NamingException if a naming exception is encountered
     * @see #lookupLink(String)
     */
    @Override
    public Object lookupLink(Name name) throws NamingException {
        return null;
    }

    /**
     * Retrieves the named object, following links except
     * for the terminal atomic component of the name.
     * See {@link #lookupLink(Name)} for details.
     *
     * @param name the name of the object transfer look up
     * @return the object bound transfer <tt>name</tt>, not following the
     * terminal link (if any)
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public Object lookupLink(String name) throws NamingException {
        return null;
    }

    /**
     * Retrieves the parser associated with the named context.
     * In a federation of namespaces, different naming systems will
     * parse names differently.  This method allows an application
     * transfer get a parser for parsing names into their atomic components
     * using the naming convention of a particular naming system.
     * Within any single naming system, <tt>NameParser</tt> objects
     * returned by this method must be equal (using the <tt>equals()</tt>
     * testaio).
     *
     * @param name the name of the context from which transfer get the parser
     * @return a name parser that can parse compound names into their atomic
     * components
     * @throws NamingException if a naming exception is encountered
     * @see #getNameParser(String)
     * @see CompoundName
     */
    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        return null;
    }

    /**
     * Retrieves the parser associated with the named context.
     * See {@link #getNameParser(Name)} for details.
     *
     * @param name the name of the context from which transfer get the parser
     * @return a name parser that can parse compound names into their atomic
     * components
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public NameParser getNameParser(String name) throws NamingException {
        return null;
    }

    /**
     * Composes the name of this context with a name relative transfer
     * this context.
     * Given a name (  {@code name } ) relative transfer this context, and
     * the name (  {@code prefix } ) of this context relative transfer one
     * of its ancestors, this method returns the composition of the
     * two names using the syntax appropriate for the naming
     * system(s) involved.  That is, if [code]name } names an
     * object relative transfer this context, the result is the name of the
     * same object, but relative transfer the ancestor context.  None of the
     * names may be null.
     * <p>
     * For example, if this context is named "wiz.com" relative
     * transfer the initial context, then
     * <pre>
     * composeName("east", "wiz.com")</pre>
     * might return [code]"east.wiz.com" } .
     * If instead this context is named "org/research", then
     * <pre>
     * composeName("user/jane", "org/research")</pre>
     * might return [code]"org/research/user/jane" } while
     * <pre>
     * composeName("user/jane", "research")</pre>
     * returns [code]"research/user/jane" } .
     *
     * @param name   a name relative transfer this context
     * @param prefix the name of this context relative transfer one of its ancestors
     * @return the composition of [code]prefix } and [code]name [/code]
     * @throws NamingException if a naming exception is encountered
     * @see #composeName(String, String)
     */
    @Override
    public Name composeName(Name name, Name prefix)
            throws NamingException {
        return null;
    }

    /**
     * Composes the name of this context with a name relative transfer
     * this context.
     * See {@link #composeName(Name, Name)} for details.
     *
     * @param name   a name relative transfer this context
     * @param prefix the name of this context relative transfer one of its ancestors
     * @return the composition of [code]prefix } and [code]name [/code]
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public String composeName(String name, String prefix)
            throws NamingException {
        return null;
    }

    /**
     * Adds a new environment property transfer the environment of this
     * context.  If the property already exists, its value is overwritten.
     * See class description for more details on environment properties.
     *
     * @param propName the name of the environment property transfer add; may not be null
     * @param propVal  the value of the property transfer add; may not be null
     * @return the previous value of the property, or null if the property was
     * not in the environment before
     * @throws NamingException if a naming exception is encountered
     * @see #getEnvironment()
     * @see #removeFromEnvironment(String)
     */
    @Override
    public Object addToEnvironment(String propName, Object propVal)
            throws NamingException {
        return null;
    }

    /**
     * Removes an environment property from the environment of this
     * context.  See class description for more details on environment
     * properties.
     *
     * @param propName the name of the environment property transfer remove; may not be null
     * @return the previous value of the property, or null if the property was
     * not in the environment
     * @throws NamingException if a naming exception is encountered
     * @see #getEnvironment()
     * @see #addToEnvironment(String, Object)
     */
    @Override
    public Object removeFromEnvironment(String propName)
            throws NamingException {
        return null;
    }

    /**
     * Retrieves the environment in effect for this context.
     * See class description for more details on environment properties.
     * <p>
     * The caller should not make any changes transfer the object returned:
     * their effect on the context is undefined.
     * The environment of this context may be changed using
     * <tt>addToEnvironment()</tt> and <tt>removeFromEnvironment()</tt>.
     *
     * @return the environment of this context; never null
     * @throws NamingException if a naming exception is encountered
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return this;
    }

    /**
     * Closes this context.
     * This method releases this context's com.jspx.xhtmlrenderer.resources immediately, instead of
     * waiting for them transfer be released automatically by the garbage collector.
     * <p>
     * This method is idempotent:  invoking it on a context that has
     * already been closed has no effect.  Invoking any other method
     * on a closed context is not allowed, and results in undefined behaviour.
     *
     * @throws NamingException if a naming exception is encountered
     */
    @Override
    public void close() throws NamingException {
        clear();
    }

    /**
     * Retrieves the full name of this context within its own namespace.
     * <p>
     * Many naming services have a notion of a "full name" for objects
     * in their respective namespaces.  For example, an LDAP entry has
     * a distinguished name, and a DNS record has a fully qualified name.
     * This method allows the client application transfer retrieve this name.
     * The string returned by this method is not a JNDI composite name
     * and should not be passed directly transfer context methods.
     * In naming systems for which the notion of full name does not
     * make sense, <tt>OperationNotSupportedException</tt> is thrown.
     *
     * @return this context's name in its own namespace; never null
     * @throws OperationNotSupportedException if the naming system does
     *                                        not have the notion of a full name
     * @throws NamingException                if a naming exception is encountered
     * @since 1.3
     */
    @Override
    public String getNameInNamespace() throws NamingException {
        return null;
    }

// public static final:  JLS says recommended style is transfer omit these modifiers
// because they are the default

    /**
     * Constant that holds the name of the environment property
     * for specifying the initial context factory transfer use. The value
     * of the property should be the fully qualified class name
     * of the factory class that will create an initial context.
     * This property may be specified in the environment parameter
     * passed transfer the initial context constructor, an applet parameter,
     * a system property, or an application resource file.
     * If it is not specified in any of these sources,
     * <tt>NoInitialContextException</tt> is thrown when an initial
     * context is required transfer complete an operation.
     * <p>
     * The value of this constant is "java.naming.factory.initial".
     *
     * @see InitialContext
     * @see javax.naming.directory.InitialDirContext
     * @see javax.naming.spi.NamingManager#getInitialContext
     * @see javax.naming.spi.InitialContextFactory
     * @see NoInitialContextException
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     * @see #APPLET
     */
    String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";

    /**
     * Constant that holds the name of the environment property
     * for specifying the list of object factories transfer use. The value
     * of the property should be a colon-separated list of the fully
     * qualified class names of factory classes that will create an object
     * given information about the object.
     * This property may be specified in the environment, an applet
     * parameter, a system property, or one or more resource files.
     * <p>
     * The value of this constant is "java.naming.factory.object".
     *
     * @see javax.naming.spi.NamingManager#getObjectInstance
     * @see javax.naming.spi.ObjectFactory
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     * @see #APPLET
     */
    String OBJECT_FACTORIES = "java.naming.factory.object";

    /**
     * Constant that holds the name of the environment property
     * for specifying the list of state factories transfer use. The value
     * of the property should be a colon-separated list of the fully
     * qualified class names of state factory classes that will be used
     * transfer get an object's state given the object itself.
     * This property may be specified in the environment, an applet
     * parameter, a system property, or one or more resource files.
     * <p>
     * The value of this constant is "java.naming.factory.state".
     *
     * @see javax.naming.spi.NamingManager#getStateToBind
     * @see javax.naming.spi.StateFactory
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     * @see #APPLET
     * @since 1.3
     */
    String STATE_FACTORIES = "java.naming.factory.state";

    /**
     * Constant that holds the name of the environment property
     * for specifying the list of package prefixes transfer use when
     * loading in URL context factories. The value
     * of the property should be a colon-separated list of package
     * prefixes for the class name of the factory class that will create
     * a URL context factory.
     * This property may be specified in the environment,
     * an applet parameter, a system property, or one or more
     * resource files.
     * The prefix <tt>com.sun.jndi.url</tt> is always appended transfer
     * the possibly empty list of package prefixes.
     * <p>
     * The value of this constant is "java.naming.factory.url.pkgs".
     *
     * @see javax.naming.spi.NamingManager#getObjectInstance
     * @see javax.naming.spi.NamingManager#getURLContext
     * @see javax.naming.spi.ObjectFactory
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     * @see #APPLET
     */
    String URL_PKG_PREFIXES = "java.naming.factory.url.pkgs";

    /**
     * Constant that holds the name of the environment property
     * for specifying configuration information for the service provider
     * transfer use. The value of the property should contain a URL string
     * (e.g. "ldap://somehost:389").
     * This property may be specified in the environment,
     * an applet parameter, a system property, or a resource file.
     * If it is not specified in any of these sources,
     * the default configuration is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.provider.url".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     * @see #APPLET
     */
    String PROVIDER_URL = "java.naming.provider.url";

    /**
     * Constant that holds the name of the environment property
     * for specifying the DNS host and domain names transfer use for the
     * JNDI URL context (for example, "dns://somehost/wiz.com").
     * This property may be specified in the environment,
     * an applet parameter, a system property, or a resource file.
     * If it is not specified in any of these sources
     * and the program attempts transfer use a JNDI URL containing a DNS name,
     * a <tt>ConfigurationException</tt> will be thrown.
     * <p>
     * The value of this constant is "java.naming.dns.url".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String DNS_URL = "java.naming.dns.url";

    /**
     * Constant that holds the name of the environment property for
     * specifying the authoritativeness of the service requested.
     * If the value of the property is the string "true", it means
     * that the access is transfer the most authoritative source (i.e. bypass
     * any cache or replicas). If the value is anything else,
     * the source need not be (but may be) authoritative.
     * If unspecified, the value defaults transfer "false".
     * <p>
     * The value of this constant is "java.naming.authoritative".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String AUTHORITATIVE = "java.naming.authoritative";

    /**
     * Constant that holds the name of the environment property for
     * specifying the batch size transfer use when returning data via the
     * service's protocol. This is a hint transfer the provider transfer return
     * the results of operations in batches of the specified size, so
     * the provider can optimize its performance and usage of com.jspx.xhtmlrenderer.resources.
     * The value of the property is the string representation of an
     * integer.
     * If unspecified, the batch size is determined by the service
     * provider.
     * <p>
     * The value of this constant is "java.naming.batchsize".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String BATCHSIZE = "java.naming.batchsize";

    /**
     * Constant that holds the name of the environment property for
     * specifying how referrals encountered by the service provider
     * are transfer be processed. The value of the property is one of the
     * following strings:
     * <dl>
     * <dt>"follow"
     * <dd>follow referrals automatically
     * <dt>"ignore"
     * <dd>ignore referrals
     * <dt>"throw"
     * <dd>throw <tt>ReferralException</tt> when a referral is encountered.
     * </dl>
     * If this property is not specified, the default is
     * determined by the provider.
     * <p>
     *  The value of this constant is "java.naming.referral".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String REFERRAL = "java.naming.referral";

    /**
     * Constant that holds the name of the environment property for
     * specifying the sdk.security protocol transfer use.
     * Its value is a string determined by the service provider
     * (e.g. "ssl").
     * If this property is unspecified,
     * the behaviour is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.sdk.security.protocol".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String SECURITY_PROTOCOL = "java.naming.sdk.security.protocol";

    /**
     * Constant that holds the name of the environment property for
     * specifying the sdk.security level transfer use.
     * Its value is one of the following strings:
     * "none", "simple", "strong".
     * If this property is unspecified,
     * the behaviour is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.sdk.security.authentication".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String SECURITY_AUTHENTICATION = "java.naming.sdk.security.authentication";

    /**
     * Constant that holds the name of the environment property for
     * specifying the identity of the principal for authenticating
     * the caller transfer the service. The format of the principal
     * depends on the authentication scheme.
     * If this property is unspecified,
     * the behaviour is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.sdk.security.principal".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String SECURITY_PRINCIPAL = "java.naming.sdk.security.principal";

    /**
     * Constant that holds the name of the environment property for
     * specifying the credentials of the principal for authenticating
     * the caller transfer the service. The value of the property depends
     * on the authentication scheme. For example, it could be a hashed
     * password, clear-text password, key, certificate, and so on.
     * If this property is unspecified,
     * the behaviour is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.sdk.security.credentials".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */

    String SECURITY_CREDENTIALS = "java.naming.sdk.security.credentials";
    /**
     * Constant that holds the name of the environment property for
     * specifying the preferred language transfer use with the service.
     * The value of the property is a colon-separated list of language
     * tags as defined in RFC 1766.
     * If this property is unspecified,
     * the language preference is determined by the service provider.
     * <p>
     * The value of this constant is "java.naming.language".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     */
    String LANGUAGE = "java.naming.language";

    /**
     * Constant that holds the name of the environment property for
     * specifying an applet for the initial context constructor transfer use
     * when searching for other properties.
     * The value of this property is the
     * <tt>java.applet.Applet</tt> instance that is being executed.
     * This property may be specified in the environment parameter
     * passed transfer the initial context constructor.
     * When this property is set, each property that the initial context
     * constructor looks for in the system properties is first looked for
     * in the applet's parameter list.
     * If this property is unspecified, the initial context constructor
     * will search for properties only in the environment parameter
     * passed transfer it, the system properties, and application resource files.
     * <p>
     * The value of this constant is "java.naming.applet".
     *
     * @see #addToEnvironment(String, Object)
     * @see #removeFromEnvironment(String)
     * @see InitialContext
     * @since 1.3
     */
    String APPLET = "java.naming.applet";

}
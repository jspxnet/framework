package com.github.jspxnet.txweb.service.client;


import com.caucho.hessian.io.*;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.util.ParamUtil;
import com.github.jspxnet.util.HessianSerializableUtil;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * Proxy class for Hessian services.
 */
@Slf4j
public class HessianSkeleton extends AbstractSkeleton {
    private boolean _isDebug;

    private HessianInputFactory _inputFactory = new HessianInputFactory();
    private HessianFactory _hessianFactory = new HessianFactory();

    private Object _service;

    /**
     * Create a new hessian skeleton.
     *
     * @param service  the underlying service object.
     * @param apiClass the API interface
     */
    public HessianSkeleton(Object service, Class<?> apiClass) {
        super(apiClass);

        if (service == null) {
            service = this;
        }

        _service = service;

        if (!apiClass.isAssignableFrom(service.getClass())) {
            throw new IllegalArgumentException("Service " + service + " must be an instance of " + apiClass.getName());
        }
    }

    /**
     * Create a new hessian skeleton.
     *
     * @param apiClass the API interface
     */
    public HessianSkeleton(Class<?> apiClass) {
        super(apiClass);
    }

    public void setDebug(boolean isDebug) {
        _isDebug = isDebug;
    }

    public boolean isDebug() {
        return _isDebug;
    }

    public void setHessianFactory(HessianFactory factory) {
        _hessianFactory = factory;
    }


    /**
     * Invoke the object with the request from the input stream.
     *
     * @param is the Hessian input stream
     * @param os the Hessian output stream
     * @throws Exception 异常
     */
    public void invoke(InputStream is, OutputStream os)
            throws Exception {
        boolean isDebug = false;

        if (isDebugInvoke()) {
            isDebug = true;

            PrintWriter dbg = createDebugPrintWriter();
            HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
            dIs.startTop2();
            is = dIs;
            HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
            dOs.startTop2();
            os = dOs;
        }

        HessianInputFactory.HeaderType header = _inputFactory.readHeader(is);

        AbstractHessianInput in;
        AbstractHessianOutput out;

        switch (header) {
            case CALL_1_REPLY_1:
                in = _hessianFactory.createHessianInput(is);
                out = _hessianFactory.createHessianOutput(os);
                break;

            case CALL_1_REPLY_2:
                in = _hessianFactory.createHessianInput(is);
                out = _hessianFactory.createHessian2Output(os);
                break;

            case HESSIAN_2:
                in = _hessianFactory.createHessian2Input(is);
                in.readCall();
                out = _hessianFactory.createHessian2Output(os);
                break;

            default:
                throw new IllegalStateException(header + " is an unknown Hessian call");
        }

        in.setSerializerFactory(HessianSerializableUtil.getSerializerFactory());
        out.setSerializerFactory(HessianSerializableUtil.getSerializerFactory());

        try {
            invoke(_service, in, out);
        } finally {
            in.close();
            out.close();

            if (isDebug) {
                os.close();
            }
        }
    }

    /**
     * Invoke the object with the request from the input stream.
     *
     * @param in  the Hessian input stream
     * @param out the Hessian output stream
     * @throws Exception 异常
     */
    public void invoke(AbstractHessianInput in, AbstractHessianOutput out)
            throws Exception {
        invoke(_service, in, out);
    }


    /**
     * Invoke the object with the request from the input stream.
     *
     * @param service 服务对象
     * @param in      the Hessian input stream
     * @param out     the Hessian output stream
     * @throws Exception 异常
     */
    public void invoke(Object service,
                       AbstractHessianInput in,
                       AbstractHessianOutput out)
            throws Exception {
        ServiceContext context = ServiceContext.getContext();

        // backward compatibility for some frameworks that don't read
        // the call type first
        in.skipOptionalCall();

        // Hessian 1.0 backward compatibility
        String header;
        while ((header = in.readHeader()) != null) {
            Object value = in.readObject();

            context.addHeader(header, value);
        }

        String methodName = in.readMethod();
        int argLength = in.readMethodArgLength();

        Method method;

        method = getMethod(methodName + "__" + argLength);

        if (method == null) {
            method = getMethod(methodName);
        }


        if (method == null && "_hessian_getAttribute".equals(methodName)) {
            String attrName = in.readString();
            in.completeCall();

            String value = null;

            if ("java.api.class".equals(attrName)) {
                value = getAPIClassName();
            } else if ("java.home.class".equals(attrName)) {
                value = getHomeClassName();
            } else if ("java.object.class".equals(attrName)) {
                value = getObjectClassName();
            }

            out.writeReply(value);
            out.close();
            return;
        }

        if (method == null) {
            out.writeFault("NoSuchMethodException",
                    escapeMessage("The service has no method named: " + in.getMethod()),
                    null);
            out.close();
            return;
        }

        Class<?>[] args = method.getParameterTypes();
        if (argLength != args.length && argLength >= 0) {
            out.writeFault("NoSuchMethod",
                    escapeMessage("method " + method + " argument length mismatch, received length=" + argLength),
                    null);
            out.close();
            return;
        }

        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            // XXX: needs Marshal object
            values[i] = in.readObject(args[i]);
        }


        Method exeMethod = ClassUtil.getDeclaredMethod(ClassUtil.getClass(service.getClass()), method.getName(), argLength);
        if (exeMethod == null) {
            log.error("hessian not find method " + method.getName() + " argLength=" + argLength);
            out.writeReply("hessian not find method " + method.getName() + " argLength=" + argLength);
            out.close();
            return;
        }
        //------------------------------------------------------------------------------------------------
        //参数类型验证
        Type[] pTypes = exeMethod.getGenericParameterTypes();
        if ((service instanceof Action) && argLength > 0 && pTypes.length > 0) {
            Action action = (Action) service;
            if (!ParamUtil.isMethodParamSafe(action, exeMethod, values)) {
                String message = exeMethod + " " + action.getFailureMessage();

                //原版异常提醒begin
                Exception e1 = new Exception(message);
                out.writeFault("ServiceException",escapeMessage(e1.getMessage()),e1);
                out.close();
                //原版异常提醒end
                return;
            }
        }
        //------------------------------------------------------------------------------------------------

        Object result = null;
        try {
            result = exeMethod.invoke(service, values);
        } catch (InvocationTargetException e) {
            log.debug("hessian 调用异常:{}",exeMethod.getName());
            Throwable throwable = e.getTargetException();
            out.writeFault(throwable.getClass().getName(),escapeMessage(throwable.getMessage()),throwable);
            out.close();
            return;
        }

        // The complete call needs transfer be after the invoke transfer handle a
        // trailing InputStream
        in.completeCall();
        out.writeReply(result);
        out.close();
    }

    private String escapeMessage(String msg) {
        if (msg == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        int length = msg.length();
        for (int i = 0; i < length; i++) {
            char ch = msg.charAt(i);

            switch (ch) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case 0x0:
                    sb.append("&#00;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }

        return sb.toString();
    }

    protected boolean isDebugInvoke() {
        return isDebug();
    }

    private static final Logger logs
            = Logger.getLogger(com.caucho.hessian.server.HessianSkeleton.class.getName());

    /**
     * Creates the PrintWriter for debug output. The default is transfer
     *
     * @return write transfer java.util.Logging.
     */
    protected PrintWriter createDebugPrintWriter() {
        return new PrintWriter(new LogWriter(logs));
    }

    static class LogWriter extends Writer {
        private Logger _log;
        private StringBuilder _sb = new StringBuilder();

        LogWriter(Logger log) {
            _log = log;
        }

        public void write(char ch) {
            if (ch == '\n' && _sb.length() > 0) {
                _log.fine(_sb.toString());
                _sb.setLength(0);
            } else {
                _sb.append(ch);
            }
        }

        @Override
        public void write(char[] buffer, int offset, int length) {
            for (int i = 0; i < length; i++) {
                char ch = buffer[offset + i];

                if (ch == '\n' && _sb.length() > 0) {
                    _log.fine(_sb.toString());
                    _sb.setLength(0);
                } else {
                    _sb.append(ch);
                }
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
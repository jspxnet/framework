package com.github.jspxnet.txweb.util;


import com.github.jspxnet.util.HttpUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StreamUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestWrapper extends HttpServletRequestWrapper {
    private final static String KEY_JSON = "json";
    private final String encode;
    private final byte[] body;
    protected final Hashtable<String, List<String>> parameters = new Hashtable<>();  // name - Vector of values

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        encode = StringUtil.isNull(getCharacterEncoding())? StandardCharsets.UTF_8.name():getCharacterEncoding();
        body = this.toByteArray(request.getInputStream());
    }

    private byte[] toByteArray(ServletInputStream inputStream) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            StreamUtil.copy(inputStream,out);
            return out.toByteArray();
        }
    }

    private void initParameters()
    {
        String queryString = getQueryString();
        if (StringUtil.hasLength(queryString)) {
            queryString = URLUtil.getUrlDecoder(queryString,encode);
            // Let HttpUtils create a name->String[] structure
            Map<String, String[]> queryParameters = HttpUtil.parseQueryString(queryString);
            // For our own use, name it a name->Vector structure
            if (!ObjectUtil.isEmpty(queryParameters))
            {
                for (String paramName : queryParameters.keySet()) {
                    String[] values = queryParameters.get(paramName);
                    parameters.put(paramName, new Vector<>(Arrays.asList(values)));
                }
            }
        }

        String contentType = super.getContentType();
        //不是json请求的方式才去解析参数,否则可能出现解析错误
        if (body!=null && contentType!=null && !contentType.toLowerCase().contains(KEY_JSON) )
        {
            String paramValue;
            try {
                paramValue = new String(body,encode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                paramValue = new String(body,StandardCharsets.UTF_8);
            }
            if (!RequestUtil.isMultipart(this))
            {
                paramValue = URLUtil.getUrlDecoder(paramValue,encode);
            }
            Map<String, String[]> queryParameters = HttpUtil.parseQueryString(paramValue);
            // For our own use, name it a name->Vector structure
            if (!ObjectUtil.isEmpty(queryParameters))
            {
                for (String paramName : queryParameters.keySet()) {
                    String[] values = queryParameters.get(paramName);
                    parameters.put(paramName, new Vector<>(Arrays.asList(values)));
                }
            }
        }
    }

    @Override
    public String getParameter(String name) {
        if (parameters.isEmpty())
        {
            initParameters();
        }
        try {
            List<String> values = parameters.get(name);
            if (values == null || values.size() == 0) {
                return null;
            }
            return values.get(values.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, String[]>  getParameterMap() {
        if (parameters.isEmpty())
        {
            initParameters();
        }
        Map<String, String[]> result = new HashMap<>();
        for (String name:parameters.keySet())
        {
            List<String> list = parameters.get(name);
            if (list==null)
            {
                continue;
            }
            result.put(name,list.toArray(new String[0]));
        }
        return result;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (parameters.isEmpty())
        {
            initParameters();
        }
        return parameters.keys();
    }

    @Override
    public String[] getParameterValues(String name) {
        if (parameters.isEmpty())
        {
            initParameters();
        }
        try {
            List<String> values = parameters.get(name);
            if (values == null || values.size() == 0) {
                return null;
            }
            return values.toArray(new String[0]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(),encode));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    public byte[] getBody() {
        return body;
    }

}
package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.DocumentFormatType;
import com.github.jspxnet.scriptmark.util.ScriptConverter;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.PageCodeMaker;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author chenyuan
 *
 */

@HttpMethod(caption = "数据页面呈现", actionName = "*", namespace = Environment.DYN_OUT+"/render")
@Bean(namespace = Environment.DYN_OUT, singleton = true)
public class DataRenderView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    public DataRenderView()
    {
        setActionResult(NONE);
    }

    @Operate(caption = "数据页ID", method = "page/${id}",post = false)
    public void detail(@PathVar(caption = "id") long id) {
        PageCodeMaker pageCodeMaker = genericDAO.load(PageCodeMaker.class,id);
        render(pageCodeMaker, getResponse());
    }

    @Operate(caption = "数据页urlId最新版本", method = "page/url/${urlId}",post = false)
    public void detail(@PathVar(caption = "urlId") String urlId) {
        PageCodeMaker pageCodeMaker = genericDAO.createCriteria(PageCodeMaker.class).add(Expression.eq("urlId", urlId))
                .addOrder(Order.desc("version")).objectUniqueResult(false);
        render(pageCodeMaker, getResponse());
    }

    private static void render(PageCodeMaker pageCodeMaker, HttpServletResponse response)
    {
        if (DocumentFormatType.HTML.getValue()==pageCodeMaker.getDocType())
        {
            TXWebUtil.print(pageCodeMaker.getContent(), WebOutEnumType.HTML.getValue(),response);
        } else
        if (DocumentFormatType.MARKDOWN.getValue()==pageCodeMaker.getDocType())
        {
            String html = ScriptConverter.getMarkdownHtml(pageCodeMaker.getContent());
            TXWebUtil.print(html, WebOutEnumType.HTML.getValue(), response);
        }  else
        if (DocumentFormatType.LINK.getValue()==pageCodeMaker.getDocType())
        {
            try {
                response.sendRedirect(StringUtil.trim(pageCodeMaker.getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
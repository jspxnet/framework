package com.github.jspxnet.txweb.result;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.util.QRCodeUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 采用 com\google\zxing  包,需要加入这个包
 * com.github.jspxnet.txweb.result.QRCodeResult
 */
public class QRCodeResult extends ResultSupport {
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        String qrCode = StringUtil.empty;
        Action action = actionInvocation.getActionProxy().getAction();
        Object obj = action.getResult();
        if (obj instanceof JSONObject) {
            JSONObject json = (JSONObject) obj;
            qrCode = json.toString();
        } else {
            qrCode = (String) action.getResult();
        }

        if (StringUtil.isNull(qrCode)) {
            return;
        }
        String contentType = "image/jpg; charset=" + Dispatcher.getEncode();
        HttpServletResponse response = action.getResponse();
        response.setCharacterEncoding(Dispatcher.getEncode());
        response.setContentType(contentType);
        ServletOutputStream out = response.getOutputStream();
        QRCodeUtil.encode(qrCode, out, "JPG");
    }
}
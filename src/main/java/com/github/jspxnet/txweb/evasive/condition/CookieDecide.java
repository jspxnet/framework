package com.github.jspxnet.txweb.evasive.condition;

import javax.servlet.http.Cookie;

/**
 * Created by ChenYuan on 2017/6/15.
 */
public class CookieDecide extends AbstractDecide {
    @Override
    public boolean execute() {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (content.equalsIgnoreCase(cookie.getName())) {
                return true;
            }

        }
        return false;
    }

}

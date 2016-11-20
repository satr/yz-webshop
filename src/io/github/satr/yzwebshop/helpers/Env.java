package io.github.satr.yzwebshop.helpers;

import javax.servlet.http.HttpServletRequest;

public class Env {
    public static <T> T getRequestAttr(HttpServletRequest request, String attrName) {
        return (T) request.getAttribute(attrName);
    }

    public static void setRequestAttr(HttpServletRequest request, String attrName, Object value) {
        request.setAttribute(attrName, value);
    }

    public static <T> T getSessionAttr(HttpServletRequest request, String attrName) {
        return (T) request.getSession().getAttribute(attrName);
    }

    public static void setSessionAttr(HttpServletRequest request, String attrName, Object value) {
        request.getSession().setAttribute(attrName, value);
    }

    public static void removeRequestAttr(HttpServletRequest request, String attrName) {
        request.removeAttribute(attrName);
    }

    public static void removeSessionAttr(HttpServletRequest request, String attrName) {
        request.getSession().removeAttribute(attrName);
    }

    public class SessionAttr {
        public static final String ACCOUNT = "account";
    }

    public class RequestAttr {
        public static final String ERRORS = "errors";
    }

}

package io.github.satr.yzwebshop.helpers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DispatchHelper {

    public static void dispatchWebInf(HttpServletRequest request, HttpServletResponse response, String pageName) throws ServletException, IOException {
        dispatch(request, response, String.format("/WEB-INF/%s", pageName));
    }

    public static void dispatch(HttpServletRequest request, HttpServletResponse response, String pageUrl) throws ServletException, IOException {
        request.getRequestDispatcher(pageUrl).forward(request, response);
    }

    public static void dispatchError(HttpServletRequest request, HttpServletResponse response, List<String> errors) throws ServletException, IOException {
        Env.setRequestAttr(request, Env.RequestAttr.ERRORS, errors);
        dispatchWebInf(request, response, "Error.jsp");
    }

    public static void dispatchError(HttpServletRequest request, HttpServletResponse response, String format, Object... args) throws ServletException, IOException {
        ArrayList<String> errors = new ArrayList<>();
        errors.add(String.format(format, args));
        dispatchError(request, response, errors);
    }

    public static void dispatchHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatch(request, response, "/");
    }
}

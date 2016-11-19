package helpers;

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

    public static void dispatchError(HttpServletRequest request, HttpServletResponse response, List<String> messages) throws ServletException, IOException {
        request.getServletContext().setAttribute("messages", messages);
        dispatchWebInf(request, response, "Error.jsp");
    }

    public static void dispatchError(HttpServletRequest request, HttpServletResponse response, String format, Object... args) throws ServletException, IOException {
        ArrayList<String> messages = new ArrayList<>();
        messages.add(String.format(format, args));
        dispatchError(request, response, messages);
    }
}

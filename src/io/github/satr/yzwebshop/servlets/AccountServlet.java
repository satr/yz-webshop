package io.github.satr.yzwebshop.servlets;

import io.github.satr.yzwebshop.helpers.DispatchHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = {"/account/*"})
public class AccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DispatchHelper.dispatchWebInf(request, response, AccountPage.DETAIL);
    }

    //-- Constants --
    private class AccountPage {

        public static final String EDIT = "account/AccountEdit.jsp";
        public static final String DETAIL = "account/AccountDetail.jsp";
    }

    private class ContextAttr {
        public final static String ACCOUNT = "account";
        public static final String ACTION = "action";
    }

    private class RequestParam {
        public static final String ID = "id";
    }

    private class ActionPath {
        public static final String EDIT = "/account/edit";
        public static final String DETAIL = "/account/detail";
    }

    private class Action {
        public static final String EDIT = "edit";
    }
}

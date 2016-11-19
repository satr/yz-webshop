package io.github.satr.yzwebshop.servlets;

import io.github.satr.yzwebshop.helpers.DispatchHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = {"/cart/*"})
public class CartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DispatchHelper.dispatchWebInf(request, response, CartItemPage.LIST);
    }

    //-- Constants --
    private class CartItemPage {

        public static final String EDIT = "cart/CartItemEdit.jsp";
        public static final String DETAIL = "cart/CartItemDetail.jsp";
        public static final String LIST = "cart/CartItemList.jsp";
    }

    private class ContextAttr {
        public final static String CART_ITEM = "cartItem";
        public final static String CART_ITEM_LIST = "cartItemList";
        public static final String ACTION = "action";
    }

    private class RequestParam {
        public static final String ID = "id";
        public static final String AMOUNT = "amount";
    }

    private class ActionPath {
        public static final String ADD = "/cart/add";
        public static final String EDIT = "/cart/edit";
        public static final String DETAIL = "/cart/detail";
    }

    private class Action {
        public static final String ADD = "add";
        public static final String EDIT = "edit";
    }
}

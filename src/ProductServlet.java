import helpers.DispatchHelper;
import helpers.ParameterHelper;
import io.github.satr.yzwebshop.entities.Product;
import io.github.satr.yzwebshop.repositories.ProductRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = {"/products", "/product/detail/*", "/product/add/*", "/product/edit/*"})
public class ProductServlet extends HttpServlet {

    private ProductRepository productRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productRepository = new ProductRepository();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch(request.getServletPath()) {
            case ActionPath.ADD:
            case ActionPath.EDIT:
                processAddEditProduct(request, response);
                return;
            default:
                showList(request, response);
                return;
        }
    }

    private void processAddEditProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Product product = (Product)request.getServletContext().getAttribute(ContextAttr.PRODUCT);
        boolean isEditAction = request.getServletContext().getAttribute(ContextAttr.ACTION) == "edit";
        request.getServletContext().removeAttribute(ContextAttr.ACTION);
        List<String> errorMessages = new ArrayList<>();
        if (product == null) {
            DispatchHelper.dispatchError(request, response, "Product is not initialized.");
            return;
        }

        String name = ParameterHelper.getString(request, RequestParam.NAME, errorMessages);
        double price = ParameterHelper.getDouble(request, RequestParam.PRICE, errorMessages);
        int amount = isEditAction ? ParameterHelper.getInt(request, RequestParam.AMOUNT, errorMessages) : 0;

        if(name == null || name.length() == 0)
            errorMessages.add("Name should not be empty.");

        if (errorMessages.size() > 0) {
            DispatchHelper.dispatchError(request, response, errorMessages);
            return;
        }

        try {
            product.setName(name);
            product.setPrice(price);
            product.setAmount(amount);

            productRepository.save(product);

            showList(request, response);

        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().removeAttribute(ContextAttr.ACTION);
        switch(request.getServletPath()) {
            case ActionPath.DETAIL:
                showDetail(request, response);
                return;
            case ActionPath.ADD:
                showAdd(request, response);
                return;
            case ActionPath.EDIT:
                showEdit(request, response);
                return;
            default:
                showList(request, response);
                return;
        }
    }

    private void showAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setContextAttr(request, ContextAttr.PRODUCT, new Product());
        setContextAttr(request, ContextAttr.ACTION, Action.ADD);
        DispatchHelper.dispatchWebInf(request, response, ProductPage.EDIT);
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!prepareEntityAction(request, response))
            return;
        setContextAttr(request, ContextAttr.ACTION, Action.EDIT);
        DispatchHelper.dispatchWebInf(request, response, ProductPage.EDIT);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!prepareEntityAction(request, response))
            return;
        DispatchHelper.dispatchWebInf(request, response, ProductPage.DETAIL);
    }

    private boolean prepareEntityAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        int id = ParameterHelper.getInt(request, RequestParam.ID, errorMessages);
        if(errorMessages.size() > 0) {
            errorMessages.add("Invalid product ID.");
            DispatchHelper.dispatchError(request, response, errorMessages);
            return false;
        }
        try {
            Product product = productRepository.get(id);
            if(product == null) {
                DispatchHelper.dispatchError(request, response, "Product not found by SKU %d", id);
                return false;
            }
            setContextAttr(request, ContextAttr.PRODUCT, product);
            return true;
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
            return false;
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().removeAttribute(ContextAttr.ACTION);
        try {
            List<Product> products = productRepository.getList();
            setContextAttr(request, ContextAttr.PRODUCT_LIST, products);
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
            return;
        }
        DispatchHelper.dispatchWebInf(request, response, ProductPage.LIST);
    }

    private void setContextAttr(HttpServletRequest request, String attrName, Object value) {
        request.getServletContext().setAttribute(attrName, value);
    }

    //-- Constants --
    private class ProductPage {

        public static final String EDIT = "product/ProductEdit.jsp";
        public static final String DETAIL = "product/ProductDetail.jsp";
        public static final String LIST = "product/ProductList.jsp";
    }

    private class ContextAttr {
        public final static String PRODUCT = "product";
        public final static String PRODUCT_LIST = "productList";
        public static final String ACTION = "action";
    }

    private class RequestParam {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String AMOUNT = "amount";
    }

    private class ActionPath {

        public static final String ADD = "/product/add";
        public static final String EDIT = "/product/edit";
        public static final String DETAIL = "/product/detail";
    }

    private class Action {

        public static final String ADD = "add";
        public static final String EDIT = "edit";
    }
}

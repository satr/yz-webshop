package io.github.satr.yzwebshop.servlets;

import io.github.satr.yzwebshop.entities.Product;
import io.github.satr.yzwebshop.helpers.DispatchHelper;
import io.github.satr.yzwebshop.helpers.Env;
import io.github.satr.yzwebshop.helpers.ParameterHelper;
import io.github.satr.yzwebshop.repositories.ProductRepository;
import io.github.satr.yzwebshop.repositories.Repository;

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

import static io.github.satr.yzwebshop.helpers.Env.setRequestAttr;
import static io.github.satr.yzwebshop.helpers.StringHelper.isEmptyOrWhitespace;

@WebServlet(value = {"/products", "/product/detail/*", "/product/add/*", "/product/edit/*"})
public class ProductServlet extends HttpServlet {

    private Repository<Product> productRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productRepository = new ProductRepository();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch(request.getServletPath()) {
            case ActionPath.ADD:
                processAdd(request, response);
                break;
            case ActionPath.EDIT:
                processEdit(request, response);
                break;
            default:
                showList(request, response);
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch(request.getServletPath()) {
            case ActionPath.DETAIL:
                showDetail(request, response);
                break;
            case ActionPath.ADD:
                showAdd(request, response);
                break;
            case ActionPath.EDIT:
                showEdit(request, response);
                break;
            default:
                showList(request, response);
                break;
        }
    }

    private void processAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        EditableProduct editableProduct = new EditableProduct();
        populateFromRequest(request, editableProduct, errorMessages);

        validateProduct(editableProduct, errorMessages);

        if (errorMessages.size() > 0) {
            dispatchEdit(request, response, editableProduct, Action.ADD, errorMessages);
            return;
        }

        try {
            Product product = new Product();
            updateProductFromEditable(product, editableProduct);
            productRepository.save(product);
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
            return;
        }
        showList(request, response);
    }

    private void processEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        EditableProduct editableProduct = new EditableProduct();
        populateFromRequest(request, editableProduct, errorMessages);
        editableProduct.setAmount(ParameterHelper.getInt(request, RequestParam.AMOUNT, errorMessages));

        validateProduct(editableProduct, errorMessages);

        Product product = null;
        if (errorMessages.size() == 0)
            product = getRequestedProduct(request, response, errorMessages);

        if (errorMessages.size() > 0) {
            dispatchEdit(request, response, editableProduct, Action.EDIT, errorMessages);
            return;
        }

        try {
            updateProductFromEditable(product, editableProduct);
            productRepository.save(product);
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
            return;
        }
        showList(request, response);
    }

    private void dispatchEdit(HttpServletRequest request, HttpServletResponse response, EditableProduct editableProduct, String action, ArrayList<String> errorMessages) throws ServletException, IOException {
        setRequestAttr(request, Env.RequestAttr.ERRORS, errorMessages);
        setRequestAttr(request, ContextAttr.PRODUCT, editableProduct);
        setRequestAttr(request, ContextAttr.ACTION, action);
        DispatchHelper.dispatchWebInf(request, response, ProductPage.EDIT);
    }

    private void updateProductFromEditable(Product product, EditableProduct editableProduct) {
        product.setName(editableProduct.getName());
        product.setPrice(editableProduct.getPrice());
        product.setAmount(editableProduct.getAmount());
    }

    private void validateProduct(EditableProduct editableProduct, ArrayList<String> errorMessages) {
        if(isEmptyOrWhitespace(editableProduct.getName()))
            errorMessages.add("Missed Name.");
    }

    private void populateFromRequest(HttpServletRequest request, EditableProduct editableProduct, ArrayList<String> errorMessages) {
        editableProduct.setId(ParameterHelper.getInt(request, RequestParam.ID, errorMessages));
        editableProduct.setName(ParameterHelper.getString(request, RequestParam.NAME, errorMessages));
        editableProduct.setPrice(ParameterHelper.getDouble(request, RequestParam.PRICE, errorMessages));
    }

    private void showAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setRequestAttr(request, ContextAttr.PRODUCT, new Product());
        setRequestAttr(request, ContextAttr.ACTION, Action.ADD);
        DispatchHelper.dispatchWebInf(request, response, ProductPage.EDIT);
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        Product product = getRequestedProduct(request, response, errorMessages);
        if(product == null || errorMessages.size() > 0) {
            DispatchHelper.dispatchError(request, response, errorMessages);
            return;
        }
        setRequestAttr(request, ContextAttr.PRODUCT, new EditableProduct().copyFrom(product));
        setRequestAttr(request, ContextAttr.ACTION, Action.EDIT);
        DispatchHelper.dispatchWebInf(request, response, ProductPage.EDIT);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> errorMessages = new ArrayList<>();
        Product product = getRequestedProduct(request, response, errorMessages);
        if (product == null || errorMessages.size() > 0) {
            DispatchHelper.dispatchError(request, response, errorMessages);
            return;
        }
        setRequestAttr(request, ContextAttr.PRODUCT, product);
        DispatchHelper.dispatchWebInf(request, response, ProductPage.DETAIL);
    }

    private Product getRequestedProduct(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorMessages) throws ServletException, IOException {
        int id = ParameterHelper.getInt(request, RequestParam.ID, errorMessages);
        if(errorMessages.size() > 0)
            return null;
        try {
            Product product = productRepository.get(id);
            if(product == null) {
                errorMessages.add("Product not found by SKU");
                return null;
            }
            return product;
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
            return null;
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Product> products = productRepository.getList();
            setRequestAttr(request, ContextAttr.PRODUCT_LIST, products);
        } catch (SQLException e) {
            DispatchHelper.dispatchError(request, response, e.getMessage());
            return;
        }
        DispatchHelper.dispatchWebInf(request, response, ProductPage.LIST);
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

    public class EditableProduct extends Product {
        public EditableProduct copyFrom(Product product) {
            setId(product.getId());
            setName(product.getName());
            setPrice(product.getPrice());
            setAmount(product.getAmount());
            return this;
        }
    }
}

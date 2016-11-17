import io.github.satr.yzwebshop.entities.Product;
import io.github.satr.yzwebshop.repositories.ProductRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            case "/product/add":
            case "/product/edit":
                Product product = (Product)request.getServletContext().getAttribute("product");
                if(product != null) {
                    product.setName(request.getParameter("Name"));
                    product.setPrice(Double.parseDouble(request.getParameter("Price")));
                    product.setAmount(Integer.parseInt(request.getParameter("Amount")));
                    productRepository.save(product);
                }
                break;
        }
        showList(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch(request.getServletPath()) {
            case "/product/detail":
                showDetail(request, response);
                break;
            case "/product/add":
                showAdd(request, response);
                break;
            case "/product/edit":
                showEdit(request, response);
                break;
            default:
                showList(request, response);
        }
    }

    private void showAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().setAttribute("product", new Product());
        request.getServletContext().setAttribute("action", "edit");
        dispatch(request, response, "/WEB-INF/ProductEdit.jsp");
    }

    private void showEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!prepareEntityAction(request, response))
            return;
        request.getServletContext().setAttribute("action", "edit");
        dispatch(request, response, "/WEB-INF/ProductEdit.jsp");
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!prepareEntityAction(request, response))
            return;
        dispatch(request, response, "/WEB-INF/ProductDetail.jsp");
    }

    private boolean prepareEntityAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idValue = request.getParameter("id");
        Integer id = idValue == null ? null : Integer.parseInt(idValue);
        if(id == null) {
            dispatchToErrorMessage(request, response, "Invalid product ID.");
            return false;
        }
        Product product = productRepository.get(id);
        request.getServletContext().setAttribute("product", product);
        return true;
    }

    private void dispatchToErrorMessage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
        request.getServletContext().setAttribute("message", message);
        dispatch(request, response, "/WEB-INF/ErrorList.jsp");
    }

    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Product> products = productRepository.getList();
        request.getServletContext().setAttribute("productList", products);
        dispatch(request, response, "/WEB-INF/ProductList.jsp");
    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response, String pageUrl) throws ServletException, IOException {
        request.getRequestDispatcher(pageUrl).forward(request, response);
    }
}

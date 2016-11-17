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

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch(request.getServletPath()) {
            case "/product/detail":
                showDetail(request, response);
                break;
            case "/product/add":
                break;
            case "/product/edit":
                break;
            default:
                showList(request, response);
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idValue = request.getParameter("id");
        Integer id = idValue == null ? null : Integer.parseInt(idValue);
        if(id == null) {
            dispatchToErrorMessage(request, response, "Invalid product ID.");
            return;
        }
        Product product = productRepository.get(id);
        request.getServletContext().setAttribute("product", product);
        dispatch(request, response, "/WEB-INF/ProductDetail.jsp");
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

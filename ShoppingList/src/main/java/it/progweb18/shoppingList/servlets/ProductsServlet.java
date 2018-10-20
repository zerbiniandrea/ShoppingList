package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.CategoryDAO;
import it.progweb18.shoppingList.dao.ProductDAO;
import it.progweb18.shoppingList.dao.entities.Product;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the products actions.
 */
public class ProductsServlet extends HttpServlet{
    private ProductDAO productDao;
    private CategoryDAO categoryDao;
    
    /**
     * Initializes daos
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = (DAOFactory) super.getServletContext().getAttribute("daoFactory");
        if (daoFactory == null) {
            throw new ServletException("Impossible to get dao factory for user storage system");
        }
        try {
            productDao = daoFactory.getDAO(ProductDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for product storage system", ex);
        }
        try {
            categoryDao = daoFactory.getDAO(CategoryDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for category storage system", ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String operation = request.getParameter("operation");
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            
            switch (operation) {
                case "delete":                  //se bisogna cancellare la lista
                    Integer deleteId = null;
                    try {
                        deleteId = Integer.valueOf(request.getParameter("deleteId"));
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    product.setId(deleteId);
                    boolean deleted=productDao.delete(product);
                    request.getSession().setAttribute("deleted", deleted);
                    break;
                case "cred":                    //se bisogna creare o modificare una lista
                    Integer productId = null;
                    try {
                        productId = Integer.valueOf(request.getParameter("idProduct"));
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    product.setId(productId);
                    if (productId == null) {
                        product.setCategory(categoryDao.getByPrimaryKey(Integer.valueOf(request.getParameter("idCategory"))));
                        productDao.insert(product);
                    } else {
                        productDao.update(product);
                    }
                    break;
                default:
                    break;
            }
        } catch (DAOException ex) {
            //TODO: log exception
        }

        String contextPath = getServletContext().getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }

        response.sendRedirect(response.encodeRedirectURL(contextPath + "restricted/products.html"));
    }
}

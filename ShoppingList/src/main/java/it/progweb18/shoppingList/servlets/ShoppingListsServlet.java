package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.CategoryDAO;
import it.progweb18.shoppingList.dao.ProductDAO;
import it.progweb18.shoppingList.dao.ShoppingListDAO;
import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.Product;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the shopping lists actions.
 */
public class ShoppingListsServlet extends HttpServlet {
    private ShoppingListDAO shoppingListDao;
    private CategoryDAO categoryDao;
    private UserDAO userDao;
    private ProductDAO productDao;
    
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
            shoppingListDao = daoFactory.getDAO(ShoppingListDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for shopping-list storage system", ex);
        }
        try {
            userDao = daoFactory.getDAO(UserDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for user storage system", ex);
        }
        try {
            categoryDao = daoFactory.getDAO(CategoryDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for category storage system", ex);
        }
        try {
            productDao = daoFactory.getDAO(ProductDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for product storage system", ex);
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
        
        Integer userId = null;
        try {
            userId = Integer.valueOf(request.getParameter("idUser"));
        } catch (RuntimeException ex) {
            //TODO: log the exception
        }
        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String operation = request.getParameter("operation");
        Integer shoppingListId = null;
        
        try {
            ShoppingList shoppingList = new ShoppingList();
            shoppingList.setName(name);
            shoppingList.setDescription(description);
            User user;
            switch (operation) {    
                case "deleteNotification":
                    user = userDao.getByPrimaryKey(userId);
                    Integer deleteMessageId = null;
                    try{
                        deleteMessageId = Integer.valueOf(request.getParameter("deleteMessageId"));
                    } catch(RuntimeException ex){
                        //TODO: log the exception
                    }                    
                    try {
                        shoppingListId = Integer.valueOf(request.getParameter("deleteShoppingListId"));
                        shoppingList.setId(shoppingListId);
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    shoppingListDao.deleteNotification(shoppingList,deleteMessageId,user);
                    break;               
                case "notify":
                    user = userDao.getByPrimaryKey(userId);
                    Integer notifyMessage = null;
                    try{
                        notifyMessage = Integer.valueOf(request.getParameter("notifyMessage"));
                    } catch(RuntimeException ex){
                        //TODO: log the exception
                    }                    
                    try {
                        shoppingListId = Integer.valueOf(request.getParameter("notifyShoppingListId"));
                        shoppingList.setId(shoppingListId);
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    boolean notified = shoppingListDao.notifyUsers(shoppingList,notifyMessage,user);
                    request.getSession().setAttribute("notified", notified);
                    break;               
                case "order":
                    Integer orderType = null;
                    try{
                        orderType = Integer.valueOf(request.getParameter("orderShoppingLists"));
                    } catch(RuntimeException ex){
                        //TODO: log the exception
                    }
                    request.getSession().setAttribute("order", orderType);
                    break;
                case "deleteProduct":
                    Product deleteProduct = new Product();
                    Integer deleteProductId = null;
                    try {
                        deleteProductId = Integer.valueOf(request.getParameter("deleteProductId"));
                        deleteProduct.setId(deleteProductId);
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    try {
                        shoppingListId = Integer.valueOf(request.getParameter("deleteProductShoppingListId"));
                        shoppingList.setId(shoppingListId);
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    productDao.deleteProductFromList(deleteProduct, shoppingList);
                    break;
                case "addProduct":
                    Product addProduct = new Product();
                    String idCategory = request.getParameter("productCategoryId");
                    Integer productSelect = null;
                    Integer quantity = null;
                    
                    try {
                        productSelect = Integer.valueOf(request.getParameter("productSelect"+idCategory));
                        addProduct.setId(productSelect);
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    try {
                        shoppingListId = Integer.valueOf(request.getParameter("productShoppingListId"));
                        shoppingList.setId(shoppingListId);
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    try {
                        quantity = Integer.valueOf(request.getParameter("quantity"));
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    
                    productDao.deleteProductFromList(addProduct, shoppingList);
                    
                    if(quantity != 0)
                        productDao.linkProductToShoppingList(addProduct, shoppingList, quantity);
                    break;
                case "share":                   //se bisogna condividere la lista
                    String email = request.getParameter("email");
                    Integer shareId = null;
                    try {
                        shareId = Integer.valueOf(request.getParameter("shareId"));
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    shoppingList.setId(shareId);
                    int shareUserId=userDao.getByEmail(email);
                    user = userDao.getByPrimaryKey(shareUserId);
                    boolean shared=shoppingListDao.linkShoppingListToUser(shoppingList, user);
                    request.getSession().setAttribute("shared", shared);
                    break;
                case "delete":                  //se bisogna cancellare la lista
                    Integer deleteId = null;
                    try {
                        deleteId = Integer.valueOf(request.getParameter("deleteId"));
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    shoppingList.setId(deleteId);
                    boolean deleted=shoppingListDao.deleteUserList(shoppingList);
                    request.getSession().setAttribute("deleted", deleted);
                    break;
                case "cred":                    //se bisogna creare o modificare una lista
                    try {
                        shoppingListId = Integer.valueOf(request.getParameter("idShoppingList"));
                    } catch (RuntimeException ex) {
                        //TODO: log the exception
                    }
                    shoppingList.setId(shoppingListId);
                    if (shoppingListId == null) {
                        shoppingList.setCategory(categoryDao.getByPrimaryKey(Integer.valueOf(request.getParameter("idCategory"))));
                        shoppingListDao.insert(shoppingList);
                    } else {
                        shoppingListDao.update(shoppingList);
                    }   
                    user = userDao.getByPrimaryKey(userId);
                    shoppingListDao.linkShoppingListToUser(shoppingList, user);
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

        response.sendRedirect(response.encodeRedirectURL(contextPath + "restricted/shopping.lists.html?id=" + userId));
    }
}

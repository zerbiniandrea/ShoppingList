package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the login actions.
 */
public class LoginServlet extends HttpServlet {
    private UserDAO userDao;
    private final static String USER_COOKIE_NAME="ProgWeb18.ShoppingList.UserID";

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
            userDao = daoFactory.getDAO(UserDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for user storage system", ex);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("loginEmail");
        String password = request.getParameter("loginPassword");
        String rememberMe = request.getParameter("rememberMe");
        String wrongLogin;
        String url="ooops.jsp";
        
        String contextPath = getServletContext().getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        
        try {
            if(email != null && password != null){
                User user = userDao.getByEmailAndPassword(email, password);
                if (user == null) {
                    wrongLogin="true";
                    url="guest.setup";
                } else {
                    request.getSession().setAttribute("user", user);
                    wrongLogin="false";
                    if("true".equals(rememberMe)){
                        Cookie cookie=new Cookie(USER_COOKIE_NAME,Integer.toString(user.getId()));
                        cookie.setPath("/");
                        cookie.setMaxAge(604800);
                        response.addCookie(cookie);
                    }
                    
                    if (user.getType().equals("admin")) {
                        url="restricted/products.html";
                    } else {
                        /*
                        Guest guest=guestDao.getGuest(request.getCookies());
                        List<ShoppingList> guestShoppingLists = shoppingListDao.getByGuestId(guest.getId());
                        for(ShoppingList shoppingList : guestShoppingLists){
                            shoppingListDao.linkShoppingListToUser(shoppingList, user);
                        }
                        */
                        
                        /*
                        *   -------DELETE GUEST COOKIES-------
                        *   
                        Cookie[] cookies = request.getCookies();
                        for(Cookie cookie : cookies){
                            if(cookie.getName().equals(GUEST_COOKIE_NAME)){
                                cookie.setValue("");
                                cookie.setPath("/");
                                cookie.setMaxAge(0);
                                response.addCookie(cookie);
                            }
                        }
                        */
                        url="restricted/shopping.lists.html";
                    }
                }

                request.getSession().setAttribute("wrongLogin", wrongLogin);
            }
            response.sendRedirect(response.encodeRedirectURL(contextPath + url));
        } catch (DAOException ex) {
            request.getServletContext().log("Impossible to retrieve the user", ex);
        }
    }
}

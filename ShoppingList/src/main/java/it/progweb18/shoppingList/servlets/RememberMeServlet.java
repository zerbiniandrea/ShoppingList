package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the remember me actions.
 */
public class RememberMeServlet extends HttpServlet {
    private UserDAO userDao;
    private final static String COOKIE_NAME="ProgWeb18.ShoppingList.UserID";

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
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id=-1;
        String rememberMe=request.getParameter("rememberMe");
        String url="guest.setup";
        
        String contextPath = getServletContext().getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        
        Cookie[] cookies=request.getCookies();
        if( cookies != null ) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)){
                    id=parseInt(cookie.getValue());
                }
            }
        }
        
        if(id>0){
            try {
                User user=userDao.getById(id);
                
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                    
                    if(rememberMe != null && rememberMe.equals("true")){
                        Cookie cookie=new Cookie(COOKIE_NAME,Integer.toString(user.getId()));
                        cookie.setMaxAge(604800);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }

                    if (user.getType().equals("admin")) {
                        url = "restricted/products.html";
                    } else {
                        url = "restricted/shopping.lists.html?id=" + user.getId();
                    }
                }
            } catch (DAOException ex) {
                Logger.getLogger(RememberMeServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        request.getSession().setAttribute("wrongLogin", "false");
        response.sendRedirect(response.encodeRedirectURL(contextPath + url));
            
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

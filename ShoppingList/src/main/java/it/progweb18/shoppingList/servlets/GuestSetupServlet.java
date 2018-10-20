package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.GuestDAO;
import it.progweb18.shoppingList.dao.ShoppingListDAO;
import it.progweb18.shoppingList.dao.entities.Guest;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the guest actions.
 */
public class GuestSetupServlet extends HttpServlet {
    private GuestDAO guestDao;
    private DAOFactory daoFactory;
    private ShoppingListDAO shoppingListDao;
    private final static String COOKIE_NAME="ProgWeb18.ShoppingList.GuestID";
    
    /**
     * Initializes daos
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        daoFactory = (DAOFactory) super.getServletContext().getAttribute("daoFactory");
        if (daoFactory == null) {
            throw new ServletException("Impossible to get dao factory for user storage system");
        }
        try {
            shoppingListDao = daoFactory.getDAO(ShoppingListDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for shopping-list storage system", ex);
        }
        try {
            guestDao = daoFactory.getDAO(GuestDAO.class);
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
        response.setContentType("text/html;charset=UTF-8");
        
        String contextPath = getServletContext().getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        
        try {
            Cookie[] cookies=request.getCookies();
            Guest guest = guestDao.getGuest(cookies);

            if (guest == null) {
                response.sendRedirect(response.encodeRedirectURL(contextPath + "ooops.jsp"));
            } else {
                Cookie cookie=new Cookie(COOKIE_NAME,Integer.toString(guest.getId()));
                cookie.setPath("/");
                cookie.setMaxAge(604800);
                response.addCookie(cookie);
                
                //SESSION GUEST ATTRIBUTES
                List<ShoppingList> shoppingLists = shoppingListDao.getByGuestId(guest.getId());
                
                request.getSession().setAttribute("contextPath", contextPath);
                request.getSession().setAttribute("guest", guest);
                request.getSession().setAttribute("shoppingLists", shoppingLists);
                response.sendRedirect("guest.shopping.lists.html");
            }
        } catch (DAOException ex) {
            request.getServletContext().log("Impossible to retrieve the guest", ex);
        }
    }

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
}

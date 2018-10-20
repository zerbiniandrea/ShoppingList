package it.progweb18.shoppingList.filters;

import it.progweb18.shoppingList.dao.CategoryDAO;
import it.progweb18.shoppingList.dao.GuestDAO;
import it.progweb18.shoppingList.dao.ProductDAO;
import it.progweb18.shoppingList.dao.ShoppingListDAO;
import it.progweb18.shoppingList.dao.entities.Category;
import it.progweb18.shoppingList.dao.entities.Guest;
import it.progweb18.shoppingList.dao.entities.Product;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter that check if access to shopping lists is authorized.
 */
public class GuestShoppingListsFilter implements Filter {

    private static final boolean DEBUG = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public GuestShoppingListsFilter() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (DEBUG) {
            log("ShoppingListsFilter:DoBeforeProcessing");
        }

        DAOFactory daoFactory = (DAOFactory) request.getServletContext().getAttribute("daoFactory");
        if (daoFactory == null) {
            throw new RuntimeException(new ServletException("Impossible to get dao factory for guest storage system"));
        }
        GuestDAO guestDao = null;
        try {
            guestDao = daoFactory.getDAO(GuestDAO.class);
            request.setAttribute("guestDao", guestDao);
        } catch (DAOFactoryException ex) {
            throw new RuntimeException(new ServletException("Impossible to get dao factory for guest storage system", ex));
        }

        ShoppingListDAO shoppingListDao = null;
        try {
            shoppingListDao = daoFactory.getDAO(ShoppingListDAO.class);
            request.setAttribute("shoppingListDao", shoppingListDao);
        } catch (DAOFactoryException ex) {
            throw new RuntimeException(new ServletException("Impossible to get the dao factory for shopping list storage system", ex));
        }
             
        CategoryDAO categoryDao = null;
        try {
            categoryDao = daoFactory.getDAO(CategoryDAO.class);
            request.setAttribute("categoryDao", categoryDao);
        } catch (DAOFactoryException ex) {
            throw new RuntimeException(new ServletException("Impossible to get the dao factory for shopping list storage system", ex));
        }
        
        ProductDAO productDao = null;
        try {
            productDao = daoFactory.getDAO(ProductDAO.class);
            request.setAttribute("productDao", productDao);
        } catch (DAOFactoryException ex) {
            throw new RuntimeException(new ServletException("Impossible to get the dao factory for product storage system", ex));
        }
        String contextPath = request.getServletContext().getContextPath();
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        request.setAttribute("contextPath", contextPath);

        Integer guestId = null;
        Guest guest = null;
        Guest authenticatedGuest = null;
        
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            authenticatedGuest = (Guest) session.getAttribute("guest");
        }
        
        try {
            guestId = Integer.valueOf(request.getParameter("id"));
        } catch (RuntimeException ex) {
            if (session != null) {
                guest = authenticatedGuest;
            }
            if (guest != null) {
                guestId = guest.getId();
            }
        }
        if (guestId == null) {
            if (!response.isCommitted()) {
                ((HttpServletResponse) response).sendRedirect(((HttpServletResponse) response).encodeRedirectURL(contextPath + "guest.setup"));
            }
        }

        try {
            if (guest == null) {
                guest = guestDao.getByPrimaryKey(guestId);
            }
        } catch (DAOException ex) {
            throw new RuntimeException(new ServletException("Impossible to get guest or shopping lists", ex));
        }

        try {
            Integer order = (Integer) ((HttpServletRequest) request).getSession(false).getAttribute("order");
            if(order == null)
                order = 1;
            List<ShoppingList> shoppingLists = shoppingListDao.getByGuestId(guestId,order);
            for(ShoppingList shoppingList : shoppingLists){
                shoppingList.setProducts(productDao.getByShoppingListId(shoppingList.getId()));
            }
            request.setAttribute("shoppingLists", shoppingLists);
            request.setAttribute("order", order);
        } catch (DAOException ex) {
            throw new RuntimeException(new ServletException("Impossible to get guest or shopping lists", ex));
        }
        
        try {
            List<Category> categories = categoryDao.getAll();
            request.setAttribute("categories", categories);
        } catch (DAOException ex) {
            throw new RuntimeException(new ServletException("Impossible to get categories", ex));
        }
        
        try {
            List<Product> products=productDao.getAll();
            request.setAttribute("products", products);
        } catch (DAOException ex) {
            throw new RuntimeException(new ServletException("Impossible to get products", ex));
        }
        
        try {
            Integer shoppingListsCount=guestDao.countShoppingLists(guest);
            guest.setShoppingListsCount(shoppingListsCount);
            request.setAttribute("guest", guest);
        } catch (DAOException ex) {
            throw new RuntimeException(new ServletException("Impossible to get products", ex));
        }
        if (response.isCommitted()) {
            request.getServletContext().log("shopping.lists.html is already committed");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (DEBUG) {
            log("ShoppingListsFilter:DoAfterProcessing");
        }
        //Nothing to post-process
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (DEBUG) {
            log("ShoppingListsFilter:doFilter()");
        }

        doBeforeProcessing(request, response);

        Throwable problem = null;
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            problem = ex;
            log(ex.getMessage());
//            ex.printStackTrace();
        }

        doAfterProcessing(request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            ((HttpServletResponse) response).sendError(500, problem.getMessage());
        }
    }

    /**
     * Return the filter configuration object for this filter.
     *
     * @return
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (DEBUG) {
                log("ShoppingListsFilter:Initializing filter");
            }
        }
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    public void log(String msg, Throwable t) {
        filterConfig.getServletContext().log(msg, t);
    }
}

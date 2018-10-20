package it.progweb18.shoppingList.filters;

import it.progweb18.shoppingList.dao.CategoryDAO;
import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.Category;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CategoriesFilter implements Filter{

    private static final boolean DEBUG = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public CategoriesFilter() {
    }
    
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (DEBUG) {
            log("CategoriesFilter:DoBeforeProcessing");
        }
        
        if (request instanceof HttpServletRequest) {
            ServletContext servletContext = ((HttpServletRequest) request).getServletContext();

            String contextPath = servletContext.getContextPath();
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            request.setAttribute("contextPath", contextPath);

            HttpSession session = ((HttpServletRequest) request).getSession(false);
            User authenticatedUser = null;
            if (session != null) {
                authenticatedUser = (User) session.getAttribute("user");
            }
            if (authenticatedUser != null) {
                String avatarPath = "../images/avatars/" + authenticatedUser.getAvatarPath();
                request.setAttribute("avatarPath", avatarPath);

                if (!authenticatedUser.getType().equals("admin")) {

                    String requestUrl = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
                    if (requestUrl == null) {
                        requestUrl = ((HttpServletRequest) request).getHeader("referer");
                    }
                    if (requestUrl == null) {
                        requestUrl = contextPath + "/shopping.lists.html";
                    }
//                    ((HttpServletResponse) response).sendRedirect(((HttpServletResponse) response).encodeRedirectURL(requestUrl));
                    RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(((HttpServletResponse) response).encodeRedirectURL(requestUrl));
                    dispatcher.forward(request, response);
                    return;
                }
            }

            DAOFactory daoFactory = (DAOFactory) request.getServletContext().getAttribute("daoFactory");
            if (daoFactory == null) {
                throw new RuntimeException(new ServletException("Impossible to get dao factory for user storage system"));
            }
            try {
                UserDAO userDao = daoFactory.getDAO(UserDAO.class);
                if (userDao != null) {
                    request.setAttribute("userDao", userDao);
                }
            } catch (DAOFactoryException ex) {
                throw new RuntimeException(new ServletException("Impossible to get dao factory for user storage system", ex));
            }
            
            CategoryDAO categoryDao = null;
            try {
                categoryDao = daoFactory.getDAO(CategoryDAO.class);
                request.setAttribute("categoryDao", categoryDao);
            } catch (DAOFactoryException ex) {
                throw new RuntimeException(new ServletException("Impossible to get the dao factory for category storage system", ex));
            }
            
            try {
                List<Category> categories = categoryDao.getAll();
                request.setAttribute("categories", categories);
            } catch (DAOException ex) {
                throw new RuntimeException(new ServletException("Impossible to get categories", ex));
            }
        }
        
        if (response.isCommitted()) {
            request.getServletContext().log("shopping.lists.html is already committed");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (DEBUG) {
            log("ProductFilter:DoAfterProcessing");
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
            log("ProductFilter:doFilter()");
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

    
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }
    
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
    
    @Override
    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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

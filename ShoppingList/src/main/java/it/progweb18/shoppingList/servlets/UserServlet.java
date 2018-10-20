package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet that handles the user updating action.
 */
@MultipartConfig
public class UserServlet extends HttpServlet {
    private UserDAO userDao;

    /**
     * Initializes daos
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = (DAOFactory) super.getServletContext().getAttribute("daoFactory");
        if (daoFactory == null) {
            throw new ServletException("Impossible to get dao factory for storage system");
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
        File avatarsFolder = new File(getServletContext().getInitParameter("avatarsFolder"));
        
        /*
        if (avatarsFolder == null) {
            throw new ServletException("Avatars folder not configured");
        }
        
        avatarsFolder = getServletContext().getRealPath(avatarsFolder);
        */
        //TODO: If avatarsFolder doesn't exist, create it
        
        String cp = getServletContext().getContextPath();
        if (!cp.endsWith("/")) {
            cp += "/";
        }
        
        
        Integer userId = Integer.valueOf(request.getParameter("idUser"));
        /*
        String lastName = request.getParameter("lastname");
        if (lastName == null) {
            lastName = "";
        }
        
        String firstName = request.getParameter("firstname");
        if (firstName == null) {
            firstName = "";
        }
        
        String type = request.getParameter("type");
        if (type == null) {
            type  = "";
        }
        
        */
        User user = null;
        if (userId != null) {
            try {
                user = userDao.getByPrimaryKey(userId);
            } catch (DAOException ex) {
                response.sendError(500, ex.getMessage());
                return;
            }
        }
        
        if (user == null) {
            response.sendRedirect(cp + "guest.setup");
            return;
        }
        
        
        boolean modified = false;
        /*
        if (!firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            modified = true;
        }
        if (!lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
            modified = true;
        }
        if (!type.equals(user.getType())) {
            user.setType(type);
            modified = true;
        }
        */
        
        Part filePart = request.getPart("avatar");
        if ((filePart != null) && (filePart.getSize() > 0)) {
            //String filename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();//MSIE  fix.
            try (InputStream fileContent = filePart.getInputStream()) {
                File file = new File(avatarsFolder, Integer.toString(user.getId()));
                file.delete();
                Files.copy(fileContent, file.toPath());
                user.setAvatarPath(Integer.toString(user.getId()));
                modified = true;
                        
                //WAITS FOR THE IMAGE TO LOAD/COPY
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileAlreadyExistsException ex) {
                getServletContext().log("File \"" + Integer.toString(user.getId()) + "\" already exists on the server");
            } catch (RuntimeException ex) {
                //TODO: handle the exception
                getServletContext().log("impossible to upload the file", ex);
            }
        }
        
        if (modified) {
            try {
                userDao.update(user);
            } catch (DAOException ex) {
                response.sendError(500, ex.getMessage());
            }
            User authenticatedUser = (User) request.getSession(false).getAttribute("user");
            if (authenticatedUser.getId().equals(user.getId())) {
                authenticatedUser.setType(user.getType());
                authenticatedUser.setAvatarPath(user.getAvatarPath());
                authenticatedUser.setEmail(user.getEmail());
                authenticatedUser.setFirstName(user.getFirstName());
                authenticatedUser.setLastName(user.getLastName());
                authenticatedUser.setPassword(user.getPassword());
                authenticatedUser.setShoppingListsCount(user.getShoppingListsCount());
            }
        }
        
        if (user.getType().equals("admin")) {
            response.sendRedirect(cp+"restricted/products.html");
        } else {
            response.sendRedirect(cp+"restricted/shopping.lists.html");
        }
    }
}

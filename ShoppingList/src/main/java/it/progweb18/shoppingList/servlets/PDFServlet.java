package it.progweb18.shoppingList.servlets;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;
import it.progweb18.shoppingList.dao.ShoppingListDAO;
import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.ShoppingList;
import it.progweb18.shoppingList.dao.entities.User;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Servlet that handles exporting to PDF.
 */
public class PDFServlet extends HttpServlet {

    private ShoppingListDAO shoppingListDao;
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
            shoppingListDao = daoFactory.getDAO(ShoppingListDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for shopping-lists storage system", ex);
        }
        try {
            userDao = daoFactory.getDAO(UserDAO.class);
        } catch (DAOFactoryException ex) {
            throw new ServletException("Impossible to get dao factory for user storage system", ex);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pdfFolder = getServletContext().getInitParameter("pdfFolder");
        if (pdfFolder == null) {
            throw new ServletException("PDFs folder not configured");
        }
        
        pdfFolder = getServletContext().getRealPath(pdfFolder);

        Integer userId = null;
        try {
            userId = Integer.valueOf(request.getParameter("id"));
        } catch (NumberFormatException | NullPointerException ex) {
            throw new ServletException("No valid user id");
        }

        User user = null;
        try {
            user = userDao.getByPrimaryKey(userId);
        } catch (DAOException ex) {
            throw new ServletException("Impossible to get the user with id: " + userId, ex);
        }

        List<ShoppingList> shoppingLists = null;
        try {
            shoppingLists = shoppingListDao.getByUserId(userId);
        } catch (DAOException ex) {
            throw new ServletException("Impossible to get the list of shopping-lists for user with id: " + userId, ex);
        }

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                PDStreamUtils.write(
                        contents,
                        "Shopping-lists collection",
                        PDType1Font.HELVETICA_BOLD,
                        26,
                        80,
                        700,
                        Color.BLUE);
                PDStreamUtils.write(
                        contents,
                        "The following table lists all the Shopping-lists for \"" + user.getLastName() + " " + user.getFirstName() + "\".",
                        PDType1Font.HELVETICA_BOLD,
                        14,
                        80,
                        675,
                        Color.BLUE);

                float margin = 80;
                float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
                float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                
                boolean drawContent = true;
                float yStart = yStartNewPage;
                float bottomMargin = 70;
                float yPosition = 660;
                
                BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);
                Row<PDPage> header = table.createRow(20);
                header.createCell(20, "Name");
                header.createCell(50, "Description");
                table.addHeaderRow(header);
                
                for (ShoppingList shoppingList : shoppingLists) {
                    Row<PDPage> row = table.createRow(12);
                    row.createCell(shoppingList.getName());
                    row.createCell(shoppingList.getDescription());
                }
                
                table.draw();
            }

            doc.save(new File(pdfFolder, "user-" + userId + "-" + Calendar.getInstance().getTimeInMillis() + ".pdf"));

            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment; filename='shopping-lists.pdf'");
            doc.save(response.getOutputStream());
        }

        String contextPath = getServletContext().getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }

        response.sendRedirect(response.encodeRedirectURL(contextPath + "restricted/shopping.lists.html?id=" + userId));
    }
}

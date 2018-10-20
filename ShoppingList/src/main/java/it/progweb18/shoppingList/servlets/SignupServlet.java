package it.progweb18.shoppingList.servlets;

import it.progweb18.shoppingList.dao.UserDAO;
import it.progweb18.shoppingList.dao.entities.GoogleMail;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.exceptions.DAOFactoryException;
import it.unitn.aa1718.webprogramming.persistence.utils.dao.factories.DAOFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the signup actions.
 */
public class SignupServlet extends HttpServlet {
    private final String SUBJECT = "Your ShoppingList account is almost ready!";
    private final String MESSAGE1="This is your verification code: ";
    private final String MESSAGE2="<br>Go here and insert this code to verify your account: <br> http://localhost:8084/ShoppingList/verify.html";
    
    private UserDAO userDao;

    /*
    *   OLD SEND EMAIL (NO JAVA CLASS)
    *
    private void sendEmail(String email, String code){
        final String host = getServletContext().getInitParameter("smtp-hostname");
        final String port = getServletContext().getInitParameter("smtp-port");
        final String username = getServletContext().getInitParameter("smtp-username");
        final String password = getServletContext().getInitParameter("smtp-password");
        
        String subject = "Iscrizione a lista della spesa!";

        String message = "This is your verification code: "+code+"<br>Go here and insert this code to verify your account: <br> http://localhost:8084/ShoppingList/verify.html";

        StringBuilder plainTextMessageBuilder = new StringBuilder();
        plainTextMessageBuilder.append(message).append("\n");
        StringBuilder htmlMessageBuilder = new StringBuilder();
        message = message.replace(" ", "&nbsp;");
        message = message.replace("\n", "<br>");
        htmlMessageBuilder.append(message).append("<br>");

        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.socketFactory.port", port);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.debug", "true");
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
            
        });
        
        try {
            Multipart multipart = new MimeMultipart("alternative");
            
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(plainTextMessageBuilder.toString());
            
            BodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setContent(htmlMessageBuilder.toString(), "text/html; charset=utf-8");
            
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);
            
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, "Shopping List - Notifier"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(multipart);
            
            Transport.send(msg);
            
        } catch (MessagingException | UnsupportedEncodingException me) {
            Logger.getLogger(getClass().getName()).severe(me.toString());
        }
        
    }
    */

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
        String url = "ooops.jsp";
        boolean alreadyExists=false;
        
        String contextPath = getServletContext().getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        
        if(request.getParameter("privacy").equals("true")){
            String email = request.getParameter("signupEmail");
            String password = request.getParameter("signupPassword");
            String name = request.getParameter("signupName");
            String lastname = request.getParameter("signupLastname");
            String avatarpath = "null";

            //REGISTRAZIONE CON VERIFICA VIA EMAIL

            try {
                if(userDao.alreadyExists(email)){
                    url="guest.setup";
                    alreadyExists=true;
                }else{            
                    String code=userDao.createNotVerified(email, password, name, lastname, avatarpath);

                    if(code != null){
                        String message = MESSAGE1+code+MESSAGE2;

                        GoogleMail.sendEmail(email,SUBJECT,message);
                        url="verify.html";
                    }else
                        url="ooops.jsp";
                }
            } catch (DAOException ex) {
                Logger.getLogger(SignupServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            //REGISTRAZIONE SENZA VERIFICA VIA EMAIL
            /*
            try {
                User user = userDao.create(email, password, name, lastname, avatarpath);
                if (user == null) {
                    response.sendRedirect(response.encodeRedirectURL(contextPath + "login.jsp"));
                } else {
                    request.getSession().setAttribute("user", user);
                    if (user.getType().equals("admin")) {
                        response.sendRedirect(response.encodeRedirectURL(contextPath + "restricted/users.html"));
                    } else {
                        response.sendRedirect(response.encodeRedirectURL(contextPath + "restricted/shopping.lists.html?id=" + user.getId()));
                    }
                }
            } catch (DAOException ex) {
                request.getServletContext().log("Impossible to retrieve the user", ex);
            }
            */
        }else{
            url="guest.setup";
        }
        
        request.getSession().setAttribute("alreadyExists", alreadyExists);
        response.sendRedirect(response.encodeRedirectURL(contextPath + url));
    }
}
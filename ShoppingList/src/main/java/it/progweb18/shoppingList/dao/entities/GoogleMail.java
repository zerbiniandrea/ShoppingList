package it.progweb18.shoppingList.dao.entities;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * The entity that sends mails using gmail
 */
public class GoogleMail {
    private static final String HOST="smtp.gmail.com";
    private static final String PORT="465";
    private static final String USERNAME="listadellaspesa.progweb18@gmail.com";
    private static final String PASSWORD="otqqfztvfagulzph";
    
    /**
     * Sends an email to the given email containing the given code
     * @param email the destination
     * @param subject the subject of the email
     * @param message the message contained in the email
     * @throws java.io.UnsupportedEncodingException
     */
    public static void sendEmail(String email, String subject, String message) throws UnsupportedEncodingException{
        /*
        *   OLD VERSION -- SERVLET CONTEXT+WEB.XML PARAMETERS
        *
        final String host = sc.getInitParameter("smtp-hostname");
        final String port = sc.getInitParameter("smtp-port");
        final String username = sc.getInitParameter("smtp-username");
        final String password = sc.getInitParameter("smtp-password");
        */
        
        /*
        *   OLD VERSION - ONE FUNCTION FOR EACH TIME SENDEMAIL IS CALLED
        *   (NO SUBJECT/MESSAGE PASSED AS A PARAMETER)
        *
        String subject = "Iscrizione a lista della spesa!";
        String message = "This is your verification code: "+code+"<br>Go here and insert this code to verify your account: <br> http://localhost:8084/ShoppingList/verify.html";
        */
        try {
            StringBuilder plainTextMessageBuilder = new StringBuilder();
            plainTextMessageBuilder.append(message).append("\n");
            StringBuilder htmlMessageBuilder = new StringBuilder();
            message = message.replace(" ", "&nbsp;");
            message = message.replace("\n", "<br>");
            htmlMessageBuilder.append(message).append("<br>");
            
            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", HOST);
            props.setProperty("mail.smtp.port", PORT);
            props.setProperty("mail.smtp.socketFactory.port", PORT);
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.debug", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
                
            });
            
            Multipart multipart = new MimeMultipart("alternative");
            
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(plainTextMessageBuilder.toString());
            
            BodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setContent(htmlMessageBuilder.toString(), "text/html; charset=utf-8");
            
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);
            
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(USERNAME, "Shopping List - Notifier"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(multipart);
            
            Transport.send(msg);
        } catch (MessagingException ex) {
            Logger.getLogger(GoogleMail.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

package br.com.thiaguten.javamail.client.web;

import br.com.thiaguten.javamail.core.*;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/emailServlet", name = "Email Servlet")
public class EmailServlet extends HttpServlet {

    private static final long serialVersionUID = -6357235852648684816L;

//    static {
//        // proxy
//        System.setProperty("http.proxyHost", "");
//        System.setProperty("http.proxyPort", "");
//        java.net.Authenticator.setDefault(new java.net.Authenticator() {
//            @Override
//            protected java.net.PasswordAuthentication getPasswordAuthentication() {
//                return new java.net.PasswordAuthentication("", "".toCharArray());
//            }
//        });
//    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
                // NOP
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String from = req.getParameter("from");
        final String to = req.getParameter("to");
        final String subject = req.getParameter("subject");
        final String message = req.getParameter("message");

        EmailSender sender = new SimpleEmailSender(new EmailConfiguration());
        Email email = new SimpleEmail(from, to, subject, message);
        try {
            sender.send(email);
            printOutput(resp, "Sent message successfully!");
        } catch (MessagingException e) {
            printOutput(resp, "Message not sent! " + e.getMessage());
        }
    }

    private void printOutput(HttpServletResponse resp, String... outputs) {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = null;
        try {
            writer = resp.getWriter();
            writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
            writer.write("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><title>Message</title></head><body>");
            for (Object output : outputs) {
                writer.write("<pre>" + output + "</pre>");
            }
            writer.write("<a href=\"index.jsp\">Back</a>");
            writer.write("</body></html>");
            writer.flush();
        } catch (IOException e) {
            getServletContext().log("Error printing output", e);
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }
}

package org.jivesoftware.smack;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
description = "Login Servlet",
urlPatterns = {
        "/LoginServlet"
})
public class LoginServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;


        public void init() throws ServletException {}

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

                //get request parameters for userID and password
                String user = request.getParameter("j_username");
                String pwd = request.getParameter("j_password");

                //AV MEG 
                try {
                        connectToXMPPServer(response, user, pwd);
                        response.sendRedirect("https://hmis.moh.gov.rw/hmis/");

                } catch (XMPPException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
                        PrintWriter out = response.getWriter();
                        out.println("<font color=red>Either user name or password is wrong.</font>");
                        rd.include(request, response);
                }
        }

        private void connectToXMPPServer(HttpServletResponse response, String user, String pwd) throws XMPPException {

                BOSHConfiguration config = new BOSHConfiguration(false, "127.0.0.1", 7070, "/http-bind/", "hmis", "xmpp:127.0.0.1:5222");

                //create a connection
                BOSHConnection connection = new BOSHConnection(config);

                connection.connect();
                connection.login(user, pwd, "", true);
                Cookie jidCookie = new Cookie("jid", connection.getUser());
                Cookie sidCookie = new Cookie("sid", connection.getConnectionID());
                Cookie ridCookie = new Cookie("rid", connection.getRid());

                jidCookie.setPath("/hmis");
                sidCookie.setPath("/hmis");
                ridCookie.setPath("/hmis");

                //setting cookie to expiry in 20 minutes
                jidCookie.setMaxAge(20 * 60);
                sidCookie.setMaxAge(20 * 60);
                ridCookie.setMaxAge(20 * 60);


                response.setHeader("Access-Control-Allow-Origin", "https://hmis.moh.gov.rw/"); 
                response.setHeader("Access-Control-Max-Age", "360");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET");
                response.setHeader("Access-Control-Allow-Headers", "Origin");
                response.setHeader("Access-Control-Expose-Headers","Access-Control-Allow-Origin");

                response.addCookie(jidCookie);
                response.addCookie(sidCookie);
                response.addCookie(ridCookie);
                
                connection.disconnect();
        }
}
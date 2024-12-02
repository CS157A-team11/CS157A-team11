package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/logout")
@MultipartConfig
public class Logout extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        UserDaoImpl userDao = new UserDaoImpl();
        UserTokenDaoImpl userTokenDao = new UserTokenDaoImpl();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response res = new Response("error", "Unauthenticated");
            response.getWriter().print(res.toJson());
            return;
        }

        UserToken userToken = null;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                userToken = userTokenDao.getByToken(cookie.getValue());
                break;
            }
        }

        if (userToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Response res = new Response("error", "Invalid token");
            response.getWriter().print(res.toJson());
            return;
        }

        // Delete token from database
        userTokenDao.deleteToken(userToken.getToken());

        // Clear cookie
        Cookie cookie = new Cookie("token", "");
        cookie.setPath("/");  // Important: set the same path as when creating
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Response res = new Response("success", "Logged out successfully");
        response.getWriter().print(res.toJson());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
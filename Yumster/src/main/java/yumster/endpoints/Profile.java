package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.obj.User;
import yumster.obj.UserToken;

@WebServlet("/api/v1/user/profile")
public class Profile extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            User user = authenticateUser(request);
            if (user == null) {
                sendError(response, "Unauthorized", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("username", user.getUname());
            response.getWriter().write(jsonResponse.toString());
            
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private User authenticateUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        UserDaoImpl userDao = new UserDaoImpl();
        UserTokenDaoImpl tokenDao = new UserTokenDaoImpl();

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                UserToken token = tokenDao.getByToken(cookie.getValue());
                if (token != null) {
                    return userDao.getById(token.getUserId());
                }
            }
        }
        return null;
    }

    private void sendError(HttpServletResponse response, String message, int status) 
            throws IOException {
        response.setStatus(status);
        JsonObject error = new JsonObject();
        error.addProperty("status", "error");
        error.addProperty("message", message);
        response.getWriter().write(error.toString());
    }
}
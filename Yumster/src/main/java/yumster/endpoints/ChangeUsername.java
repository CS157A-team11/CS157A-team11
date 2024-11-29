package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import yumster.dao.UserDao;
import yumster.dao.UserTokenDao;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/change-username")
@MultipartConfig
public class ChangeUsername extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ChangeUsername.class);
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Get authenticated user
            User user = authenticateUser(request);
            if (user == null) {
                sendError(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String newUsername = request.getParameter("new_username");
            String currentUsername = request.getParameter("current_username");

            if (StringUtils.isEmpty(newUsername) || StringUtils.isEmpty(currentUsername)) {
                sendError(response, "Required fields not filled", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!currentUsername.equals(user.getUname())) {
                sendError(response, "Current username is incorrect", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            UserDaoImpl userDao = new UserDaoImpl();
            if (userDao.checkExists("", newUsername)) {
                sendError(response, "Username already taken", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean updated = userDao.updateUsername(newUsername, user.getId());
            if (!updated) {
                sendError(response, "Failed to update username", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Username updated successfully");
            jsonResponse.addProperty("newUsername", newUsername);
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            log.error("Error in change username: " + e.getMessage(), e);
            e.printStackTrace();
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
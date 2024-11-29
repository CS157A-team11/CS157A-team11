package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import com.google.gson.JsonObject;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.obj.User;
import yumster.obj.UserToken;

@WebServlet("/api/v1/user/change-password")
@MultipartConfig
public class ChangePassword extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            User user = authenticateUser(request);
            if (user == null) {
                sendError(response, "Unauthorized access", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String oldPassword = request.getParameter("old_password");
            String newPassword = request.getParameter("new_password");

            if (oldPassword == null || newPassword == null) {
                sendError(response, "Missing required parameters", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!encoder.matches(oldPassword, user.getPasswordHash())) {
                sendError(response, "Current password is incorrect", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (newPassword.length() < 8) {
                sendError(response, "New password must be at least 8 characters", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String hashedPassword = encoder.encode(newPassword);
            UserDaoImpl userDao = new UserDaoImpl();
            boolean updated = userDao.updatePassword(hashedPassword, user);

            if (!updated) {
                sendError(response, "Failed to update password", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Password updated successfully");
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
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
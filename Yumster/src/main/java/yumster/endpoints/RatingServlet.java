package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yumster.dao.RatingDaoImpl;
import yumster.obj.User;
import javax.servlet.http.Cookie;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.obj.UserToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebServlet("/api/v1/ratings/*")
@MultipartConfig
public class RatingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final RatingDaoImpl ratingDao = new RatingDaoImpl();
    private final Gson gson = new Gson();
    private static final Log log = LogFactory.getLog(RatingServlet.class);

    
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

            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                int recipeId = Integer.parseInt(pathInfo.substring(1));
                Integer rating = ratingDao.getUserRating(user.getId(), recipeId);
                int reputation = ratingDao.getRecipeReputation(recipeId);
                
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("rating", rating);  // Changed from userRating to rating
                jsonResponse.addProperty("reputation", reputation);
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            User user = authenticateUser(request);
            if (user == null) {
                sendError(response, "Unauthorized", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String requestBody = request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
            RatingRequest ratingRequest = gson.fromJson(requestBody, RatingRequest.class);
            
            if (ratingRequest == null || ratingRequest.getRating() < 1 || ratingRequest.getRating() > 5) {
                sendError(response, "Invalid rating", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean success = ratingDao.addRating(
                user.getId(), 
                ratingRequest.getRecipeId(), 
                ratingRequest.getRating()
            );

            if (success) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                response.getWriter().write(jsonResponse.toString());
            } else {
                sendError(response, "Failed to save rating", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error in doPost: " + e.getMessage(), e);
            sendError(response, "Internal server error: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
    
    
    private static class RatingRequest {
        private int recipeId;
        private int rating;
        
        public int getRecipeId() { return recipeId; }
        public int getRating() { return rating; }
    }
}
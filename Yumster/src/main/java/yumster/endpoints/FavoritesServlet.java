package yumster.endpoints;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yumster.dao.*;
import yumster.obj.*;

@WebServlet("/api/v1/favorites/*")
@MultipartConfig
public class FavoritesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(FavoritesServlet.class);
    private final RecipeDaoImpl recipeDao = new RecipeDaoImpl();
    private final Gson gson = new Gson();
    
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

            // Explicitly use Recipe from yumster.obj package
            Map<Character, List<yumster.obj.Recipe>> allRecipes = recipeDao.getRecipesByAlphabet();
            List<yumster.obj.Recipe> userFavorites = recipeDao.getUserFavorites(user.getId());

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.add("recipes", gson.toJsonTree(allRecipes));
            jsonResponse.add("favorites", gson.toJsonTree(userFavorites));
            
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            log.error("Error in doGet: " + e.getMessage(), e);
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
            
            if (requestBody.isEmpty()) {
                sendError(response, "Empty request body", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            UpdateFavoritesRequest updateRequest = gson.fromJson(requestBody, UpdateFavoritesRequest.class);
            
            if (updateRequest == null || updateRequest.getRecipeIds() == null) {
                sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean updated = recipeDao.updateUserFavorites(user.getId(), updateRequest.getRecipeIds());
            
            if (updated) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Favorites updated successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                sendError(response, "Failed to update favorites", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error in doPost: " + e.getMessage(), e);
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

    public static class UpdateFavoritesRequest {
        private List<Integer> recipeIds;
        
        public UpdateFavoritesRequest() {}
        
        public UpdateFavoritesRequest(List<Integer> recipeIds) {
            this.recipeIds = recipeIds;
        }
        
        public List<Integer> getRecipeIds() { return recipeIds; }
        public void setRecipeIds(List<Integer> recipeIds) { this.recipeIds = recipeIds; }
    }
}
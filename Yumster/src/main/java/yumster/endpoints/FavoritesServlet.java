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
import yumster.obj.Recipe;  // Explicit import
import yumster.obj.User;
import yumster.obj.UserToken;

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

            Map<Character, List<Recipe>> allRecipes = recipeDao.getRecipesByAlphabet();
            List<Recipe> userFavorites = recipeDao.getUserFavorites(user.getId());

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

            String pathInfo = request.getPathInfo();
            if (pathInfo != null) {
                switch (pathInfo) {
                    case "/add":
                        handleAddFavorite(request, response, user);
                        break;
                    case "/remove":
                        handleRemoveFavorite(request, response, user);
                        break;
                    case "/update":
                        handleUpdateFavorites(request, response, user);
                        break;
                    default:
                        sendError(response, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            log.error("Error in doPost: " + e.getMessage(), e);
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleAddFavorite(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String requestBody = request.getReader().lines()
            .reduce("", (accumulator, actual) -> accumulator + actual);
        AddFavoriteRequest addRequest = gson.fromJson(requestBody, AddFavoriteRequest.class);
        
        if (addRequest == null || addRequest.getRecipeId() == 0) {
            sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean added = recipeDao.addUserFavorite(user.getId(), addRequest.getRecipeId());
        if (added) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Recipe added to favorites");
            response.getWriter().write(jsonResponse.toString());
        } else {
            sendError(response, "Failed to add favorite", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleRemoveFavorite(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String requestBody = request.getReader().lines()
            .reduce("", (accumulator, actual) -> accumulator + actual);
        RemoveFavoriteRequest removeRequest = gson.fromJson(requestBody, RemoveFavoriteRequest.class);
        
        if (removeRequest == null || removeRequest.getRecipeId() == 0) {
            sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean removed = recipeDao.removeUserFavorite(user.getId(), removeRequest.getRecipeId());
        if (removed) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Recipe removed from favorites");
            response.getWriter().write(jsonResponse.toString());
        } else {
            sendError(response, "Failed to remove favorite", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateFavorites(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String requestBody = request.getReader().lines()
            .reduce("", (accumulator, actual) -> accumulator + actual);
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

    private static class AddFavoriteRequest {
        private int recipeId;
        public int getRecipeId() { return recipeId; }
    }

    private static class RemoveFavoriteRequest {
        private int recipeId;
        public int getRecipeId() { return recipeId; }
    }

    private static class UpdateFavoritesRequest {
        private List<Integer> recipeIds;
        public List<Integer> getRecipeIds() { return recipeIds; }
    }
}
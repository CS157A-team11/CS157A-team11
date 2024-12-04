package yumster.endpoints;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yumster.dao.*;
import yumster.obj.User;
import yumster.obj.UserToken;
import yumster.obj.Ingredient;

import java.util.Arrays;

@WebServlet("/api/v1/dietary-restrictions/*")
@MultipartConfig
public class DietaryRestrictionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(DietaryRestrictionsServlet.class);
    private final DietaryRestrictionDaoImpl restrictionDao = new DietaryRestrictionDaoImpl();
    private final IngredientDaoImpl ingredientDao = new IngredientDaoImpl();
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

            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.startsWith("/search")) {
                handleIngredientSearch(request, response);
            } else {
                List<Ingredient> userRestrictions = restrictionDao.getUserRestrictions(user.getId());
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                jsonResponse.add("restrictions", gson.toJsonTree(userRestrictions));
                response.getWriter().write(jsonResponse.toString());
            }
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
            if (pathInfo == null) {
                sendError(response, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            switch (pathInfo) {
                case "/add":
                    handleAddRestriction(request, response, user);
                    break;
                case "/remove":
                    handleRemoveRestriction(request, response, user);
                    break;
                case "/update":
                    handleUpdateRestrictions(request, response, user);
                    break;
                default:
                    sendError(response, "Invalid endpoint", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error in doPost: " + e.getMessage(), e);
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleIngredientSearch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String query = request.getParameter("q");
        if (query == null || query.trim().isEmpty()) {
            sendError(response, "Search query is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<String> keywords = Arrays.asList(query.trim().split("\\s+"));
        List<Ingredient> ingredients = ingredientDao.getIngredientByKeywords(keywords);
        
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("status", "success");
        jsonResponse.add("ingredients", gson.toJsonTree(ingredients));
        response.getWriter().write(jsonResponse.toString());
    }


    private void handleAddRestriction(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String requestBody = request.getReader().lines()
            .reduce("", (accumulator, actual) -> accumulator + actual);
        AddRestrictionRequest addRequest = gson.fromJson(requestBody, AddRestrictionRequest.class);
        
        if (addRequest == null || addRequest.getIngredientId() == 0) {
            sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean added = restrictionDao.addUserRestriction(user.getId(), addRequest.getIngredientId());
        if (added) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Restriction added successfully");
            response.getWriter().write(jsonResponse.toString());
        } else {
            sendError(response, "Failed to add restriction", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    

    private void handleRemoveRestriction(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String requestBody = request.getReader().lines()
            .reduce("", (accumulator, actual) -> accumulator + actual);
        RemoveRestrictionRequest removeRequest = gson.fromJson(requestBody, RemoveRestrictionRequest.class);
        
        if (removeRequest == null || removeRequest.getIngredientId() == 0) {
            sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean removed = restrictionDao.removeUserRestriction(user.getId(), removeRequest.getIngredientId());
        if (removed) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Restriction removed successfully");
            response.getWriter().write(jsonResponse.toString());
        } else {
            sendError(response, "Failed to remove restriction", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateRestrictions(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String requestBody = request.getReader().lines()
            .reduce("", (accumulator, actual) -> accumulator + actual);
        UpdateRestrictionsRequest updateRequest = gson.fromJson(requestBody, UpdateRestrictionsRequest.class);
        
        if (updateRequest == null || updateRequest.getIngredientIds() == null) {
            sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean updated = restrictionDao.updateUserRestrictions(user.getId(), updateRequest.getIngredientIds());
        if (updated) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Restrictions updated successfully");
            response.getWriter().write(jsonResponse.toString());
        } else {
            sendError(response, "Failed to update restrictions", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

    // Request classes
    private static class AddRestrictionRequest {
        private int ingredientId;
        public int getIngredientId() { return ingredientId; }
    }

    private static class RemoveRestrictionRequest {
        private int ingredientId;
        public int getIngredientId() { return ingredientId; }
    }

    private static class UpdateRestrictionsRequest {
        private List<Integer> ingredientIds;
        public List<Integer> getIngredientIds() { return ingredientIds; }
    }
}
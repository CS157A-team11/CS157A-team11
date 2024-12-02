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
import yumster.obj.*;

@WebServlet("/api/v1/cooking-methods/*")
@MultipartConfig
public class CookingMethodsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(CookingMethodsServlet.class);
    private final CookingMethodDaoImpl cookingMethodDao = new CookingMethodDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        addCorsHeaders(response);
        
        try {
            User user = authenticateUser(request);
            if (user == null) {
                sendError(response, "Unauthorized", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<Integer> userMethods = cookingMethodDao.getUserMethods(user.getId());
            List<CookingMethod> allMethods = cookingMethodDao.getAllMethods();

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.add("methods", gson.toJsonTree(allMethods));
            jsonResponse.add("userMethods", gson.toJsonTree(userMethods));
            
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
        addCorsHeaders(response);
        
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

            UpdateMethodsRequest updateRequest = gson.fromJson(requestBody, UpdateMethodsRequest.class);
            
            if (updateRequest == null || updateRequest.getMethodIds() == null) {
                sendError(response, "Invalid request format", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean updated = cookingMethodDao.updateUserMethods(user.getId(), updateRequest.getMethodIds());
            
            if (updated) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Cooking methods updated successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                sendError(response, "Failed to update cooking methods", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error in doPost: " + e.getMessage(), e);
            sendError(response, "Internal server error: " + e.getMessage(), 
                     HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private void sendError(HttpServletResponse response, String message, int status) 
            throws IOException {
        response.setStatus(status);
        JsonObject error = new JsonObject();
        error.addProperty("status", "error");
        error.addProperty("message", message);
        response.getWriter().write(error.toString());
    }

    public static class UpdateMethodsRequest {
        private List<Integer> methodIds;
        
        public UpdateMethodsRequest() {}
        
        public UpdateMethodsRequest(List<Integer> methodIds) {
            this.methodIds = methodIds;
        }
        
        public List<Integer> getMethodIds() { return methodIds; }
        public void setMethodIds(List<Integer> methodIds) { this.methodIds = methodIds; }
    }
}
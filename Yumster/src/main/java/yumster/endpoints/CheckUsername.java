package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import yumster.dao.UserDaoImpl;

@WebServlet("/api/v1/auth/check-username")
public class CheckUsername extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDaoImpl userDao = new UserDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String requestBody = request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
            JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
            String username = requestJson.get("username").getAsString();

            boolean exists = userDao.checkExists("", username);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("exists", exists);
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Invalid request format");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(error.toString());
        }
    }
}
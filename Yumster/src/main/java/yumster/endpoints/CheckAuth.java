package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

@WebServlet("/Yumster/api/v1/auth/checkingNow")
public class CheckAuth extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    response.setContentType("application/json");
	    UserDaoImpl userDao = new UserDaoImpl();
	    UserTokenDaoImpl userTokenDao = new UserTokenDaoImpl();

	    JsonObject jsonResponse = new JsonObject();
	    jsonResponse.addProperty("status", "success");  // Add status field

	    Cookie[] cookies = request.getCookies();
	    if (cookies == null) {
	        jsonResponse.addProperty("authenticated", false);
	        response.getWriter().print(jsonResponse.toString());
	        return;
	    }

	    User user = null;
	    for (Cookie cookie : cookies) {
	        if (cookie.getName().equals("token")) {
	            UserToken userToken = userTokenDao.getByToken(cookie.getValue());
	            if (userToken != null) {
	                user = userDao.getById(userToken.getUserId());
	            }
	            break;
	        }
	    }

	    jsonResponse.addProperty("authenticated", user != null);
	    response.getWriter().print(jsonResponse.toString());
	}
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
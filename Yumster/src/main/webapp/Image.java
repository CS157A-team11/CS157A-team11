package yumster.endpoints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.dao.RecipeDao;
import yumster.dao.RecipeDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/image/*")
@MultipartConfig

public class Image extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String PATH = "C:/";
	
    // Constructors
    public Image() {}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		RecipeDao recipeDao = new RecipeDaoImpl();
		
		

		String getIdString = request.getPathInfo(); // remove prepending /
		if (StringUtils.isEmpty(getIdString)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		getIdString = getIdString.substring(1);
		Integer getId;
		try {
			getId = Integer.valueOf(getIdString); 	
		} catch (NumberFormatException e) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
        String filename = request.getPathInfo().substring(1);
        File file = new File(PATH,filename);
        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        response.setHeader("Content-Length",String.valueOf(file.length()));
        response.setHeader("Content-Disposition","inline; filename=\""+filename +"\"");
        Files.copy(file.toPath(),response.getOutputStream());
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDaoImpl userDao = new UserDaoImpl();
		UserTokenDaoImpl userTokenDao = new UserTokenDaoImpl();
		RecipeDao recipeDao = new RecipeDaoImpl();
		
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated Token");
			response.getWriter().print(res.toJson());
			return;
		}
		User user = null;
		for (int i = 0; i < cookies.length; i++) {
			String name = cookies[i].getName();
			String value = cookies[i].getValue();

			if (name.equals("token")) {
				UserToken userToken = userTokenDao.getByToken(value);
				if (userToken != null) {
					user = userDao.getById(userToken.getUserId()); // here because scoping
				} 
			}
		}
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated User");
			response.getWriter().print(res.toJson());
			return;
		}
		// authenticated
		
		String name = request.getParameter("name").trim();
		String instructions = request.getParameter("instructions").trim();
		String time = request.getParameter("time").trim();
		String servings = request.getParameter("servings").trim();

		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(instructions) ||
				StringUtils.isEmpty(time) || StringUtils.isEmpty(servings)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		Integer timeInt, servingsInt;
		try {
			timeInt = Integer.valueOf(time);
			servingsInt = Integer.valueOf(servings);
		} catch (NumberFormatException e) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		yumster.obj.Recipe recipe = new yumster.obj.Recipe();
		recipe.setName(name);
		recipe.setInstructions(instructions);
		recipe.setTime(timeInt);
		recipe.setServings(servingsInt);
		
		if (recipeDao.insert(recipe)) {			
			Response res = new Response();
			response.getWriter().print(res.toJson());
			return;
		};
		
		Response res = new Response("error", "Internal Server Error");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().print(res.toJson());
	}
	
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDaoImpl userDao = new UserDaoImpl();
		UserTokenDaoImpl userTokenDao = new UserTokenDaoImpl();
		RecipeDao recipeDao = new RecipeDaoImpl();
		
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated");
			response.getWriter().print(res.toJson());
			return;
		}
		User user = null;
		for (int i = 0; i < cookies.length; i++) {
			String name = cookies[i].getName();
			String value = cookies[i].getValue();

			if (name.equals("token")) {
				UserToken userToken = userTokenDao.getByToken(value);
				if (userToken != null) {
					user = userDao.getById(userToken.getUserId()); // here because scoping
				} 
			}
		}
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated");
			response.getWriter().print(res.toJson());
			return;
		}
		// authenticated
		
		String getIdString = request.getPathInfo(); // remove prepending /
		if (StringUtils.isEmpty(getIdString)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		getIdString = getIdString.substring(1);
		Integer getId;
		try {
			getId = Integer.valueOf(getIdString); 	
		} catch (NumberFormatException e) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		yumster.obj.Recipe recipe = recipeDao.getById(getId);
		
		if (recipe.getUserId() != user.getId()) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		// Owner of Recipe
		
		if (recipeDao.delete(recipe)) {
			Response res = new Response();
			response.getWriter().print(res.toJson());
			return;
		};
		
		Response res = new Response("error", "Internal Server Error");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().print(res.toJson());
		return;
		
	}
}
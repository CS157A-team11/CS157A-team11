package yumster.endpoints;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

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
import com.google.gson.reflect.TypeToken;

import yumster.dao.UserDao;
import yumster.dao.UserTokenDao;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.dao.RecipeDao;
import yumster.dao.RecipeDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

@WebServlet("/api/v1/recipe-filter")
@MultipartConfig
public class RecipeFilter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Handles GET requests for filtering recipes.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDao userDao = new UserDaoImpl();
		UserTokenDao userTokenDao = new UserTokenDaoImpl();
		RecipeDao recipeDao = new RecipeDaoImpl();
		
		// Authenticate the user
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated Token");
			response.getWriter().print(res.toJson());
			return;
		}
		User user = null;
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();

			if (name.equals("token")) {
				UserToken userToken = userTokenDao.getByToken(value);
				if (userToken != null) {
					user = userDao.getById(userToken.getUserId());
				}
			}
		}

		// Parse query parameters
		String keywordsStr = request.getParameter("keywords");
		String latestStr = request.getParameter("latest");
		String useUserIngredientsStr = request.getParameter("useUserIngredients");

		// Validate keywords parameter
		if (StringUtils.isEmpty(keywordsStr)) {
			Response res = new Response("error", "Required field 'keywords' not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}

		// Parse and process keywords
		List<String> keywords = Arrays.asList(keywordsStr.split(","));

		// Parse optional parameters
		boolean latest = latestStr != null && latestStr.equalsIgnoreCase("true");
		boolean useUserIngredients = useUserIngredientsStr != null && useUserIngredientsStr.equalsIgnoreCase("true");

		// Call the RecipeDaoImpl.filter method
		List<yumster.obj.Recipe> recipes = recipeDao.filter(user != null ? user.getId() : 0, keywords, latest, useUserIngredients);

		// Handle null results
		if (recipes == null) {
			Response res = new Response("error", "Something went wrong.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(res.toJson());
			return;
		}

		// Build the response with the filtered recipes
		Response res = new Response();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		JsonElement jsonElement = gson.toJsonTree(res);
		jsonElement.getAsJsonObject().add("recipes", gson.toJsonTree(recipes));
		response.getWriter().print(gson.toJson(jsonElement));
	}

	/**
	 * Rejects POST requests for this endpoint.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
}

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
import yumster.dao.IngredientDao;
import yumster.dao.IngredientDaoImpl;
import yumster.dao.RecipeCookingMethodDao;
import yumster.dao.RecipeCookingMethodDaoImpl;
import yumster.dao.RecipeIngredientsDao;
import yumster.dao.RecipeIngredientsDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/recipe-search")
@MultipartConfig

public class RecipeSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDao userDao = new UserDaoImpl();
		UserTokenDao userTokenDao = new UserTokenDaoImpl();
		RecipeDao recipeDao = new RecipeDaoImpl();
		RecipeCookingMethodDao recipeCookingMethodDao = new RecipeCookingMethodDaoImpl();
		RecipeIngredientsDao recipeIngredientsDao = new RecipeIngredientsDaoImpl();
		
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

		String pageStr = request.getParameter("page").trim();
		String limitStr = request.getParameter("limit").trim();
		String sort = request.getParameter("sort");

		
		if (StringUtils.isEmpty(pageStr) || StringUtils.isEmpty(limitStr)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		if (sort.isEmpty()) {
			sort = "upvotes";
		}

		Integer page;
		Integer limit;
		try {
			page = Integer.valueOf(pageStr); 
			limit = Integer.valueOf(limitStr);
		} catch (NumberFormatException e) {
			Response res = new Response("error", "Invalid Nums");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		List<String> allowedSort = Arrays.asList("recommended", "ingredients", "upvotes", "newest");
		if (allowedSort.indexOf(sort) == -1) {
			Response res = new Response("error", "Sort field not allowed.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		Integer min = limit*page-limit;
		List<yumster.obj.Recipe> recipes = null;

		if (user == null) {
			recipes = recipeDao.search(min, limit, sort, 0);
		} else {
			recipes = recipeDao.search(min, limit, sort, user.getId());			
		}
		
		if (recipes == null) {
			Response res = new Response("error", "Something went wrong.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(res.toJson());
			return;
		}
		// recipe w/id exists
		Response res = new Response();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		JsonElement jsonElement = gson.toJsonTree(res);
		jsonElement.getAsJsonObject().add("recipes", gson.toJsonTree(recipes));
		response.getWriter().print(gson.toJson(jsonElement));
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
}
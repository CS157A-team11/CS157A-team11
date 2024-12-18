package yumster.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
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
import yumster.dao.ImageDao;
import yumster.dao.ImageDaoImpl;
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
@WebServlet("/api/v1/recipe/*")
@MultipartConfig

public class Recipe extends HttpServlet {
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
		IngredientDao ingredientDao = new IngredientDaoImpl();
		RecipeCookingMethodDao recipeCookingMethodDao = new RecipeCookingMethodDaoImpl();

		
		Cookie[] cookies = request.getCookies();
		User user = null;
		if (cookies != null) {
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
		}

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
		List<yumster.obj.Ingredient> ingredients = ingredientDao.getIngredientsByRecipeId(getId);
		
		if (recipe == null) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		List<Integer> methods = recipeCookingMethodDao.getRecipeCookingMethods(recipe.getId());
		// recipe w/id exists
		Response res = new Response();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		JsonElement jsonElement = gson.toJsonTree(res);
		jsonElement.getAsJsonObject().add("data", gson.toJsonTree(recipe));
		jsonElement.getAsJsonObject().add("ingredients", gson.toJsonTree(ingredients));
		jsonElement.getAsJsonObject().add("cookingMethods", gson.toJsonTree(methods));
		jsonElement.getAsJsonObject().addProperty("owns", user == null ? false : recipe.getUserId() == user.getId());
		response.getWriter().print(gson.toJson(jsonElement));
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDao userDao = new UserDaoImpl();
		UserTokenDao userTokenDao = new UserTokenDaoImpl();
		RecipeDao recipeDao = new RecipeDaoImpl();
		RecipeCookingMethodDao recipeCookingMethodDao = new RecipeCookingMethodDaoImpl();
		RecipeIngredientsDao recipeIngredientsDao = new RecipeIngredientsDaoImpl();
		ImageDao imageDao = new ImageDaoImpl();
		
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
		
		Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
		Integer fn = null;
		boolean img = false;
		if (filePart != null && filePart.getSize() > 0) {
			String filePath = getServletContext().getRealPath("/") +"images/";
		    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
		    System.out.println(fileName);
		    System.out.println(filePath);
		    InputStream fileContent = filePart.getInputStream();
		    fn = imageDao.getNewId();
		    File file = new File(filePath + fn.toString() + ".jpg"); // Assumption.\
		    file.createNewFile(); 
		    try(OutputStream outputStream = new FileOutputStream(file)){
		        IOUtils.copy(fileContent, outputStream);
		    } catch (FileNotFoundException e) {
		    	System.out.println(e);
		        // handle exception here
		    } catch (IOException e) {
		    	System.out.println(e);
		        // handle exception here
		    }
		    img = true;
		}
		    
		String name = request.getParameter("name").trim();
		String instructions = request.getParameter("instructions").trim();
		String time = request.getParameter("time").trim();
		String servings = request.getParameter("servings").trim();
		String quantity_json = request.getParameter("quantity").trim();
		String ingredients_json = request.getParameter("ingredients").trim();
		String methods_json = request.getParameter("methods").trim();

		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(instructions) ||
				StringUtils.isEmpty(time) || StringUtils.isEmpty(servings) ||
				StringUtils.isEmpty(quantity_json) || StringUtils.isEmpty(ingredients_json) ||
				StringUtils.isEmpty(methods_json)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		Gson gsonParser = new Gson();
		Type listType = new TypeToken<List<Integer>>(){}.getType();
		List<Integer> quantity = gsonParser.fromJson(quantity_json, listType);
		List<Integer> ingredients = gsonParser.fromJson(ingredients_json, listType);
		List<Integer> methods = gsonParser.fromJson(methods_json, listType);
		
		if (quantity.size() != ingredients.size() || quantity.size() == 0) {
			Response res = new Response("error", "Required Field not filled or incorrectly filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}

		Integer timeInt, servingsInt;
		try {
			timeInt = Integer.valueOf(time);
			servingsInt = Integer.valueOf(servings);
		} catch (NumberFormatException e) {
			Response res = new Response("error", "Invalid Numbers");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		yumster.obj.Recipe recipe = new yumster.obj.Recipe();
		recipe.setUserId(user.getId());
		recipe.setName(name);
		recipe.setInstructions(instructions);
		recipe.setTime(timeInt);
		recipe.setServings(servingsInt);
		
		if (recipeDao.insert(recipe)) {			
			recipe = recipeDao.getLatestByUserId(user.getId());
			if (img) recipeDao.setImage(new Integer(recipe.getId()), fn);
			recipeCookingMethodDao.insertRecipeMethods(recipe.getId(), methods);
			recipeIngredientsDao.insertRecipeIngredients(recipe.getId(), ingredients, quantity);
			Response res = new Response();
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			JsonElement jsonElement = gson.toJsonTree(res);
			jsonElement.getAsJsonObject().addProperty("recipeId", recipe.getId());
			response.getWriter().print(gson.toJson(jsonElement));
			return;
		};
		
		Response res = new Response("error", "Internal Server Error");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().print(res.toJson());
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		response.setContentType("application/json");
		UserDao userDao = new UserDaoImpl();
		UserTokenDao userTokenDao = new UserTokenDaoImpl();
		RecipeDao recipeDao = new RecipeDaoImpl();
		RecipeCookingMethodDao recipeCookingMethodDao = new RecipeCookingMethodDaoImpl();
		RecipeIngredientsDao recipeIngredientsDao = new RecipeIngredientsDaoImpl();
		ImageDao imageDao = new ImageDaoImpl();
		
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
		
		if (recipe == null) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		// recipe exists
		
		if (recipe.getUserId() != user.getId()) {
			Response res = new Response("error", "Invalid ID");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		// is owner of recipe
		
		Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
		Integer fn = null;
		boolean img = false;
		if (filePart != null && filePart.getSize() > 0) {
			String filePath = getServletContext().getRealPath("/") +"images/";
		    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
//		    System.out.println(fileName);
//		    System.out.println(filePath);
		    InputStream fileContent = filePart.getInputStream();
		    fn = imageDao.getNewId();
		    File file = new File(filePath + fn.toString() + ".jpg"); // Assumption.
		    file.createNewFile(); 
		    try(OutputStream outputStream = new FileOutputStream(file)){
		        IOUtils.copy(fileContent, outputStream);
		        img = true;
		    } catch (FileNotFoundException e) {
		    	System.out.println(e);
		        // handle exception here
		    } catch (IOException e) {
		    	System.out.println(e);
		        // handle exception here
		    }
		}
		    
		String name = request.getParameter("name").trim();
		String instructions = request.getParameter("instructions").trim();
		String time = request.getParameter("time").trim();
		String servings = request.getParameter("servings").trim();
		String quantity_json = request.getParameter("quantity").trim();
		String ingredients_json = request.getParameter("ingredients").trim();
		String methods_json = request.getParameter("methods").trim();

		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(instructions) ||
				StringUtils.isEmpty(time) || StringUtils.isEmpty(servings) ||
				StringUtils.isEmpty(quantity_json) || StringUtils.isEmpty(ingredients_json) ||
				StringUtils.isEmpty(methods_json)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		Gson gsonParser = new Gson();
		Type listType = new TypeToken<List<Integer>>(){}.getType();
		List<Integer> quantity = gsonParser.fromJson(quantity_json, listType);
		List<Integer> ingredients = gsonParser.fromJson(ingredients_json, listType);
		List<Integer> methods = gsonParser.fromJson(methods_json, listType);
		
		if (quantity.size() != ingredients.size() || quantity.size() == 0) {
			Response res = new Response("error", "Required Field not filled or incorrectly filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}

		Integer timeInt, servingsInt;
		try {
			timeInt = Integer.valueOf(time);
			servingsInt = Integer.valueOf(servings);
		} catch (NumberFormatException e) {
			Response res = new Response("error", "Invalid Numbers");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		recipe.setName(name);
		recipe.setInstructions(instructions);
		recipe.setTime(timeInt);
		recipe.setServings(servingsInt);
		
		if (recipeDao.update(recipe)) {			
			if (img) recipeDao.setImage(new Integer(recipe.getId()), fn);
			recipeCookingMethodDao.deleteRecipeMethods(recipe.getId()); // delete
			recipeCookingMethodDao.insertRecipeMethods(recipe.getId(), methods); // add back
			recipeIngredientsDao.deleteRecipeIngredients(recipe.getId()); //delete
			recipeIngredientsDao.insertRecipeIngredients(recipe.getId(), ingredients, quantity); // add back
			Response res = new Response();
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			JsonElement jsonElement = gson.toJsonTree(res);
			jsonElement.getAsJsonObject().addProperty("recipeId", recipe.getId());
			response.getWriter().print(gson.toJson(jsonElement));
			return;
		};
		
		Response res = new Response("error", "Internal Server Error");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().print(res.toJson());
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDao userDao = new UserDaoImpl();
		UserTokenDao userTokenDao = new UserTokenDaoImpl();
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
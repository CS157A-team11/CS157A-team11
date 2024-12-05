package yumster.dao;

import java.util.List;
import java.util.Map;
import yumster.obj.Recipe;

public interface RecipeDao {
    List<Recipe> getAllRecipes();
    Map<Character, List<Recipe>> getRecipesByAlphabet();
    List<Recipe> getUserFavorites(int userId);
    boolean addUserFavorite(int userId, int recipeId);
    boolean removeUserFavorite(int userId, int recipeId);
    boolean updateUserFavorites(int userId, List<Integer> recipeIds);
    Recipe getById(Integer id);  // Add this method
    boolean insert(Recipe recipe);  // Add this if not already present
    boolean update(Recipe recipe);
    boolean delete(Recipe recipe);  // Add this if not already present
    Recipe getLatestByUserId(Integer id);
    List<Recipe> search(Integer min, Integer limit, String sort, Integer userId);
    boolean setImage(Integer recipeId, Integer imageId);
}
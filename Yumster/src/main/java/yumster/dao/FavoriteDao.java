package yumster.dao;

import java.util.List;
import yumster.obj.Recipe;

public interface FavoriteDao {
    boolean addFavorite(int userId, int recipeId);
    boolean removeFavorite(int userId, int recipeId);
    List<Recipe> getUserFavorites(int userId);
    boolean updateUserFavorites(int userId, List<Integer> recipeIds);
    boolean checkFavoriteExists(int userId, int recipeId);
}
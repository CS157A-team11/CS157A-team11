package yumster.dao;

import java.util.List;
import yumster.obj.Ingredient;



public interface DietaryRestrictionDao {
    List<Ingredient> searchIngredients(String keyword);
    List<Integer> getRestrictedRecipes(int userId, List<Integer> ingredientIds);
    boolean addUserRestriction(int userId, int ingredientId);
    boolean removeUserRestriction(int userId, int ingredientId);
    List<Ingredient> getUserRestrictions(int userId);
    boolean updateUserRestrictions(int userId, List<Integer> ingredientIds);
}
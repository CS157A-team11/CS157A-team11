package yumster.dao;

import java.util.List;

public interface RecipeIngredientsDao {
    List<Integer> getRecipeIngredients(int recipeId);
    boolean insertRecipeIngredients(int recipeId, List<Integer> ingredients, List<Integer> quantity);
    boolean deleteRecipeIngredients(int recipeId);
}
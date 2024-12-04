package yumster.dao;

import java.util.List;
import yumster.obj.CookingMethod;

public interface RecipeCookingMethodDao {
    List<Integer> getRecipeCookingMethods(int recipeId);
    boolean insertRecipeMethods(int recipeId, List<Integer> methodIds);
    boolean deleteRecipeMethods(int recipeId);
}
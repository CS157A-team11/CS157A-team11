package yumster.dao;

import java.util.List;
import java.util.Map;

import yumster.obj.Ingredient;

public interface IngredientDao {
    List<Ingredient> getIngredientByKeywords(List<String> keywords);
    Ingredient getIngredientById(int id);
}
package yumster.dao;

import java.util.List;

import yumster.obj.Recipe;

public interface RecipeDao {
	public boolean insert(Recipe recipe);
	
	/**
	 * Checks if a Recipe with that ID exists
	 * @param int id
	 * @return boolean status, success or fail
	 */
	public boolean checkExists(int id);
	
	public List<Recipe> getByUserId(int id);
	
	public Recipe getById(int id);
	
	public boolean update(Recipe recipe);
	
	default public boolean delete(Recipe recipe) {
		return deleteById(recipe.getRecipeId());
	};
	
	public boolean deleteById(int id);
}
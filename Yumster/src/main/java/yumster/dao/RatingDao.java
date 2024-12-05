package yumster.dao;

public interface RatingDao {
	
    boolean addRating(int userId, int recipeId, int rating);
    boolean updateRating(int userId, int recipeId, int rating);
    Integer getUserRating(int userId, int recipeId);
    boolean updateRecipeReputation(int recipeId);
    int getRecipeReputation(int recipeId);
}

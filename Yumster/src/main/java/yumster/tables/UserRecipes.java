package yumster.tables;

public class UserRecipes {
    private int userId;
    private int recipeId;

    // Constructors
    public UserRecipes(int userId, int recipeId) {
        super();
        this.userId = userId;
        this.recipeId = recipeId;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}

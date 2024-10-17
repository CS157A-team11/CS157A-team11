package yumster.tables;

public class RecipeIngredients {
    private int recipeId;
    private int ingredientId;

    // Constructors
    public RecipeIngredients(int recipeId, int ingredientId) {
        super();
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }

    // Getters and Setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }
}

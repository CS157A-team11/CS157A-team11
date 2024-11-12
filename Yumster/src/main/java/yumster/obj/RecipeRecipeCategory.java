package yumster.obj;

public class RecipeRecipeCategory {
    private int recipeId;
    private int categoryId;

    // Constructors
    public RecipeRecipeCategory(int recipeId, int categoryId) {
        super();
        this.recipeId = recipeId;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}

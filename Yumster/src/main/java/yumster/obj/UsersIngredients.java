package yumster.obj;

public class UsersIngredients {
    private int userId;
    private int ingredientId;

    // Constructors
    public UsersIngredients(int userId, int ingredientId) {
        super();
        this.userId = userId;
        this.ingredientId = ingredientId;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }
}

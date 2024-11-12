package yumster.obj;

public class IngredientAlternative {
    private int alternativeId;      // Primary key
    private int ingredientId;
    private int ingredientId2;
    
    // Constructors
    public IngredientAlternative(int alternativeId, int ingredientId, int ingredientId2) {
        super();
        this.alternativeId = alternativeId;
        this.ingredientId = ingredientId;
        this.ingredientId2 = ingredientId2;
    }
    
    public IngredientAlternative() {}

    // Getters and Setters
    public int getAlternativeId() {
        return alternativeId;
    }

    public void setAlternativeId(int alternativeId) {
        this.alternativeId = alternativeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public int getIngredientId2() {
        return ingredientId2;
    }

    public void setIngredientId2(int ingredientId2) {
        this.ingredientId2 = ingredientId2;
    }
}

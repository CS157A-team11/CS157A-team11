package yumster.obj;

public class SpicyRecipes {
    private int recipeId;
    private String spicinessLevel;

    // Constructors
    public SpicyRecipes(int recipeId, String spicinessLevel) {
        super();
        this.recipeId = recipeId;
        this.spicinessLevel = spicinessLevel;
    }

    // Getters and Setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getSpicinessLevel() {
        return spicinessLevel;
    }

    public void setSpicinessLevel(String spicinessLevel) {
        this.spicinessLevel = spicinessLevel;
    }
}

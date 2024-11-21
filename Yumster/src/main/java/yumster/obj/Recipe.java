package yumster.obj;

import com.google.gson.annotations.Expose;

public class Recipe {
    private int recipeId;
    @Expose private String name;
    @Expose private String instructions;
    @Expose private int time;
    @Expose private int servings;
    private int userId;

    // Constructors
    public Recipe(int recipeId, String name, String instructions, int time, int servings, int userId) {
        super();
        this.recipeId = recipeId;
        this.name = name;
        this.instructions = instructions;
        this.time = time;
        this.servings = servings;
        this.userId = userId;
    }

    public Recipe() {}

    // Getters and Setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

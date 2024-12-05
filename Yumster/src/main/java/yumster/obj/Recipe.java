package yumster.obj;

import com.google.gson.annotations.Expose;

public class Recipe {
    @Expose private int id;
    @Expose private String name;
    @Expose private String instructions;
    @Expose private int time;
    @Expose private int servings;
    @Expose private int reputation;
    @Expose private int imageId;
    private int userId;

    public Recipe() {}
    
    public Recipe(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }
    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    public void setImageId(int imageId) { this.imageId = imageId; }
}
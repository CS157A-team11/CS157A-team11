package yumster.obj;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private int time;
    private int servings;
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
}
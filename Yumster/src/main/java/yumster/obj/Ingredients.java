package yumster.obj;

public class Ingredients {
    private int ingredientId;     // Primary key
    private String ingredientName;
    private int kcals;
    private String alias;
    private String macros;
    
    // Constructors
    public Ingredients(int ingredientId, String ingredientName, int kcals, String alias, String macros) {
        super();
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.kcals = kcals;
        this.alias = alias;
        this.macros = macros;
    }
    
    public Ingredients() {}

    // Getters and Setters
    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getKcals() {
        return kcals;
    }

    public void setKcals(int kcals) {
        this.kcals = kcals;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMacros() {
        return macros;
    }

    public void setMacros(String macros) {
        this.macros = macros;
    }
}

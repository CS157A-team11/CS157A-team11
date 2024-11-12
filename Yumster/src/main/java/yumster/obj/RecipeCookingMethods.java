package yumster.obj;

public class RecipeCookingMethods {
    private int methodId;
    private String cookingMethod;

    // Constructors
    public RecipeCookingMethods(int methodId, String cookingMethod) {
        super();
        this.methodId = methodId;
        this.cookingMethod = cookingMethod;
    }

    public RecipeCookingMethods() {}

    // Getters and Setters
    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public String getCookingMethod() {
        return cookingMethod;
    }

    public void setCookingMethod(String cookingMethod) {
        this.cookingMethod = cookingMethod;
    }
}
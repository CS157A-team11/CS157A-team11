package yumster.tables;

public class CookingMethods {
    private int methodId;
    private String name;

    // Constructors
    public CookingMethods(int methodId, String name) {
        super();
        this.methodId = methodId;
        this.name = name;
    }

    public CookingMethods() {}

    // Getters and Setters
    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

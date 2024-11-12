package yumster.obj;

public class UserCookingMethod {
    private int userId;
    private int methodId;

    // Constructors
    public UserCookingMethod(int userId, int methodId) {
        super();
        this.userId = userId;
        this.methodId = methodId;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }
}

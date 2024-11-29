package yumster.obj;

public class CookingMethod {
    private int id;
    private String name;

    public CookingMethod() {}
    
    public CookingMethod(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "CookingMethod{id=" + id + ", name='" + name + "'}";
    }
}
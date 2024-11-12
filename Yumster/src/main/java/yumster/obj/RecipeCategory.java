package yumster.obj;

public class RecipeCategory {
    private int categoryId;
    private CategoryName categoryName;  
    private CategoryType categoryType;  
    
    // Constructors
    public RecipeCategory(int categoryId, CategoryName categoryName, CategoryType categoryType) {
        super();
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }
    
    public RecipeCategory() {}

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(CategoryName categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }
}

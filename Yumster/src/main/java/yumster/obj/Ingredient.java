package yumster.obj;

import com.google.gson.annotations.Expose;

public class Ingredient {
    @Expose private int ingredientId; // Primary key
    @Expose private String ingredientName;
    @Expose private int energy; //kcals
    @Expose private String foodCategory, dataType;
    @Expose private double totalFat, transFat, polyUnsatFat, monoUnsatFat, satFat, sodium, totalCarbs;
    @Expose private double dietaryFiber, totalSugar, addedSugar, protein;
    @Expose private int quantity;
    // Constructors
    public Ingredient(int ingredientId, String ingredientName, int energy, String foodCategory, String dataType,
    		double totalFat, double transFat, double polyUnsatFat, double monoUnsatFat, double satFat, double sodium,
    		double totalCarbs, double dietaryFiber, double totalSugar, double addedSugar, double protein) {
        super();
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.energy = energy;
        this.foodCategory = foodCategory;
        this.dataType = dataType;
        this.totalFat = totalFat;
        this.transFat = transFat;
        this.polyUnsatFat = polyUnsatFat;
        this.monoUnsatFat = monoUnsatFat;
        this.satFat = satFat;
        this.sodium = sodium;
        this.totalCarbs = totalCarbs;
        this.dietaryFiber = dietaryFiber;
        this.totalSugar = totalSugar;
        this.addedSugar = addedSugar;
        this.protein = protein;
    }
    
    public Ingredient() {}

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

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public double getTransFat() {
        return transFat;
    }

    public void setTransFat(double transFat) {
        this.transFat = transFat;
    }

    public double getPolyUnsatFat() {
        return polyUnsatFat;
    }

    public void setPolyUnsatFat(double polyUnsatFat) {
        this.polyUnsatFat = polyUnsatFat;
    }

    public double getMonoUnsatFat() {
        return monoUnsatFat;
    }

    public void setMonoUnsatFat(double monoUnsatFat) {
        this.monoUnsatFat = monoUnsatFat;
    }

    public double getSatFat() {
        return satFat;
    }

    public void setSatFat(double satFat) {
        this.satFat = satFat;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public double getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(double dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public double getTotalSugar() {
        return totalSugar;
    }

    public void setTotalSugar(double totalSugar) {
        this.totalSugar = totalSugar;
    }

    public double getAddedSugar() {
        return addedSugar;
    }

    public void setAddedSugar(double addedSugar) {
        this.addedSugar = addedSugar;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }
    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
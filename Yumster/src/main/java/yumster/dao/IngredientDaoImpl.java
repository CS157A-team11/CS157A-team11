package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.Ingredient;
import yumster.obj.Recipe;

public class IngredientDaoImpl implements IngredientDao {
    private Log log = LogFactory.getLog(IngredientDaoImpl.class);

    public Ingredient getIngredientById(int id) {
        try {
        	DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            String sql = "SELECT IngredientName, energy, foodCategory, dataType, totalFat, "
            		+ "transFat, polyUnsatFat, monoUnsatFat, satFat, sodium, totalCarbs, dietaryFiber, "
            		+ "totalSugar, addedSugar, protein FROM ingredients WHERE ingredientID = ?;";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();
	            
	            if (rs.next()) {
	                Ingredient ingredient = new Ingredient(
	                		id, rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getDouble(5),
	                		rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getDouble(9), rs.getDouble(10),
	                		rs.getDouble(11), rs.getDouble(12), rs.getDouble(13), rs.getDouble(14), rs.getDouble(15));
	                return ingredient;
	            }
            }
        } catch (SQLException e) {
            log.error("Error getting ingredient by ID: " + e.getMessage(), e);
        }
        return null;
    };

	
	public List<Ingredient> getIngredientByKeywords(List<String> keywords) {
        try {
        	DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            StringBuilder sql = new StringBuilder("SELECT ingredientID, ingredientName, energy, foodCategory, dataType, totalFat, "
            		+ "transFat, polyUnsatFat, monoUnsatFat, satFat, sodium, totalCarbs, dietaryFiber, "
            		+ "totalSugar, addedSugar, protein FROM ingredients WHERE ingredientName LIKE ?");
            for (int i = 1; i < keywords.size(); i++) {
            	sql.append(" AND ingredientName LIKE ?");
            }
            sql.append(";");
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            	for (int i = 0; i < keywords.size(); i++) {
                	ps.setString(i+1, "%" + 
                			keywords.get(i).replace("!", "!!")
                		    .replace("%", "!%")
                		    .replace("_", "!_")
                		    .replace("[", "![") + "%");
                }
	            ResultSet rs = ps.executeQuery();
	            List<Ingredient> ingredients = new ArrayList<Ingredient>();
	            while (rs.next()) {
	                Ingredient ingredient = new Ingredient(
	                		rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5),
	                		rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getDouble(9), rs.getDouble(10),
	                		rs.getDouble(11), rs.getDouble(12), rs.getDouble(13), rs.getDouble(14), rs.getDouble(15),
	                		rs.getDouble(16));
	                ingredients.add(ingredient);
	            }
	            return ingredients;
            }
        } catch (SQLException e) {
            log.error("Error getting ingredients: " + e.getMessage(), e);
        }
        return null;
    };
    
    public List<Ingredient> getIngredientsByRecipeId(int id) {
    	try {
        	DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            StringBuilder sql = new StringBuilder("SELECT ingredients.ingredientID, ingredientName, energy, foodCategory, dataType, totalFat, "
            		+ "transFat, polyUnsatFat, monoUnsatFat, satFat, sodium, totalCarbs, dietaryFiber, "
            		+ "totalSugar, addedSugar, protein, quantity FROM ingredients, recipe_ingredients "
            		+ "WHERE recipe_ingredients.recipeId = ? AND ingredients.ingredientId = recipe_ingredients.ingredientId;");
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            	ps.setInt(1, id);
            	ResultSet rs = ps.executeQuery();
	            List<Ingredient> ingredients = new ArrayList<Ingredient>();
	            while (rs.next()) {
	            	System.out.println(rs.getString(2));
	                Ingredient ingredient = new Ingredient(
	                		rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5),
	                		rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getDouble(9), rs.getDouble(10),
	                		rs.getDouble(11), rs.getDouble(12), rs.getDouble(13), rs.getDouble(14), rs.getDouble(15),
	                		rs.getDouble(16));
	                ingredient.setQuantity(rs.getInt(17));
	                ingredients.add(ingredient);
	            }
	            return ingredients;
            }
        } catch (SQLException e) {
            log.error("Error getting ingredients: " + e.getMessage(), e);
        }
        return null;
    }
}
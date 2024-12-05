package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.Ingredient;
import yumster.obj.Recipe;

public class RecipeIngredientsDaoImpl implements RecipeIngredientsDao {
    private Log log = LogFactory.getLog(RecipeIngredientsDaoImpl.class);

    public List<Integer> getRecipeIngredients(int recipeId) {    
        try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            String sql = "SELECT ingredientId, quantity FROM recipe_ingredients WHERE recipeId = ?;";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
        	ps.setInt(1, recipeId);
            ResultSet rs = ps.executeQuery();
            List<Integer> ret = new ArrayList<Integer>();
            while (rs.next()) {
                ret.add(rs.getInt(1));
            }
            return ret;
        }
        } catch (SQLException e) {
            log.error("Error getting all recipes: " + e.getMessage(), e);
        }
        return null;
    };
    
    public boolean insertRecipeIngredients(int recipeId, List<Integer> ingredients, List<Integer> quantity) {
    	try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            StringBuilder sql = new StringBuilder("INSERT INTO recipe_ingredients (recipeId, ingredientId, quantity) VALUES");
            sql.append(" (?, ?, ?)");
            for (int i = 1; i < ingredients.size(); i++) {            	
            	sql.append(", (?, ?, ?)");
            }
            sql.append(";");
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
        	for (int i = 0; i < ingredients.size(); i++) {            	
            	ps.setInt((3*i)+1, recipeId);
            	ps.setInt((3*i)+2, ingredients.get(i));
            	ps.setInt((3*i)+3, quantity.get(i));
            }
            return ps.execute();
        }
        } catch (SQLException e) {
            log.error("Error getting all recipes: " + e.getMessage(), e);
        }
        return false;
    };
    
    public boolean deleteRecipeIngredients(int recipeId) {
    	try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            String sql = "DELETE FROM recipe_ingredients WHERE recipeId = ?;";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
            	return ps.execute();
            }
        } catch (SQLException e) {
            log.error("Error getting all recipes: " + e.getMessage(), e);
        }
        return false;
    };
}
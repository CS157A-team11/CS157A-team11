package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.CookingMethod;
import yumster.obj.Recipe;

public class RecipeCookingMethodDaoImpl implements RecipeCookingMethodDao {
    private Log log = LogFactory.getLog(RecipeCookingMethodDaoImpl.class);

    public List<Integer> getRecipeCookingMethods(int recipeId) {    
        try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            String sql = "SELECT MethodId FROM recipe_cookingmethods WHERE recipeId = ?;";
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
    
    public boolean insertRecipeMethods(int recipeId, List<Integer> methodIds) {
    	try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            StringBuilder sql = new StringBuilder("INSERT INTO recipe_cookingmethods (recipeId, methodId) VALUES");
            sql.append(" (?, ?)");
            for (int i = 1; i < methodIds.size(); i++) {            	
            	sql.append(", (?, ?)");
            }
            sql.append(";");
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
        	for (int i = 0; i < methodIds.size(); i++) {            	
            	ps.setInt((2*i)+1, recipeId);
            	ps.setInt((2*i)+2, methodIds.get(i));
            }
            return ps.execute();
        }
        } catch (SQLException e) {
            log.error("Error getting all recipes: " + e.getMessage(), e);
        }
        return false;
    };
    
    public boolean deleteRecipeMethods(int recipeId) {
    	try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            String sql = "DELETE FROM recipe_cookingmethods WHERE recipeId = ?;";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
            	return ps.execute();
            }
        } catch (SQLException e) {
            log.error("Error getting all recipes: " + e.getMessage(), e);
        }
        return false;
    };
}
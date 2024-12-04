package yumster.dao;

import java.sql.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import yumster.obj.Ingredient;

public class DietaryRestrictionDaoImpl implements DietaryRestrictionDao {
    private Log log = LogFactory.getLog(DietaryRestrictionDaoImpl.class);
    
    public List<Ingredient> searchIngredients(String keyword) {
        List<Ingredient> ingredients = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT IngredientID, IngredientName FROM ingredients WHERE IngredientName LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();
            
            while (rs.next()) {
                // Use constructor instead of setters
                Ingredient ingredient = new Ingredient(
                    rs.getInt("IngredientID"),
                    rs.getString("IngredientName"),
                    0, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            log.error("Error searching ingredients: " + e.getMessage());
        } finally {
            closeResources(rs, ps, con);
        }
        return ingredients;
    }

    public List<Integer> getRestrictedRecipes(int userId, List<Integer> ingredientIds) {
        List<Integer> restrictedRecipes = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT ri.RecipeID FROM recipe_ingredients ri " +
                "WHERE ri.IngredientID IN (");
            for (int i = 0; i < ingredientIds.size(); i++) {
                sql.append(i == 0 ? "?" : ", ?");
            }
            sql.append(")");
            
            ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < ingredientIds.size(); i++) {
                ps.setInt(i + 1, ingredientIds.get(i));
            }
            
            rs = ps.executeQuery();
            while (rs.next()) {
                restrictedRecipes.add(rs.getInt("RecipeID"));
            }
        } catch (SQLException e) {
            log.error("Error getting restricted recipes: " + e.getMessage());
        } finally {
            closeResources(rs, ps, con);
        }
        return restrictedRecipes;
    }

    
        
    public boolean addUserRestriction(int userId, int ingredientId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
           
            if (userId <= 0 || ingredientId <= 0) {
                log.error("Invalid input - UserId: " + userId + ", IngredientId: " + ingredientId);
                return false;
            }

            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            con.setAutoCommit(false);  // Start transaction

            // Check if restriction already exists
            String checkSql = "SELECT 1 FROM user_restrictions WHERE UserID = ? AND IngredientID = ?";
            ps = con.prepareStatement(checkSql);
            ps.setInt(1, userId);
            ps.setInt(2, ingredientId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                log.error("Restriction already exists - UserId: " + userId + ", IngredientId: " + ingredientId);
                return false;
            }

            
            rs.close();
            ps.close();

            String insertSql = "INSERT INTO user_restrictions (UserID, IngredientID) VALUES (?, ?)";
            ps = con.prepareStatement(insertSql);
            ps.setInt(1, userId);
            ps.setInt(2, ingredientId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                con.commit();
                return true;
            }

            con.rollback();
            log.error("Failed to add restriction - UserId: " + userId + ", IngredientId: " + ingredientId);
            return false;
            
            
        } catch (SQLException e) {
            log.error("Error adding user restriction - UserId: " + userId + 
                      ", IngredientId: " + ingredientId + 
                      ", Error: " + e.getMessage(), e);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    log.error("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            closeResources(rs, ps, con);
        }
    }
  
    public boolean removeUserRestriction(int userId, int ingredientId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "DELETE FROM user_restrictions WHERE UserID = ? AND IngredientID = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, ingredientId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error removing user restriction: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }

    public List<Ingredient> getUserRestrictions(int userId) {
        List<Ingredient> restrictions = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT i.IngredientID, i.IngredientName FROM ingredients i " +
                        "JOIN user_restrictions ur ON i.IngredientID = ur.IngredientID " +
                        "WHERE ur.UserID = ? ORDER BY i.IngredientName";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                    rs.getInt("IngredientID"),
                    rs.getString("IngredientName"),
                    0, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                );  
                restrictions.add(ingredient);
            }
        } catch (SQLException e) {
            log.error("Error getting user restrictions: " + e.getMessage());
        } finally {
            closeResources(rs, ps, con);
        }
        return restrictions;
    }
    
    private boolean verifyIngredientExists(Connection con, int ingredientId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM ingredients WHERE IngredientID = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, ingredientId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public boolean updateUserRestrictions(int userId, List<Integer> ingredientIds) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            con.setAutoCommit(false);
            

            String deleteSql = "DELETE FROM user_restrictions WHERE UserID = ?";
            ps = con.prepareStatement(deleteSql);
            ps.setInt(1, userId);
            ps.executeUpdate();
            
            String insertSql = "INSERT INTO user_restrictions (UserID, IngredientID) VALUES (?, ?)";
            ps = con.prepareStatement(insertSql);
            
            for (Integer ingredientId : ingredientIds) {
                ps.setInt(1, userId);
                ps.setInt(2, ingredientId);
                ps.addBatch();
            }
            
            ps.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) {
            log.error("Error updating user restrictions: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    log.error("Error rolling back: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection con) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            log.error("Error closing resources: " + e.getMessage());
        }
    }
}
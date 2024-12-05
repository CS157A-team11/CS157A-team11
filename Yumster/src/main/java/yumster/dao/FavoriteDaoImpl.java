package yumster.dao;

import java.sql.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import yumster.obj.Recipe;

public class FavoriteDaoImpl implements FavoriteDao {
    private Log log = LogFactory.getLog(FavoriteDaoImpl.class);
    
    public boolean addFavorite(int userId, int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "INSERT INTO user_recipe (UserID, RecipeID, Type) VALUES (?, ?, 'favorite')";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error adding favorite: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public boolean removeFavorite(int userId, int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "DELETE FROM user_recipe WHERE UserID = ? AND RecipeID = ? AND Type = 'favorite'";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error removing favorite: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public List<Recipe> getUserFavorites(int userId) {
        List<Recipe> favorites = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT r.RecipeID, r.Name FROM recipes r " +
                        "JOIN user_recipe ur ON r.RecipeID = ur.RecipeID " +
                        "WHERE ur.UserID = ? AND ur.Type = 'favorite' " +
                        "ORDER BY r.Name";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("RecipeID"));
                recipe.setName(rs.getString("Name"));
                favorites.add(recipe);
            }
        } catch (SQLException e) {
            log.error("Error getting user favorites: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return favorites;
    }
    
    public boolean updateUserFavorites(int userId, List<Integer> recipeIds) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getNonAutoCommitConnection();
            
            // Delete existing favorites
            String deleteSql = "DELETE FROM user_recipe WHERE UserID = ? AND Type = 'favorite'";
            ps = con.prepareStatement(deleteSql);
            ps.setInt(1, userId);
            ps.executeUpdate();
            
            // Insert new favorites
            String insertSql = "INSERT INTO user_recipe (UserID, RecipeID, Type) VALUES (?, ?, 'favorite')";
            ps = con.prepareStatement(insertSql);
            
            for (Integer recipeId : recipeIds) {
                ps.setInt(1, userId);
                ps.setInt(2, recipeId);
                ps.addBatch();
            }
            
            ps.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) {
            log.error("Error updating favorites: " + e.getMessage(), e);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    log.error("Error rolling back: " + ex.getMessage(), ex);
                }
            }
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public boolean checkFavoriteExists(int userId, int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT 1 FROM user_recipe WHERE UserID = ? AND RecipeID = ? AND Type = 'favorite'";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            rs = ps.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            log.error("Error checking favorite exists: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(rs, ps, con);
        }
    }
    
    private void closeResources(ResultSet rs, PreparedStatement ps, Connection con) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            log.error("Error closing resources: " + e.getMessage(), e);
        }
    }
}
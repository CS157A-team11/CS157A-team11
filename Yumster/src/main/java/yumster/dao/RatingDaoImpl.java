package yumster.dao;

import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RatingDaoImpl implements RatingDao {
    private Log log = LogFactory.getLog(RatingDaoImpl.class);
    
    @Override
    public boolean addRating(int userId, int recipeId, int rating) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            con.setAutoCommit(false);
            
            // Add/Update rating
            String sql = "INSERT INTO user_recipe_rating (UserID, RecipeID, Rating) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE Rating = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            ps.setInt(3, rating);
            ps.setInt(4, rating);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Ensure reputation is updated
                boolean reputationUpdated = updateRecipeReputation(recipeId);
                if (reputationUpdated) {
                    con.commit();
                    return true;
                }
                con.rollback();
            }
            return false;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    log.error("Error rolling back transaction: " + ex.getMessage());
                }
            }
            log.error("Error adding rating: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }

    @Override
    public Integer getUserRating(int userId, int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT Rating FROM user_recipe_rating WHERE UserID = ? AND RecipeID = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt("Rating") : null;
        } catch (SQLException e) {
            log.error("Error getting user rating: " + e.getMessage());
            return null;
        } finally {
            closeResources(rs, ps, con);
        }
    }

    @Override
    public int getRecipeReputation(int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT COALESCE(SUM(Rating), 0) as Reputation FROM user_recipe_rating WHERE RecipeID = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, recipeId);
            rs = ps.executeQuery();
            
            return rs.next() ? rs.getInt("Reputation") : 0;
        } catch (SQLException e) {
            log.error("Error getting recipe reputation: " + e.getMessage());
            return 0;
        } finally {
            closeResources(rs, ps, con);
        }
    }

    @Override
    public boolean updateRecipeReputation(int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            
            // First get the sum of ratings
            String sumSql = "SELECT COALESCE(SUM(Rating), 0) as TotalRating FROM user_recipe_rating WHERE RecipeID = ?";
            ps = con.prepareStatement(sumSql);
            ps.setInt(1, recipeId);
            ResultSet rs = ps.executeQuery();
            
            int totalRating = 0;
            if (rs.next()) {
                totalRating = rs.getInt("TotalRating");
            }
            rs.close();
            ps.close();

            // Then update the recipes table
            String updateSql = "UPDATE recipes SET Reputation = ? WHERE RecipeID = ?";
            ps = con.prepareStatement(updateSql);
            ps.setInt(1, totalRating);
            ps.setInt(2, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error updating recipe reputation: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }

    @Override
    public boolean updateRating(int userId, int recipeId, int rating) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            con.setAutoCommit(false);
            String sql = "UPDATE user_recipe_rating SET Rating = ? WHERE UserID = ? AND RecipeID = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, rating);
            ps.setInt(2, userId);
            ps.setInt(3, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                if (updateRecipeReputation(recipeId)) {
                    con.commit();
                    return true;
                }
            }
            con.rollback();
            return false;
        } catch (SQLException e) {
            log.error("Error updating rating: " + e.getMessage(), e);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    log.error("Error rolling back transaction: " + ex.getMessage(), ex);
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
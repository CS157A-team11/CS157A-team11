package yumster.dao;

import java.sql.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import yumster.obj.Recipe;

public class RecipeDaoImpl implements RecipeDao {
    private Log log = LogFactory.getLog(RecipeDaoImpl.class);

    public Map<Character, List<Recipe>> getRecipesByAlphabet() {
        Map<Character, List<Recipe>> recipesMap = new TreeMap<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT RecipeID, Name FROM recipes ORDER BY Name;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Recipe recipe = new Recipe(rs.getInt("RecipeID"), rs.getString("Name"));
                char firstLetter = recipe.getName().toUpperCase().charAt(0);
                recipesMap.computeIfAbsent(firstLetter, k -> new ArrayList<>()).add(recipe);
            }
        } catch (SQLException e) {
            log.error("Error getting recipes: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return recipesMap;
    }

    
    public boolean delete(Recipe recipe) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "DELETE FROM recipes WHERE RecipeID = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, recipe.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error deleting recipe: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    
    public boolean insert(Recipe recipe) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "INSERT INTO recipes (Name, Instructions, Time, Servings, UserID) VALUES (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql);
            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getInstructions());
            ps.setInt(3, recipe.getTime());
            ps.setInt(4, recipe.getServings());
            ps.setInt(5, recipe.getUserId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error inserting recipe: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public Recipe getById(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT RecipeID, Name, Instructions, Time, Servings, UserID FROM recipes WHERE RecipeID = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("RecipeID"));
                recipe.setName(rs.getString("Name"));
                recipe.setInstructions(rs.getString("Instructions"));
                recipe.setTime(rs.getInt("Time"));
                recipe.setServings(rs.getInt("Servings"));
                recipe.setUserId(rs.getInt("UserID"));
                return recipe;
            }
        } catch (SQLException e) {
            log.error("Error getting recipe by ID: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return null;
    }
    
    
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT RecipeID, Name FROM recipes ORDER BY Name;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("RecipeID"));
                recipe.setName(rs.getString("Name"));
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            log.error("Error getting all recipes: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return recipes;
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
                        "ORDER BY r.Name;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                favorites.add(new Recipe(rs.getInt("RecipeID"), rs.getString("Name")));
            }
        } catch (SQLException e) {
            log.error("Error getting user favorites: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return favorites;
    }

    public boolean removeUserFavorite(int userId, int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "DELETE FROM user_recipe WHERE UserID = ? AND RecipeID = ? AND Type = 'favorite';";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error removing user favorite: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public boolean addUserFavorite(int userId, int recipeId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "INSERT INTO user_recipe (UserID, RecipeID, Type) VALUES (?, ?, 'favorite');";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error adding user favorite: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public boolean updateUserFavorites(int userId, List<Integer> recipeIds) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            con.setAutoCommit(false);
            
            // Delete existing favorites
            String deleteSql = "DELETE FROM user_recipe WHERE UserID = ? AND Type = 'favorite';";
            ps = con.prepareStatement(deleteSql);
            ps.setInt(1, userId);
            ps.executeUpdate();
            
            // Insert new favorites
            String insertSql = "INSERT INTO user_recipe (UserID, RecipeID, Type) VALUES (?, ?, 'favorite');";
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
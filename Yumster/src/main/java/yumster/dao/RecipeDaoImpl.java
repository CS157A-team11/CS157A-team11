package yumster.dao;

import java.sql.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import yumster.obj.Recipe;
import yumster.obj.User;

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
    
    public boolean update(Recipe recipe) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "UPDATE recipes SET Name = ?, Instructions = ?, Time = ?, Servings = ? WHERE recipeId = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getInstructions());
            ps.setInt(3, recipe.getTime());
            ps.setInt(4, recipe.getServings());
            ps.setInt(5, recipe.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error updating recipe: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, ps, con);
        }
    }
    
    public boolean setImage(Integer recipeId, Integer imageId) {
        DbConnection dbCon = new DbConnection();
        Connection con = dbCon.getConnection();
        String sql = "UPDATE recipes SET imageId = ? WHERE recipeId = ?;";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, imageId);
            ps.setInt(2, recipeId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error updating recipe: " + e.getMessage(), e);
            return false;
        }
    }
    
    public Recipe getLatestByUserId(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT RecipeID, Name, Instructions, Time, Servings, UserID FROM recipes WHERE userID = ? ORDER BY recipeID DESC;";
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
    
    public Recipe getById(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT RecipeID, Name, Instructions, Time, Servings, UserID, Reputation, ImageId FROM recipes WHERE RecipeID = ?;";
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
                recipe.setReputation(rs.getInt("Reputation"));
                recipe.setImageId(rs.getInt("ImageId"));
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
            con = dbCon.getNonAutoCommitConnection();
            
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
    
    public List<Recipe> search(Integer min, Integer limit, String sort, Integer userId) {
        try {
            DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            StringBuilder sql = new StringBuilder("SELECT RecipeID, Name, Instructions, Time, Servings, UserID, Reputation, ImageId FROM recipes");
            switch (sort) {
            	case "upvotes":
            		sql.append(" ORDER BY Reputation DESC, RecipeId DESC");
            		break;
            	case "newest":
            		sql.append(" ORDER BY RecipeId DESC");
            		break;
            	default:
            		sql.append(" ORDER BY Reputation DESC, RecipeId DESC");
            		break;
            }
            sql.append(" LIMIT ? OFFSET ?;");
//            System.out.println(sql);
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            	ps.setInt(1, limit);
            	ps.setInt(2, min);
	            ResultSet rs = ps.executeQuery();
	            List<Recipe> recipes = new ArrayList<>();
	
	            while (rs.next()) {
	                Recipe recipe = new Recipe();
	                recipe.setId(rs.getInt("RecipeID"));
	                recipe.setName(rs.getString("Name"));
	                recipe.setInstructions(rs.getString("Instructions"));
	                recipe.setTime(rs.getInt("Time"));
	                recipe.setServings(rs.getInt("Servings"));
	                recipe.setUserId(rs.getInt("UserID"));
	                recipe.setReputation(rs.getInt("Reputation"));
	                recipe.setImageId(rs.getInt("ImageId"));
	                recipes.add(recipe);
	            }
	            return recipes;
	        } catch (SQLException e) {
	        	System.out.println(e);
	            log.error("Error searching recipe: " + e.getMessage(), e);
	        }
        } catch (Exception e) {
        	System.out.println(e);
            log.error("Error searching recipe: " + e.getMessage(), e);
        }
        return null;
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
    
    private List<String> sanitizeKeywords(List<String> keywords) {
        List<String> sanitizedKeywords = new ArrayList<>();
        for (String keyword : keywords) {
            sanitizedKeywords.add(
                keyword.replace("!", "!!")
                       .replace("%", "!%")
                       .replace("_", "!_")
                       .replace("[", "![")
            );
        }
        return sanitizedKeywords;
    }
    
    public List<Recipe> filter(int userId, List<String> keywords) {
        List<Recipe> recipes = new ArrayList<>();
        List<String> sanitizedKeywords = sanitizeKeywords(keywords);

        String sql = "SELECT DISTINCT RecipeID, Name, Instructions, Time, Servings, UserID, ImageID, Reputation "
                   + "FROM recipes "
                   + "WHERE RecipeID NOT IN ("
                   + "    SELECT DISTINCT RecipeID "
                   + "    FROM recipe_ingredients "
                   + "    WHERE IngredientID IN ("
                   + "        SELECT IngredientID "
                   + "        FROM user_restrictions "
                   + "        WHERE UserID = ?"
                   + "    )"
                   + ") "
                   + "AND NOT EXISTS ("
                   + "    SELECT 1 "
                   + "    FROM recipe_cookingmethods rc "
                   + "    WHERE rc.RecipeID = recipes.RecipeID "
                   + "    AND rc.MethodID NOT IN ("
                   + "        SELECT MethodID "
                   + "        FROM users_cooking_method "
                   + "        WHERE UserID = ?"
                   + "    )"
                   + ") "
                   + "AND (";
        if (sanitizedKeywords.size() > 0) {
	        for (int i = 0; i < sanitizedKeywords.size(); i++) {
	            sql += "Name LIKE ?";
	            if (i < sanitizedKeywords.size() - 1) {
	                sql += " OR ";
	            }
	        }
        } else {
        	sql += "1=1";
        }
        sql += ");";
//        sql += ") "
//             + "AND NOT EXISTS ("
//             + "    SELECT 1 "
//             + "    FROM recipe_ingredients ri "
//             + "    WHERE ri.RecipeID = recipes.RecipeID "
//             + "    AND ri.IngredientID NOT IN ("
//             + "        SELECT IngredientID "
//             + "        FROM users_ingredients "
//             + "        WHERE UserID = ?"
//             + "    )"
//             + ");";

        try {
        	DbConnection dbCon = new DbConnection();
            Connection con = dbCon.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            int parameterIndex = 1;
            ps.setInt(parameterIndex++, userId);  // User ID for restrictions
            ps.setInt(parameterIndex++, userId);  // User ID for cooking methods

            for (String keyword : sanitizedKeywords) {
                ps.setString(parameterIndex++, "%" + keyword + "%");  // Bind keywords
            }

//            ps.setInt(parameterIndex++, userId);  // User ID for ingredients
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("RecipeID"));
                    recipe.setName(rs.getString("Name"));
                    recipe.setInstructions(rs.getString("Instructions"));
                    recipe.setTime(rs.getInt("Time"));
                    recipe.setServings(rs.getInt("Servings"));
                    recipe.setUserId(rs.getInt("UserID"));
                    recipe.setImageId(rs.getInt("ImageID"));
                    recipe.setReputation(rs.getInt("Reputation"));
                    recipes.add(recipe);
                }
            }
        } catch (SQLException e) {
            log.error("Error fetching recipes: " + e.getMessage(), e);
        }
        return recipes;
    }


}
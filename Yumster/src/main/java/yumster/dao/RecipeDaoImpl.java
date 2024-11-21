package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.Recipe;

public class RecipeDaoImpl implements RecipeDao {
    private Log log = LogFactory.getLog(RecipeDaoImpl.class);

	public boolean insert(Recipe recipe) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "INSERT INTO recipes (name, instructions, time, servings) VALUES (?,?,?,?);";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, recipe.getName());
			ps.setString(2, recipe.getInstructions());
			ps.setInt(3, recipe.getTime());
			ps.setInt(4, recipe.getServings());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a Recipe with that ID exists
	 * @param int id
	 * @return boolean status, success or fail
	 */
	public boolean checkExists(int id) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT COUNT(*) FROM recipes WHERE recipeId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			double cnt = rs.getDouble(1);
			if (cnt >= 1) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	};
	
	public ArrayList<Recipe> getByUserId(int id) {
		ArrayList<Recipe> retList = new ArrayList<Recipe>();
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT recipeId, name, instructions, time, servings, userId FROM recipes WHERE userId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Recipe newRecipe = new Recipe(
						rs.getInt(1),
						rs.getString(2),
						rs.getString(3),
						rs.getInt(4),
						rs.getInt(5),
						rs.getInt(6));
				retList.add(newRecipe);				
			};
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retList;
	};
	
	public Recipe getById(int id) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT name, instructions, time, servings, userId FROM recipes WHERE recipeId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) { // id is PK, so only 1 possible.
				Recipe recipe = new Recipe(
						id,
						rs.getString(1),
						rs.getString(2),
						rs.getInt(3),
						rs.getInt(4),
						rs.getInt(5));
				if (rs.next()) {
					log.error("Expected one result from getById, found more");
					return null;
				}
				return recipe;
			} else {
				log.error("Expected one result from getById, found 0");
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	};
	
	public boolean update(Recipe recipe) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "UPDATE recipes SET name = ?, instructions = ?, time = ?, servings = ? WHERE recipeId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, recipe.getName());
			ps.setString(2, recipe.getInstructions());
			ps.setInt(3, recipe.getTime());
			ps.setInt(4, recipe.getServings());
			ps.setInt(5, recipe.getRecipeId());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	};
	
	public boolean transfer(Recipe recipe, int newUserId) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "UPDATE recipes SET userId = ? WHERE recipeId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, recipe.getRecipeId());
			ps.setInt(2, newUserId);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	};
	
	public boolean deleteById(int id) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "DELETE FROM recipes WHERE recipeId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	};
}
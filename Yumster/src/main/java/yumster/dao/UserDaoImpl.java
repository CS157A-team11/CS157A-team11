package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.User;

public class UserDaoImpl implements UserDao {
    private Log log = LogFactory.getLog(UserDaoImpl.class);
	
    /**
     * Insert a new user into the database
     * @param user
     * @return boolean status, success or fail
     */
	public boolean insert(User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "INSERT INTO users (username, commonName, email, passwordHash) VALUES (?,?,?,?);";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getUname());
			ps.setString(2, user.getCname());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPasswordHash());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	};
	
	/**
	 * Checks if a username or email is taken/user exists with that data.
	 * @param email
	 * @param username
	 * @return User or null
	 */
	public boolean checkExists(String email, String username) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT COUNT(*) FROM users WHERE email = ? OR username = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, username);
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
	
	/**
	 * Gets user by userid
	 * @param userId
	 * @param user
	 * @return User or null
	 */
	public User getById(int userId) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, username, commonName, email, passwordHash FROM users WHERE userId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.next(); // id is PK, so only 1 possible.
				User user = new User();
				user.setId(rs.getInt(1));
				user.setUname(rs.getString(2));
				user.setCname(rs.getString(3));
				user.setEmail(rs.getString(4));
				user.setPassword(rs.getString(5));
				return user;
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
	
	/**
	 * Gets user by email
	 * @param email
	 * @param user
	 * @return User or null
	 */
	public User getByEmail(String email) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, username, commonName, email, passwordHash FROM users WHERE email = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User user = new User();
				user.setId(rs.getInt(1));
				user.setUname(rs.getString(2));
				user.setCname(rs.getString(3));
				user.setEmail(rs.getString(4));
				user.setPassword(rs.getString(5));
				rs.next(); // move to after last line
				if (rs.next()) {
					log.error("Expected one result from getByEmail, found more");
					return null;
				}
				return user;
			} else {
				log.error("Expected one result from getByEmail, found 0");
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	};
	
	/**
	 * Gets user by username
	 * @param username
	 * @param user
	 * @return boolean status, success or fail
	 */
	public User getByUsername(String username) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, username, commonName, email, passwordHash FROM users WHERE username = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.next();
				User user = new User();
				user.setId(rs.getInt(1));
				user.setUname(rs.getString(2));
				user.setCname(rs.getString(3));
				user.setEmail(rs.getString(4));
				user.setPassword(rs.getString(5));
				rs.next(); // move to after last line
				if (rs.next()) {
					log.error("Expected one result from getByUsername, found more");
					return null;
				}
				return user;
			} else {
				log.error("Expected one result from getByUsername, found none");
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	};
	
	public boolean updateUsername(String username, int userId) {
	    DbConnection dbCon = new DbConnection();
	    Connection con = dbCon.getConnection();
	    String sql = "UPDATE users SET username = ? WHERE userId = ?";
	    try (PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, username);
	        ps.setInt(2, userId);
	        int rowsAffected = ps.executeUpdate();
	        
	        if(rowsAffected == 1) {
	            return true;
	        } else {
	            log.error("Expected exactly one update from updateUsername got " + rowsAffected);
	            return false;
	        }
	    } catch (SQLException e) {
	        log.error("Error updating username: " + e.getMessage());
	        return false;
	    }
	};

	public boolean updatePassword(String newPasswordHash, int userId) {
	    DbConnection dbCon = new DbConnection();
	    Connection con = dbCon.getConnection();
	    String sql = "UPDATE users SET passwordHash = ? WHERE userId = ?";
	    try {
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.setString(1, newPasswordHash);
	        ps.setInt(2, userId);
	        int rowsAffected = ps.executeUpdate();
	        
	        if(rowsAffected == 1) {
	            return true;
	        } else {
	            log.error("Expected exactly one update from updatePassword got " + rowsAffected);
	            return false;
	        }
	    } catch (SQLException e) {
	        log.error("Error updating password: " + e.getMessage());
	        return false;
	    }
	};

}
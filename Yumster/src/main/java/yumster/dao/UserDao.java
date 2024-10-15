package yumster.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.dao.DbConnection;
import yumster.User;

public class UserDao {
    private Log log = LogFactory.getLog(UserDao.class);
	
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
	}
	
	/**
	 * Checks if a username or email is taken/user exists with that data.
	 * @param email
	 * @param username
	 * @return boolean status, success or fail
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
	}
	
	/**
	 * Gets user by userid, uses an empty user passed from above
	 * @param userId
	 * @param user
	 * @return boolean status, success or fail
	 */
	public boolean getById(int userId, User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, username, commonName, email, passwordHash FROM users WHERE id = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.last();
				if (rs.getRow() == 1) {
					user.setId(rs.getInt(1));
					user.setUname(rs.getString(2));
					user.setCname(rs.getString(3));
					user.setEmail(rs.getString(4));
					user.setPassword(rs.getString(5));
				} else {
					log.error("Expected one result from getById, found " + rs.getRow());
					return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Gets user by email, uses an empty user passed from above
	 * @param email
	 * @param user
	 * @return boolean status, success or fail
	 */
	public boolean getByEmail(String email, User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, username, commonName, email, passwordHash FROM users WHERE email = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.last();
				if (rs.getRow() == 1) {
					user.setId(rs.getInt(1));
					user.setUname(rs.getString(2));
					user.setCname(rs.getString(3));
					user.setEmail(rs.getString(4));
					user.setPassword(rs.getString(5));
				} else {
					log.error("Expected one result from getByEmail, found " + rs.getRow());
					return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Gets user by username, uses an empty user passed from above
	 * @param username
	 * @param user
	 * @return boolean status, success or fail
	 */
	public boolean getByUsername(String username, User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, username, commonName, email, passwordHash FROM users WHERE username = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.last();
				if (rs.getRow() == 1) {
					user.setId(rs.getInt(1));
					user.setUname(rs.getString(2));
					user.setCname(rs.getString(3));
					user.setEmail(rs.getString(4));
					user.setPassword(rs.getString(5));
				} else {
					log.error("Expected one result from getByUsername, found " + rs.getRow());
					return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean updateUsername(String username, int userId) {
	    DbConnection dbCon = new DbConnection();
	    Connection con = dbCon.getConnection();
	    String sql = "UPDATE users SET Username = ? WHERE UserId = ?";
	    try {
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.setString(1, username);
	        ps.setInt(2, userId);
	        int rowsAffected = ps.executeUpdate();
	        if(rowsAffected > 0) {
	            return true;
	        } else {
	        	log.error("Expected more than zero updates from updateUsername");
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean updatePassword(String newPasswordHash, int userId) {
	    DbConnection dbCon = new DbConnection();
	    Connection con = dbCon.getConnection();
	    String sql = "UPDATE users SET PasswordHash = ? WHERE UserId = ?";
	    try {
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.setString(1, newPasswordHash);
	        ps.setInt(2, userId);
	        int rowsAffected = ps.executeUpdate();
	        
	        if(rowsAffected > 0) {
	            return true;
	        } else {
	            System.out.println("Expected more than zero updates from updatePassword");
	            return false;
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
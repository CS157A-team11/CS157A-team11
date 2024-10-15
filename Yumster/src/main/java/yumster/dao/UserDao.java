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
	
	public boolean getByEmail(String email, User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT username, commonName, email, passwordHash FROM users WHERE email = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.last();
				if (rs.getRow() == 1) {
					user.setUname(rs.getString(1));
					user.setCname(rs.getString(2));
					user.setEmail(rs.getString(3));
					user.setPassword(rs.getString(4));
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
	
	public boolean getByUsername(String username, User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT username, commonName, email, passwordHash FROM users WHERE username = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.last();
				if (rs.getRow() == 1) {
					user.setUname(rs.getString(1));
					user.setCname(rs.getString(2));
					user.setEmail(rs.getString(3));
					user.setPassword(rs.getString(4));
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
	
	public boolean updateUsername(String username, User user) {
	    DbConnection dbCon = new DbConnection();
	    Connection con = dbCon.getConnection();
	    String sql = "UPDATE users SET username = ? WHERE userId = ?";
	    try {
	        PreparedStatement pstmt = con.prepareStatement(sql);
	        pstmt.setString(1, username);
	        //pstmt.setInt(2, user.getUserID());
	        int rowsAffected = pstmt.executeUpdate();
	        
	        // Check if the update was successful
	        if(rowsAffected > 0) {
	            return true; // Update successful
	        } else {
	            return false; // Update failed
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Update failed
	    }
	}

	public boolean updatePassword(String password) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		return true;
	}
}
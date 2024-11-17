package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.EmailVerification;

public class EmailVerificationDaoImpl implements EmailVerificationDao {
    private Log log = LogFactory.getLog(UserDao.class);
	
    /**
     * Insert a new token into the database
     * @param token
     * @param userId
     * @param expiration
     * @return boolean status, success or fail
     */
	public EmailVerification insert(int userId, String token, long expiration) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "INSERT INTO emailverification (code, userId, expirationtime) VALUES (?,?,?);";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, token);
			ps.setInt(2, userId);
			ps.setLong(3, expiration);
			int res = ps.executeUpdate();
			if (res == 1) {
				return new EmailVerification(userId, token, expiration);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	/**
     * Get EmailVerification by token, check if still valid
     * @param token
     * @return EmailVerification, null if none valid
     */
	public EmailVerification getByToken(String token) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, expirationtime from emailverification WHERE code = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, token);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.next();
				// check validity
				if (rs.getLong(2) > (System.currentTimeMillis() / 1000L))  {
					EmailVerification verifToken = new EmailVerification(rs.getInt(1), token, rs.getLong(2));
					rs.next();
					if (rs.next()) {
						return null;
					}
					return verifToken;
				} else {
					deleteToken(token); // invalid, remove from table //TODO decide on keeping for logs
					return null;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public long getLatestExpirationByUserId(int userId) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT MAX(expirationtime) from emailverification WHERE userId = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.next();
				return rs.getLong(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -2;
	}
	
	// TODO, decide if we want to invalidate using another column, for logging user access
	/**
	 * Delete token
	 * @param token
	 * @return boolean success or fail
	 */
	public boolean deleteToken(String token) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "DELETE FROM emailverification WHERE code = ?;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, token);
			int rowsAffected = ps.executeUpdate();
	        if(rowsAffected == 1) {
	            return true;
	        } else {
	        	log.error("Expected exactly one update from deleteToken got " + rowsAffected);
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}
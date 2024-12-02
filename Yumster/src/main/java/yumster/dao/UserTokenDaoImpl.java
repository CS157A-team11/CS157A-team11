package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.UserToken;

public class UserTokenDaoImpl implements UserTokenDao {
    private Log log = LogFactory.getLog(UserDao.class);
	
    /**
     * Insert a new token into the database
     * @param token
     * @param userId
     * @param expiration
     * @return boolean status, success or fail
     */
	public UserToken insert(int userId, String token, long expiration) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "INSERT INTO usertokens (userToken, userId, expiration) VALUES (?,?,?);";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, token);
			ps.setInt(2, userId);
			ps.setLong(3, expiration);
			int res = ps.executeUpdate();
			if (res == 1) {
				return new UserToken(userId, token, expiration);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	/**
     * Get UserToken by token, check if still valid
     * @param token
     * @return UserToken, null if none valid
     */
	public UserToken getByToken(String token) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "SELECT userId, expiration from usertokens WHERE userToken = ?;";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, token);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				rs.next();
				// check validity
				if (rs.getLong(2) > (System.currentTimeMillis() / 1000L))  {
					UserToken userToken = new UserToken(rs.getInt(1), token, rs.getLong(2));
					rs.next();
					if (rs.next()) {
						return null;
					}
					return userToken;
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
	
	// TODO, decide if we want to invalidate using another column, for logging user access
	/**
	 * Delete token
	 * @param token
	 * @return boolean success or fail
	 */
	public boolean deleteToken(String token) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "DELETE FROM usertokens WHERE userToken = ?;";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
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
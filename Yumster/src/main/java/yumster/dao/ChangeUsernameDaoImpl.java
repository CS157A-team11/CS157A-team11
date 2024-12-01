package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import yumster.obj.User;

public class ChangeUsernameDaoImpl implements ChangeUsernameDao {
    private Log log = LogFactory.getLog(ChangeUsernameDaoImpl.class);

    public User getCurrentUsername(int userId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT Username FROM Users WHERE UserID = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(userId);
                user.setUname(rs.getString("Username"));
                return user;
            }
        } catch (SQLException e) {
            log.error("Error getting current username: " + e.getMessage());
        } finally {
            closeResources(rs, ps, con);
        }
        return null;
    }

    public boolean validateCurrentUsername(String username, int userId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT 1 FROM Users WHERE Username = ? AND UserID = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, userId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            log.error("Error validating username: " + e.getMessage());
            return false;
        } finally {
            closeResources(rs, ps, con);
        }
    }

    public boolean checkUsernameExists(String username) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT 1 FROM Users WHERE Username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            log.error("Error checking username existence: " + e.getMessage());
            return true; // Return true to prevent username update in case of error
        } finally {
            closeResources(rs, ps, con);
        }
    }

    public boolean updateUsername(String newUsername, int userId) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "UPDATE Users SET Username = ? WHERE UserID = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, newUsername);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Error updating username: " + e.getMessage());
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
package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.CookingMethod;

public class CookingMethodDaoImpl implements CookingMethodDao {
    private Log log = LogFactory.getLog(CookingMethodDaoImpl.class);

    public List<Integer> getUserMethods(int userId) {
        List<Integer> methods = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT MethodID FROM users_cooking_method WHERE UserID = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                methods.add(rs.getInt("MethodID"));
            }
        } catch (SQLException e) {
            log.error("Error getting user methods: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return methods;
    }

    public boolean updateUserMethods(int userId, List<Integer> methodIds) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            con.setAutoCommit(false);
            
            // Delete existing methods
            deleteUserMethods(userId);
            
            // Insert new methods
            String sql = "INSERT INTO users_cooking_method (UserID, MethodID) VALUES (?, ?);";
            ps = con.prepareStatement(sql);
            
            for (Integer methodId : methodIds) {
                ps.setInt(1, userId);
                ps.setInt(2, methodId);
                ps.addBatch();
            }
            
            ps.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) {
            log.error("Error updating user methods: " + e.getMessage(), e);
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
    
    public boolean deleteUserMethods(int userId) {
        DbConnection dbCon = new DbConnection();
        Connection con = dbCon.getConnection();
        String sql = "DELETE FROM users_cooking_method WHERE UserID = ?;";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error("Error deleting user methods: " + e.getMessage());
            return false;
        }
    }

    public List<CookingMethod> getAllMethods() {
        List<CookingMethod> methods = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            DbConnection dbCon = new DbConnection();
            con = dbCon.getConnection();
            String sql = "SELECT MethodID, Name FROM cookingmethods ORDER BY MethodID;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                CookingMethod method = new CookingMethod();
                method.setId(rs.getInt("MethodID"));
                method.setName(rs.getString("Name"));
                methods.add(method);
            }
        } catch (SQLException e) {
            log.error("Error getting all methods: " + e.getMessage(), e);
        } finally {
            closeResources(rs, ps, con);
        }
        return methods;
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
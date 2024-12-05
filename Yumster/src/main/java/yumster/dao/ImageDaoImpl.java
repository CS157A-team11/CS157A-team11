package yumster.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yumster.obj.Recipe;

public class ImageDaoImpl implements ImageDao {
    private Log log = LogFactory.getLog(ImageDao.class);

    public Integer getNewId() {
        DbConnection dbCon = new DbConnection();
        Connection con = dbCon.getNonAutoCommitConnection();
        String sql = "INSERT INTO images VALUES ();";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
            	con.commit();
            	return getLatest();
            }
            con.rollback();
        } catch (SQLException e) {
            log.error("Error adding user favorite: " + e.getMessage(), e);
        }
	    return null;
    };
    
    public Integer getLatest() {
        DbConnection dbCon = new DbConnection();
        Connection con = dbCon.getNonAutoCommitConnection();
        String sql = "SELECT imageID FROM images;";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error getting recipe by ID: " + e.getMessage(), e);
        }
        return null;
    }
}
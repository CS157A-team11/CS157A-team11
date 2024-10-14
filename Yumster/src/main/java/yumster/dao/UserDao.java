package yumster.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import yumster.dao.DbConnection;
import yumster.User;

public class UserDao {
	public String insert(User user) {
		DbConnection dbCon = new DbConnection();
		Connection con = dbCon.getConnection();
		String sql = "INSERT INTO users (username, commonName, email, passwordHash) VALUES (?,?,?,?);";
		String result = "Data Entered Successfully";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getUname());
			ps.setString(2, user.getCname());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPasswordHash());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = "Data Not Entered Successfully";
			e.printStackTrace();
		}
		return result;
	}
}
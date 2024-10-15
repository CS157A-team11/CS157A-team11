package yumster;

import yumster.dao.UserDao;

public class User {
	private String cname, uname, email, password;
	private static UserDao rdao = new UserDao();
	
	public User(String uname, String cname, String email, String password) {
		super();
		this.cname = cname;
		this.uname = uname;
		this.email = email;
		this.password = password;
	}
	
	public User() {}

	public static boolean insert(User user) {
		return rdao.insert(user);
	}
	
	public static boolean checkExists(String email, String username) {
		return rdao.checkExists(email, username);
	}
	
	public static boolean getByEmail(String email, User user) {
		return rdao.getByEmail(email, user);
	}
	
	public static boolean getByUsername(String username, User user) {
		return rdao.getByUsername(username, user);
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPasswordHash() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}
	

}
package yumster;

public class User 
{
	private String cname,uname,email, password;

	public User(String uname, String cname, String email, String password) {
		super();
		this.cname = cname;
		this.uname = uname;
		this.email = email;
		this.password = password;
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
		this.cname= cname;
	}
	
}
package yumster.obj;

public class UserToken {
	private int userId;
	private String token;
	private long expiration;
	
	public UserToken() {}
	
	public UserToken(int userId, String token, long expiration) {
		this.userId = userId;
		this.token = token;
		this.expiration = expiration;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setUname(String token) {
		this.token = token;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

}
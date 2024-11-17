package yumster.obj;

public class EmailVerification {
    private int emailVerificationId, userId;
    private long expiration;
    private String verificationCode;

    // Constructors
    public EmailVerification(int userId, String verificationCode, long expiration) {
        super();
        this.userId = userId;
        this.verificationCode = verificationCode;
        this.expiration = expiration;
    }
    
    public EmailVerification(int emailVerificationId, int userId, String verificationCode, long expiration) {
        super();
        this.emailVerificationId = emailVerificationId;
        this.userId = userId;
        this.verificationCode = verificationCode;
        this.expiration = expiration;
    }

    public EmailVerification() {}

    // Getters and Setters
    public int getEmailVerificationId() {
        return emailVerificationId;
    }

    public void setEmailVerificationId(int emailVerificationId) {
        this.emailVerificationId = emailVerificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
}

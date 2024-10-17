package yumster.tables;

public class EmailVerification {
    private int emailVerificationId;
    private int userId;
    private String verificationCode;

    // Constructors
    public EmailVerification(int emailVerificationId, int userId, String verificationCode) {
        super();
        this.emailVerificationId = emailVerificationId;
        this.userId = userId;
        this.verificationCode = verificationCode;
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
}

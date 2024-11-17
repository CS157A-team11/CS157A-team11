package yumster.dao;


import yumster.obj.EmailVerification;

interface EmailVerificationDao {
	public EmailVerification insert(int userId, String token, long expiration);
	
	/**
	 * Gets userId associated with token if it exists.
	 * @param token
	 * @return boolean status
	 */
	public EmailVerification getByToken(String token);
	
	/**
	 * Gets latest expiration time if it exists. -1 otherwise.
	 * @param token
	 * @return boolean status
	 */
	public long getLatestExpirationByUserId(int userId);
}
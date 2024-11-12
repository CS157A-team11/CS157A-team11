package yumster.dao;


import yumster.obj.UserToken;

interface UserTokenDao {
	public UserToken insert(int userId, String token, long expiration);
	
	/**
	 * Gets userId associated with token if it exists.
	 * @param token
	 * @return boolean status
	 */
	public UserToken getByToken(String token);
}
package yumster.dao;

import yumster.User;

interface UserDao {
	public boolean insert(User user);
	
	/**
	 * Checks if a username or email (both fields with same data) is taken/user exists with that data.
	 * @param emailOrUsername
	 * @return boolean status, success or fail
	 */
	default public boolean checkExists(String emailOrUsername) {
		return checkExists(emailOrUsername, emailOrUsername);
	};
	
	public boolean checkExists(String email, String username);
	
	public User getByEmail(String email);
	
	public User getByUsername(String username);

	default public boolean updateUsername(String username, User user) {
		return updateUsername(username, user.getId());
	};
	
	public boolean updateUsername(String username, int userId);
	
	default public boolean updatePassword(String passwordHash, User user) {
		return updatePassword(passwordHash, user.getId());
	};
	
	public boolean updatePassword(String passwordHash, int userId);
}
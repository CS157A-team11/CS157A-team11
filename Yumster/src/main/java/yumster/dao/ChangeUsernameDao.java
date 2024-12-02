package yumster.dao;

import yumster.obj.User;

public interface ChangeUsernameDao {
    User getCurrentUsername(int userId);
    boolean validateCurrentUsername(String username, int userId);
    boolean checkUsernameExists(String username);
    boolean updateUsername(String newUsername, int userId);
}
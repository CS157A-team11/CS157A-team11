package yumster.dao;

import java.util.List;
import yumster.obj.CookingMethod;

public interface CookingMethodDao {
    List<Integer> getUserMethods(int userId);
    boolean updateUserMethods(int userId, List<Integer> methodIds);
    boolean deleteUserMethods(int userId);
    List<CookingMethod> getAllMethods();
}
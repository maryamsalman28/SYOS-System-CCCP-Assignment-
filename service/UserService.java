// File: service/UserService.java
package service;

import dao.UserDAO;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public boolean registerUser(String name, String email, String password, String phone, String address, String role) {
        return userDAO.addUser(name, email, password, phone, address, role);
    }

    public boolean loginUser(String email, String password) {  
        return userDAO.validateUser(email, password);
    }
}

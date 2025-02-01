package test.service;

import service.UserService;
import dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    
    private UserService userService;
    private UserDAO mockUserDAO;

    @BeforeEach
    void setUp() throws Exception {
        // Instantiate the actual UserService
        userService = new UserService();

        // Create a mock of UserDAO
        mockUserDAO = mock(UserDAO.class);

        // Use reflection to set the private userDAO field inside UserService
        Field userDaoField = UserService.class.getDeclaredField("userDAO");
        userDaoField.setAccessible(true);
        userDaoField.set(userService, mockUserDAO);
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "password123";
        String phone = "1234567890";
        String address = "123 Street";
        String role = "customer";

        when(mockUserDAO.addUser(name, email, password, phone, address, role)).thenReturn(true);

        // When
        boolean result = userService.registerUser(name, email, password, phone, address, role);

        // Then
        assertTrue(result, "User registration should succeed");
        verify(mockUserDAO).addUser(name, email, password, phone, address, role);
    }

    @Test
    void testRegisterUser_Failure() {
        // Given
        String name = "Jane Doe";
        String email = "jane@example.com";
        String password = "password123";
        String phone = "0987654321";
        String address = "456 Avenue";
        String role = "admin";

        when(mockUserDAO.addUser(name, email, password, phone, address, role)).thenReturn(false);

        // When
        boolean result = userService.registerUser(name, email, password, phone, address, role);

        // Then
        assertFalse(result, "User registration should fail");
        verify(mockUserDAO).addUser(name, email, password, phone, address, role);
    }

    @Test
    void testLoginUser_ValidCredentials() {
        // Given
        String email = "valid@example.com";
        String password = "securePassword";

        when(mockUserDAO.validateUser(email, password)).thenReturn(true);

        // When
        boolean result = userService.loginUser(email, password);

        // Then
        assertTrue(result, "Login should be successful with valid credentials");
        verify(mockUserDAO).validateUser(email, password);
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        // Given
        String email = "invalid@example.com";
        String password = "wrongPassword";

        when(mockUserDAO.validateUser(email, password)).thenReturn(false);

        // When
        boolean result = userService.loginUser(email, password);

        // Then
        assertFalse(result, "Login should fail with invalid credentials");
        verify(mockUserDAO).validateUser(email, password);
    }
}

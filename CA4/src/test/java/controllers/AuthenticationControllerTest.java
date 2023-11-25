package controllers;

import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

import static defines.Errors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    Baloot balootMock;
    AuthenticationController authenticationController = new AuthenticationController();

    @BeforeEach
    void setUp() {
        balootMock = mock(Baloot.class);
        authenticationController.setBaloot(balootMock);
    }

    @Test
    @DisplayName("Test AuthenticationController Login successfully login")
    public void testLoginHttpStatusOk() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = Map.of("username", "user", "password", "pass");
        doNothing().when(balootMock).login("user", "pass");
        ResponseEntity<String> response = authenticationController.login(input);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("login successfully!", response.getBody());
    }

    @Test
    @DisplayName("Test AuthenticationController Login with non existing user")
    public void testLoginHttpStatusNotFound() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = Map.of("username", "user", "password", "pass");
        doThrow(new NotExistentUser()).when(balootMock).login("user", "pass");
        ResponseEntity<String> response = authenticationController.login(input);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_USER, response.getBody());
    }

    @Test
    @DisplayName("Test AuthenticationController Login with incorrect password")
    public void testLoginHttpStatusUnauthorized() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = Map.of("username", "user", "password", "incorrectPass");
        doThrow(new IncorrectPassword()).when(balootMock).login("user", "incorrectPass");
        ResponseEntity<String> response = authenticationController.login(input);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(INCORRECT_PASSWORD, response.getBody());
    }

    @Test
    @DisplayName("Test AuthenticationController Signup successfully signup")
    public void testSignupHttpStatusOk() throws UsernameAlreadyTaken {
        Map<String, String> input = Map.of(
                "username", "user",
                "password", "pass",
                "email", "user@mail.com",
                "birthDate", "2023",
                "address", "address");

        User userMock = mock(User.class);
        balootMock.addUser(userMock);
        verify(balootMock).addUser(userMock);
        ResponseEntity<String> response = authenticationController.signup(input);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("signup successfully!", response.getBody());
    }

    @Test
    @DisplayName("Test AuthenticationController Signup with username already taken")
    public void testSignupHttpStatusBadRequest() throws UsernameAlreadyTaken {
        Map<String, String> input = Map.of(
                "username", "user",
                "password", "pass",
                "email", "user@mail.com",
                "birthDate", "2023",
                "address", "address");

        doThrow(new UsernameAlreadyTaken()).when(balootMock).addUser(argThat(user -> user.getUsername().equals("user")));
        ResponseEntity<String> response = authenticationController.signup(input);
        verify(balootMock).addUser(argThat(user -> user.getUsername().equals("user")));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(USERNAME_ALREADY_TAKEN, response.getBody());
    }
}
package controllers;

import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.NotExistentUser;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static defines.Errors.*;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;


import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;


@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class UserControllerTestAPI {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private Baloot balootMock;

    @BeforeEach
    public void setUp() {
        userController.setBaloot(balootMock);
    }

    @Test
    @DisplayName("Test getUserById with existed id")
    public void testGetUserByIdWithExistedId() throws Exception {
        String userId = "1";
        User user = new User("username", "password", "user@gmail.com", "2001-11-25", "address");
        when(balootMock.getUserById(userId)).thenReturn(user);
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.birthDate", is(user.getBirthDate())))
                .andExpect(jsonPath("$.address", is(user.getAddress())));
    }

    @Test
    @DisplayName("Test getUserById with not existed id")
    public void testGetUserByIdWithNotExistedId() throws Exception {
        String userId = "1";
        when(balootMock.getUserById(userId)).thenThrow(new NotExistentUser());
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test addCredit with existed userId, correct credit format, and correct credit range")
    public void testAddCreditWithExistedUserIdAndCorrectCreditFormatAndCorrectCreditRange() throws Exception {
        String userId = "1";
        User user = new User("username", "password", "user@gmail.com", "2001-11-25", "address");
        Map<String, String> input = Map.of("credit", "100");
        when(balootMock.getUserById(userId)).thenReturn(user);
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("credit added successfully!"));
    }

    @Test
    @DisplayName("Test addCredit with not existed userId")
    public void testAddCreditWithNotExistedUserId() throws Exception {
        String userId = "1";
        Map<String, String> input = Map.of("credit", "100");
        when(balootMock.getUserById(userId)).thenThrow(new NotExistentUser());
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));
    }

    @Test
    @DisplayName("Test addCredit with existed userId and incorrect credit format")
    public void testAddCreditWithExistedUserIdAndIncorrectCreditFormat() throws Exception {
        String userId = "1";
        User user = new User("username", "password", "user@gmail.com", "2001-11-25", "address");
        Map<String, String> input = Map.of("credit", "1c");
        when(balootMock.getUserById(userId)).thenReturn(user);
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));
    }

    @Test
    @DisplayName("Test addCredit with existed userId and negative credit")
    public void testAddCreditWithExistedUserIdAndNegativeCredit() throws Exception {
        String userId = "1";
        User user = new User("username", "password", "user@gmail.com", "2001-11-25", "address");
        Map<String, String> input = Map.of("credit", "-100");
        when(balootMock.getUserById(userId)).thenReturn(user);
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_CREDIT_RANGE));
    }

    @Test
    @DisplayName("Test addCredit with null credit")
    public void testAddCreditWithNullCredit() throws Exception {
        String userId = "1";
        Map <String, String> input = Map.of();
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));
    }
}
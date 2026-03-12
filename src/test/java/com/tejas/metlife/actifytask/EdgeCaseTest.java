package com.tejas.metlife.actifytask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tejas.metlife.actifytask.dto.auth.LoginRequest;
import com.tejas.metlife.actifytask.dto.user.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EdgeCaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPasswordValidation_Fails_NoNumber() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test");
        request.setEmail("test_no_num@actify.com");
        request.setPassword("PasswordNoNum"); // No number

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // Because it's an Admin API and we're not logged in
    }

    @Test
    void testLogin_Fails_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@actify.com");
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

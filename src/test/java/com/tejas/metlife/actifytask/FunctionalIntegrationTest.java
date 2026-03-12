package com.tejas.metlife.actifytask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tejas.metlife.actifytask.dto.auth.LoginRequest;
import com.tejas.metlife.actifytask.dto.auth.LoginResponse;
import com.tejas.metlife.actifytask.dto.task.AssignTaskRequest;
import com.tejas.metlife.actifytask.dto.user.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FunctionalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullWorkflow() throws Exception {
        // 1. Login as Admin
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@actify.com");
        adminLogin.setPassword("Admin@123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseStr, LoginResponse.class);
        String adminToken = "Bearer " + loginResponse.getToken();

        // 2. Admin: Create a new user
        CreateUserRequest newUser = new CreateUserRequest();
        newUser.setName("New User");
        newUser.setEmail("newuser@actify.com");
        newUser.setPassword("Password@123");
        newUser.setRoles(Set.of("USER"));

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@actify.com"));

        // 3. Admin: Try to create duplicate user (Error Handling)
        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());

        // 4. Login as Manager
        LoginRequest managerLogin = new LoginRequest();
        managerLogin.setEmail("manager@actify.com");
        managerLogin.setPassword("Manager@123");

        loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerLogin)))
                .andExpect(status().isOk())
                .andReturn();

        loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        String managerToken = "Bearer " + loginResponse.getToken();

        // 5. Manager: View users and tasks
        mockMvc.perform(get("/api/manager/users")
                        .header("Authorization", managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 6. Manager: Assign task to the new user
        // We need the ID of the new user. Let's fetch all users first as admin.
        MvcResult usersResult = mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();
        
        // Find the new user's ID from the list
        String usersJson = usersResult.getResponse().getContentAsString();
        
        // Let's use a simpler way by parsing the array
        Long newUserId = null;
        com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(usersJson);
        for (com.fasterxml.jackson.databind.JsonNode node : root) {
            if ("newuser@actify.com".equals(node.get("email").asText())) {
                newUserId = node.get("id").asLong();
                break;
            }
        }

        if (newUserId == null) {
            throw new RuntimeException("New user not found in the list");
        }

        AssignTaskRequest assignTask = new AssignTaskRequest();
        assignTask.setUserId(newUserId);
        assignTask.setTitle("Test Task");
        assignTask.setDescription("Description of test task");

        mockMvc.perform(post("/api/manager/tasks/assign")
                        .header("Authorization", managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));

        // 7. Login as the new User
        LoginRequest userLogin = new LoginRequest();
        userLogin.setEmail("newuser@actify.com");
        userLogin.setPassword("Password@123");

        loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andReturn();

        loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        String userToken = "Bearer " + loginResponse.getToken();

        // 8. User: View own profile
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@actify.com"));

        // 9. User: View own tasks
        mockMvc.perform(get("/api/user/tasks")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        // 10. RBAC: User tries to access Admin API (Unauthorized Access Handling)
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());

        // 11. Invalid Token Handling
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}

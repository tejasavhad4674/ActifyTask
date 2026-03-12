package com.tejas.metlife.actifytask.controller;

import com.tejas.metlife.actifytask.dto.task.TaskResponse;
import com.tejas.metlife.actifytask.dto.user.UserResponse;
import com.tejas.metlife.actifytask.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserResponse getProfile(Authentication authentication) {
        return userService.getOwnProfile(authentication.getName());
    }

    @GetMapping("/tasks")
    public List<TaskResponse> getOwnTasks(Authentication authentication) {
        return userService.getOwnTasks(authentication.getName());
    }
}

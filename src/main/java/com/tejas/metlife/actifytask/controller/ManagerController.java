package com.tejas.metlife.actifytask.controller;

import com.tejas.metlife.actifytask.dto.task.AssignTaskRequest;
import com.tejas.metlife.actifytask.dto.task.TaskResponse;
import com.tejas.metlife.actifytask.dto.user.UserResponse;
import com.tejas.metlife.actifytask.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/users")
    public List<UserResponse> getUsersWithTasks() {
        return managerService.getAllUsersWithTasks();
    }

    @PostMapping("/tasks/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse assignTask(@Valid @RequestBody AssignTaskRequest request) {
        return managerService.assignTask(request);
    }
}

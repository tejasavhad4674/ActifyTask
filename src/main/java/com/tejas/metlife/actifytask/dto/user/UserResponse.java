package com.tejas.metlife.actifytask.dto.user;

import com.tejas.metlife.actifytask.dto.task.TaskResponse;

import java.util.List;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private List<TaskResponse> tasks;

    public UserResponse(Long id, String name, String email, Set<String> roles, List<TaskResponse> tasks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }
}

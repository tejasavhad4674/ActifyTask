package com.tejas.metlife.actifytask.service;

import com.tejas.metlife.actifytask.dto.task.TaskResponse;
import com.tejas.metlife.actifytask.dto.user.UserResponse;
import com.tejas.metlife.actifytask.model.AppUser;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(AppUser user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        List<TaskResponse> tasks = user.getTasks().stream()
                .sorted(Comparator.comparing(task -> task.getId() == null ? 0L : task.getId()))
                .map(task -> new TaskResponse(task.getId(), task.getTitle(), task.getDescription()))
                .toList();

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), roles, tasks);
    }
}

package com.tejas.metlife.actifytask.service;

import com.tejas.metlife.actifytask.dto.task.AssignTaskRequest;
import com.tejas.metlife.actifytask.dto.task.TaskResponse;
import com.tejas.metlife.actifytask.dto.user.UserResponse;
import com.tejas.metlife.actifytask.exception.ResourceNotFoundException;
import com.tejas.metlife.actifytask.model.AppUser;
import com.tejas.metlife.actifytask.model.UserTask;
import com.tejas.metlife.actifytask.repository.AppUserRepository;
import com.tejas.metlife.actifytask.repository.UserTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ManagerService {

    private final AppUserRepository appUserRepository;
    private final UserTaskRepository userTaskRepository;
    private final UserMapper userMapper;

    public ManagerService(AppUserRepository appUserRepository,
                          UserTaskRepository userTaskRepository,
                          UserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.userTaskRepository = userTaskRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAllUsersWithTasks() {
        return appUserRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public TaskResponse assignTask(AssignTaskRequest request) {
        AppUser user = appUserRepository.findById(Objects.requireNonNull(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        UserTask task = new UserTask();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssignedTo(user);

        UserTask saved = userTaskRepository.save(task);
        return new TaskResponse(saved.getId(), saved.getTitle(), saved.getDescription());
    }
}

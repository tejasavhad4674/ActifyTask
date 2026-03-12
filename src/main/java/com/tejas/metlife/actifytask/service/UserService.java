package com.tejas.metlife.actifytask.service;

import com.tejas.metlife.actifytask.dto.task.TaskResponse;
import com.tejas.metlife.actifytask.dto.user.UserResponse;
import com.tejas.metlife.actifytask.exception.ResourceNotFoundException;
import com.tejas.metlife.actifytask.model.AppUser;
import com.tejas.metlife.actifytask.repository.AppUserRepository;
import com.tejas.metlife.actifytask.repository.UserTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final UserTaskRepository userTaskRepository;
    private final UserMapper userMapper;

    public UserService(AppUserRepository appUserRepository,
                       UserTaskRepository userTaskRepository,
                       UserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.userTaskRepository = userTaskRepository;
        this.userMapper = userMapper;
    }

    public UserResponse getOwnProfile(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    public List<TaskResponse> getOwnTasks(String email) {
        return userTaskRepository.findByAssignedToEmail(email).stream()
                .map(task -> new TaskResponse(task.getId(), task.getTitle(), task.getDescription()))
                .toList();
    }
}

package com.tejas.metlife.actifytask.service;

import com.tejas.metlife.actifytask.dto.user.AssignRoleRequest;
import com.tejas.metlife.actifytask.dto.user.CreateUserRequest;
import com.tejas.metlife.actifytask.dto.user.UpdateUserRequest;
import com.tejas.metlife.actifytask.dto.user.UserResponse;
import com.tejas.metlife.actifytask.exception.DuplicateUserException;
import com.tejas.metlife.actifytask.exception.ResourceNotFoundException;
import com.tejas.metlife.actifytask.model.AppUser;
import com.tejas.metlife.actifytask.model.Role;
import com.tejas.metlife.actifytask.repository.AppUserRepository;
import com.tejas.metlife.actifytask.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AdminService(AppUserRepository appUserRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        UserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("User with email already exists: " + request.getEmail());
        }

        AppUser user = new AppUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(resolveRoles(request.getRoles() == null || request.getRoles().isEmpty()
                ? Set.of("USER")
                : request.getRoles()));

        return userMapper.toResponse(appUserRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return appUserRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(getExistingUser(id));
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        AppUser user = getExistingUser(id);

        if (!user.getEmail().equals(request.getEmail()) && appUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("User with email already exists: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return userMapper.toResponse(appUserRepository.save(user));
    }

    public void deleteUser(Long id) {
        AppUser user = getExistingUser(id);
        appUserRepository.delete(Objects.requireNonNull(user));
    }

    public UserResponse assignRoles(Long userId, AssignRoleRequest request) {
        AppUser user = getExistingUser(userId);
        user.setRoles(resolveRoles(request.getRoles()));
        return userMapper.toResponse(appUserRepository.save(user));
    }

    private AppUser getExistingUser(Long id) {
        return appUserRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(String::toUpperCase)
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name)))
                .collect(Collectors.toSet());
    }
}

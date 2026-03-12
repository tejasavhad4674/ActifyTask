package com.tejas.metlife.actifytask.config;

import com.tejas.metlife.actifytask.model.AppUser;
import com.tejas.metlife.actifytask.model.Role;
import com.tejas.metlife.actifytask.model.UserTask;
import com.tejas.metlife.actifytask.repository.AppUserRepository;
import com.tejas.metlife.actifytask.repository.RoleRepository;
import com.tejas.metlife.actifytask.repository.UserTaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               AppUserRepository appUserRepository,
                               UserTaskRepository userTaskRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = createRoleIfMissing(roleRepository, "ADMIN");
            Role managerRole = createRoleIfMissing(roleRepository, "MANAGER");
            Role userRole = createRoleIfMissing(roleRepository, "USER");

                createUserIfMissing(appUserRepository, passwordEncoder,
                    "Admin User", "admin@actify.com", "Admin@123", Set.of(adminRole, userRole));
            AppUser manager = createUserIfMissing(appUserRepository, passwordEncoder,
                    "Manager User", "manager@actify.com", "Manager@123", Set.of(managerRole, userRole));
            AppUser user = createUserIfMissing(appUserRepository, passwordEncoder,
                    "Regular User", "user@actify.com", "User@1234", Set.of(userRole));

            if (userTaskRepository.count() == 0) {
                UserTask task1 = new UserTask();
                task1.setTitle("Complete onboarding");
                task1.setDescription("Finish all onboarding formalities");
                task1.setAssignedTo(user);

                UserTask task2 = new UserTask();
                task2.setTitle("Review weekly reports");
                task2.setDescription("Analyze performance reports for the week");
                task2.setAssignedTo(manager);

                userTaskRepository.save(task1);
                userTaskRepository.save(task2);
            }
        };
    }

    private Role createRoleIfMissing(RoleRepository roleRepository, String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return roleRepository.save(role);
                });
    }

    private AppUser createUserIfMissing(AppUserRepository appUserRepository,
                                        PasswordEncoder passwordEncoder,
                                        String name,
                                        String email,
                                        String rawPassword,
                                        Set<Role> roles) {
        return appUserRepository.findByEmail(email)
                .orElseGet(() -> {
                    AppUser user = new AppUser();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(rawPassword));
                    user.setRoles(roles);
                    return appUserRepository.save(user);
                });
    }
}

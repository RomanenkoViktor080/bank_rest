package com.example.bankcards.init;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.role.Role;
import com.example.bankcards.entity.role.RoleType;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {
    @Value("${spring.admin.username}")
    private String username;
    @Value("${spring.admin.password}")
    private String password;

    private final UserRepository userRepository;
    private final RoleRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        password = passwordEncoder.encode(password);
        if (
                username != null && !username.isBlank()
                && password != null && !password.isBlank()
                && !userRepository.existsByUsername(username)

        ) {
            Role role = repository.getByTypeOrThrow(RoleType.ADMIN);

            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(role);

            userRepository.save(admin);
            ;
        }
    }
}

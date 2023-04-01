package com.itmo2022_final_project.user;

import com.itmo2022_final_project.exceptions.AppException;
import com.itmo2022_final_project.project.Project;
import com.itmo2022_final_project.security.PasswordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;

    @Autowired
    public UserService(UserRepository userRepository, PasswordConfig passwordConfig) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
    }

    public User saveNewUser(User user) {
        if (user.getFirstName() == null) {
            throw new AppException("First name is missing");
        }
        if (user.getLastName() == null) {
            throw new AppException("Last name is missing");
        }
        if (user.getEmail() == null) {
            throw new AppException("Email is missing");
        }
        if (user.getUsername() == null) {
            throw new AppException("Username is missing");
        }
        if (user.getPassword() == null) {
            throw new AppException("Password is missing");
        }
        if (!(userRepository.findByUsername(user.getUsername()) == null)) {
            throw new AppException("User with username " + user.getUsername() + " already exists");
        }
        if (!(userRepository.findByEmail(user.getEmail()) == null)) {
            throw new AppException("User with email " + user.getEmail() + " already exists");
        }
        user.setPassword(passwordConfig.passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Set<Project> getProjectsByUserName(String name) {
        return userRepository.findByUsername(name).getProjects();
    }
}

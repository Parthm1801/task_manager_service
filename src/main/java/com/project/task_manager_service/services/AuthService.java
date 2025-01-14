package com.project.task_manager_service.services;

import com.project.task_manager_service.entitiy.User;
import com.project.task_manager_service.repositories.UserRepository;
import com.project.task_manager_service.type.AuthRequest;
import com.project.task_manager_service.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil tokenUtil;
    private final PasswordEncoder passwordEncoder;

    private void attachAuthToken(User user, HttpServletResponse response) {
        String token = tokenUtil.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                )
        );

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // Cookie applies to the entire application
        cookie.setMaxAge(10 * 60 * 60); // Token expiry in seconds (10 hours)
        response.addCookie(cookie);
    }

    public void register(User user, HttpServletResponse response) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        attachAuthToken(user, response);
    }

    public void login(AuthRequest authRequest, HttpServletResponse response) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        attachAuthToken(user, response);
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

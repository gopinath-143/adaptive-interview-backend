package com.interview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import com.interview.entity.User;
import com.interview.repository.UserRepository;
import com.interview.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        User user =
                userRepository
                        .findByUsername(
                                request.getUsername());

        if (user == null) {

            throw new RuntimeException(
                    "User Not Found");
        }

        if (!user.getPassword()
                .equals(
                        request.getPassword())) {

            throw new RuntimeException(
                    "Invalid Password");
        }

        String token =
                JwtUtil.generateToken(
                        user.getUsername());

        return new LoginResponse(
                token,
                user.getRole());
    }
}
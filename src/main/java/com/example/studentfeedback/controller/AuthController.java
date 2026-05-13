package com.example.studentfeedback.controller;

import com.example.studentfeedback.common.dto.ApiResponse;
import com.example.studentfeedback.dto.LoginRequest;
import com.example.studentfeedback.dto.LoginResponse;
import com.example.studentfeedback.dto.SignUpRequest;
import com.example.studentfeedback.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<Void> signUp(@RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.success("Sign up successful", null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ApiResponse.success("Login successful", new LoginResponse(token));
    }
}

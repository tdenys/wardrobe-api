package com.example.apidressing.controller;

import com.example.apidressing.gen.api.AuthApi;
import com.example.apidressing.gen.model.AuthRequest;
import com.example.apidressing.gen.model.AuthResponse;
import com.example.apidressing.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> register(AuthRequest body) {
        String token = authService.register(body.getEmail(), body.getPassword());
        return new ResponseEntity<>(new AuthResponse().token(token), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest body) {
        String token = authService.login(body.getEmail(), body.getPassword());
        return ResponseEntity.ok(new AuthResponse().token(token));
    }
}

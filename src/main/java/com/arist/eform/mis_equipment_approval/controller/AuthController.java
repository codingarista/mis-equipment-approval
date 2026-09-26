package com.arist.eform.mis_equipment_approval.controller;

import com.arist.eform.mis_equipment_approval.dto.LoginRequest;
import com.arist.eform.mis_equipment_approval.dto.LoginResponse;
import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.service.AuthService;
import com.arist.eform.mis_equipment_approval.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getUsername(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("帳號或密碼錯誤");
        }

        String token = tokenService.createToken(user);

        LoginResponse response = new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getRole(),
            user.getDepartmentId(),
            token
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        tokenService.invalidateToken(token);
        return ResponseEntity.ok().build();
    }
}
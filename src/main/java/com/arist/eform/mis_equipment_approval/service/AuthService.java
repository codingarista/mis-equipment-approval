package com.arist.eform.mis_equipment_approval.service;

import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 驗證帳號密碼是否正確,成功回傳 User,失敗回傳 null
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null; // 找不到這個帳號
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null; // 密碼不符
        }

        return user; // 驗證成功
    }
}
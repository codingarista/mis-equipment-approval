package com.arist.eform.mis_equipment_approval.service;

import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 驗證帳號密碼是否正確,成功回傳 User,失敗回傳 null
    // 注意:目前用明碼直接比對,之後會改成 BCrypt 加密比對
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null; // 找不到這個帳號
        }

        if (!user.getPassword().equals(password)) {
            return null; // 密碼不符
        }

        return user; // 驗證成功
    }
}
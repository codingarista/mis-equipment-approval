package com.arist.eform.mis_equipment_approval.service;

import com.arist.eform.mis_equipment_approval.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    // 存放「Token → 使用者」的對應關係,存在記憶體裡
    // 注意:伺服器重新啟動後,這裡的資料會全部消失,使用者需要重新登入
    private final ConcurrentHashMap<String, User> tokenStore = new ConcurrentHashMap<>();

    // 登入成功時呼叫,產生一組新的 Token,並記住它屬於哪個使用者
    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user);
        return token;
    }

    // 依照 Token,查出目前是哪一位使用者。查不到就回傳 null
    public User getUserByToken(String token) {
        return tokenStore.get(token);
    }

    // 登出時呼叫,把這個 Token 從記憶體中移除,讓它失效
    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }
}
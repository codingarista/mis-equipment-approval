package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.Role;
import com.arist.eform.mis_equipment_approval.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    // 建構子注入(Constructor Injection):Spring 會自動把 JdbcTemplate 準備好、傳進來
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 依照帳號查詢一位使用者,找不到就回傳 null
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, full_name, role, department_id FROM users WHERE username = ?";

        List<User> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setDepartmentId(rs.getInt("department_id"));
            return user;
        }, username);

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}
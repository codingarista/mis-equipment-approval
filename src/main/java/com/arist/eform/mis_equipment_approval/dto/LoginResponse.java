package com.arist.eform.mis_equipment_approval.dto;

import com.arist.eform.mis_equipment_approval.model.Role;

public class LoginResponse {

    private Integer id;
    private String username;
    private String fullName;
    private Role role;
    private Integer departmentId;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Integer id, String username, String fullName, Role role, Integer departmentId, String token) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.departmentId = departmentId;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
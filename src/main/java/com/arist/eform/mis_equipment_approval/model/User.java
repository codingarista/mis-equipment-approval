package com.arist.eform.mis_equipment_approval.model;

public class User {

    private Integer id;
    private String username;
    private String password;
    private String fullName;
    private Role role;
    private Integer departmentId;

    // 無參數建構子(Spring 和資料庫查詢工具需要用它來建立空白物件)
    public User() {
    }

    // 有參數建構子(方便手動建立一個完整的物件)
    public User(Integer id, String username, String password, String fullName, Role role, Integer departmentId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.departmentId = departmentId;
    }

    // Getter 和 Setter(讓外部能安全地讀取/修改這些欄位)
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
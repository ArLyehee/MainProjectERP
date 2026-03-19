package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class User {

    private Long          userId;
    private String        username;
    private String        password;
    private String        name;
    private String        email;
    private String        phone;
    private String        role;       // ADMIN, MANAGER, EMPLOYEE
    private String        department;
    private String        status;      // ACTIVE, INACTIVE
    private String        permissions; // comma-separated page keys
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    // Getters
    public Long          getUserId()     { return userId; }
    public String        getUsername()   { return username; }
    public String        getPassword()   { return password; }
    public String        getName()       { return name; }
    public String        getEmail()      { return email; }
    public String        getPhone()      { return phone; }
    public String        getRole()       { return role; }
    public String        getDepartment() { return department; }
    public String        getStatus()      { return status; }
    public String        getPermissions() { return permissions; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }

    // Setters
    public void setUserId(Long userId)                    { this.userId = userId; }
    public void setUsername(String username)              { this.username = username; }
    public void setPassword(String password)              { this.password = password; }
    public void setName(String name)                      { this.name = name; }
    public void setEmail(String email)                    { this.email = email; }
    public void setPhone(String phone)                    { this.phone = phone; }
    public void setRole(String role)                      { this.role = role; }
    public void setDepartment(String department)          { this.department = department; }
    public void setStatus(String status)                  { this.status = status; }
    public void setPermissions(String permissions)        { this.permissions = permissions; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)     { this.updatedAt = updatedAt; }
}

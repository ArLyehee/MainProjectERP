package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.User;
import com.gaebalfan.erp.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userMapper.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Map<String, String> body) {
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(passwordEncoder.encode(body.get("password")));
        user.setName(body.get("name"));
        user.setEmail(body.get("email"));
        user.setPhone(body.get("phone"));
        user.setRole(body.getOrDefault("role", "EMPLOYEE"));
        user.setDepartment(body.get("department"));
        user.setStatus("ACTIVE");
        userMapper.insertUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userMapper.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = new User();
        user.setUserId(id);
        user.setName(body.get("name"));
        user.setEmail(body.get("email"));
        user.setPhone(body.get("phone"));
        user.setRole(body.getOrDefault("role", "EMPLOYEE"));
        user.setDepartment(body.get("department"));
        userMapper.updateUser(user);
        // 비밀번호 변경 요청이 있을 경우
        if (body.get("password") != null && !body.get("password").isEmpty()) {
            userMapper.updatePassword(id, passwordEncoder.encode(body.get("password")));
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (body.get("password") != null && !body.get("password").isEmpty()) {
            userMapper.updatePassword(id, passwordEncoder.encode(body.get("password")));
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userMapper.updateStatus(id, body.get("status"));
        return ResponseEntity.ok().build();
    }
}

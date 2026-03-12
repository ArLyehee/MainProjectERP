package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Department;
import com.gaebalfan.erp.domain.Position;
import com.gaebalfan.erp.mapper.EmployeeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import com.gaebalfan.erp.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(EmployeeMapper employeeMapper, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 부서 목록
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(employeeMapper.findAllDepartments());
    }

    // 부서 등록
    @PostMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addDepartment(@RequestBody Map<String, String> body) {
        employeeMapper.insertDepartment(body.get("departmentName"));
        return ResponseEntity.ok().build();
    }

    // 부서 삭제
    @DeleteMapping("/departments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        employeeMapper.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    // 직급 목록
    @GetMapping("/positions")
    public ResponseEntity<List<Position>> getPositions() {
        return ResponseEntity.ok(employeeMapper.findAllPositions());
    }

    // 직급 등록
    @PostMapping("/positions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addPosition(@RequestBody Map<String, String> body) {
        employeeMapper.insertPosition(body.get("positionName"));
        return ResponseEntity.ok().build();
    }

    // 직급 삭제
    @DeleteMapping("/positions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        employeeMapper.deletePosition(id);
        return ResponseEntity.ok().build();
    }

    // 내 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(Authentication auth, @RequestBody Map<String, String> body) {
        String username = auth.getName();
        String oldPw = body.get("oldPassword");
        String newPw = body.get("newPassword");
        var userOpt = userMapper.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다."));
        var user = userOpt.get();
        if (!passwordEncoder.matches(oldPw, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "현재 비밀번호가 올바르지 않습니다."));
        }
        userMapper.updatePassword(user.getUserId(), passwordEncoder.encode(newPw));
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }
}

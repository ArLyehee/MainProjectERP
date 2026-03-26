package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Attendance;
import com.gaebalfan.erp.mapper.EmployeeMapper;
import com.gaebalfan.erp.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;
    private final EmployeeMapper employeeMapper;

    public AttendanceController(AttendanceService service, EmployeeMapper employeeMapper) {
        this.service = service;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping("/check-in")
    public ResponseEntity<Void> checkIn(@RequestBody Map<String, Object> body) {
        if (body.get("employeeId") == null || body.get("employeeId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "직원을 선택하세요.").build();

        Attendance att = new Attendance();
        att.setEmployeeId(Long.parseLong(body.get("employeeId").toString()));
        att.setWorkDate(LocalDate.now());
        att.setCheckIn(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        service.insert(att);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/check-out")
    public ResponseEntity<Void> checkOut(@PathVariable Long id) {
        service.checkOut(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> registerLeave(@RequestBody Map<String, Object> body) {
        if (body.get("employeeId") == null || body.get("employeeId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "직원을 선택하세요.").build();
        if (body.get("leaveDate") == null || body.get("leaveDate").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "날짜를 입력하세요.").build();

        Attendance att = new Attendance();
        att.setEmployeeId(Long.parseLong(body.get("employeeId").toString()));
        att.setWorkDate(LocalDate.parse(body.get("leaveDate").toString()));
        String leaveType = body.getOrDefault("leaveType", "ANNUAL").toString();
        att.setLeaveType(leaveType);
        service.insertLeave(att);
        if ("ANNUAL".equals(leaveType) || "SICK".equals(leaveType)) {
            employeeMapper.decrementRemaining(att.getEmployeeId());
        }
        return ResponseEntity.ok().build();
    }
}

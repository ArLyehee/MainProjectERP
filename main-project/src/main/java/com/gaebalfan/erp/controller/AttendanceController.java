package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Attendance;
import com.gaebalfan.erp.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping("/check-in")
    public ResponseEntity<Void> checkIn(@RequestBody Map<String, Object> body) {
        Attendance att = new Attendance();
        att.setEmployeeId(Long.parseLong(body.get("employeeId").toString()));
        att.setWorkDate(LocalDate.now());
        att.setCheckIn(LocalDateTime.now());
        service.insert(att);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/check-out")
    public ResponseEntity<Void> checkOut(@PathVariable Long id) {
        service.checkOut(id);
        return ResponseEntity.ok().build();
    }
}

package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Attendance;
import com.gaebalfan.erp.mapper.AttendanceMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceMapper mapper;

    public AttendanceService(AttendanceMapper mapper) {
        this.mapper = mapper;
    }

    public List<Attendance> findAll() {
        return mapper.findAll();
    }

    public List<Attendance> findByEmployeeId(Long employeeId) {
        return mapper.findByEmployeeId(employeeId);
    }

    public void insert(Attendance obj) {
        mapper.insert(obj);
    }

    public void checkOut(Long attendanceId) {
        mapper.updateCheckOut(attendanceId);
    }
}

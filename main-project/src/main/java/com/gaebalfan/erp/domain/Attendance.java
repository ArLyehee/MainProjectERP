package com.gaebalfan.erp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    private Long          attendanceId;
    private Long          employeeId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private LocalDate     workDate;

    // 조인용
    private String        employeeName;

    public Long          getAttendanceId() { return attendanceId; }
    public Long          getEmployeeId()   { return employeeId; }
    public LocalDateTime getCheckIn()      { return checkIn; }
    public LocalDateTime getCheckOut()     { return checkOut; }
    public LocalDate     getWorkDate()     { return workDate; }
    public String        getEmployeeName() { return employeeName; }

    public void setAttendanceId(Long attendanceId)        { this.attendanceId = attendanceId; }
    public void setEmployeeId(Long employeeId)            { this.employeeId = employeeId; }
    public void setCheckIn(LocalDateTime checkIn)         { this.checkIn = checkIn; }
    public void setCheckOut(LocalDateTime checkOut)       { this.checkOut = checkOut; }
    public void setWorkDate(LocalDate workDate)           { this.workDate = workDate; }
    public void setEmployeeName(String employeeName)      { this.employeeName = employeeName; }
}

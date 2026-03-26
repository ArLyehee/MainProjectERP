package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Attendance {
    private Long       attendanceId;
    private Long       employeeId;
    private LocalDate  workDate;
    private String     checkIn;
    private String     checkOut;
    private BigDecimal workHours;
    private BigDecimal overtimeHours;
    private Integer    lateMinutes;
    private Integer    earlyLeaveMinutes;
    private Integer    dailyPay;

    private String     leaveType;

    // 조인용
    private String     employeeName;

    public Long       getAttendanceId()      { return attendanceId; }
    public Long       getEmployeeId()        { return employeeId; }
    public LocalDate  getWorkDate()          { return workDate; }
    public String     getCheckIn()           { return checkIn; }
    public String     getCheckOut()          { return checkOut; }
    public BigDecimal getWorkHours()         { return workHours; }
    public BigDecimal getOvertimeHours()     { return overtimeHours; }
    public Integer    getLateMinutes()       { return lateMinutes; }
    public Integer    getEarlyLeaveMinutes() { return earlyLeaveMinutes; }
    public Integer    getDailyPay()          { return dailyPay; }
    public String     getLeaveType()          { return leaveType; }
    public String     getEmployeeName()      { return employeeName; }

    public void setAttendanceId(Long v)        { this.attendanceId = v; }
    public void setEmployeeId(Long v)          { this.employeeId = v; }
    public void setWorkDate(LocalDate v)       { this.workDate = v; }
    public void setCheckIn(String v)           { this.checkIn = v; }
    public void setCheckOut(String v)          { this.checkOut = v; }
    public void setWorkHours(BigDecimal v)     { this.workHours = v; }
    public void setOvertimeHours(BigDecimal v) { this.overtimeHours = v; }
    public void setLateMinutes(Integer v)      { this.lateMinutes = v; }
    public void setEarlyLeaveMinutes(Integer v){ this.earlyLeaveMinutes = v; }
    public void setDailyPay(Integer v)         { this.dailyPay = v; }
    public void setLeaveType(String v)         { this.leaveType = v; }
    public void setEmployeeName(String v)      { this.employeeName = v; }
}

package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Employee {
    private Long          employeeId;
    private String        name;
    private String        email;
    private String        phone;
    private LocalDate     hireDate;
    private Long          departmentId;
    private Long          positionId;
    private BigDecimal    salary;
    private String        status;  // ACTIVE, RESIGNED
    private LocalDateTime createdAt;
    private Integer       annualLeave;
    private Integer       remaining;
    private String        photoPath;

    // 조인용
    private String        departmentName;
    private String        positionName;

    public Long          getEmployeeId()    { return employeeId; }
    public String        getName()          { return name; }
    public String        getEmail()         { return email; }
    public String        getPhone()         { return phone; }
    public LocalDate     getHireDate()      { return hireDate; }
    public Long          getDepartmentId()  { return departmentId; }
    public Long          getPositionId()    { return positionId; }
    public BigDecimal    getSalary()        { return salary; }
    public String        getStatus()        { return status; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public Integer       getAnnualLeave()   { return annualLeave; }
    public Integer       getRemaining()     { return remaining; }
    public String        getPhotoPath()     { return photoPath; }
    public String        getDepartmentName(){ return departmentName; }
    public String        getPositionName()  { return positionName; }

    public void setEmployeeId(Long employeeId)            { this.employeeId = employeeId; }
    public void setName(String name)                      { this.name = name; }
    public void setEmail(String email)                    { this.email = email; }
    public void setPhone(String phone)                    { this.phone = phone; }
    public void setHireDate(LocalDate hireDate)           { this.hireDate = hireDate; }
    public void setDepartmentId(Long departmentId)        { this.departmentId = departmentId; }
    public void setPositionId(Long positionId)            { this.positionId = positionId; }
    public void setSalary(BigDecimal salary)              { this.salary = salary; }
    public void setStatus(String status)                  { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setAnnualLeave(Integer annualLeave)        { this.annualLeave = annualLeave; }
    public void setRemaining(Integer remaining)            { this.remaining = remaining; }
    public void setPhotoPath(String photoPath)             { this.photoPath = photoPath; }
    public void setDepartmentName(String departmentName)  { this.departmentName = departmentName; }
    public void setPositionName(String positionName)      { this.positionName = positionName; }
}

package com.gaebalfan.erp.domain;

public class Department {
    private Long   departmentId;
    private String departmentName;

    public Long   getDepartmentId()   { return departmentId; }
    public String getDepartmentName() { return departmentName; }

    public void setDepartmentId(Long departmentId)          { this.departmentId = departmentId; }
    public void setDepartmentName(String departmentName)    { this.departmentName = departmentName; }
}

package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Employee;
import com.gaebalfan.erp.domain.Department;
import com.gaebalfan.erp.domain.Position;
import com.gaebalfan.erp.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeMapper mapper) {
        this.mapper = mapper;
    }

    public List<Employee> findAll() {
        return mapper.findAll();
    }

    public Employee findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Employee obj) {
        mapper.insert(obj);
    }

    public void update(Employee obj) {
        mapper.update(obj);
    }

    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);
    }

    public List<Department> findAllDepartments() {
        return mapper.findAllDepartments();
    }

    public List<Position> findAllPositions() {
        return mapper.findAllPositions();
    }
}

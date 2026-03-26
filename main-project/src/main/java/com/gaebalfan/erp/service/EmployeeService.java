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

    public List<Employee> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }

    public int count(String q) {
        return mapper.count(q);
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

    public void updatePhoto(Long id, String photoPath) {
        mapper.updatePhoto(id, photoPath);
    }

    public List<Department> findAllDepartments() {
        return mapper.findAllDepartments();
    }

    public List<Position> findAllPositions() {
        return mapper.findAllPositions();
    }
}

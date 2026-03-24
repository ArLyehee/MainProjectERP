package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Employee;
import com.gaebalfan.erp.domain.Department;
import com.gaebalfan.erp.domain.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface EmployeeMapper {
    List<Employee> findAll();
    List<Employee> findAllPaged(@Param("offset") int offset, @Param("size") int size);
    int count();
    Employee findById(@Param("employeeId") Long employeeId);
    void insert(Employee employee);
    void update(Employee employee);
    void updateStatus(@Param("employeeId") Long employeeId, @Param("status") String status);
    List<Department> findAllDepartments();
    List<Position> findAllPositions();
    void insertDepartment(@Param("departmentName") String departmentName);
    void deleteDepartment(@Param("departmentId") Long departmentId);
    void insertPosition(@Param("positionName") String positionName);
    void deletePosition(@Param("positionId") Long positionId);
}
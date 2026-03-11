package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface AttendanceMapper {
    List<Attendance> findByEmployeeId(@Param("employeeId") Long employeeId);
    void insert(Attendance attendance);
    void updateCheckOut(@Param("attendanceId") Long attendanceId);
}
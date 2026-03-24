package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface WorkOrderMapper {
    List<WorkOrder> findAll();
    List<WorkOrder> findAllPaged(@Param("offset") int offset, @Param("size") int size);
    int count();
    WorkOrder findById(@Param("workOrderId") Long workOrderId);
    void insert(WorkOrder workOrder);
    void updateStatus(@Param("workOrderId") Long workOrderId, @Param("status") String status);
}
package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.WorkOrder;
import com.gaebalfan.erp.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkOrderService {

    private final WorkOrderMapper mapper;

    public WorkOrderService(WorkOrderMapper mapper) {
        this.mapper = mapper;
    }

    public List<WorkOrder> findAll() {
        return mapper.findAll();
    }

    public WorkOrder findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(WorkOrder obj) {
        mapper.insert(obj);
    }

    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);
    }
}

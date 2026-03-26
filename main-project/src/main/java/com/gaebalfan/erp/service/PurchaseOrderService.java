package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.PurchaseOrder;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderMapper mapper;

    public PurchaseOrderService(PurchaseOrderMapper mapper) {
        this.mapper = mapper;
    }

    public List<PurchaseOrder> findAll() {
        return mapper.findAll();
    }

    public List<PurchaseOrder> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }

    public int count(String q) {
        return mapper.count(q);
    }

    public PurchaseOrder findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(PurchaseOrder obj) {
        if (obj.getPoCode() == null || obj.getPoCode().isBlank()) {
            obj.setPoCode("PO-" + System.currentTimeMillis());
        }
        mapper.insert(obj);
    }

    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);
    }
}

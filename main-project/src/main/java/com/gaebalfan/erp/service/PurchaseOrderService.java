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

    public PurchaseOrder findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(PurchaseOrder obj) {
        mapper.insert(obj);
    }
}
